SUMMARY = "A complete service manager for s6 systems"
DESCRIPTION = "A suite of programs that can start and stop services, both long-running daemons and one-time initialization scripts, in the proper order according to a dependency tree."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 s6-linux-init execline"
LIC_FILES_CHKSUM = "file://COPYING;md5=0280da83329b0ff1dfa49e1444f1cd97"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "00bdfa596fa7161512e972ec9282a2abd8fd0e31f09177bad7a2bc3d8f283982"

PV = "0.5.4.2"

inherit s6-skarnet
