#! /usr/bin/sh
# deploy.sh                                                2022-01-07
# Deploys the current MulTEx release and the web site
echo $- $0
set -ue
#mvn -e clean compile
# For some reason during the first compilation after clean we get an error:
# multex.Exc.throwMe is not compilable (unreported exception Exc)
# Thus once more:
sh mvnw clean deploy  site site:deploy

