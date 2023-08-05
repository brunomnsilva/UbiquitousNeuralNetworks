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

package com.brunomnsilva.neuralnetworks.models.som;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.ConsoleProgressBar;
import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;

/**
 * An implementation fo the Batch learning algorithm.
 * <br/>
 * In Batch learning the contributions from each training input is accumulated
 * for each neuron. Only at the end of each training epoch are the prototypes
 * adjusted.
 * <br/>
 * The Batch learning algorithm does not use a <i>learning rate</i> parameter.
 * <br/>
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 26.
 *
 * @author brunomnsilva
 */
public class BatchLearning implements OfflineLearning {

    private final double iSigma, fSigma;
    private final int orderEpochs, convergenceEpochs;

    /**
     * Constructor that initializes the parameters of the training algorithm.
     * @param iSigma the initial radius of the neighborhood function
     * @param fSigma the final radius of the neighborhood function
     * @param orderEpochs how many ordering epochs of the training algorithm are performed
     * @param convergenceEpochs how many convergence epochs of the training algorithm are performed
     */
    public BatchLearning(double iSigma, double fSigma,
                         int orderEpochs, int convergenceEpochs) {
        Args.requireNonNegative(iSigma, "iSigma");
        Args.requireNonNegative(fSigma, "fSigma");
        Args.requireNonNegative(orderEpochs, "orderEpochs");
        Args.requireNonNegative(convergenceEpochs, "convergenceEpochs");

        this.iSigma = iSigma;
        this.fSigma = fSigma;
        this.orderEpochs = orderEpochs;
        this.convergenceEpochs = convergenceEpochs;
    }

    @Override
    public void train(SelfOrganizingMap som, Dataset dataset) {
        Args.requireEqual(som.dimensionality, "som.dimensionality",
                dataset.inputDimensionality(), "dataset.numberInputs()");

        // Prepare accumulators
        VectorN[][] numerator = new VectorN[som.getWidth()][som.getHeight()];
        double[][] denominator = new double[som.getWidth()][som.getHeight()];
        for(int i=0; i < numerator.length; ++i) {
            for(int j=0; j < numerator[0].length; ++j) {
                numerator[i][j] = VectorN.zeros(som.getDimensionality());
                denominator[i][j] = 0; // redundant, but explicit
            }
        }

        int totalEpochs = orderEpochs + convergenceEpochs;

        ConsoleProgressBar progress = new ConsoleProgressBar(totalEpochs);
        int epochCount = 1;

        /* Epoch training, recycling dataset training samples  */
        for(int e = -orderEpochs + 1; e <= convergenceEpochs; ++e) {

            progress.update(epochCount++);

            // Ordering phase: Keep learning parameters high
            // Convergence phase: Decrease learning parameters monotonically
            double sigma;
            if(e <= 0) {
                sigma = iSigma;
            } else {
                sigma = DecayFunction.exponential(iSigma, fSigma, e, convergenceEpochs);
            }

            for (DatasetItem item : dataset) { // Epoch begin
                VectorN input = item.getInput();

                PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

                for (PrototypeNeuron neuron : som) {
                    double dist = som.latticeDistanceBetween(bmu, neuron);
                    double neigh = NeighboringFunction.gaussian(dist, sigma);

                    // Expecting a finite [0, 1] neighborhood function value.
                    // We discard updates when the function value is < 0.01 - this has a negligible
                    // influence the on final result and is much more performant during convergence phase
                    if (neigh > 1 || neigh < 0.01 || Double.isInfinite(neigh) || Double.isInfinite(-neigh)) {
                        continue;
                    }

                    int x = neuron.getIndexX();
                    int y = neuron.getIndexY();

                    VectorN acc = input.copy();
                    acc.multiply(neigh);
                    numerator[x][y].add(acc);

                    denominator[x][y] += neigh;
                }
            } // Epoch end

            adjustPrototypes(som, numerator, denominator);

            som.prototypesUpdated();
        }
    }

    private void adjustPrototypes(SelfOrganizingMap som, VectorN[][] numerator, double[][] denominator) {
        // Adjust prototypes from accumulators
        // and reset the later ones for the next epoch
        for(int i=0; i < numerator.length; ++i) {
            for(int j=0; j < numerator[0].length; ++j) {
                if(denominator[i][j] > 0) {
                    VectorN prototype = som.get(i, j).getPrototype();
                    prototype.fill(0); // Just to avoid new allocation
                    prototype.add(numerator[i][j]);
                    prototype.divide(denominator[i][j]);
                }

                // Reset accumulators
                numerator[i][j].fill(0);
                denominator[i][j] = 0;
            }
        }
    }
}
