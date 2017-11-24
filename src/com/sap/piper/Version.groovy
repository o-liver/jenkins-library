package com.sap.piper

import hudson.AbortException

/**
 * Version
 * Handles version numbers.
 */

class Version implements Serializable {

    final def major
    final def minor
    final def patch

    /**
     * Constructor Version(major, minor, patch)
     * @param major
     *           The major version number.
     * @param minor
     *           The minor version number.
     * @param patch
     *           The patch version number.
     */
    Version(major, minor, patch = -1) {
      if (major < 0) throw new IllegalArgumentException("The parameter 'major' can not have a value less than 0.")
      if (minor < 0) throw new IllegalArgumentException("The parameter 'minor' can not have a value less than 0.")
      this.major = major
      this.minor = minor
      this.patch = patch
    }

    /**
     * Constructor Version(text)
     * @param text
     *           As an alternative to calling the constructor with `major`, `minor`, and `patch` version numbers, you can pass this as a String of format 'major.minor.patch'.
     */
    Version(text) {
        if (!text) throw new IllegalArgumentException("The parameter 'text' can not be null or empty.")
        def group = text =~ /(\d+[.]\d+[.]\d+)/
        if (!group) throw new AbortException("The version '$text' has an unexpected format. The expected format is <major.minor.patch>.")
        def i = group[0].size()-1
        def versionNumbers = group[0][i].split("\\.")
        major = versionNumbers[0].toInteger()
        minor = versionNumbers[1].toInteger()
        patch = versionNumbers[2].toInteger()
    }

    /**
     * Indicates whether some other version instance is equal to this one. The two versions are considered equal when they have the same `major`, `minor` and `patch` version number.
     * @param version
     *           The Version instance to compare to this Version instance.
     * @return `true` if `major`, `minor` and `patch` version numbers are equal to each other. Otherwise `false`.
     */
    @Override
    boolean equals(version) {
        if (!version) throw new IllegalArgumentException("The parameter 'version' can not be null.")
        return major == version.major && minor == version.minor && patch == version.patch
    }

    /**
     * Checks whether this Version instance is higher than the other Version instance.
     * @param version
     *           The Version instance to compare to this Version instance.
     * @return `true` if this Version instance is higher than the other Version instance. Otherwise `false`.
     */
    def isHigher(version) {
        if (!version) throw new IllegalArgumentException("The parameter 'version' can not be null.")
        return major > version.major || major == version.major && ( minor > version.minor || minor == version.minor && patch > version.patch)
    }

    /**
     * Checks whether a version is compatible. Two versions are compatible if the major version number is the same, while the minor and patch version number are the same or higher.
     * @param version
     *           The Version instance to compare to this Version instance.
     * @return `true` if this Version instance is compatible to the other Version instance. Otherwise `false`.
     */
    def isCompatibleVersion(version) {
        if (!version) throw new IllegalArgumentException("The parameter 'version' can not be null.")
        return this == version || isHigher(version) && major == version.major
    }

    /**
     * Print the version number in format '<major>.<minor>.<patch>'. If no patch version number exists the format is '<major>.<minor>'.
     * @return A String consisting of `major`, `minor` and if available `patch`, separated by dots.
     */
    @Override
    String toString() {
        return patch != -1 ? "$major.$minor.$patch".toString() : "$major.$minor".toString()
    }
}

