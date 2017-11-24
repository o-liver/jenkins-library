package com.sap.piper

import hudson.AbortException
import java.io.File

/**
 * FileUtils
 * Provides file system related utility functions.
 *
 */

class FileUtils implements Serializable {

    /**
     * Checks whether a file exists and is a directory.
     * @param dir
     *           The directory to be checked. In case it is relative path it is checked against the
     current working directory. In case of doubt use the absolute path (prefix the directory with `pwd`).
     */
    static validateDirectory(dir) {
        if (!dir) throw new IllegalArgumentException("The parameter 'dir' can not be null or empty.")
        def file = new File(dir)
        if (!file.exists()) throw new AbortException("'${file.getAbsolutePath()}' does not exist.")
        if (!file.isDirectory()) throw new AbortException("'${file.getAbsolutePath()}' is not a directory.")
    }

    /**
     * Check whether a directory is not empty. Before the directory is checked, `validateDirectory(dir)` is executed.
     * @param dir
     *           The directory to be checked. In case it is relative path it is checked against the
     current working directory. In case of doubt use the absolute path (prefix the directory with `pwd`).
     */
    static validateDirectoryIsNotEmpty(dir) {
        validateDirectory(dir)
        def file = new File(dir)
        if (file.list().size() == 0) throw new AbortException("'${file.getAbsolutePath()}' is empty.")
    }
}
