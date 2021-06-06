FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0002-network.c-Add-S6-readiness-notification.patch"

inherit s6rc

S6RC_LONGRUNS = "lighttpd"
S6RC_LONGRUN_lighttpd[dependencies] = "mount-devpts mount-temp hostname"
S6RC_LONGRUN_lighttpd[notification-fd] = "3"
S6RC_LONGRUN_lighttpd[bundles] = "network default"
S6RC_LONGRUN_lighttpd[run] = "/usr/sbin/lighttpd -D -f /etc/lighttpd/lighttpd.conf"
S6RC_LONGRUN_lighttpd_log[user] = "logger"
S6RC_INITD_SYMLINKS = "${S6RC_LONGRUNS}"
