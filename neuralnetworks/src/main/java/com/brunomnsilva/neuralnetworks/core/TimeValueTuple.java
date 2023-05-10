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

/**
 * A tuple of a time and a value, with a natural ordering based on the time.
 *
 * @author brunomnsilva
 */
public class TimeValueTuple implements Comparable<TimeValueTuple> {

    /**
     * The time value of the tuple.
     */
    private final long time;

    /**
     * The value of the tuple.
     */
    private final double value;

    /**
     * Constructs a new TimeValueTuple with the specified time and value.
     * @param time the time value of the tuple
     * @param value the value of the tuple
     */
    public TimeValueTuple(final long time, final double value) {
        this.time = time;
        this.value = value;
    }

    /**
     * Returns the time value of the tuple.
     * @return the time value of the tuple
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the value of the tuple.
     * @return the value of the tuple
     */
    public double getValue() {
        return value;
    }

    /**
     * Compares this tuple to another tuple based on their time values.
     * @param other the other tuple to compare to
     * @return a negative integer, zero, or a positive integer as this tuple is less than, equal to, or greater than the specified tuple
     */
    @Override
    public int compareTo(TimeValueTuple other) {
        return (int)(this.time - other.time);
    }
}

