DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS_${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=c2becd2c2579701b65222d136ce1c138"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "29e368516b1a3c61a6d1f8680645cc122a2e02127debec91738f170a3b93b8c5"

PV = "1.0.6.3"

inherit s6-skarnet

SRC_URI += "file://rc.init\
            file://rc.shutdown\
            file://runlevel\
            file://s6-init-kbd.patch"

do_install_append() {
  install -m 0755 ${WORKDIR}/rc.init ${WORKDIR}/rc.shutdown \
                  ${WORKDIR}/runlevel ${D}${sysconfdir}/s6-linux-init/skel/
}
