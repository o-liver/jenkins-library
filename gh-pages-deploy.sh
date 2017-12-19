#!/bin/bash

. .travis.env

if [[ "${TRAVIS_PULL_REQUEST}" != "false" ]]; then
    echo "Github page deployment not performed for pull requests"
    # Check for build errors or warnings
    cd $TRAVIS_BUILD_DIR/documentation
    mkdocs build --clean --verbose --strict
    exit $?
elif [[ "${TRAVIS_BRANCH}" != "master" ]]; then
    echo "Github page deployment not performed for non-master."
    exit 0
fi

# We can't deploy if any of these vars are empty
for variable in "$KEY" "$IV" "$ENCRYPTED_FILE"; do
    if [ -z "$variable" ]; then
        echo "ERROR: Found empty KEY, IV, or ENCRYPTED_FILE variable. Exiting."
        exit 1
    fi
done

openssl aes-256-cbc -K "$KEY" -iv "$IV" -in "$ENCRYPTED_FILE" -out deploy-key -d

chmod 600 deploy-key
eval `ssh-agent -s`
ssh-add deploy-key
git config user.name "Travis CI Publisher"
git remote add gh-token "git@github.com:$TRAVIS_REPO_SLUG.git";
git fetch gh-token && git fetch gh-token gh-pages:gh-pages
echo "Pushing to gh-pages of repository $TRAVIS_REPO_SLUG"
cd $TRAVIS_BUILD_DIR/documentation
mkdocs gh-deploy -v --clean --remote-name gh-token