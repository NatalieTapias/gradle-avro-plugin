/**
 * Copyright © 2015 Commerce Technologies, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.commercehub.avro.depresolver;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

class FileState implements Comparable<FileState> {
    private final File sourceFile;
    private String errorMessage;
    private Set<String> duplicateTypeNames = new TreeSet<String>();

    FileState(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    File getSourceFile() {
        return sourceFile;
    }

    Set<String> getDuplicateTypeNames() {
        return duplicateTypeNames;
    }

    void clearError() {
        errorMessage = null;
    }

    void setError(Throwable ex) {
        this.errorMessage = ex.getMessage();
    }

    void addDuplicateTypeName(String typeName) {
        duplicateTypeNames.add(typeName);
    }

    public boolean containsDuplicateTypeName(String typeName) {
        return duplicateTypeNames.contains(typeName);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public int compareTo(FileState o) {
        return sourceFile.compareTo(o.sourceFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileState fileState = (FileState) o;
        return sourceFile.equals(fileState.sourceFile);
    }

    @Override
    public int hashCode() {
        return sourceFile.hashCode();
    }

    String getPath() {
        return sourceFile.getPath();
    }
}