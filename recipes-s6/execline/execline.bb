SUMMARY = "A small scripting language, to be used in place of a shell in non-interactive scripts."
DESCRIPTION = "execline is a (non-interactive) scripting language, like sh - but its syntax is quite different from a traditional shell syntax. The execlineb program is meant to be used as an interpreter for a text file; the other commands are essentially useful inside an execlineb script."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI += "file://0001-execline-multicall-make-sort-independent-of-locale.patch"
SRC_URI[sha256sum] = "9365012558a1e3c019cafc6eb574b0f5890495fb02652f20efdd782d577b1601"

PV = "2.9.2.0"

inherit s6-skarnet
EXTRA_S6CONF = "--enable-multicall"
