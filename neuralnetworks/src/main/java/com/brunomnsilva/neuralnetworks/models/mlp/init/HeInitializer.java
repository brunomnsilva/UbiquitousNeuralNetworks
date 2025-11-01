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
 * Implements the He (Kaiming) initialization method.
 * <p>
 * This initialization is designed for use with ReLU and related activation
 * functions. It maintains the variance of activations by sampling weights
 * from a normal distribution with standard deviation sqrt(2 / fanIn).
 * </p>
 *
 * @author brunomnsilva
 */
public class HeInitializer implements WeightInitializer {

    @Override
    public double initialize(int fanIn, int fanOut, Random rand) {
        return rand.nextGaussian() * Math.sqrt(2.0 / fanIn);
    }
}

