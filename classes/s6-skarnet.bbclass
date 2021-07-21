FILES_${PN} += "/libexec"
BBCLASSEXTEND = "native"

SYSDEPS_class-target = "${STAGING_DIR_TARGET}"
SYSDEPS_class-native = "${STAGING_DIR_NATIVE}"

do_configure() {
  ${S}/configure --enable-static --libdir=${prefix}/lib --enable-static-libc \
                 --includedir=${prefix}/include --prefix=${base_prefix} \
		 --with-include=${SYSDEPS}/${prefix}/include \
		 --with-lib=${SYSDEPS}/${prefix}/lib \
                 --with-sysdeps=${SYSDEPS}/usr/lib/skalibs/sysdeps
}

do_configure_class-native() {
  ${S}/configure --enable-static --enable-shared --libdir=${prefix}/lib \
                 --includedir=${prefix}/include --prefix=${base_prefix}
}

do_compile() {
  oe_runmake CC="${CC}"
}

do_install () {
  oe_runmake install DESTDIR=${D}
}
