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
 * Implements the Xavier (Glorot) initialization method.
 * <p>
 * This initialization is suitable for activation functions such as
 * sigmoid or tanh. It helps maintain the variance of activations
 * approximately constant across layers, which improves convergence.
 * </p>
 *
 * <p>
 * The weight values are sampled uniformly from the range: [-limit, +limit]}, where limit = sqrt(6 / (fanIn + fanOut))}.
 * </p>
 *
 * @author brunomnsilva
 */
public class XavierInitializer implements WeightInitializer {

    @Override
    public double initialize(int fanIn, int fanOut, Random rand) {
        double limit = Math.sqrt(6.0 / (fanIn + fanOut));
        return (rand.nextDouble() * 2 - 1) * limit; // uniform in [-limit, +limit]
    }
}
