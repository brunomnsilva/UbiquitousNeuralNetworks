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
 * A base class for running mean filters that processes a sequence of values by computing the mean over a sliding window.
 *
 * @author brunomnsilva
 */
public abstract class AbstractRunningMeanFilter {

    /**
     * The name of the filter.
     */
    protected String name;

    /**
     * The size of the sliding window.
     */
    protected int windowSize;

    /**
     * Constructs a new running mean filter with the specified name and window size.
     * @param name       the name of the filter, must not be null
     * @param windowSize the size of the sliding window, must be greater than or equal to 1
     */
    public AbstractRunningMeanFilter(String name, int windowSize) {
        Args.nullNotPermitted(name, "name");
        Args.requireGreaterEqualThan(windowSize, "windowSize", 1);

        this.name = name;
        this.windowSize = windowSize;
    }

    /**
     * Returns the name of the filter.
     * @return the name of the filter
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the size of the sliding window.
     * @return the size of the sliding window
     */
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Processes a new value through the filter and outputs the current running mean.
     * @param value the new value
     * @return the output of the filter
     */
    public abstract double filter(double value);

    /**
     * Convenience method to return the last value computed by the filter.
     * @return last value computed by the filter
     */
    public abstract double lastOutput();
}

