/*
 * The MIT License
 *
 * Ubiquitous Neural Networks | Copyright 2025  brunomnsilva@gmail.com
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

package com.brunomnsilva.neuralnetworks.models.mlp.init;

import java.util.Random;

/**
 * Strategy interface for weight initialization in a multilayer perceptron (MLP).
 * <p>
 * Implementations of this interface define how to generate the initial weight
 * values for the connections (synapses) between two layers of neurons.
 * </p>
 *
 * @author brunomnsilva
 */
public interface WeightInitializer {

    /**
     * Computes an initial weight value given the size of the source and target layers.
     *
     * @param fanIn  the number of input connections (neurons in the previous layer)
     * @param fanOut the number of output connections (neurons in the next layer)
     * @param rand   the random number generator to use
     * @return a weight value according to the chosen initialization strategy
     */
    double initialize(int fanIn, int fanOut, Random rand);
}
