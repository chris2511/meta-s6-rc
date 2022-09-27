
S6RC_DIR = "s6-rc"
S6RC_LOG_SIZE ?= "s100000 n10"

S6RC_BASEDIR = "${sysconfdir}/${S6RC_DIR}"
FILES:${PN}:append = " ${S6RC_BASEDIR}"
RDEPENDS:${PN}:append = " execline"

inherit useradd
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN}:prepend = " --system --home ${localstatedir}/log \
                            --no-create-home --shell /bin/false \
                            --user-group logger;"

# This class and its use is documented in the README.md
python do_s6rc_create_tree() {
    from pathlib import Path
    import shutil

    def getVarFlagsExpand(var):
        flags = d.getVarFlags(var) or { }
        for f in flags:
            flags[f] = d.expand(flags[f])
        return flags

    def array_to_dir(dir, data):
        try:
            f = dir
            os.makedirs(f)

            for touchfile in data:
                f = dir + "/" + touchfile
                Path(f).touch()
        except OSError:
            bb.fatal('Unable to create %s' % f)

    def array_to_file(f, data):
        try:
            with open(f, 'w') as cfgfile:
                cfgfile.write("\n".join(data) + "\n")
        except OSError:
            bb.fatal('Unable to open %s' % f)

    # Create the tree directory and 'type' file
    def write_type(tree, rc_type):
        bb.utils.mkdirhier(tree)
        array_to_file(tree + "/type", [ rc_type ])

    # for each key in the 'files' dict, check whether it is part
    # of 'valid_files' and put its value into the file with name 'key'
    # if a file called "name.<valid_file>" exists, us it
    # check that all files listed in 'mandatory' do exist
    def write_verbatim(workdir, tree, name, files, valid_files, mandatory):
        # Check that all "files" are valid_files
        for sfile in files:
            if not sfile in valid_files:
                raise bb.parse.SkipRecipe("'%s' in S6RC_*_%s is not a valid file. Valid files are: %s" % (sfile, name, ' '.join(valid_files)))

        for sfile in valid_files:
            # explicitly provided source file <name>.<sfile> like dropbear.run
            # takes precedence
            sourcefile = workdir + "/" + name + "." + sfile
            if os.path.exists(sourcefile):
                shutil.copyfile(sourcefile, tree + "/" + sfile)
                if sfile in files and sfile.find("-log.") != -1:
                  bb.warn("Ignoring S6RC_*_%s[%s], because a source file %s exists" % (name, sfile, sourcefile))
            elif sfile in files:
                # By default the content is a one-element array
                # with a single line entry
                content = [ files[sfile].strip() ]
                if sfile == "consumer-for":
                    content = files[sfile].split()
                if sfile == "dependencies":
                    # Dependencies are listed in dependencies.d
                    array_to_dir(tree + "/" + "dependencies.d",
                                 files[sfile].split())
                    continue
                if sfile == "run" and files[sfile][0:2] != "#!":
                    # inline run scripts are expected to be execlineb
                    content.insert(0, "#!/bin/execlineb -P")
                if sfile == "finish" and files[sfile][0:2] != "#!":
                    # inline finish scripts are expected to be execlineb
                    # and recieve the run's exit code as argv[1]
                    content.insert(0, "#!/bin/execlineb -S1")

                array_to_file(tree + "/" + sfile, content)

            elif sfile in mandatory:
                raise bb.parse.SkipRecipe("Error with S6RC_*_%s: '%s' files are mandatory. Either as S6RC_*_%s[%s] or as source file '%s.%s'." % (name, sfile, name, sfile, name, sfile))
            else:
                continue

            if sfile in [ "run", "finish", "up", "down", "check-instance" ]:
                os.chmod(tree + "/" + sfile, 0o755)

            if sfile == "check-instance":
                os.makedirs(tree + "/data")
                os.rename(tree + "/" + sfile, tree + "/data/" + sfile)

    workdir = d.getVar("WORKDIR")
    workdir_s6 = workdir + "/" + d.getVar("S6RC_DIR")
    if os.path.exists(workdir_s6):
        shutil.rmtree(workdir_s6)
    basetree = workdir_s6 + "/tree"
    templatetree = workdir_s6 + "/templates"

    if d.getVar("INIT_MANAGER") != "s6": return

    valid_files_atomic = [ "timeout-up", "timeout-down", "flag-essential",
                           "dependencies", "bundles", "influences" ]
    valid_files_service = [ "run", "finish", "down", "notification-fd",
                            "timeout-kill", "timeout-finish",
                            "max-death-tally", "down-signal" ]
    special_log_files = [ "umask", "user", "script", "dir", "mode" ]

    # Bundles
    for bundle in (d.getVar('S6RC_BUNDLES', True) or "").split():
        tree = basetree + "/" + bundle
        contents = (d.getVar('S6RC_BUNDLE_%s' % bundle) or "").split()
        if not contents:
            raise bb.parse.SkipRecipe("S6RC_BUNDLE_%s is mandatory." % bundle)

        write_type(tree, "bundle")
        array_to_dir(tree + "/contents.d", contents)

    # Oneshots
    for oneshot in (d.getVar('S6RC_ONESHOTS', True) or "").split():
        tree = basetree + "/" + oneshot
        write_type(tree, "oneshot")

        valid_files = valid_files_atomic + [ "up", "down" ]
        files = getVarFlagsExpand("S6RC_ONESHOT_%s" % oneshot)

        write_verbatim(workdir, tree, oneshot, files, valid_files, [ "up" ])

    # Longruns
    for longrun in (d.getVar('S6RC_LONGRUNS', True) or "").split():
        tree = basetree + "/" + longrun
        write_type(tree, "longrun")
        valid_files = valid_files_atomic + valid_files_service + \
                      [ "producer-for", "consumer-for", "no-log" ]
        files = getVarFlagsExpand("S6RC_LONGRUN_%s" % longrun)
        log = getVarFlagsExpand('S6RC_LONGRUN_%s_log' % longrun)
        do_logger = not "no-log" in files
        if do_logger:
            logname = longrun + "-log"
            files["producer-for"] = logname

        write_verbatim(workdir, tree, longrun, files, valid_files, [ "run" ])
        # Automatic log-service generation
        if do_logger:
            tree = basetree + "/" + logname
            write_type(tree, "longrun")

            files = { "run": "#!/bin/execlineb -P\n\
umask %s s6-setuidgid %s s6-log -d3 %s %s" %
                        (log.get("umask", "0037"), log.get("user", "logger"),
                         log.get("script", "T " + d.getVar('S6RC_LOG_SIZE', True)),
                         log.get("dir", "/var/log/" + longrun)),
                      "notification-fd": "3",
                      "consumer-for": longrun,
                      "dependencies": "mount-temp" }
            for i in special_log_files:
                log.pop(i, None)
            write_verbatim(workdir, tree, logname, { **files, **log },
                           valid_files, [ "run" ])
    # Templates
    for template in (d.getVar('S6RC_TEMPLATES', True) or "").split():
        tree = templatetree + "/" + template
        bb.utils.mkdirhier(tree)
        valid_files = valid_files_service + [ "check-instance", "mode" ]
        files = getVarFlagsExpand("S6RC_TEMPLATES_%s" % template)
        log = getVarFlagsExpand('S6RC_TEMPLATES_%s_log' % template)

        write_verbatim(workdir, tree, template, files, valid_files, [ "run" ])
        # Automatic log-service generation
        if "mode" in log:
            logname = template + "-log"
            logmode = log.get("mode", "")
            if logmode == "per-instance":
                # Create a logfile for each service instance
                tree = templatetree + "/" + template + "/log"
                bb.utils.mkdirhier(tree)
                logdir = log.get("dir", "/var/log/" + template + "/${instance}@" + template)

                files = { "run": "#!/bin/execlineb -P\n\
envfile ../env/instance\n\
importas instance INSTANCE\n\
umask %s s6-setuidgid %s foreground { mkdir -p %s } s6-log %s %s" %
                    (log.get("umask", "0037"),
                    log.get("user", "logger"),
                    logdir,
                    log.get("script", "T " + d.getVar('S6RC_LOG_SIZE', True)),
                    logdir) }

            elif logmode == "all-in-one":
                # Combine all instances of a template into a single log file
                valid_files = valid_files_atomic + valid_files_service + \
                    [ "producer-for", "consumer-for" ]
                tree = basetree + "/" + logname
                write_type(tree, "longrun")

                files = { "run": "#!/bin/execlineb -P\n\
piperw 0 4\n\
foreground { s6-fdholder-store -d4 /service/s6rc-fdholder/s pipe:s6rc-%s }\n\
umask %s s6-setuidgid %s s6-log -d3 %s %s" %
                            (template, log.get("umask", "0037"),
                            log.get("user", "logger"),
                            log.get("script", "T " + d.getVar('S6RC_LOG_SIZE', True)),
                            log.get("dir", "/var/log/" + template)),
                        "bundles": "default",
                        "notification-fd": "3",
                        "dependencies": "mount-temp" }
            else:
                raise bb.parse.SkipRecipe("'%s' in S6RC_*_%s[%s] is not a valid option. Valid options are: %s" % (logmode, logname, "mode", "'', 'all-in-one', 'per-instance' or don't set it at all to disable logging."))

            for i in special_log_files:
                log.pop(i, None)
            write_verbatim(workdir, tree, logname, { **files, **log },
                           valid_files, [ "run" ])
}
addtask do_s6rc_create_tree after do_compile before do_install
do_s6rc_create_tree[vardeps] += "S6RC_BUNDLE_basic"
python () {
  vardeps = []
  # Collect all S6RC_[BUNDLE|ONESHOT|LONGRUN|TEMPLATE]_* variables for
  # do_s6rc_create_tree[vardeps]
  for prefix in [ "BUNDLE", "ONESHOT", "LONGRUN", "TEMPLATE" ]:
    for var in (d.getVar("S6RC_" + prefix + "S") or "").split():
      vardeps.append("S6RC_" + prefix + "_" + var)
      vardeps.append("S6RC_" + prefix + "_" + var + "_log")

  d.appendVarFlag('do_s6rc_create_tree', 'vardeps',
                  " " + " ".join(vardeps))
}

fakeroot do_s6rc_install_tree() {
  if test "${INIT_MANAGER}" = "s6"; then
    install -d ${D}${sysconfdir}
    cp -r ${WORKDIR}/${S6RC_DIR} ${D}${sysconfdir}
  fi
}
addtask do_s6rc_install_tree after do_install before do_package
do_s6rc_install_tree[depends] += "virtual/fakeroot-native:do_populate_sysroot"

pkg_postinst:${PN}:append () {
  if test "${INIT_MANAGER}" = "s6"; then
    export PN="${PN}"
    cd $D${S6RC_BASEDIR}/tree
    $D/sbin/rc-finish ${S6RC_LONGRUNS} ${S6RC_ONESHOTS} ${S6RC_BUNDLES}
    for link in ${S6RC_INITD_SYMLINKS}; do
      mkdir -p $D/etc/init.d
      rm -f $D/etc/init.d/${link}
      ln -sf s6-startstop $D/etc/init.d/${link}
    done
  fi
}
