SUMMARY = "A small daemon managing kernel hotplug events, similarly to udevd."
DESCRIPTION = "It uses the same configuration file as mdev, which is a hotplug manager integrated in the Busybox suite of tools. However, mdev needs to be registered in /proc/sys/kernel/hotplug, and the kernel forks an instance of mdev for every event; by contrast, mdevd is a daemon and does not fork."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=b1f9b9243b1ddb05372cb312c390fb5c"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "460372b3d3bac9f3574156ed7e48618f9f4a361cb52c4494ad9063248ab4a4ff"

PV = "0.1.5.2"

inherit s6-skarnet
