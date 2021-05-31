FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

EXTRA_OECONF += "--disable-lastlog --disable-utmp --disable-wtmp"
SRC_URI += "file://dropbear.run"

inherit s6rc

S6RC_LONGRUNS = "dropbear"
S6RC_LONGRUN_dropbear[dependencies] = "mount-devpts mount-temp hostname"
S6RC_LONGRUN_dropbear[bundles] = "network default"
S6RC_LONGRUN_dropbear_log[user] = "logger"
S6RC_INITD_SYMLINKS = "${S6RC_LONGRUNS}"
