# Quarantine Monitor

This repo serves as a monorepo for all the code related to group 5's CPEN 391 project.

Our members are:

- David Kang
- Leslie Cheng
- Naomi Chen
- Preet Shah
- Wilson Gee

The nested directories will have more info about each component of our system.

## Motivation

The motivation for this project is the lack of a confident quarantine monitoring system, especially since millions of people still travel to Canada despite the impacts of the COVID-19 pandemic.

Although the Government of Canada set in place a mandatory quarantine policy for travellers without symptoms of COVID-19, there still lacks a confident way of enforcing this rule.

Given that many countries are experiencing high levels of COVID-19 contamination, it is essential to track individuals coming from high-risk countries.

## Solution

This repository contains our quarantine monitoring project, which is comprised of two parts:

1. **A physical device** (DE1-SoC FPGA) that acts as the "base station" located inside the user’s home. It connects with the user’s phone to actively determine whether the user is in quarantine. 
2. **A mobile application** that stores the user's basic profile and connects with the DE1 via bluetooth to continuously exchange information. An "admin" mode to the app which allows public health administrators to view and monitor individuals.
## Key Features

1. Periodic facial verification to ensure the user is in proximity of their mobile device
2. Tamper detection of the base station
3. Analytics for health administrators
4. Seamless, one-time system setup
