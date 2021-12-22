SUMMARY = "skarnet.org's small & secure supervision software suite."
DESCRIPTION = "Comes with an ultra-fast init replacement, process management tools, an asynchronous locking library, and more."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "ad7f204587634eeb20ef8f7a7beb6dd63ba3080a46a3a650448ca7cc0826f90a"

PV = "2.11.0.1"

inherit s6-skarnet
