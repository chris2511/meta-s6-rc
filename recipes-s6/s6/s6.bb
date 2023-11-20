SUMMARY = "skarnet.org's small & secure supervision software suite."
DESCRIPTION = "Comes with an ultra-fast init replacement, process management tools, an asynchronous locking library, and more."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline"
RDEPENDS:${PN} = "execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "f5cc749042649c574a920ba288b5bd99f294266b5e5f7401501c3b279f52ee18"

PV = "2.12.0.1"

inherit s6-skarnet
