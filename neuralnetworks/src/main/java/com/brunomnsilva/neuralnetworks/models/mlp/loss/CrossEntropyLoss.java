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

package com.brunomnsilva.neuralnetworks.models.mlp.loss;

public class CrossEntropyLoss implements LossFunction {

    @Override
    public double computeLoss(double[] predicted, double[] target) {
        double loss = 0;
        for (int i = 0; i < predicted.length; i++) {
            loss -= target[i] * Math.log(predicted[i] + 1e-15); // epsilon for stability
        }
        return loss;
    }

    @Override
    public double[] derivative(double[] predicted, double[] target) {
        double[] delta = new double[predicted.length];
        for (int i = 0; i < predicted.length; i++) {
            delta[i] = predicted[i] - target[i]; // gradient for Softmax + Cross-Entropy
        }
        return delta;
    }

    public static void main(String[] args) {
        double[] target = {0, 1, 0};
        double[] probabilities = {0.05, 0.9, 0.05};

        CrossEntropyLoss loss = new CrossEntropyLoss();
        System.out.println(loss.computeLoss(probabilities, target));
    }
}
