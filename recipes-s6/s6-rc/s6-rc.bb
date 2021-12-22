SUMMARY = "A complete service manager for s6 systems"
DESCRIPTION = "A suite of programs that can start and stop services, both long-running daemons and one-time initialization scripts, in the proper order according to a dependency tree."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 s6-linux-init execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=c2becd2c2579701b65222d136ce1c138"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "93a31d2bd39569f684792973b556ac02723bdb60bd1ae76e9a0ea1f4a9b43f27"
PV = "0.5.3.0"

inherit s6-skarnet
