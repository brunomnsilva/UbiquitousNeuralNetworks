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

package com.brunomnsilva.neuralnetworks.models.artsom;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.ConsoleProgressBar;
import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.models.art.MicroCategory;
import com.brunomnsilva.neuralnetworks.models.art.MicroCategoryUtils;
import com.brunomnsilva.neuralnetworks.models.som.DecayFunction;
import com.brunomnsilva.neuralnetworks.models.som.NeighboringFunction;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

import java.util.Collection;

/**
 * A Batch learning algorithm, but where the input data consists in micro-categories from the StreamART2A model.
 * <br/>
 * The only different to the original Batch learning algorithm is that the prototype's adjustments are weighed by
 * the relative weights of the micro-categories.
 *
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 78.
 *
 * @see com.brunomnsilva.neuralnetworks.models.som.BatchLearning
 *
 * @author brunomnsilva
 */
public class MicroCategoryBatchLearning {

    private double iSigma, fSigma;
    private int orderEpochs, finetuneEpochs;

    /**
     * Constructor that initializes the parameters of the training algorithm.
     * @param iSigma the initial radius of the neighborhood function
     * @param fSigma the final radius of the neighborhood function
     * @param orderEpochs how many ordering epochs of the training algorithm are performed
     * @param fineTuneEpochs how many fine-tuning epochs of the training algorithm are performed
     */
    public MicroCategoryBatchLearning(double iSigma, double fSigma,
                         int orderEpochs, int fineTuneEpochs) {
        Args.requireNonNegative(iSigma, "iSigma");
        Args.requireNonNegative(fSigma, "fSigma");
        Args.requireNonNegative(orderEpochs, "orderEpochs");
        Args.requireNonNegative(fineTuneEpochs, "fineTuneEpochs");

        this.iSigma = iSigma;
        this.fSigma = fSigma;
        this.orderEpochs = orderEpochs;
        this.finetuneEpochs = fineTuneEpochs;
    }

    /**
     * Apply the parameterized learning algorithm to train a <code>som</code> with the collection
     * of <code>microCategories</code>.
     * @param som the self-organizing map to train
     * @param microCategories the micro-categories to train with
     */
    public void train(SelfOrganizingMap som, Collection<MicroCategory> microCategories) {
        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(microCategories, "microCategories");
        Args.requireGreaterEqualThan(microCategories.size(), "microCategories.size()", 1);

        MicroCategory first = microCategories.iterator().next(); // Get an example for validation
        Args.requireEqual(som.getDimensionality(), "som.getDimensionality()",
                first.getPrototype().dimensions(), "microCategory dimensionality");

        // Prepare accumulators
        VectorN[][] numerator = new VectorN[som.getWidth()][som.getHeight()];
        double[][] denominator = new double[som.getWidth()][som.getHeight()];
        for(int i=0; i < numerator.length; ++i) {
            for(int j=0; j < numerator[0].length; ++j) {
                numerator[i][j] = VectorN.zeros(som.getDimensionality());
                denominator[i][j] = 0; // redundant
            }
        }

        int totalEpochs = orderEpochs + finetuneEpochs;

        // Used for scaling the prototype's adjustments
        double maxWeight = MicroCategoryUtils.maximumWeightAmong(microCategories);

        /* Epoch training, recycling dataset training samples  */
        ConsoleProgressBar progress = new ConsoleProgressBar(totalEpochs);

        for(int e = 1; e <= totalEpochs; ++e) {

            progress.update(e);

            double sigma = DecayFunction.exponential(iSigma, fSigma, e, orderEpochs);

            for (MicroCategory category : microCategories) { // Epoch begin
                VectorN input = category.getPrototype();
                double categoryScaledWeight = category.getWeight() / maxWeight;

                PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

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

                    int x = neuron.getIndexX();
                    int y = neuron.getIndexY();

                    VectorN acc = input.copy();
                    acc.multiply( categoryScaledWeight * neigh ); //Scaled
                    numerator[x][y].add(acc);

                    denominator[x][y] += (categoryScaledWeight * neigh); //Scaled
                }
            } // Epoch end

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

            som.prototypesUpdated();
        }
    }
}
