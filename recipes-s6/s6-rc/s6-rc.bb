DESCRIPTION = "S6 RC Framework"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS_${PN} = "s6 s6-linux-init execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=c2becd2c2579701b65222d136ce1c138"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz \
           file://0001-s6-rc-compile-Fix-setting-of-flag-essential.patch \
           "
SRC_URI[sha256sum] = "2a8d1cd455c05c8502b34517cfe79841aa46d08ad969076b37a15d3910617f89"

PV = "0.5.2.2"

inherit s6-skarnet
