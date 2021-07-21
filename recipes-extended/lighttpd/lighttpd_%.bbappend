FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "\
  file://lighttpd_sockets.up\
  file://lighttpd.run\
  file://socket_activation.conf\
  file://ssl.conf\
  file://https_keygen.up\
  file://openssl-cert.conf\
"

PACKAGECONFIG_append = "openssl"

do_install_append() {
  install -d ${D}${sysconfdir}/lighttpd.d ${D}${sysconfdir}/lighttpd
  install -m 0644 ${WORKDIR}/ssl.conf ${WORKDIR}/socket_activation.conf \
		${D}${sysconfdir}/lighttpd.d/
  install -m 0644 ${WORKDIR}/openssl-cert.conf ${D}${sysconfdir}/lighttpd
}

inherit s6rc

S6RC_ONESHOTS = "lighttpd_sockets https_keygen"

S6RC_LONGRUNS = "lighttpd"
S6RC_LONGRUN_lighttpd[dependencies] = "mount-devpts mount-temp hostname lighttpd_sockets https_keygen"
S6RC_LONGRUN_lighttpd[notification-fd] = "3"
S6RC_LONGRUN_lighttpd[bundles] = "network default"
S6RC_LONGRUN_lighttpd_log[user] = "logger"
S6RC_INITD_SYMLINKS = "${S6RC_LONGRUNS}"