SUMMARY = "Extend core-image by s6 tasks"

OVERRIDES:append = ":${INIT_MANAGER}"

fakeroot s6rc_compile_tree() {
    s6-rc-compile ${IMAGE_ROOTFS}/etc/s6-rc/compiledA ${IMAGE_ROOTFS}/etc/s6-rc/tree
    ln -sf compiledA ${IMAGE_ROOTFS}/etc/s6-rc/compiled
}

DEPENDS:s6 += "s6-rc-native"
IMAGE_PREPROCESS_COMMAND:append:s6 += " s6rc_compile_tree;"
