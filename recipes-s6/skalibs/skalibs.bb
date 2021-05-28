DESCRIPTION = "Skarnet base libraries"
HOMEPAGE = "http://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = ""
LIC_FILES_CHKSUM = "file://COPYING;md5=d096eb937732001e90b6c48fe07906c0"

SRC_URI = "http://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "b780b0ae650dda0c3ec5f8975174998af2d24c2a2e2be669b1bab46e73b1464d"

PV = "2.10.0.3"

inherit s6-skarnet

do_configure() {
    ${S}/configure --with-sysdep-devurandom=y --enable-static \
		--libdir=/usr/lib --enable-static-libc --prefix=${base_prefix}
}

do_configure_class-native() {
    ${S}/configure --prefix=${base_prefix} --includedir=${prefix}/include
}
