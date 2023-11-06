SUMMARY = "A small scripting language, to be used in place of a shell in non-interactive scripts."
DESCRIPTION = "execline is a (non-interactive) scripting language, like sh - but its syntax is quite different from a traditional shell syntax. The execlineb program is meant to be used as an interpreter for a text file; the other commands are essentially useful inside an execlineb script."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "9ab55d561539dfa76ff4a97906fa995fc4a288e3de5225cb1a9d8fa9e9ebc49b"

PV = "2.9.4.0"

inherit s6-skarnet
EXTRA_S6CONF = "--enable-multicall"
