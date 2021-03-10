# Firmware

My debug files have been included to this. Remember to change the compile targets (we target cpp), your scatter location, and your project debug location. Note that we're no longer working bare-metal, so you'll want to run these on the Linux distribution that's given to us.

## Getting Started

Full disclosure: this development workflow is ass. If you have any improvements, please, _please_ update this README.

### Requirements

- EDS Shell (you could optionally use a regular shell in WSL/Linux, but you need the Altera IP libraries)
- Ethernet cable
- Mini-USB cable
- RFS Board with WiFi firmware installed
- MicroSD card with Linux installed
- DE1-SoC configured properly to run Linux (refer to [../linux/README.md])

The EDS Shell should have all the tools you need to compile, so just develop and run `make`. In order to transfer the file to the DE1-SoC, you can use `scp`. By default, the username is `root` and the SSH password is `terasic`.

For example, if the binary name is `de1_binary`, you'll want to run this on your host:

```bash
scp de1_binary root@<IP>:~/
```

This will copy the binary to your home directory.

Since we don't have a static IP set, your router's DHCP will likely assign a random IP every time you power on the device. Thus, we have two options: (1) check your router's page for the IP of the DE1, or (2) connect to the device over Serial and run `ifconfig` to get the IP of the `eth0` interface.

Optionally, you could modify the Makefile and add your board's IP, so you can run `make upload`.

### Misc

You'll want to set `SOCEDS_DEST_ROOT` to `/mnt/c/altera/15.0/embedded` if you're on WSL. You'll also want to install the following compilers:

```bash
sudo apt-get install gcc-arm-linux-gnueabihf g++-arm-linux-gnueabihf
```
