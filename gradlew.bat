@echo off
setlocal
set SCRIPT_DIR=%~dp0
pushd %SCRIPT_DIR%notes_frontend
call gradlew %*
popd
endlocal
