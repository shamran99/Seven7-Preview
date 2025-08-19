#!/bin/bash

echo "starting creating sync.."

#variables
folderToSync=test
authFile=authFile
jdrivsyncjar=./lib/jdrivesync-0.3.0-jar-with-dependencies.jar

#process
if [ ! -d "$folderToSync" ]; then
	mkdir $folderToSync
fi

if [ -f "$authFile" ]; then
	echo "Please delete $authFile and continue."
	exit 1
fi

java -jar $jdrivsyncjar -u -l $folderToSync --no-delete  -a $authFile
vim $authFile
