SUMMARY = "A small daemon managing kernel hotplug events, similarly to udevd."
DESCRIPTION = "It uses the same configuration file as mdev, which is a hotplug manager integrated in the Busybox suite of tools. However, mdev needs to be registered in /proc/sys/kernel/hotplug, and the kernel forks an instance of mdev for every event; by contrast, mdevd is a daemon and does not fork."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=28d25c958b3e78a070bd476755ead763"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "ac2fcf9004f07904592c5894e2c401e15bb027ecf37bcb8ea661e2a7993447be"

PV = "0.1.6.3"

inherit s6-skarnet
