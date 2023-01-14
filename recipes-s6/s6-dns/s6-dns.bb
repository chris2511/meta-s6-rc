SUMMARY = "Good, usable DNS client libraries and command-line DNS client utilities."
DESCRIPTION = "s6-dns is a suite of DNS client programs and libraries for Unix systems, as an alternative to the BIND, djbdns or other DNS clients."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "56979b5d5125c38071a80b5e3df0d4a6b2a7c52bb863a2410b6e3d797ffe1ee8"

PV = "2.3.5.5"

inherit s6-skarnet
