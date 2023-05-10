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

import com.brunomnsilva.neuralnetworks.core.VectorN;

/**
 * A min-max dataset normalization implementation.
 *
 * @author brunomnsilva
 */
public class MinMaxNormalization extends DatasetNormalization {

    private DatasetSummary summary;

    private VectorN inDivider;
    private VectorN outDivider;

    /**
     * Creates a new instance of MinMaxNormalization.
     * @param dataset the Dataset to compute normalization information from
     */
    public MinMaxNormalization(Dataset dataset) {
        // Delegate computations
        summary = new DatasetSummary(dataset);

        inDivider = summary.inputMaximumValues().copy();
        inDivider.subtract(summary.inputMinimumValues());

        outDivider = summary.outputMaximumValues().copy();
        outDivider.subtract(summary.outputMinimumValues());
    }

    @Override
    public void normalize(Dataset dataset) {
        /*
         * Performs min-max scaling on the given data set.
         * x = (x - min) / (max - min)
         */
        VectorN minInput = summary.inputMinimumValues();
        VectorN minOutput = summary.outputMinimumValues();

        for (DatasetItem item : dataset) {
            item.getInput().subtract(minInput);
            item.getInput().divide(inDivider);
            item.getTargetOutput().subtract(minOutput);
            item.getTargetOutput().divide(outDivider);
        }

    }

    @Override
    public void denormalize(Dataset dataset) {
        VectorN minInput = summary.inputMinimumValues();
        VectorN minOutput = summary.outputMinimumValues();

        for (DatasetItem item : dataset) {
            item.getInput().multiply(inDivider);
            item.getInput().add(minInput);
            item.getTargetOutput().subtract(minOutput);
            item.getTargetOutput().divide(outDivider);
        }
    }

    @Override
    public DatasetItem normalize(DatasetItem item) {
        VectorN input = normalizeInput(item.getInput());
        VectorN output = normalizeOutput(item.getTargetOutput());
        return new DatasetItem(input, output);
    }

    @Override
    public DatasetItem denormalize(DatasetItem item) {
        //x = x * (max - min) + min
        VectorN input = denormalizeInput(item.getInput());
        VectorN output = denormalizeOutput(item.getTargetOutput());
        return new DatasetItem(input, output);
    }

    @Override
    public VectorN normalizeInput(VectorN input) {
        //x = (x - min) / (max - min)
        VectorN min = summary.inputMinimumValues();

        VectorN denorm = input.copy();
        denorm.subtract(min);
        denorm.divide(inDivider);
        return denorm;
    }

    @Override
    public VectorN normalizeOutput(VectorN output) {
        //x = (x - min) / (max - min)
        VectorN min = summary.outputMinimumValues();

        VectorN denorm = output.copy();
        denorm.subtract(min);
        denorm.divide(outDivider);
        return denorm;
    }

    @Override
    public VectorN denormalizeInput(VectorN input) {
        VectorN min = summary.inputMinimumValues();

        VectorN denorm = input.copy();
        denorm.multiply(inDivider);
        denorm.add(min);
        return denorm;
    }

    @Override
    public VectorN denormalizeOutput(VectorN output) {
        VectorN min = summary.outputMinimumValues();

        VectorN denorm = output.copy();
        denorm.multiply(outDivider);
        denorm.add(min);
        return denorm;
    }
}
