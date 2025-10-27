/*
 * The MIT License
 *
 * Ubiquitous Neural Networks | Copyright 2023-2025  brunomnsilva@gmail.com
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.brunomnsilva.neuralnetworks.models.mlp.activation;

/**
 * An activation function.
 * <br/>
 * Must provide the computation of the activation and the derivative of the function.
 *
 * @author brunomnsilva
 */
public interface ActivationFunction {

     /**
      * Computation of the activation function for an input value.
      * @param x the input value
      * @return the activation function value
      */
     double compute(double x);

     /**
      * The derivative of the function for an input value.
      * @param fx the input value
      * @return the derivative value of the function
      */
     double derivative(double fx);

     /**
      * Optional computation of the activation function for a vector of values.
      * This is useful for vector-level activations like Softmax.
      * Default implementation throws UnsupportedOperationException.
      * @param x the input vector
      * @return the activated vector
      */
     default double[] compute(double[] x) {
          throw new UnsupportedOperationException("Vector activation not supported for this function");
     }

     /**
      * Optional derivative for vector-level activation.
      * Default implementation throws UnsupportedOperationException.
      * @param fx the activated vector
      * @return the derivative vector
      */
     default double[] derivative(double[] fx) {
          throw new UnsupportedOperationException("Vector derivative not supported for this function");
     }

     /**
      * Defines if this a vector-level activation function, i.e., the activation output of
      * a neuron depends on the entire layer output. Usually used only in the output layer,
      * e.g., the softmax function.
      *
      * @return true if it is a vector-level activation function; false, otherwise.
      */
     default boolean isVectorActivation() {
          return false;  // default for per-neuron activations
     }
}
