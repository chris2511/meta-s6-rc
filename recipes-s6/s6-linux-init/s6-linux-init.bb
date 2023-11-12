SUMMARY = "A tool to automate the creation of suitable stage 1 init binaries for s6-based Linux systems."
DESCRIPTION = "s6-linux-init is a set of minimalistic tools used to create a s6-based init system, including a /sbin/init binary, on a Linux kernel. It is a part of the s6 ecosystem."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=0280da83329b0ff1dfa49e1444f1cd97"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "11ae08d0a66210b5e56b6baee0f6b9b1b69fbedb05fda1285b115a31ca46c77f"

PV = "1.1.2.0"

inherit s6-skarnet

PACKAGES =+ "${PN}-skel"
FILES:${PN}-skel = "${sysconfdir}/s6-linux-init/skel"
