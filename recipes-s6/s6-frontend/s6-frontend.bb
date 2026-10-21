SUMMARY = "A higher-level interface to the s6 ecosystem."
DESCRIPTION = "A frontend to the s6 init system. It provides the one-stop-shop s6 command, that is used to address every part of the init system."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs execline s6-linux-init s6 s6-rc util-linux"
RDEPENDS:${PN} = ""
LIC_FILES_CHKSUM = "file://COPYING;md5=bf2e714ed36534bb7976bdbae107223b"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "4bce4e9bec8c1de9e3ee5ef764580421504a3c6303eff96997b69d1b89219536"

PV = "0.2.0.0"

inherit s6-skarnet
