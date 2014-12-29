@echo off
setlocal ENABLEDELAYEDEXPANSION

SET APP_HOME=%~dp0
SET LIBS=%APP_HOME%\lib
SET CLASSPATH=""

for /F %%a in ('dir /B /S %LIBS%') DO (SET CLASSPATH=!CLASSPATH!;%%a)

call java -cp %CLASSPATH% nl.ulso.magisto.MagistoLauncher $*
