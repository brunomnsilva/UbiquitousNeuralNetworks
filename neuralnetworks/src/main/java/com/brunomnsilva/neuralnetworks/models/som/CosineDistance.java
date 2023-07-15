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

package com.brunomnsilva.neuralnetworks.models.som;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.VectorN;

/**
 * An implementation of the Cosine (similarity) metric distance.
 * Cosine similarity is the cosine of the angle between the vectors;
 * that is, it is the dot product of the vectors divided by the product of their lengths.
 *
 * @author brunomnsilva
 */
public class CosineDistance implements MetricDistance {
    @Override
    public double distanceBetween(VectorN a, VectorN b) {
        Args.requireEqual(a.dimensions(), "a.dimensions()", b.dimensions(), "b.dimensions()");


        int len = a.dimensions();
        double distance = 0;
        for(int i=0; i < len; i++) {
            distance += (a.get(i) * b.get(i));
        }

        return 1 - distance / (a.magnitude() * b.magnitude());
    }
}
