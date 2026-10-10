SUMMARY = "A set of general-purpose C programming libraries all skarnet.org software depends on."
DESCRIPTION = "skalibs is a package centralizing the free software / open source C development files used for building all software at skarnet.org: it contains essentially general-purpose libraries."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = ""
LIC_FILES_CHKSUM = "file://COPYING;md5=ea5b4b9a81f21793db1a769bee7302f3"

SRC_URI = "${S6_DOWNLOAD_URL}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "f9c905e74935c6fe911c7e344e3e89d5fbd2014c1a04650b524b15ce9b5635d1"

PV = "2.15.1.0"

inherit s6-skarnet

do_configure() {
    ${S}/configure --with-sysdep-devurandom=y --enable-static \
		--libdir=${libdir} --enable-static-libc --prefix=${root_prefix}
}

do_configure:class-native() {
    ${S}/configure --prefix=${root_prefix} --includedir=${includedir} --libdir=${libdir}
}

INSANE_SKIP:${PN} = "useless-rpaths"
