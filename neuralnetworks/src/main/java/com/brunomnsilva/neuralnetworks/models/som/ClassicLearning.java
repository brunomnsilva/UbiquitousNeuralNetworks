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

import com.brunomnsilva.neuralnetworks.core.*;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;

/**
 * An implementation fo the Batch learning algorithm.
 * <br/>
 * The classic learning algorithm, sometimes referred as the <i>online</i> algorithm
 * adjusts the prototypes at each learning iteration (i.e., for each training input).
 * <br/>
 * It uses the Kohonen learning rule:
 * <br/>
 * W(t+1) = W(t) + alpha * neighboring_f * [ input - W(t) ]
 * <br/>
 * where <i>alpha</i> is the learning rate for the current iteration and
 * <i>neighboring_f</i> is the neighboring function that weighs the updates
 * based on the distance of a neuron to the best matching unit found at the
 * current iteration.
 *
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 21.
 *
 * @author brunomnsilva
 */
public class ClassicLearning implements OfflineLearning {

    private final double iAlpha, fAlpha, iSigma, fSigma;
    private final int orderEpochs, fineTuneEpochs;

    /**
     * Constructor that initializes the parameters of the training algorithm.
     * @param iAlpha the initial learning rate
     * @param fAlpha the final learning rate
     * @param iSigma the initial radius of the neighborhood function
     * @param fSigma the final radius of the neighborhood function
     * @param orderEpochs how many ordering epochs of the training algorithm are performed
     * @param fineTuneEpochs how many fine-tuning epochs of the training algorithm are performed
     */
    public ClassicLearning(double iAlpha, double fAlpha, double iSigma, double fSigma,
                           int orderEpochs, int fineTuneEpochs) {
        Args.requireNonNegative(iAlpha, "iAlpha");
        Args.requireNonNegative(fAlpha, "fAlpha");
        Args.requireNonNegative(iSigma, "iSigma");
        Args.requireNonNegative(fSigma, "fSigma");
        Args.requireNonNegative(orderEpochs, "orderEpochs");
        Args.requireNonNegative(fineTuneEpochs, "fineTuneEpochs");

        this.iAlpha = iAlpha;
        this.fAlpha = fAlpha;
        this.iSigma = iSigma;
        this.fSigma = fSigma;
        this.orderEpochs = orderEpochs;
        this.fineTuneEpochs = fineTuneEpochs;
    }

    @Override
    public void train(SelfOrganizingMap som, Dataset dataset) {
        Args.requireEqual(som.dimensionality, "som.dimensionality",
                dataset.inputDimensionality(), "dataset.numberInputs()");

        int nSamples = dataset.size();
        int totalEpochs = orderEpochs + fineTuneEpochs;
        int orderIterations = nSamples * orderEpochs;
        int totalIterations = totalEpochs * nSamples;

        /* Epoch training, recycling dataset training samples  */
        ConsoleProgressBar progress = new ConsoleProgressBar(totalEpochs);

        int currentIteration = 0;
        for(int e = 1; e <= totalEpochs; ++e) {

            progress.update(e);

            for (DatasetItem item : dataset) {
                double alpha = DecayFunction.exponential(iAlpha, fAlpha, currentIteration, orderIterations);
                double sigma = DecayFunction.exponential(iSigma, fSigma, currentIteration, orderIterations);

                // Here is where the decay of the learning rate and neighborhood distance are controlled.
                // If we want a decay across all iterations (instead of order/tuning), we can do, e.g.:
                //double alpha = DecayFunction.exponential(iAlpha, fAlpha, currentIteration, totalIterations);
                //double sigma = DecayFunction.linear(iSigma, fSigma, currentIteration, totalIterations);

                VectorN input = item.getInput();
                PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

                adjustPrototypes(som, bmu, input, alpha, sigma);

                som.prototypesUpdated();

                currentIteration++;
            }
        }

    }

    private void adjustPrototypes(SelfOrganizingMap som, PrototypeNeuron bmu, VectorN input, double alpha, double sigma) {

        for (PrototypeNeuron neuron : som) {
            double dist = som.latticeDistanceBetween(bmu, neuron);
            double neigh = NeighboringFunction.gaussian(dist, sigma);
            //double neigh = NeighboringFunction.bubble(dist, sigma);

            // Expecting a finite [0, 1] neighborhood function value.
            // We discard updates when the function value is < 0.01 - this has a negligible
            // influence the on final result and is much more performant during convergence phase
            if (neigh > 1 || neigh < 0.01 || Double.isInfinite(neigh) || Double.isInfinite(-neigh)) {
                continue;
            }

            // Kohonen learning rule:
            // W(t+1) = W(t) + alpha * gauss * [ input - W(t) ]

            VectorN delta = input.copy();
            delta.subtract(neuron.getPrototype());
            delta.multiply( alpha * neigh);

            neuron.getPrototype().add(delta);
        }
    }
}
