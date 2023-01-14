SUMMARY = "A tool to automate the creation of suitable stage 1 init binaries for s6-based Linux systems."
DESCRIPTION = "s6-linux-init is a set of minimalistic tools used to create a s6-based init system, including a /sbin/init binary, on a Linux kernel. It is a part of the s6 ecosystem."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=0280da83329b0ff1dfa49e1444f1cd97"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "29d0631194501902448eed0c46840d60d80eb3bb97afc330b4d11561448fc7cc"

PV = "1.0.8.1"

inherit s6-skarnet

SRC_URI += "file://s6-init-kbd.patch"

PACKAGES =+ "${PN}-skel"
FILES:${PN}-skel = "${sysconfdir}/s6-linux-init/skel"
