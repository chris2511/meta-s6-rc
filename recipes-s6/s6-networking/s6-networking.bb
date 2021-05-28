DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline s6 s6-dns"
RDEPENDS_${PN} = "s6-dns"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "a3fbca789dc5c82b3055fdbd2f55110902198f3136447617ce8f8b79fa8aa554"

PV = "2.4.1.1"

inherit s6-skarnet
