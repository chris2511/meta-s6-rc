SUMMARY = "Extend core-image by s6 tasks"

fakeroot s6rc_compile_tree() {
    rm -rf ${IMAGE_ROOTFS}/etc/s6-rc/compiledA
    s6-rc-compile ${IMAGE_ROOTFS}/etc/s6-rc/compiledA ${IMAGE_ROOTFS}/etc/s6-rc/tree
    ln -sf compiledA ${IMAGE_ROOTFS}/etc/s6-rc/compiled
}

DEPENDS += "${@ 's6-rc-native' if d.getVar('INIT_MANAGER') == 's6' else ''}"
IMAGE_PREPROCESS_COMMAND:append = " ${@ 's6rc_compile_tree;' if d.getVar('INIT_MANAGER') == 's6' else ''}"
