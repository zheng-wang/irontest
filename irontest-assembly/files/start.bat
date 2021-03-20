@echo off
setlocal enabledelayedexpansion
cd %~dp0

set count=0
for /f %%i in ('dir /b /on irontest-*.jar 2^>nul') do (
  set tmpfile=%%i
  REM exclude the upgrade jar
  if "x!tmpfile:-upgrade-to-=!"=="x!tmpfile!" (
    set /a count+=1
	set jarFile=!tmpfile!
  )
)

if "%~1"=="team" (
  set batFileName=start-team
  set javaCommand=java -Djava.net.useSystemProxies=true -Ddw.mode=team -jar %jarFile% server config.yml
) else (
  set batFileName=start
  set javaCommand=java -Djava.net.useSystemProxies=true -jar %jarFile% server config.yml
)

if %count% EQU 0 (
  echo Iron Test jar file not found. & pause
) else (
  if %count% GTR 1 (
    echo Multiple Iron Test jar files found. Please leave only one and rerun the %batFileName% command. & pause
  ) else (
    echo %javaCommand%
    %javaCommand%
  )
)