SUMMARY = "Good, usable DNS client libraries and command-line DNS client utilities."
DESCRIPTION = "s6-dns is a suite of DNS client programs and libraries for Unix systems, as an alternative to the BIND, djbdns or other DNS clients."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "aeeb2776cb234e90397a78c6aa3725924aa0710c1035ca70dd5628b84c6701d1"

PV = "2.3.7.0"

inherit s6-skarnet
