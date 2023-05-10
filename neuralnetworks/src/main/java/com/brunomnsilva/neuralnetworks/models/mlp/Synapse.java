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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.brunomnsilva.neuralnetworks.models.mlp;

import java.util.Random;

/**
 * Models a synapse in a MLP network.
 * <br/>
 * A synapse connects two neurons: a <i>source</i> to a <i>sink</i> and conducts a value that is weighed by the synapse <code>strength</code>.
 * This strength is initialized before training and is adjusted during training, e.g., backpropagation.
 *
 * @see Backpropagation
 *
 * @author brunomnsilva
 */
public class Synapse {

    public static final double MIN_INIT_STRENGTH = -0.3; //-1.05;
    public static final double MAX_INIT_STRENGTH = 0.3; //1.05;

    private static final Random rand = new Random();

    /** The source of the synapse **/
    private Neuron source;

    /** The synapse sink **/
    private Neuron sink;

    /** The strength of the synapse **/
    private double strength;


    /**
     * Creates a synapse that connects two neurons with a random weight.
     * @param source the source of the synapse
     * @param sink the sink of the synapse
     */
    public Synapse(Neuron source, Neuron sink) {
        this.source = source;
        this.sink = sink;
        strength = (MAX_INIT_STRENGTH - MIN_INIT_STRENGTH) * rand.nextDouble() + MIN_INIT_STRENGTH;
    }

    /**
     * Creates a synapse that connects two neurons with a random weight.
     * @param source the source of the synapse
     * @param sink the sink of the synapse
     * @param strength the initial strength of the synapse
     */
    public Synapse(Neuron source, Neuron sink, double strength) {
        this(source, sink);
        this.strength = strength;
    }

    /**
     * Returns the conducted value of the synapse. This value corresponds to the output value of its <i>source</i>,
     * weighed by the <code>strength</code> of the synapse.
     * @see Neuron#getOutputValue()
     * @see #getStrength()
     * @return
     */
    public double getConductedValue() {
        return getSource().getOutputValue() * strength;
    }

    /**
     * Returns the current strength (weight) of this synapse.
     * @return the current strength (weight) of this synapse
     */
    public double getStrength() {
        return strength;
    }

    /**
     * Manually sets the current strength (weight) of this synapse.
     * @param strength the strength (weight)
     */
    public void setStrength(double strength) {
        this.strength = strength;
    }

    /**
     * Adjusts the current strength.
     * <br/>
     * The new strength value is computed by <code>strength += adjustment</code>.
     *
     * @param adjustment the adjustment value
     */
    public void adjustStrength(double adjustment) {
        this.strength += adjustment;
    }

    /**
     * Returns the source of this synapse.
     * @return the source of this synapse
     */
    public Neuron getSource() {
        return source;
    }

    /**
     * Returns the sink of this synapse.
     * @return the sink of this synapse
     */
    public Neuron getSink() {
        return sink;
    }

    public String toString() {
        return String.format("%s -- {strength = %.6f | conducted = %.6f} -- %s",
                source, strength, getConductedValue(), sink);
    }
}
