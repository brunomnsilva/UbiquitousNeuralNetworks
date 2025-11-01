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
 * A simple implementation of a running mean filter that maintains a buffer of the last <code>windowSize</code> input values
 * and outputs their mean. The filter is updated incrementally and does not require recomputation of the
 * entire history of input values.
 *
 * @author brunomnsilva
 */
public class SimpleRunningMeanFilter extends AbstractRunningMeanFilter {

    /**
     * Buffer to hold the input values, until a limit of <code>windowSize</code>.
     * It will be treated as a "circular array".
     */
    private double[] buffer;
    /**
     * Index used in the circular array management.
     */
    private int bufferIndex;
    /**
     * Current sum of the values stored at <code>buffer</code>.
     */
    private double sum;
    /**
     * For keeping the last computed value of the filter.
     */
    private double lastOutput;

    /**
     * Constructs a new simple running mean filter with the specified name and window size.
     *
     * @param name       the name of the filter, must not be null
     * @param windowSize the size of the sliding window, must be greater than or equal to 1
     */
    public SimpleRunningMeanFilter(String name, int windowSize) {
        super(name, windowSize);

        this.buffer = new double[windowSize];
        this.bufferIndex = 0;
        this.sum = 0;
        this.lastOutput = 0;
    }

    @Override
    public double filter(double value) {
        // Subtract the oldest value from the sum
        sum -= buffer[bufferIndex];

        // Add the new value to the sum
        sum += value;

        // Store the new value in the buffer
        buffer[bufferIndex] = value;

        // Increment the buffer index, wrapping around if necessary
        bufferIndex = (bufferIndex + 1) % windowSize;

        // Calculate the running mean
        return (lastOutput = sum / windowSize);
    }

    @Override
    public double lastOutput() {
        return lastOutput;
    }

}
