SUMMARY = "Extend core-image by s6 tasks"

inherit core-image
CLASSOVERRIDE = "${INIT_MANAGER}"

s6rc_compile_tree() {
        rm -rf compiled_tree
        s6-rc-compile compiled_tree ${IMAGE_ROOTFS}/etc/s6-rc/tree
}

DEPENDS_s6 += "s6-rc-native"
IMAGE_PREPROCESS_COMMAND_append_s6 += " s6rc_compile_tree;"
