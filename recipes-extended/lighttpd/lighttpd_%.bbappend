FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
  file://lighttpd_sockets.up\
  file://lighttpd.run\
  file://socket_activation.conf\
"

do_install_append() {
  install -d ${D}${sysconfdir}/lighttpd.d
  install -m 0644 ${WORKDIR}/socket_activation.conf \
		${D}${sysconfdir}/lighttpd.d/
}

inherit s6rc

S6RC_ONESHOTS = "lighttpd_sockets"

S6RC_LONGRUNS = "lighttpd"
S6RC_LONGRUN_lighttpd[dependencies] = "mount-devpts mount-temp hostname lighttpd_sockets"
S6RC_LONGRUN_lighttpd[notification-fd] = "3"
S6RC_LONGRUN_lighttpd[bundles] = "network default"
S6RC_LONGRUN_lighttpd_log[user] = "logger"
S6RC_INITD_SYMLINKS = "${S6RC_LONGRUNS}"
