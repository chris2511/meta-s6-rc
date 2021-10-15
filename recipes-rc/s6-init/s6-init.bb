SUMMARY = "s6 init scripts"
LICENSE = "MPL-2.0"
SECTION = "base"
DEPENDS = "initscripts"
RDEPENDS_${PN} = "s6 s6-rc s6-linux-init s6-networking execline"

PV = "1.1.0"
PR = "r0"

SRC_URI = "file://init\
           file://sysctl-printk.conf\
           file://mount-procsysdev.up\
           file://mount-temp.up\
           file://syslogd.run\
           file://postinsts.up\
           file://rc-recompile\
           file://rc-service\
           file://rc-finish\
           file://s6-startstop\
"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"

S = "${WORKDIR}"

inherit s6rc
INIT_D_DIR = "${sysconfdir}/init.d"

do_install() {
  install -d ${D}${base_sbindir} ${D}${INIT_D_DIR} ${D}${sysconfdir}/default
  install -m 0755 ${S}/init ${S}/rc-recompile ${S}/rc-service ${S}/rc-finish\
                    ${D}${base_sbindir}
  install -m 0755 ${S}/s6-startstop ${D}${INIT_D_DIR}
  install -m 0644 ${S}/sysctl-printk.conf ${D}${sysconfdir}
  for initscript in devpts.sh sysfs.sh; do
    install -m 0755 ${RECIPE_SYSROOT}/${INIT_D_DIR}/${initscript} \
		     ${D}${INIT_D_DIR}
  done
  install -m 0644 ${RECIPE_SYSROOT}/${sysconfdir}/default/devpts \
		 ${D}${sysconfdir}/default
}

S6RC_BUNDLES = "basic network default"
S6RC_BUNDLE_basic = "hostname getty hwclock"
S6RC_BUNDLE_network = "hostname networking"

# The default bundle lists the services explicit and doesn't reference
# other bundles to allow enabling and disabling of all services via rc-service
S6RC_BUNDLE_default = "hostname networking getty hwclock klogd syslogd watchdog"
S6RC_BUNDLE_default += "postinsts"

S6RC_ONESHOTS = "hostname mount-procsysdev mount-temp mount-all \
		mount-devpts networking udevadm hwclock postinsts ptest"

S6RC_ONESHOT_start[up] = 'echo "init-stage2 starting."'
S6RC_ONESHOT_hostname[up] = "redirfd -r 0 /etc/hostname withstdinas -E HOST hostname $HOST"
S6RC_ONESHOT_hostname[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_mount-procsysdev[flag-essential] = ""

S6RC_ONESHOT_mount-devpts[up] = "/etc/init.d/devpts.sh"
S6RC_ONESHOT_mount-devpts[dependencies] = "mount-procsysdev"

S6RC_ONESHOT_mount-all[up] = "mount -at nonfs,nosmbfs,noncpfs"
S6RC_ONESHOT_mount-all[dependencies] = "mount-procsysdev mount-temp"

S6RC_ONESHOT_networking[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_networking[up] = "/sbin/ifup -a --ignore-errors"
S6RC_ONESHOT_networking[down] = "/sbin/ifdown -a"

S6RC_ONESHOT_udevadm[dependencies] = "mount-procsysdev udevd"
S6RC_ONESHOT_udevadm[up] = "foreground { /sbin/udevadm trigger --action=add } /sbin/udevadm settle"

S6RC_ONESHOT_hwclock[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_hwclock[up] = "/sbin/hwclock --utc --hctosys"
S6RC_ONESHOT_hwclock[down] = "/sbin/hwclock --utc --systohc"

S6RC_ONESHOT_ptest[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_ptest[up] = "foreground { ptest-runner } poweroff"

S6RC_ONESHOT_postinsts[dependencies] = "mount-all"

S6RC_ONESHOT_ptest[dependencies] = "mount-procsysdev"
S6RC_ONESHOT_ptest[up] = "foreground { redirfd -w 1 /ptest.log ptest-runner } poweroff"

################## Long running services with logger
S6RC_LONGRUNS = "udevd klogd syslogd getty watchdog"
S6RC_LONGRUN_udevd[run] = "fdmove -c 2 1 /sbin/udevd"
S6RC_LONGRUN_udevd[dependencies] = "mount-procsysdev"

S6RC_LONGRUN_klogd[run] = "fdmove -c 2 1 redirfd -r 0 /proc/kmsg exec -c ucspilogd"
S6RC_LONGRUN_klogd[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_klogd_log[user] = "logger"

S6RC_LONGRUN_syslogd[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_syslogd[notification-fd] = "3"
S6RC_LONGRUN_syslogd_log[user] = "logger"

S6RC_LONGRUN_watchdog[run] = "fdmove -c 2 1 ifelse { test -c /dev/watchdog0 } { /sbin/watchdog -F /dev/watchdog0 } s6-svc -d ."
S6RC_LONGRUN_watchdog[dependencies] = "mount-procsysdev"
S6RC_LONGRUN_watchdog[flag-essential] = ""

S6RC_LONGRUN_getty[run] = "fdmove -c 2 1 /bin/busybox.nosuid getty -L 0 console vt100"
S6RC_LONGRUN_getty[dependencies] = "mount-procsysdev hostname"
