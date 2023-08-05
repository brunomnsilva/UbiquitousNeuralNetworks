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
 * <br/>
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 21.
 *
 * @author brunomnsilva
 */
public class ClassicLearning implements OfflineLearning {

    private final double iAlpha, fAlpha, iSigma, fSigma;
    private final int orderEpochs, convergenceEpochs;

    /**
     * Constructor that initializes the parameters of the training algorithm.
     * @param iAlpha the initial learning rate
     * @param fAlpha the final learning rate
     * @param iSigma the initial radius of the neighborhood function
     * @param fSigma the final radius of the neighborhood function
     * @param orderEpochs how many ordering epochs of the training algorithm are performed
     * @param convergenceEpochs how many convergence epochs of the training algorithm are performed
     */
    public ClassicLearning(double iAlpha, double fAlpha, double iSigma, double fSigma,
                           int orderEpochs, int convergenceEpochs) {
        Args.requireNonNegative(iAlpha, "iAlpha");
        Args.requireNonNegative(fAlpha, "fAlpha");
        Args.requireNonNegative(iSigma, "iSigma");
        Args.requireNonNegative(fSigma, "fSigma");
        Args.requireNonNegative(orderEpochs, "orderEpochs");
        Args.requireNonNegative(convergenceEpochs, "convergenceEpochs");

        this.iAlpha = iAlpha;
        this.fAlpha = fAlpha;
        this.iSigma = iSigma;
        this.fSigma = fSigma;
        this.orderEpochs = orderEpochs;
        this.convergenceEpochs = convergenceEpochs;
    }

    @Override
    public void train(SelfOrganizingMap som, Dataset dataset) {
        Args.requireEqual(som.dimensionality, "som.dimensionality",
                dataset.inputDimensionality(), "dataset.numberInputs()");

        int nSamples = dataset.size();
        int totalEpochs = orderEpochs + convergenceEpochs;
        int convergenceIterations = nSamples * convergenceEpochs;

        ConsoleProgressBar progress = new ConsoleProgressBar(totalEpochs);
        int epochCount = 1;

        /* Epoch training, recycling dataset training samples  */
        int currentConvergenceIteration = 0;
        for(int e = -orderEpochs + 1; e <= convergenceEpochs; ++e) {

            progress.update(epochCount++);

            for (DatasetItem item : dataset) {

                // Ordering phase: Keep learning parameters high
                // Convergence phase: Decrease learning parameters monotonically
                double alpha, sigma;
                if(e <= 0) {
                    alpha = iAlpha;
                    sigma = iSigma;
                } else {
                    alpha = DecayFunction.exponential(iAlpha, fAlpha, currentConvergenceIteration, convergenceIterations);
                    sigma = DecayFunction.exponential(iSigma, fSigma, currentConvergenceIteration, convergenceIterations);
                    currentConvergenceIteration++;
                }

                VectorN input = item.getInput();
                PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

                adjustPrototypes(som, bmu, input, alpha, sigma);
                som.prototypesUpdated();
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
