@ echo off
set rootDir=%cd%
cd %rootDir%
set/p version=���������汾�����س�����dev����
call mvn clean compile install -P %version% -Dmaven.test.skip=true
pause
::exit