DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS_${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "1d21373151704150df0e8ed199f097f6ee5d2befb9a68aca4f20f3862e5d8757"

PV = "2.10.0.3"

inherit s6-skarnet
