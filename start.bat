@echo off
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -jar "%~dp0target\xugu-mcp-1.0.jar"
