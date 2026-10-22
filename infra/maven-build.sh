#!/bin/sh
set -eu
export http_proxy="${HTTP_PROXY:-}" https_proxy="${HTTPS_PROXY:-}" no_proxy="${NO_PROXY:-}"
if [ -f /run/secrets/ca_bundle ]; then
  export CURL_CA_BUNDLE=/run/secrets/ca_bundle
  export WGETRC=/tmp/cloud-wgetrc
  printf 'ca_certificate=/run/secrets/ca_bundle\n' > "$WGETRC"
fi
if [ -f /run/secrets/java_cacerts ]; then
  export MAVEN_OPTS="${MAVEN_OPTS:-} -Djavax.net.ssl.trustStore=/run/secrets/java_cacerts"
fi
exec bash mvnw -B -ntp "$@"
