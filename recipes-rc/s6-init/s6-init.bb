SUMMARY = "s6 init scripts"
LICENSE = "MIT"
SECTION = "base"
DEPENDS = "s6-linux-init-native"
RDEPENDS:${PN} = "s6 s6-rc s6-linux-init s6-networking execline ifupdown"

PV = "1.1.1"
PR = "r0"

SRC_URI = "file://sysctl-printk.conf\
           file://mount-temp.up\
           file://mount-procsysdev.up\
           file://mount-devpts.up\
           file://fdstore-fill.up\
           file://postinsts.up\
           file://rc-recompile\
           file://rc-service\
           file://rc-finish\
           file://rc-dynamic\
           file://s6-startstop\
           file://rc.init\
           file://rc.shutdown\
           file://rc.shutdown.final\
           file://runlevel\
           file://hostname\
           file://vtlogin.run\
           file://vtlogin.check-instance\
           file://getty.run\
           file://getty.check-instance\
"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

S = "${WORKDIR}"

inherit s6rc update-alternatives
INIT_D_DIR = "${sysconfdir}/init.d"
S6_LINUX_INIT = "/etc/s6-linux-init"
S6RC_DEBUG_BOOT ?= ""
FILES:${PN} += "/service"

ALTERNATIVE:${PN} = "halt reboot poweroff shutdown"
ALTERNATIVE_PRIORITY = "200"

ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"

inherit useradd
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --user-group fdstore"
GROUPADD_PARAM:${PN} = "-r reboot"

do_compile() {
  rm -rf s6-l-i &&
  s6-linux-init-maker -p /bin:/usr/bin:/sbin:/usr/sbin \
                      -c "${S6_LINUX_INIT}" -f . \
                      ${@ "" if d.getVar("S6RC_DEBUG_BOOT") == "" else "-1" } \
                      -q 100 -t 2 -u logger s6-l-i
}

do_install() {
  install -d ${D}${base_sbindir} ${D}${INIT_D_DIR} ${D}${sysconfdir}/default \
             ${D}${S6_LINUX_INIT} ${D}${sysconfdir}/sysctl.d
  cp -R --no-dereference --preserve=mode,links,xattr s6-l-i/* \
     ${D}${S6_LINUX_INIT}
  chown logger: ${D}${S6_LINUX_INIT}/run-image/uncaught-logs
  mv ${D}${S6_LINUX_INIT}/bin/* ${D}${base_sbindir}
  rmdir ${D}${S6_LINUX_INIT}/bin
  ln -s /run/service ${D}/

  install -m 0755 ${S}/rc-recompile ${S}/rc-service ${S}/rc-finish\
                  ${S}/rc-dynamic ${D}${base_sbindir}
  install -m 0755 ${S}/s6-startstop ${S}/hostname ${D}${INIT_D_DIR}
  install -m 0644 ${S}/sysctl-printk.conf ${D}${sysconfdir}/sysctl.d/printk.conf
  chmod 664 ${D}${S6_LINUX_INIT}/run-image/service/s6-linux-init-shutdownd/fifo
  chgrp reboot ${D}${S6_LINUX_INIT}/run-image/service/s6-linux-init-shutdownd/fifo
}

S6RC_BUNDLES = "basic network default fdstorage"
S6RC_BUNDLE_basic = "hostname getty hwclock"
S6RC_BUNDLE_network = "hostname networking"
S6RC_BUNDLE_fdstorage = "fdstore fdstore-fill"

# The default bundle lists the services explicit and doesn't reference
# other bundles to allow enabling and disabling of all services via rc-service
S6RC_BUNDLE_default = "hostname networking getty hwclock klogd syslogd"
S6RC_BUNDLE_default += "watchdog postinsts sysctl"
S6RC_BUNDLE_default += "${@ 'vtlogin' if d.getVar('USE_VT') == '1' else ''}"

S6RC_ONESHOTS = "hostname mount-procsysdev mount-temp mount-all \
		mount-devpts networking udevadm hwclock postinsts \
		ptest ifup-lo sysctl vtlogin getty fdstore-fill \
"

S6RC_ONESHOT_hostname[up] = "/etc/init.d/hostname"
S6RC_ONESHOT_hostname[dependencies] = "mount-procsysdev"

S6RC_ONESHOT_mount-procsysdev[flag-essential] = ""

S6RC_ONESHOT_mount-devpts[dependencies] = "mount-procsysdev"

S6RC_ONESHOT_mount-all[up] = "mount -at nonfs,nosmbfs,noncpfs"
S6RC_ONESHOT_mount-all[dependencies] = "mount-procsysdev mount-temp mount-devpts"

S6RC_ONESHOT_sysctl[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_sysctl[up] = "elglob -0 FILES /etc/sysctl.d/*.conf if -t { test "$FILES" } /sbin/sysctl -q -p $FILES"

S6RC_ONESHOT_networking[dependencies] = "ifup-lo"
S6RC_ONESHOT_networking[up] = "/sbin/ifup -a --ignore-errors"
S6RC_ONESHOT_networking[down] = "/sbin/ifdown -a -X lo"

S6RC_ONESHOT_udevadm[dependencies] = "mount-procsysdev udevd"
S6RC_ONESHOT_udevadm[up] = "foreground { /sbin/udevadm trigger --action=add } /sbin/udevadm settle"

S6RC_ONESHOT_ifup-lo[up] = "/sbin/ifup --ignore-errors lo"

S6RC_ONESHOT_hwclock[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_hwclock[up] = "/sbin/hwclock --utc --hctosys"
S6RC_ONESHOT_hwclock[down] = "/sbin/hwclock --utc --systohc"

S6RC_ONESHOT_postinsts[dependencies] = "mount-all"

S6RC_ONESHOT_ptest[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_ptest[up] = "foreground { redirfd -w 1 /ptest.log ptest-runner } poweroff"

S6RC_ONESHOT_getty[up] = "rc-dynamic setup console@getty"
S6RC_ONESHOT_getty[dependencies] = "mount-procsysdev sysctl"

S6RC_ONESHOT_vtlogin[up] = "rc-dynamic setup 1@vtlogin 2@vtlogin 3@vtlogin"
S6RC_ONESHOT_vtlogin[dependencies] = "mount-procsysdev sysctl"

S6RC_ONESHOT_fdstore-fill[dependencies] = "fdstore"

################## Long running services with logger
S6RC_LONGRUNS = "udevd klogd syslogd watchdog fdstore"
S6RC_LONGRUN_udevd[run] = "fdmove -c 2 1 /sbin/udevd"
S6RC_LONGRUN_udevd[dependencies] = "mount-procsysdev"

S6RC_LONGRUN_klogd[run] = "fdmove -c 2 1 redirfd -r 0 /proc/kmsg exec -c s6-setuidgid logger /bin/ucspilogd"
S6RC_LONGRUN_klogd[dependencies] = "mount-procsysdev"

S6RC_LONGRUN_syslogd[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_syslogd[notification-fd] = "3"
S6RC_LONGRUN_syslogd[run] = "fdmove -c 2 1 s6-envuidgid logger s6-socklog -U -d 3"

S6RC_LONGRUN_watchdog[run] = "fdmove -c 2 1 ifelse { test -c /dev/watchdog0 } { /sbin/watchdog -F /dev/watchdog0 } s6-svc -d ."
S6RC_LONGRUN_watchdog[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_watchdog[flag-essential] = ""

S6RC_LONGRUN_fdstore[run] = "s6-envuidgid -D 0:0:0 fdstore s6-fdholder-daemon -1 -U -i data/rules s"
S6RC_LONGRUN_fdstore[notification-fd] = "1"
S6RC_LONGRUN_fdstore[no-log] = "1"

do_s6rc_install_tree:append() {
  rm -rf ${D}${S6RC_BASEDIR}/tree/fdstore/data/rules
  mkdir -p ${D}${S6RC_BASEDIR}/tree/fdstore/data/rules
  cd ${D}${S6RC_BASEDIR}/tree/fdstore/data/rules
  mkdir -p uid/0/env gid/0
  ln -sf ../../uid/0/env gid/0/env
  touch uid/0/allow gid/0/allow
  echo > uid/0/env/S6_FDHOLDER_SETDUMP
  echo > uid/0/env/S6_FDHOLDER_GETDUMP
  echo > uid/0/env/S6_FDHOLDER_LIST
  echo '^socket:|^read:|^write:' > uid/0/env/S6_FDHOLDER_STORE_REGEX
  ln -s S6_FDHOLDER_STORE_REGEX uid/0/env/S6_FDHOLDER_RETRIEVE_REGEX
}

S6RC_TEMPLATES = "getty vtlogin"
S6RC_TEMPLATE_getty[down-signal] = "SIGHUP"
S6RC_TEMPLATE_getty-log[mode] = "per-instance"
S6RC_TEMPLATE_vtlogin-log[mode] = "per-instance"
