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

package com.brunomnsilva.neuralnetworks.models.som.impl;

import com.brunomnsilva.neuralnetworks.models.som.Lattice;
import com.brunomnsilva.neuralnetworks.models.som.MetricDistance;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

/**
 * This represents a traditional self-organizing map. Can be used with classic and batch learning.
 *
 * @see com.brunomnsilva.neuralnetworks.models.som.ClassicLearning
 * @see com.brunomnsilva.neuralnetworks.models.som.BatchLearning
 *
 * @author brunomnsilva
 */
public class BasicSOM extends SelfOrganizingMap {

    /**
     * Instantiates a new self-organizing map (SOM) with default hexagonal shape lattice
     * and euclidean metric distance.
     *
     * @param width the width of the SOM lattice
     * @param height the height of the SOM lattice
     * @param dimensionality the dimensionality of the SOM prototypes
     * @throws IllegalArgumentException if width, height or dimensionality are not greater than 0
     */
    public BasicSOM(int width, int height, int dimensionality) {
        super(width, height, dimensionality);
    }

    /**
     * Instantiates a new self-organizing map (SOM).
     * @param width the width of the 2d SOM lattice
     * @param height the height of the 2d SOM lattice
     * @param dimensionality the dimensionality of the SOM prototypes
     * @param lattice the lattice shape
     * @param metricDistance the metric distance to use to compute the best matching unit
     * @throws IllegalArgumentException if width, height or dimensionality are not greater than 0;
     *                                  if lattice or metricDistance are null.
     */
    public BasicSOM(int width, int height, int dimensionality, Lattice lattice, MetricDistance metricDistance) {
        super(width, height, dimensionality, lattice, metricDistance);
    }

    @Override
    public String getImplementationName() {
        return "BasicSOM";
    }
}
