#!/bin/sh -e 
#
# /home/pi/script/termostato.sh
#
# This script is executed at the end of each multiuser runlevel.
# Make sure that the script will "exit 0" on success or any other
# value on error.
#
# In order to enable or disable this script just change the execution
# bits.
#

/usr/bin/thermostat &
