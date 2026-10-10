SUMMARY = "A small daemon managing kernel hotplug events, similarly to udevd."
DESCRIPTION = "It uses the same configuration file as mdev, which is a hotplug manager integrated in the Busybox suite of tools. However, mdev needs to be registered in /proc/sys/kernel/hotplug, and the kernel forks an instance of mdev for every event; by contrast, mdevd is a daemon and does not fork."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=00f0789e79a05a58895c51ad18687349"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz \
           file://0001-Some-libcs-have-a-char-const-strchr.patch"
SRC_URI[sha256sum] = "93d2bba7299ff3b1b9f249928c2e84e23a5af3d829ebd9677ea44535c585aa82"

PV = "0.1.8.1"

inherit s6-skarnet
