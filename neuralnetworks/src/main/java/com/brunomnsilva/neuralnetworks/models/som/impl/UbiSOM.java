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

package com.brunomnsilva.neuralnetworks.models.som.impl;

import com.brunomnsilva.neuralnetworks.core.*;
import com.brunomnsilva.neuralnetworks.models.som.*;

/**
 * The Ubiquitous Self-Organizing Map (UbiSOM).
 * <br/>
 * This algorithm was one of the main contributions of the PhD thesis. It estimates its learning parameters according
 * to its own monitoring of the model's fit to the possibly drifting underlying distribution.
 * <br/>
 * The algorithm is relatively easy to understand, but has several "moving parts". So it's best to understand its
 * behavior by reading its chapter in my thesis.
 *
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 101.
 *
 * @author brunomnsilva
 */
public class UbiSOM extends StreamingSOM {

    private final long[][] activityTimestamps;
    private final long[][] bmuTimestamps;
    private final double latticeDiagonal;
    private final double dimensionalityRatio;
    private final double alpha_0, alpha_f, sigma_0, sigma_f, beta;
    private final int T;

    private UbiSOMState currentState;

    private final AbstractRunningMeanFilter quantizationErrorRunningMean;
    private final AbstractRunningMeanFilter activityRunningMean;
    private final AbstractRunningMeanFilter driftRunningMean;

    public UbiSOM(int width, int height, int dimensionality,
                  Lattice lattice, MetricDistance metricDistance,
                  double alpha_0, double alpha_f, double sigma_0, double sigma_f, double beta, int T) {

        super(width, height, dimensionality, lattice, metricDistance);

        Args.requireNonNegative(alpha_0, "alpha_0");
        Args.requireNonNegative(alpha_f, "alpha_f");
        Args.requireNonNegative(sigma_0, "sigma_0");
        Args.requireNonNegative(sigma_f, "sigma_");
        Args.requireNonNegative(beta, "beta");
        Args.requireNonNegative(T, "T");

        this.alpha_0 = alpha_0;
        this.alpha_f = alpha_f;
        this.sigma_0 = sigma_0;
        this.sigma_f = sigma_f;
        this.beta = beta;
        this.T = T;

        activityTimestamps  = new long[width][height];
        bmuTimestamps       = new long[width][height];

        this.latticeDiagonal = StrictMath.sqrt( (width - 1)*(width - 1) + (height - 1)*(height - 1) );
        this.dimensionalityRatio = StrictMath.sqrt(dimensionality);

        this.quantizationErrorRunningMean   = new TripleCascadedMeanFilter("Mean QE", T);
        this.activityRunningMean            = new SimpleRunningMeanFilter("Mean Activity", T);
        this.driftRunningMean               = new TripleCascadedMeanFilter("Drift", T);

        orderingState();
    }

    public UbiSOM(int width, int height, int dimensionality,
                  double alpha_0, double alpha_f, double sigma_0, double sigma_f, double beta, int T) {

        this(width, height, dimensionality, new SimpleHexagonalLattice(), new EuclideanDistance(),
                alpha_0,alpha_f, sigma_0, sigma_f, beta, T);
    }

    public void orderingState() {
        int width = getWidth();
        int height = getHeight();

        // Reset activity timestamps
        for(int w=0; w < width; ++w) {
            for(int h=0; h < height; ++h) {
                // Reset (randomize) prototypes
                get(w, h).getPrototype().randomize();

                // Reset timestamps
                activityTimestamps[w][h] = 0;
                bmuTimestamps[w][h] = 0;
            }
        }
        // Transition to ordering state
        setState(new UbiSOMStateOrdering(this, alpha_0, alpha_f,sigma_0, sigma_f, T));
    }

    public void convergingState() {

        //System.out.println(driftRunningMean);

        // Get current drift value.
        // This will serve as a drift threshold for the convergence state, for which we'll transition next.
        double currentDrift = driftRunningMean.lastOutput();

        // Transition to converging state
        setState(new UbiSOMStateConverging(this, alpha_0, alpha_f,sigma_0, sigma_f, T, currentDrift));
    }

    private void setState(UbiSOMState newState) {
        this.currentState = newState;
    }

    @Override
    public void learn(VectorN input) {
        PrototypeNeuron bmu = bestMatchingUnitFor(input);

        currentState.process(bmu, input);
    }

    @Override
    public String getImplementationName() {
        return "UbiSOM";
    }

    protected void adjustWeights(PrototypeNeuron bmu, VectorN input, double alpha, double sigma) {
        // This method is called by the 'currentState' to effectively modify the SOM model.
        // Each state merely computes the 'alpha' and 'sigma' values to use.

        // I changed my original implementation (where the timestamp was the current iteration number) to:
        // - Set a timestamp of 0 (zero) to activated neurons during this learning iteration;
        // - Decrement the timestamp of all others (i.e., those unaffected by prototype adjustment)
        //
        // Before (un-refactored implementation), the activity value of the lattice was computed during the BMU computation;
        // Now it is being performed during these prototype adjustments.
        final long activatedTimestamp = 0;

        // Timestamp the BMU
        int bmuX = bmu.getIndexX();
        int bmuY = bmu.getIndexY();
        bmuTimestamps[bmuX][bmuY] = activatedTimestamp;

        // During the processing of each neuron, we must check the "recency" of activation during the
        // last T iterations. We then compute the overall score, divided by the total number of neurons.
        // In this refactored implementation, neurons whose timestamp > -T are deemed "activated" during T window size
        int activeNeuronCount = 0;
        double totalNeuronCount = width * height;

        for (PrototypeNeuron neuron : this) {

            int x = neuron.getIndexX();
            int y = neuron.getIndexY();

            // Activity monitoring of previous weight adjustment
            if(activityTimestamps[x][y] > -T) {
                activeNeuronCount++;
            }

            // Weight adjustment
            double dist = latticeDistanceBetween(bmu, neuron);
            double scaledSigma = (sigma * (this.latticeDiagonal));

            double neigh = NeighboringFunction.gaussian(dist, scaledSigma);

            if( neigh > 1 || neigh < 0.01 || Double.isInfinite(neigh) || Double.isInfinite(-neigh)) {
                // Decrement timestamp of unaffected neuron and do not adjust the prototype
                activityTimestamps[x][y]--;
                continue;
            }

            // Timestamp the activated neuron
            activityTimestamps[x][y] = activatedTimestamp;
            // Decrement the BMU activation, if not the BMU
            if(x != bmuX && y != bmuY) {
                bmuTimestamps[x][y]--;
            }

            // Adjust prototype
            VectorN delta = input.copy();
            delta.subtract(neuron.getPrototype());
            delta.multiply( alpha * neigh);

            neuron.getPrototype().add(delta);
        }

        // Self-monitoring processing of model state regarding the underlying distribution
        double quantizationError = getMetricDistance().distanceBetween(bmu.getPrototype(), input);
        double normalizedQE = quantizationError / dimensionalityRatio;
        double qe = quantizationErrorRunningMean.filter( normalizedQE );

        double currentActivity = activeNeuronCount / totalNeuronCount;
        double activity = activityRunningMean.filter(currentActivity);

        // Refactoring necessary change: during ordering phase do not take into account
        // the running mean values for QE and neuron Activity for the drift function computation
        // since they will be average by the window length T, resulting in lower values.
        // This is due to the new implementation of the running means that always
        // return a value, even when the buffers aren't full.
        if(currentState instanceof UbiSOMStateOrdering) {
            activity = 1;
            qe = normalizedQE;
        }

        double driftValue = (this.beta * qe) + ( (1 - this.beta) * (1 - activity) );
        driftRunningMean.filter(driftValue);

        prototypesUpdated();
    }

    public long getTimestampBMU(int x, int y) {
        Args.requireInRange(x, "x", 0, width - 1);
        Args.requireInRange(x, "y", 0, height - 1);

        return bmuTimestamps[x][y];
    }

    public long getTimestampActivity(int x, int y) {
        Args.requireInRange(x, "x", 0, width - 1);
        Args.requireInRange(x, "y", 0, height - 1);

        return activityTimestamps[x][y];
    }

    public double getCurrentDriftValue() {
        return driftRunningMean.lastOutput();
    }

    public double getCurrentActivityValue() {
        return activityRunningMean.lastOutput();
    }

    public double getCurrentQuantizationErrorValue() {
        return quantizationErrorRunningMean.lastOutput();
    }
}
