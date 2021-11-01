FILES:${PN} += "/libexec"
BBCLASSEXTEND = "native"

do_configure() {
  ${S}/configure --enable-static --libdir=${prefix}/lib --enable-static-libc \
                 --includedir=${prefix}/include --prefix=${base_prefix} \
                 --with-sysdeps=${STAGING_DIR_TARGET}/usr/lib/skalibs/sysdeps
}

do_configure:class-native() {
  ${S}/configure --enable-static --enable-shared --libdir=${prefix}/lib \
                 --includedir=${prefix}/include --prefix=${base_prefix}
}

do_compile() {
  oe_runmake CC="${CC}"
}

do_install () {
  oe_runmake install DESTDIR=${D}
}
