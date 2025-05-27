#!/bin/bash

set -e

rm -rf out
mkdir -p out

find src -name "*.java" > sources.txt
javac -d out @sources.txt

if [ "$1" != "" ]; then
  java -cp out app.Main "$1"
else
  java -cp out app.Main
fi
