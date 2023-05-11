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
 * A mean-centered and standard deviation of one dataset normalization implementation.
 * <br/>
 * In reality this is "standardization", which involves scaling the input feature values to have a mean of zero and a standard deviation of one.
 *
 * @author brunomnsilva
 */
public class MeanNormalization extends DatasetNormalization {

    private final VectorN meanInput, meanOutput, stdDevInput, stdDevOutput;

    /**
     * Creates a new instance of MeanNormalization.
     * @param dataset the Dataset to compute normalization information from
     */
    public MeanNormalization(Dataset dataset) {
        Args.nullNotPermitted(dataset, "dataset");

        // Compute the mean
        meanInput = VectorN.zeros( dataset.inputDimensionality() );
        meanOutput = VectorN.zeros( dataset.outputDimensionality() );

        for (DatasetItem item : dataset) {
            meanInput.add( item.getInput() );
            meanOutput.add( item.getTargetOutput() );
        }

        meanInput.divide( dataset.size() );
        meanOutput.divide( dataset.size() );

        // Compute the standard deviation
        VectorN stdDevTempInput = VectorN.zeros( dataset.inputDimensionality() );
        VectorN stdDevTempOutput = VectorN.zeros( dataset.outputDimensionality() );

        for (DatasetItem item : dataset) {
            VectorN devIn = item.getInput().copy();
            devIn.subtract(meanInput);
            devIn.multiply(devIn);
            stdDevTempInput.add( devIn );

            VectorN devOut = item.getTargetOutput().copy();
            devOut.subtract(meanOutput);
            devOut.multiply(devOut);
            stdDevTempOutput.add( devOut );
        }

        stdDevTempInput.divide( dataset.size() );
        stdDevTempOutput.divide( dataset.size() );

        double[] stdIn = new double[ stdDevTempInput.dimensions() ];
        for(int i=0; i < stdIn.length; ++i) {
            double v = stdDevTempInput.get(i);
            v = Math.sqrt(v);
            stdIn[i] = v;
        }
        stdDevInput = VectorN.fromArray(stdIn);

        double[] stdOut = new double[ stdDevTempOutput.dimensions() ];
        for(int i=0; i < stdOut.length; ++i) {
            double v = stdDevTempOutput.get(i);
            v = Math.sqrt(v);
            stdOut[i] = v;
        }
        stdDevOutput = VectorN.fromArray(stdOut);
    }

    @Override
    public void normalize(Dataset dataset) {
        // Adjust data to mean values
        for (DatasetItem item : dataset) {
            item.getInput().subtract(meanInput);
            item.getInput().divide(stdDevInput);

            item.getTargetOutput().subtract(meanOutput);
            item.getTargetOutput().divide(stdDevOutput);
        }
    }

    @Override
    public void denormalize(Dataset dataset) {
        for (DatasetItem item : dataset) {
            item.getInput().multiply(stdDevInput);
            item.getInput().add(meanInput);

            item.getTargetOutput().multiply(stdDevOutput);
            item.getTargetOutput().add(meanOutput);
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
        VectorN input = denormalizeInput(item.getInput());
        VectorN output = denormalizeOutput(item.getTargetOutput());

        return new DatasetItem(input, output);
    }

    @Override
    public VectorN normalizeInput(VectorN input) {
        VectorN norm = input.copy();
        norm.subtract(meanInput);
        norm.divide(stdDevInput);
        return norm;
    }

    @Override
    public VectorN normalizeOutput(VectorN output) {
        VectorN norm = output.copy();
        norm.subtract(meanOutput);
        norm.divide(stdDevOutput);
        return norm;
    }

    @Override
    public VectorN denormalizeInput(VectorN input) {
        VectorN norm = input.copy();
        norm.multiply(stdDevInput);
        norm.add(meanInput);
        return norm;
    }

    @Override
    public VectorN denormalizeOutput(VectorN output) {
        VectorN norm = output.copy();
        norm.multiply(stdDevOutput);
        norm.add(meanOutput);
        return norm;
    }
}
