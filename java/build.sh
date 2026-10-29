#!/bin/bash
# build.sh - compile the Java port and package it as swmm5.jar (JDK 17 or newer).
#   ./build.sh            then   ./runswmm <input file> <report file> [output file]
# runswmm gives the JVM three quarters of memory and the parallel collector; a plain `java -jar swmm5.jar`
# gets the default quarter, too little for the largest gate model (Hydrology/sewer_rdii_problem.inp).
set -e
cd "$(dirname "$0")"
rm -rf classes && mkdir classes
javac -nowarn -encoding ISO-8859-1 -d classes src/swmm5/*.java
jar cfe swmm5.jar swmm5.Main -C classes .
echo "built $(pwd)/swmm5.jar"
