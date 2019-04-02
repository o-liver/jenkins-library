/*
In case the build is performed for a pull request TRAVIS_COMMIT is a merge
commit between the base branch and the PR branch HEAD. That commit is actually built.
    But for notifying about a build status we need the commit which is currently
the HEAD of the PR branch.

    In case the build is performed for a simple branch (not associated with a PR)
In this case there is no merge commit between any base branch and HEAD of a PR branch.
The commit which we need for notifying about a build status is in this case simply
TRAVIS_COMMIT itself.
*/

println "Hello"
println "commit sha: ${env.TRAVIS_COMMIT}"
