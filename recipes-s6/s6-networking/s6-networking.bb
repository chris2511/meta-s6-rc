SUMMARY = "UCSPI Unix and TCP tools, a SSL/TLS tunnel, access control tools, and network time management utilities."
DESCRIPTION = "s6-networking is a suite of small networking utilities for Unix systems. It includes command-line client and server management, TCP access control, privilege escalation across UNIX domain sockets, IDENT protocol management and clock synchronization."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline s6 s6-dns"
RDEPENDS:${PN} = "s6-dns"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "2507c51fea5a2d07a7a77300a6502f2af4a04b6da15131f20bf984b99091ff41"

PV = "2.5.1.0"

inherit s6-skarnet
