#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

# Go to project root
cd ..

# Build project
./gradlew clean shadowJar

# Go to text UI test directory
cd text-ui-test

# Run the Java program with test input
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Convert files to Unix format (remove if `dos2unix` is not available)
if command -v dos2unix &> /dev/null
then
    cp EXPECTED.TXT EXPECTED-UNIX.TXT
    dos2unix EXPECTED-UNIX.TXT ACTUAL.TXT
fi

# Compare output with expected result
diff EXPECTED.TXT ACTUAL.TXT
if [ $? -eq 0 ]; then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi
