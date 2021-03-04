################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../hardware/wifi.cpp 

OBJS += \
./hardware/wifi.o 

CPP_DEPS += \
./hardware/wifi.d 


# Each subdirectory must supply rules for building sources it contributes
hardware/%.o: ../hardware/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: ARM C Compiler 5'
	armcc --cpp -O0 -g --md --depend_format=unix_escaped -c -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


