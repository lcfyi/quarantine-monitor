# Hardware

**BIG CAVEAT**: A lot of the files generated that have been committed are artifacts from the build, thus they probably won't be compatible with your system. You'll have to take some time to prune them; I wanted to check it into VCS just because I have a working version at the moment.

## Development


### Pre-requisites

You need DS-5 Eclipse, Quartus 15.0, and EDS 15 Command Shell.

## Setting up the DE1

Launch the EDS Command Shell with administrator permissions, then set up the preloader:
```powershell
C:/Altera/15.0/quartus/bin64/quartus_hps --cable="DE-SoC [USB-1]" -o GDBSERVER --gdbport0=3335 --preloader=C:/Altera/15.0/University_Program/Monitor_Program/arm_tools/u-boot-spl.srec --preloaderaddr=0xffff1398
```

Kill the GDB server once that's set up, then launch up DS-5 and set up a debugging connection according to tutorial 1.5.

## Bluetooth

It works in AT mode (38400), but not in the regular mode (I swear I got it to work at 115200 though).

### TODO

- Write a libary to wrap the connection processes

## Wifi

### TODO

- Fix the behaviour with the reset switch being required at startup
  - This behaviour is due to CTS being driven to a float (I haven't been able to verify which one since I don't have access to a scope); once the FPGA firmware is flashed and the module reset, the WiFi module works
- Write a library to wrap the AT commands 
  - Or alternatively, install nodeMCU and use Lua to wrap the functions we need?
## General

### TODO

- Prune the unnecessary quartus components that are dragging compilation times
- Prune the files so that we're not dependent on DS-5
- Figure out how to install Linux on the board so we don't have to deal with bare-metal programming with DS-5