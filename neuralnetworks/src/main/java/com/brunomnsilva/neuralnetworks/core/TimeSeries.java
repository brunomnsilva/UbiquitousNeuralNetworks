/*
 * The MIT License
 *
 * Ubiquitous Neural Networks | Copyright 2023  brunomnsilva@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.brunomnsilva.neuralnetworks.core;

import java.util.*;

/**
 * A collection of ordered (by time) `TimeValueTuple` objects representing a time series.
 *
 * @author brunomnsilva
 */
public class TimeSeries implements Iterable<TimeValueTuple> {

    /**
     * The list of `TimeValueTuple` objects that comprise the time series.
     */
    private final List<TimeValueTuple> series;

    /**
     * The name of the time series.
     */
    private final String name;

    /**
     * Constructs a new time series with the given name.
     *
     * @param name the name of the time series
     */
    public TimeSeries(String name) {
        this.name = name;
        this.series = new ArrayList<>();
    }

    /**
     * Returns the name of the time series.
     *
     * @return the name of the time series
     */
    public String getName() {
        return name;
    }

    /**
     * Ensures that a new time value is greater than the previously added time value.
     *
     * @param time the time to check
     * @throws IllegalArgumentException if the given time is not greater than the previously added time value
     */
    private void ensureSequence(long time) throws IllegalArgumentException {
        if( series.isEmpty()) return;
        long prev_time = series.get(series.size() - 1).getTime();
        if( time <= prev_time  ) {
            String error = String.format("Invalid time (%ld) when last appended was (%ld). Must be in increasing order.",
                    time, prev_time);
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * Appends a new time value tuple to the time series.
     *
     * @param time the time of the new tuple
     * @param value the value of the new tuple
     */
    public void append(long time, double value) {
        ensureSequence(time);
        series.add( new TimeValueTuple(time, value) );
    }

    /**
     * Appends a new time value tuple to the time series.
     *
     * @param tuple the new time value tuple to append
     */
    public void append(TimeValueTuple tuple) {
        ensureSequence(tuple.getTime());
        series.add(tuple);
    }

    /**
     * Returns the number of tuples in the time series.
     *
     * @return the number of tuples in the time series
     */
    public int size() {
        return series.size();
    }

    /**
     * Clears all tuples from the time series.
     */
    public void clear() {
        series.clear();
    }

    /**
     * Determines if the time series is empty.
     *
     * @return `true` if the time series is empty, otherwise `false`
     */
    public boolean isEmpty() {
        return series.isEmpty();
    }

    @Override
    public Iterator<TimeValueTuple> iterator() {
        return series.iterator();
    }

    @Override
    public Spliterator<TimeValueTuple> spliterator() {
        return series.spliterator();
    }
}
