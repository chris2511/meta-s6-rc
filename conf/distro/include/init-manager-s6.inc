# Use s6 for system initialization
DISTRO_FEATURES:append = " s6"
DISTRO_FEATURES_BACKFILL_CONSIDERED:append = " systemd pulseaudio sysvinit"
VIRTUAL-RUNTIME_init_manager ??= "s6-init"
VIRTUAL-RUNTIME_initscripts ??= "initscripts"
VIRTUAL-RUNTIME_login_manager ??= "busybox"
