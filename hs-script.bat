@echo off
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
cd /d %~dp0

:: 设置控制台代码页为UTF-8
chcp 65001 >nul

set curdir=%cd%

for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Dfile.encoding=utf-8 -Djna.library.path="%curdir%" -jar %%a %1
)