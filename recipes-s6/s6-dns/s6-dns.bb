DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "0c1d50e5d7bc65cbf59b6f37fbd7f6f5645694224bb8c4dc1943d7921bc74f63"

PV = "2.3.5.1"

inherit s6-skarnet
