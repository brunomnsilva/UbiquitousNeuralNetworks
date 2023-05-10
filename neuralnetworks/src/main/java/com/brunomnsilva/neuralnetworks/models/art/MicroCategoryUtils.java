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

package com.brunomnsilva.neuralnetworks.models.art;

/**
 * A utility class to process micro-categories.
 *
 * @author brunomnsilva
 */
public class MicroCategoryUtils {

    /**
     * Computes the maximum weight among all <i>micro-categories</i> in <code>microCategories</code>.
     *
     * @param microCategories Iterable of micro-categories.
     * @return the maximum weight or -1 if iterable is empty.
     */
    public static int maximumWeightAmong(Iterable<MicroCategory> microCategories) {
        int max = -1;
        for (MicroCategory category : microCategories) {
            int weight = category.getWeight();
            if(weight > max) {
                max = weight;
            }
        }
        return max;
    }
}