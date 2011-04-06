#! /usr/bin/sh
# deploy.sh                                                2007-09-19
# 2011-03-30  Knabe  Derived from deploy.bat
# Deploys the actual MulTEx release and the web site
echo $- $0
set ue
mvn -e clean compile
# For some reason during the first compilation after clean we get an error:
# multex.Exc.throwMe is not compilable (unreported exception Exc)
# Thus once more:
mvn deploy    org.codehaus.mojo:docbook-maven-plugin:transform  site:site  site:deploy

