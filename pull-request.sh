#!/bin/bash

# Run tests
mvn test -B

# Build documentation to check for build errors and warnings
echo "Github page deployment not performed for pull requests"
echo "Building documentation to check for errors"
cd $TRAVIS_BUILD_DIR/documentation
mkdocs build --clean --verbose --strict
exit $?
