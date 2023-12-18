SUMMARY = "A set of general-purpose C programming libraries all skarnet.org software depends on."
DESCRIPTION = "skalibs is a package centralizing the free software / open source C development files used for building all software at skarnet.org: it contains essentially general-purpose libraries."
HOMEPAGE = "https://skarnet.org/software/${BPN}/"
LICENSE = "ISC"
SECTION = "base"
DEPENDS = ""
LIC_FILES_CHKSUM = "file://COPYING;md5=34f73ee8aab2e0ca56980313bfd7a7bb"

SRC_URI = "https://skarnet.org/software/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "b43ebdb36f8a8df40f1a08c13b08393bce49faf334e62a0db919b3ae4afd1488"

PV = "2.14.0.1"

inherit s6-skarnet

do_configure() {
    ${S}/configure --with-sysdep-devurandom=y --enable-static \
		--libdir=${libdir} --enable-static-libc --prefix=${root_prefix}
}

do_configure:class-native() {
    ${S}/configure --prefix=${root_prefix} --includedir=${includedir}
}
