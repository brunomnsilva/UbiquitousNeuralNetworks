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
 * The purpose of this class is to approximate a Gaussian filter. Its internal implementation uses a sequence
 * of {@link SimpleRunningMeanFilter} with specific window sizes.
 *
 * @see SimpleRunningMeanFilter
 *
 * @author brunomnsilva
 */
public class TripleCascadedMeanFilter extends AbstractRunningMeanFilter {

    public static final double WINDOW_RATIO = 1.2067;
    public static final double CONSTANT = 2.08458;

    private SimpleRunningMeanFilter filter1;
    private SimpleRunningMeanFilter filter2;
    private SimpleRunningMeanFilter filter3;

    /**
     * Constructs a new triple cascaded mean filter with the specified name and window size.
     *
     * @param name       the name of the filter, must not be null
     * @param windowSize the size of the sliding window, must be greater than or equal to 1
     */
    public TripleCascadedMeanFilter(String name, int windowSize) {
        super(name, windowSize);

        int[] windowSizes = windowSizes(windowSize);

        this.filter1 = new SimpleRunningMeanFilter("Cascaded Filter 1", windowSizes[0]);
        this.filter2 = new SimpleRunningMeanFilter("Cascaded Filter 2", windowSizes[1]);
        this.filter3 = new SimpleRunningMeanFilter("Cascaded Filter 3", windowSizes[2]);
    }

    @Override
    public double filter(double value) {
        // Cascade through each filter
        double filteredValue = filter1.filter(value);
        filteredValue = filter2.filter(filteredValue);
        return filter3.filter(filteredValue);
    }

    @Override
    public double lastOutput() {
        return filter3.lastOutput();
    }

    private int[] windowSizes(int windowSize) {
        //R = 1,2067, (1/R + 1/R^2 + 1/R^3)*CONSTANT = N
        //solution: CONSTANT = N / 2,08458
        //R <- 1.2067
        double M = windowSize / CONSTANT;

        int windowSize1 = (int)Math.round(M / WINDOW_RATIO );
        int windowSize2 = (int)Math.round(M/ (WINDOW_RATIO*WINDOW_RATIO) );
        int windowSize3 = (int)Math.round(M/ (WINDOW_RATIO*WINDOW_RATIO*WINDOW_RATIO) );

        return new int[]{windowSize1, windowSize2, windowSize3};
    }
}
