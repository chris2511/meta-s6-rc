SUMMARY = "Good, usable DNS client libraries and command-line DNS client utilities."
DESCRIPTION = "s6-dns is a suite of DNS client programs and libraries for Unix systems, as an alternative to the BIND, djbdns or other DNS clients."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "2ac42f6771cc95c5e9b4d50e4713c3be51733d0f615dcd6c71ef1d9e059c76da"

PV = "2.3.5.3"

inherit s6-skarnet
