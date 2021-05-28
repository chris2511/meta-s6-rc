DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "a373f497d2335905d750e2f3be2ba47a028c11c4a7d5595dca9965c161e53aed"

PV = "2.8.0.1"

inherit s6-skarnet
