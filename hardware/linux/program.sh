#!/bin/bash

if [[ $1 =~ \.rbf$ ]]
then echo "Writing.."
else echo "Wrong extension, exiting.." && exit
fi

dd if=$1 of=/dev/fpga0 bs=1M

echo 1 > /sys/class/fpga-bridge/fpga2hps/enable
echo 1 > /sys/class/fpga-bridge/hps2fpga/enable
echo 1 > /sys/class/fpga-bridge/lwhps2fpga/enable
