SUMMARY = "A tool to automate the creation of suitable stage 1 init binaries for s6-based Linux systems."
DESCRIPTION = "s6-linux-init is a set of minimalistic tools used to create a s6-based init system, including a /sbin/init binary, on a Linux kernel. It is a part of the s6 ecosystem."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=397588315e6b6414b74abe203749f1ee"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "6fad014da162c0c81924197c57d16e1a75c133b34a20e423431a1b741e907b1d"

PV = "1.2.0.2"

inherit s6-skarnet

SKELDIR = "${sysconfdir}/s6-linux-init/skel"
EXTRA_S6CONF = "--skeldir=${SKELDIR}"

PACKAGES =+ "${PN}-skel"
FILES:${PN}-skel = "${SKELDIR}"
