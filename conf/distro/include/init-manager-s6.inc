# Use s6 for system initialization
DISTRO_FEATURES:append = " s6"
DISTRO_FEATURES_OPTED_OUT:append = " systemd pulseaudio sysvinit"
VIRTUAL-RUNTIME_init_manager ??= "s6-init"
VIRTUAL-RUNTIME_initscripts ??= ""
VIRTUAL-RUNTIME_login_manager ??= "busybox"
VIRTUAL-RUNTIME_dev_manager ??= "mdevd"
