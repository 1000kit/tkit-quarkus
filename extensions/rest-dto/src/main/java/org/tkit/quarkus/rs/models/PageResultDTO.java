/*
 * Copyright 2020 tkit.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tkit.quarkus.rs.models;

import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Deprecated
@RegisterForReflection
public class PageResultDTO<T> {

    /**
     * The total elements in the database.
     */
    private long totalElements;

    /**
     * The page index.
     */
    private int number;

    /**
     * The page size.
     */
    private int size;

    /**
     * The number of pages.
     */
    private long totalPages;

    /**
     * The data stream.
     */
    private List<T> stream;

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getStream() {
        return stream;
    }

    public void setStream(List<T> stream) {
        this.stream = stream;
    }
}
