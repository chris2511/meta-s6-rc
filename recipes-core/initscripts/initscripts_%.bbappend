# This assignment installs the "/etc/init.d/" initscripts
# into the sysroot of recipes depending on initscripts.
# Especially s6-init uses it to rob/borrow
# the one or other script
SYSROOT_DIRS:append = " ${sysconfdir}"
