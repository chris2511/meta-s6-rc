#!/bin/sh

exec 2> /dev/null

CERT="/etc/lighttpd/https-cert.pem"
CONF="/etc/lighttpd/openssl-cert.conf"

test -s "${CERT}" && exit 0

umask 0277 &&
openssl req -x509 -noenc -new -config ${CONF} > ${CERT}.tmp &&
chown www-data "${CERT}.tmp" &&
sync &&
mv "${CERT}.tmp" "${CERT}" || rm -f "${CERT}.tmp"
sync
