DESCRIPTION = "All S6 RC Packages"

inherit packagegroup

RDEPENDS:${PN} = "\
    execline \
    mdevd \
    s6 \
    s6-dns \
    s6-frontend \
    s6-linux-init \
    s6-networking \
    s6-rc \
    s6-init \
    lighttpd \
    dropbear \
"
