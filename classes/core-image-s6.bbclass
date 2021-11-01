SUMMARY = "Extend core-image by s6 tasks"

inherit core-image
OVERRIDES:append = ":${INIT_MANAGER}"

fakeroot s6rc_compile_tree() {
        rm -rf ${WORKDIR}/s6-rc-compiled_tree
        s6-rc-compile ${WORKDIR}/s6-rc-compiled_tree ${IMAGE_ROOTFS}/etc/s6-rc/tree
}

DEPENDS:s6 += "s6-rc-native"
IMAGE_PREPROCESS_COMMAND:append:s6 += " s6rc_compile_tree;"
