#!/bin/bash -ex
rm -rf bin


# Fix file names and add proper sourcing
javac -d bin -cp src:test test/TestTupleGenerator.java
javac -d bin -cp src:test test/TupleGeneratorImpl.java
javac -d bin -cp src:test test/TupleGenerator.java
javac -d bin src/ArrayUtil.java
javac -d bin src/KeyType.java  
javac -d bin src/LinHashMap.java 
javac -d bin src/Table.java

