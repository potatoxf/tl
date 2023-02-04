@echo off
set message=%1
if "%message%"=="" (
	set message="clear history and caches"
)
git checkout --orphan new
git add -A
git commit -sm %message%
git branch -D master
git branch -m master
git reflog expire --expire=now --all
git gc
git push origin master -f