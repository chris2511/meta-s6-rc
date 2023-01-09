FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

EXTRA_OECONF += "--disable-lastlog --disable-utmp --disable-wtmp"
SRC_URI += "file://dropbear.run"

inherit s6rc

S6RC_LONGRUNS = "dropbear"
S6RC_LONGRUN_dropbear[dependencies] = "mount-devpts mount-temp hostname"
S6RC_LONGRUN_dropbear[notification-fd] = "3"
S6RC_LONGRUN_dropbear[bundles] = "network default"
S6RC_INITD_SYMLINKS = "${S6RC_LONGRUNS}"

S6RC_ONESHOTS = "ssh-logout"
S6RC_ONESHOT_ssh-logout[up] = ""
S6RC_ONESHOT_ssh-logout[down] = "killall dropbear"
S6RC_ONESHOT_ssh-logout[dependencies] = "network"
S6RC_ONESHOT_ssh-logout[bundles] = "default"
