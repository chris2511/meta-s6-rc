SUMMARY = "A tool to automate the creation of suitable stage 1 init binaries for s6-based Linux systems."
DESCRIPTION = "s6-linux-init is a set of minimalistic tools used to create a s6-based init system, including a /sbin/init binary, on a Linux kernel. It is a part of the s6 ecosystem."
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = "skalibs s6 execline"
RDEPENDS:${PN} = "s6 execline"

LIC_FILES_CHKSUM = "file://COPYING;md5=c2becd2c2579701b65222d136ce1c138"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "eb523858b07bb94cfd6b3b2dbe6150f931dc9c113251c242f55899164298383f"

PV = "1.0.6.4"

inherit s6-skarnet

SRC_URI += "file://rc.init\
            file://rc.shutdown\
            file://runlevel\
            file://s6-init-kbd.patch"

do_install:append() {
  install -m 0755 ${WORKDIR}/rc.init ${WORKDIR}/rc.shutdown \
                  ${WORKDIR}/runlevel ${D}${sysconfdir}/s6-linux-init/skel/
}
