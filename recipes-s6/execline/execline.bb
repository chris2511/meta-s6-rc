SUMMARY = "A small scripting language, to be used in place of a shell in non-interactive scripts."
DESCRIPTION = "execline is a (non-interactive) scripting language, like sh - but its syntax is quite different from a traditional shell syntax. The execlineb program is meant to be used as an interpreter for a text file; the other commands are essentially useful inside an execlineb script."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5b4b9a81f21793db1a769bee7302f3"
RPROVIDES:${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '/bin/execlineb', '', d)}"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "908ed4db3a6b3a23a205d8fd4cf2a71089156f2aeae0f54656045aafad2dee32"

PV = "2.9.9.2"

inherit s6-skarnet
EXTRA_S6CONF = "--enable-multicall"
