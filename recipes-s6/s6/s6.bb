SUMMARY = "skarnet.org's small & secure supervision software suite."
DESCRIPTION = "Comes with an ultra-fast init replacement, process management tools, an asynchronous locking library, and more."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "9fb43690e6018d94733b4dc18cc6926f02454508a4dd86060c9b4b9d20dc8037"

PV = "2.11.2.0"
PR = "r0"

inherit s6-skarnet
