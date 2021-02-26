@ REM ######################################
@ REM # Variable to ignore <CR> in DOS
@ REM # line endings
@ set SHELLOPTS=igncr

@ REM ######################################
@ REM # Variable to ignore mixed paths
@ REM # i.e. G:/$SOPC_KIT_NIOS2/bin
@ set CYGWIN=nodosfilewarning


%QUARTUS_ROOTDIR%\bin64\quartus_pgm.exe -m jtag -c 1 -o "p;DE10_NANO_Bluetooth_Master.sof@2"
@ set SOPC_BUILDER_PATH=%SOPC_KIT_NIOS2%+%SOPC_BUILDER_PATH%
@ "%QUARTUS_ROOTDIR%\bin64\cygwin\bin\bash.exe" --rcfile ".\test.sh"
pause