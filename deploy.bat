    REM deploy.bat                                                2007-09-19
    REM Deploys the actual MulTEx release and the web site
CALL mvn clean    compile
    REM For some reason during the first compilation after clean we get an error:
    REM multex.Exc.throwMe is not compilable (unreported exception Exc)
    REM Thus once more:
mvn deploy    org.codehaus.mojo:docbook-maven-plugin:transform  site:site  site:deploy
PAUSE