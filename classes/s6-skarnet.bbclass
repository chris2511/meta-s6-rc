FILES:${PN} += "/libexec"
BBCLASSEXTEND = "native"

do_configure() {
  ${S}/configure --enable-static --libdir=${prefix}/lib \
                 ${@'--enable-static-libc' if "${TCLIBC}" == "musl" else ''}\
                 --includedir=${prefix}/include --prefix=${base_prefix} \
                 --with-include=${STAGING_DIR_TARGET}/${prefix}/include \
                 --with-lib=${STAGING_DIR_TARGET}/${prefix}/lib \
                 --with-sysdeps=${STAGING_DIR_TARGET}/usr/lib/skalibs/sysdeps \
                 --shebangdir=/bin --libexecdir=/usr/libexec
}

do_configure:class-native() {
  ${S}/configure --enable-static --enable-shared --libdir=${prefix}/lib \
                 --includedir=${prefix}/include --prefix=${base_prefix} \
                 --shebangdir=/bin --libexecdir=/usr/libexec
}

do_compile() {
  oe_runmake CC="${CC}"
}

do_install () {
  oe_runmake install DESTDIR=${D}
}
