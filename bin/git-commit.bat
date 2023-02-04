@echo off
set message=%1
if "%message%"=="" (
	set message="update"
)
git add .
git commit -sm %message%
git push origin master
pause