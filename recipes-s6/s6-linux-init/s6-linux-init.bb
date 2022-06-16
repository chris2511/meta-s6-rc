SUMMARY = "A tool to automate the creation of suitable stage 1 init binaries for s6-based Linux systems."
DESCRIPTION = "s6-linux-init is a set of minimalistic tools used to create a s6-based init system, including a /sbin/init binary, on a Linux kernel. It is a part of the s6 ecosystem."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=2865f4a7bf0752acdc5a4e517c8b2d2e"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "92055a7964cf66604066ad964a2c2392ee7c5e64821be03146c1341e0d8c3dc6"

PV = "1.0.8.0"

inherit s6-skarnet

SRC_URI += "file://s6-init-kbd.patch"

PACKAGES =+ "${PN}-skel"
FILES:${PN}-skel = "${sysconfdir}/s6-linux-init/skel"
