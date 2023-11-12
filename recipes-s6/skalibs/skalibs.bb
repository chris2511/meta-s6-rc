SUMMARY = "A set of general-purpose C programming libraries all skarnet.org software depends on."
DESCRIPTION = "skalibs is a package centralizing the free software / open source C development files used for building all software at skarnet.org: it contains essentially general-purpose libraries."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = ""
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "a745fd34cf84bec74398effa1b6f4c39c55736733939b126038e52b2d5dcc1d2"

PV = "2.14.0.0"

inherit s6-skarnet

do_configure() {
    ${S}/configure --with-sysdep-devurandom=y --enable-static \
		--libdir=/usr/lib --enable-static-libc --prefix=${base_prefix}
}

do_configure:class-native() {
    ${S}/configure --prefix=${base_prefix} --includedir=${prefix}/include
}
