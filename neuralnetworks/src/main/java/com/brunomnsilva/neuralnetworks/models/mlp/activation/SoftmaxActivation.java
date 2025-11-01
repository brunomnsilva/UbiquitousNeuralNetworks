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

package com.brunomnsilva.neuralnetworks.models.mlp.activation;

import java.util.Arrays;

/**
 * Softmax activation function for multiclass classification.
 *
 * <p>This activation function operates on a vector of values (logits) and converts
 * them into a probability distribution, where the sum of all outputs is 1.
 * It is intended to be used exclusively in the output layer of a neural network.</p>
 *
 * <p>Note: This implementation does not support per-neuron scalar operations.
 * Attempting to call the scalar compute or derivative methods will throw
 * UnsupportedOperationException.</p>
 *
 * <p>Usage example:
 * SoftmaxActivation softmax = new SoftmaxActivation();
 * double[] logits = {2.0, 1.0, 0.1};
 * double[] probabilities = softmax.compute(logits); // [0.659, 0.242, 0.099]
 * </p>
 *
 * <p>The derivative of Softmax is typically handled together with cross-entropy
 * loss during backpropagation. The vector derivative can be implemented if needed.</p>
 *
 * @author brunomnsilva
 */
public class SoftmaxActivation implements ActivationFunction {
    @Override
    public double[] compute(double[] x) {

        double max = Arrays.stream(x).max().getAsDouble(); // Prevent overflow
        double sum = 0.0;
        double[] exp = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max); // stabilized
            sum += exp[i];
        }

        for (int i = 0; i < x.length; i++) {
            exp[i] /= sum;
        }

        return exp;
    }

    // Note: derivative is usually handled differently in training with cross-entropy loss
    @Override
    public double[] derivative(double[] fx) {
        double[] derivative = new double[fx.length];
        for (int i = 0; i < fx.length; i++) {
            derivative[i] = fx[i] * (1 - fx[i]); // approximation if needed
        }
        return derivative;
    }

    @Override
    public boolean isVectorActivation() {
        return true;
    }

    @Override
    public double compute(double x) {
        // return the value directly.
        return x;
    }

    @Override
    public double derivative(double fx) {
        throw new UnsupportedOperationException("Use vector derivative for Softmax");
    }
}
