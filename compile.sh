#!/bin/bash
rm -rf bin



javac -d bin -cp src src/*.java 
java -cp bin src.TestTupleGenerator



