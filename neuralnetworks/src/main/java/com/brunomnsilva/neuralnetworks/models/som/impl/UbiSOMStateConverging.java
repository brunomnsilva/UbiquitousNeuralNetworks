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

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;

/**
 * A concrete UbiSOMStateConverging state for the Ubiquitous Self-Organizing Map (UbiSOM).
 *
 * More details can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 111.
 *
 * @author brunomnsilva
 */
public class UbiSOMStateConverging extends UbiSOMState {

    private final double alpha0;
    private final double sigma0;
    private final double alphaF;
    private final double sigmaF;
    private final int T;

    private double sigmaMagnification;
    private double alphaMagnification;
    private double driftThreshold;

    //variables that control out-of-control or activity decrease
    private int parametersHighCount = 0;

    /**
     * Default constructor.
     * @param model the context
     * @param alpha0 value of parameter alpha_0. Used in the UbiSOM instantiation.
     * @param alphaF value of parameter alpha_f. Used in the UbiSOM instantiation.
     * @param sigma0 value of parameter sigma_0. Used in the UbiSOM instantiation.
     * @param sigmaF value of parameter sigma_f. Used in the UbiSOM instantiation.
     * @param T value of parameter T. Used in the UbiSOM instantiation.
     * @param driftThreshold maximum drift value allowed
     */
    public UbiSOMStateConverging(UbiSOM model, double alpha0, double alphaF, double sigma0, double sigmaF, int T, double driftThreshold) {
        super(model);

        this.alpha0 = alpha0;
        this.alphaF = alphaF;
        this.sigma0 = sigma0;
        this.sigmaF = sigmaF;
        this.T = T;

        this.driftThreshold = driftThreshold;
        this.sigmaMagnification = sigmaF / driftThreshold;
        this.alphaMagnification = alphaF / driftThreshold;
    }


    @Override
    public void process(PrototypeNeuron bmu, VectorN input) {

        double drift = getModel().getCurrentDriftValue();

        double alpha = computeAlpha(drift);
        double sigma = computeSigma(drift);

        getModel().adjustWeights(bmu, input, alpha, sigma);

        // Track sustained 'sigma == sigmaF' during T iterations.
        // If so, the model can't seem to converge to the underlying distribution.
        // Hence, we transition to ordering state.
        if (compareEquality(sigma, sigmaF)) {
            parametersHighCount++;

            if (parametersHighCount >= T) {
                getModel().orderingState();
            }
        } else { // TODO: Remove else and place return at 'if' level?
            parametersHighCount = 0;
        }
    }

    private double computeAlpha(double drift) {
        if (drift > driftThreshold) {
            return alphaF;
        }

        return drift * alphaMagnification;
    }

    private double computeSigma(double drift) {
        if (drift > driftThreshold) {
            return sigmaF;
        }

        return drift * sigmaMagnification;
    }

    private boolean compareEquality(double a, double b) {
        return Math.abs(a - b) < 0.0001; //round-off errors
    }

    @Override
    public String toString() {
        return "UbiSOMStateConverging{" +
                "alpha0=" + alpha0 +
                ", sigma0=" + sigma0 +
                ", alphaF=" + alphaF +
                ", sigmaF=" + sigmaF +
                ", T=" + T +
                ", sigmaMagnification=" + sigmaMagnification +
                ", alphaMagnification=" + alphaMagnification +
                ", driftThreshold=" + driftThreshold +
                ", parametersHighCount=" + parametersHighCount +
                '}';
    }
}
