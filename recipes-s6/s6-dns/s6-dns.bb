SUMMARY = "Good, usable DNS client libraries and command-line DNS client utilities."
DESCRIPTION = "s6-dns is a suite of DNS client programs and libraries for Unix systems, as an alternative to the BIND, djbdns or other DNS clients."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "01e7c7532cc5e7a6b8915461b01c2f706b7f2211782923e25eaa54ee0c6dc7c7"

PV = "2.3.6.0"

inherit s6-skarnet
