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
import com.brunomnsilva.neuralnetworks.models.som.DecayFunction;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;

/**
 * A concrete UbiSOMStateOrdering state for the Ubiquitous Self-Organizing Map (UbiSOM).
 *
 * More details can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 111.
 *
 * @author brunomnsilva
 */
public class UbiSOMStateOrdering extends UbiSOMState {

    private final double alpha0;
    private final double alphaF;
    private final double sigma0;
    private final double sigmaF;
    private final int T; // dictates this state duration, in iterations.
    private int processedIterations;

    /**
     * Default constructor.
     * @param model the context
     * @param alpha0 value of parameter alpha_0. Used in the UbiSOM instantiation.
     * @param alphaF value of parameter alpha_f. Used in the UbiSOM instantiation.
     * @param sigma0 value of parameter sigma_0. Used in the UbiSOM instantiation.
     * @param sigmaF value of parameter sigma_f. Used in the UbiSOM instantiation.
     * @param T value of parameter T. Used in the UbiSOM instantiation.
     */
    public UbiSOMStateOrdering(UbiSOM model, double alpha0, double alphaF, double sigma0, double sigmaF, int T) {
        super(model);
        this.alpha0 = alpha0;
        this.alphaF = alphaF;
        this.sigma0 = sigma0;
        this.sigmaF = sigmaF;
        this.T = T;

        this.processedIterations = 0;
    }

    @Override
    public void process(PrototypeNeuron bmu, VectorN input) {
        processedIterations++;

        double alpha = DecayFunction.exponential(alpha0, alphaF, processedIterations, T);
        double sigma = DecayFunction.exponential(sigma0, sigmaF, processedIterations, T);

        getModel().adjustWeights(bmu, input, alpha, sigma);

        // Check if state duration ended to change state
        if( processedIterations >= T) {
            // Transition to ordering state
            getModel().convergingState();
        }
    }

    @Override
    public String toString() {
        return "UbiSOMStateOrdering{" +
                "alpha0=" + alpha0 +
                ", alphaF=" + alphaF +
                ", sigma0=" + sigma0 +
                ", sigmaF=" + sigmaF +
                ", T=" + T +
                ", processedIterations=" + processedIterations +
                '}';
    }
}
