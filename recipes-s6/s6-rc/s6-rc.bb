SUMMARY = "A complete service manager for s6 systems"
DESCRIPTION = "A suite of programs that can start and stop services, both long-running daemons and one-time initialization scripts, in the proper order according to a dependency tree."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 s6-linux-init execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=c2becd2c2579701b65222d136ce1c138"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "b2b4e120e16ae9e1b37e0f9bbb238e9affa9e89447a5f2ea28cbe2fd39bdcaf7"

PV = "0.5.2.3"

inherit s6-skarnet
