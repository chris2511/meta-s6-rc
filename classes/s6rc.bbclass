
S6RC_BASEDIR = "/etc/s6-rc"
S6RC_TREE = "${S6RC_BASEDIR}/tree"
FILES_${PN} += "${S6RC_TREE}"

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r logger"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/lib/dbus \
                            --no-create-home --shell /bin/false \
                            --gid logger logger"

# This class and its use is documented in the README.md
python do_s6rc_create_tree() {

    def array_to_file(f, data):
        try:
            cfgfile = open(f, 'w')
        except OSError:
            bb.fatal('Unable to open %s' % f)

        cfgfile.write("\n".join(data) + "\n")

    # Create the tree directory and 'type' file
    def write_type(tree, rc_type):
        bb.utils.mkdirhier(tree)
        array_to_file(tree + "/type", [ rc_type ])

    # for each key in the 'files' dict, check whether it is part
    # of 'valid_files' and put its value into the file with name 'key'
    # if a file called "name.<valid_file>" exists, us it
    # check that all files listed in 'mandatory' do exist
    def write_verbatim(workdir, tree, name, files, valid_files, mandatory):
        from shutil import copyfile
        # Check that all "files" are valid_files
        for sfile in files:
            if not sfile in valid_files:
                raise bb.parse.SkipRecipe("'%s' in S6RC_*_%s is not a valid file. Valid files are: %s" % (sfile, name, ' '.join(valid_files)))

        for sfile in valid_files:
            # explicitly provided source file <name>.<sfile> like dropbear.run
            # takes precedence
            sourcefile = workdir + "/" + name + "." + sfile
            if os.path.exists(sourcefile):
                copyfile(sourcefile, tree + "/" + sfile)
                if sfile in files:
                  bb.warn("Ignoring S6RC_*_%s[%s], because a source file %s exists" % (name, sfile, sourcefile))
            elif sfile in files:
                # By default the content is a one-element array
                # with a single line entry
                content = [ files[sfile].strip() ]
                if sfile == "dependencies":
                    # Dependencies are listed one per line
                    content = files[sfile].split()
                if sfile == "run" and files[sfile][0:2] != "#!":
                    # inline run scripts are expected to be execlineb
                    content.insert(0, "#!/bin/execlineb -P")
                if sfile != "bundles":
                    array_to_file(tree + "/" + sfile, content)

            elif sfile in mandatory:
                raise bb.parse.SkipRecipe("Error with S6RC_*_%s: '%s' files are mandatory. Either as S6RC_*_%s[%s] or as source file '%s.%s'." % (name, sfile, name, sfile, name, sfile))

    workdir = d.getVar("WORKDIR")
    basetree = workdir + "/tree"

    if d.getVar("INIT_MANAGER") != "s6": return

    valid_files_atomic = [ "timeout-up", "timeout-down",
                           "dependencies", "bundles" ]
    # Bundles
    for bundle in (d.getVar('S6RC_BUNDLES', True) or "").split():
        tree = basetree + "/" + bundle
        contents = (d.getVar('S6RC_BUNDLE_%s' % bundle) or "").split()
        if not contents:
            raise bb.parse.SkipRecipe("S6RC_BUNDLE_%s is mandatory." % bundle)

        write_type(tree, "bundle")
        array_to_file(tree + "/contents", contents)

    # Oneshots
    for oneshot in (d.getVar('S6RC_ONESHOTS', True) or "").split():
        tree = basetree + "/" + oneshot
        write_type(tree, "oneshot")

        valid_files = valid_files_atomic + [ "up", "down" ]
        files = d.getVarFlags("S6RC_ONESHOT_%s" % oneshot) or { }

        write_verbatim(workdir, tree, oneshot, files, valid_files, [ "up" ])

    # Longruns
    for longrun in (d.getVar('S6RC_LONGRUNS', True) or "").split():
        tree = basetree + "/" + longrun
        write_type(tree, "longrun")
        valid_files = valid_files_atomic + [ "producer-for", "consumer-for",
                        "run", "finish", "notification-fd", "timeout-kill",
                        "timeout-finish", "max-death-tally", "down-signal" ]
        files = d.getVarFlags("S6RC_LONGRUN_%s" % longrun) or { }
        log = d.getVarFlags('S6RC_LONGRUN_%s_log' % longrun)
        logname = ""
        if log:
            logname = longrun + "-log"
            files["producer-for"] = logname

        write_verbatim(workdir, tree, longrun, files, valid_files, [ "run" ])
        # Automatic log-service generation
        if log:
            tree = basetree + "/" + logname
            write_type(tree, "longrun")

            files = { "run": "#!/bin/execlineb -P\n\
umask %s s6-setuidgid %s s6-log -d3 %s %s" %
                        (log.get("umask", "0037"), log.get("user", "logger"),
                         log.get("script", "T s100000 n10"),
                         log.get("dir", "/var/log/" + longrun)),
                      "notification-fd": "3",
                      "consumer-for": longrun,
                      "dependencies": "mount-temp" }
            write_verbatim(workdir, tree, logname, files, valid_files, [ "run" ])
}
addtask s6rc_create_tree after do_compile before do_install

fakeroot do_s6rc_install_tree() {
  if test "${INIT_MANAGER}" = "s6"; then
    install -d ${D}${S6RC_BASEDIR}
    rm -rf ${D}${S6RC_BASEDIR}/tree
    cp -r ${WORKDIR}/tree ${D}${S6RC_BASEDIR}
  fi
}
addtask s6rc_install_tree after do_install before do_package
do_s6rc_install_tree[depends] += "virtual/fakeroot-native:do_populate_sysroot"

python populate_packages_append () {

    def s6rc_append_to_bundle(alist, atomic, var):
        bundle_list = d.getVarFlag("%s_%s" % (var, atomic), "bundles") or ""
        for bundle in bundle_list.split():
            if bundle in alist:
                alist[bundle].append(atomic)
            else:
                alist[bundle] = [ atomic ]

    if d.getVar("INIT_MANAGER") != "s6": return

    pkg = d.getVar("PN")
    postinst = d.getVar('pkg_postinst_%s' % pkg)
    if not postinst:
        postinst = '#!/bin/sh\n'

    all_bundles = { }

    for longrun in (d.getVar('S6RC_LONGRUNS', True) or "").split():
         s6rc_append_to_bundle(all_bundles, longrun, "S6RC_LONGRUN")

    for oneshot in (d.getVar('S6RC_ONESHOTS', True) or "").split():
        s6rc_append_to_bundle(all_bundles, oneshot, "S6RC_ONESHOT")

    for sub_bundle in (d.getVar('S6RC_BUNDLES', True) or "").split():
        s6rc_append_to_bundle(all_bundles, longrun, "S6RC_BUNDLE")

    for bundle in all_bundles.keys():
        postinst += """
type="$(cat "$D{s6tree}/{bundle}/type" 2>/dev/null ||:)"
case "$type" in
  "") echo 'bundle' > "$D{s6tree}/{bundle}/type" ;;
  bundle) ;;
  *) echo "Type of "$D{s6tree}/{bundle}" must be 'bundle'" exit 1 ;;
esac
echo '{subs}' >> "$D{s6tree}/{bundle}/contents"

""".format(s6tree = d.getVar("S6RC_TREE"), bundle = bundle, subs = "\n".join(all_bundles[bundle]))

    d.setVar('pkg_postinst_%s' % pkg, postinst)
    bb.warn("pkg " + pkg + " APP " + postinst)
}
