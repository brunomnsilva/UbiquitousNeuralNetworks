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

package com.brunomnsilva.neuralnetworks.dataset;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.VectorN;

/**
 * Represents a dataset item/observation/entry.
 * Each item consists of its input vector and target vectors, see {@link VectorN}.
 *
 * @author brunomnsilva
 */
public class DatasetItem {
    private VectorN input;
    private VectorN targetOutput;

    /**
     * Creates a new dataset item with the given input and target output arrays.
     *
     * @param input         the input array
     * @param targetOutput  the target output array
     */
    public DatasetItem(double[] input, double[] targetOutput) {
        Args.nullNotPermitted(input, "input");
        Args.nullNotPermitted(targetOutput, "targetOutput");

        this.input = VectorN.fromArray(input);
        this.targetOutput = VectorN.fromArray(targetOutput);
    }

    /**
     * Creates a new dataset item with the given input and target output vectors.
     *
     * @param input         the input vector
     * @param targetOutput  the target output vector
     */
    public DatasetItem(VectorN input, VectorN targetOutput) {
        Args.nullNotPermitted(input, "input");
        Args.nullNotPermitted(targetOutput, "targetOutput");

        this.input = input.copy();
        this.targetOutput = targetOutput.copy();
    }

    /**
     * Creates a new dataset item by extracting the input and target output vectors from a single array.
     *
     * @param vector           the array containing the input and target output vectors
     * @param numberColsInput  the number of columns in the input vector
     * @param numberColsOutput the number of columns in the target output vector
     */
    public DatasetItem(double[] vector, int numberColsInput, int numberColsOutput) {
        Args.nullNotPermitted(vector, "vector");
        Args.requireEqual(vector.length, "vector.length",
                numberColsInput+numberColsOutput, "numberColsInput+numberColsOutput");

        double[] input = new double[numberColsInput];
        double[] targetOutput = new double[numberColsOutput];

        System.arraycopy(vector, 0, input, 0, numberColsInput);
        System.arraycopy(vector, numberColsInput, targetOutput, 0, numberColsOutput);

        this.input = VectorN.fromArray(input);
        this.targetOutput = VectorN.fromArray(targetOutput);
    }

    /**
     * Creates a deep-copy of this dataset item.
     * @return a copy of this dataset item
     */
    public DatasetItem copy() {
        return new DatasetItem(input.values(), targetOutput.values());
    }

    /**
     * Returns the input vector of this dataset item.
     * @return the input vector of this dataset item
     */
    public VectorN getInput() {
        return input;
    }

    /**
     * Returns the target output vector of this dataset item.
     * @return the target output vector of this dataset item
     */
    public VectorN getTargetOutput() {
        return targetOutput;
    }

    @Override
    public String toString() {
        return "input=" + input + ", targetOutput=" + targetOutput;
    }
}

