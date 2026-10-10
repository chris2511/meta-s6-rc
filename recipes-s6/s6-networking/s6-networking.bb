SUMMARY = "UCSPI Unix and TCP tools, a SSL/TLS tunnel, access control tools, and network time management utilities."
DESCRIPTION = "s6-networking is a suite of small networking utilities for Unix systems. It includes command-line client and server management, TCP access control, privilege escalation across UNIX domain sockets, IDENT protocol management and clock synchronization."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline s6 s6-dns"
RDEPENDS:${PN} = "s6-dns"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5b4b9a81f21793db1a769bee7302f3"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "6f011c33ba0586ce58feee0cf854a0b087e9082fdbd24abb786148463830f341"

PV = "2.8.0.1"

inherit s6-skarnet
