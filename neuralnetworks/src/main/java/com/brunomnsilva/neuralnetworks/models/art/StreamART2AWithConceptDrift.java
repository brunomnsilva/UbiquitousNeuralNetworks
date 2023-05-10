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

package com.brunomnsilva.neuralnetworks.models.art;

import com.brunomnsilva.neuralnetworks.core.*;

import java.util.Collection;

/**
 * An extension of the StreamART2A algorithm that continuously monitors the fit of the model to the
 * underlying distribution. The fit is calculated by using a running mean filter across the quantization errors
 * of the incoming inputs and the contents of its <code>codebookStorage</code>.
 * <br/>
 * More details can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 79.
 *
 * @see StreamART2A
 *
 * @author brunomnsilva
 */
public class StreamART2AWithConceptDrift extends StreamART2A {

    /**
     * The running mean filter to filter quantization errors.
     */
    private AbstractRunningMeanFilter codebookStorageQuantizationError;

    /**
     * The TimeSeries that holds the running mean filtered values.
     */
    private TimeSeries qeTimeSeries;

    /**
     * Creates a new StreamART2AWithConceptDrift model.
     * @param dimensionality the dimensionality of the input space
     * @param dmin the minimum value of all input space variables
     * @param dmax the maximum value of all input space variables
     * @param learningRate the learning rate for prototype adjustments
     * @param landmarkWindowSize the landmark window size
     * @param q the parameter <code>q</code> - maximum number of micro-categories within a landmark window
     * @param K the parameter <code>K</code> - maximum size of the model (in micro-categories)
     */
    public StreamART2AWithConceptDrift(int dimensionality, double dmin, double dmax, double learningRate, int landmarkWindowSize, int q, int K) {
        super(dimensionality, dmin, dmax, learningRate, landmarkWindowSize, q, K);

        this.codebookStorageQuantizationError = new SimpleRunningMeanFilter("QE", landmarkWindowSize);
        this.qeTimeSeries = new TimeSeries("Running QE");
    }

    @Override
    public void learn(VectorN input) {
        super.learn(input);

        Collection<MicroCategory> codebook = super.getCodebook();
        if(codebook.isEmpty()) return;

        // Find the closest prototype
        double minDist = Double.MAX_VALUE;
        for (MicroCategory category : codebook) {
            double curDist = input.distance( category.getPrototype() );
            if(curDist < minDist) {
                minDist = curDist;
            }
        }

        double filteredValue = codebookStorageQuantizationError.filter(minDist / inputManifold);
        qeTimeSeries.append(learnInputCount, filteredValue);
    }

    /**
     * Returns the TimeSeries with the mean quantization errors over time.
     * @return the TimeSeries with the mean quantization errors over time
     */
    public TimeSeries getQuantizationErrorTimeSeries() {
        return qeTimeSeries;
    }
}
