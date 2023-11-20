SUMMARY = "UCSPI Unix and TCP tools, a SSL/TLS tunnel, access control tools, and network time management utilities."
DESCRIPTION = "s6-networking is a suite of small networking utilities for Unix systems. It includes command-line client and server management, TCP access control, privilege escalation across UNIX domain sockets, IDENT protocol management and clock synchronization."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline s6 s6-dns"
RDEPENDS:${PN} = "s6-dns"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "99fd6e3f93d6d6a95bf7e978955b7d053629591794b068eda2004ab8b4905552"

PV = "2.7.0.0"

inherit s6-skarnet
