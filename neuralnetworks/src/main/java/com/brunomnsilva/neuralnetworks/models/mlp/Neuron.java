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

import java.util.ArrayList;

/**
 * Models a MLP neuron.
 * <br/>
 * Each neuron has its own activation function, bias value and information about input synapses, i.e., connections to
 * neurons in a previous layer.
 *
 * @author brunomnsilva
 */
public abstract class Neuron {

    /** The activation function of the neuron **/
    private final ActivationFunction activationFunction;

    /** The bias of the neuron **/
    private double biasValue;


    /** The input value **/
    private double inputValue;
    /** The output value **/
    private double outputValue;
    /** The last output error value **/
    private double outputErrorValue;

    /** The dendrite (or soma) - connected synapses (inputs) to this neuron **/
    protected ArrayList<Synapse> soma = new ArrayList<>();

    public Neuron(ActivationFunction activationFunction, double biasValue) {
        this.activationFunction = activationFunction;
        this.biasValue = biasValue;
    }

    /**
     * Connects a synapse to this neuron. This is a signal-receiving synapse.
     * @param s the synapse
     */
    public void connectSynapse(Synapse s) {
        soma.add(s);
    }

    /**
     * Removes the specified synapse from this neuron
     * @param s the synapse to remove
     */
    public void removeSynapse(Synapse s) {
        soma.remove(s);
    }

    /**
     * Returns the connected (inbound) synapses in array-form.
     * @return the connected synapses
     */
    public Synapse[] getSynapses() {
        return soma.toArray(new Synapse[]{});
    }

    /**
     * Returns the current output of the neuron.
     * @return the current output of the neuron
     */
    public double getOutputValue() {
        return outputValue;
    }

    /**
     * Returns the current bias of this neuron.
     * @return the current bias of this neuron
     */
    public double getBiasValue() {
        return biasValue;
    }

    /**
     * Adjusts the bias of this neuron by the specified adjustment value.
     * <br/>
     * Bias is adjusted by <code>bias += adjustment;</code>
     * @param adjustment the adjustment value
     */
    public void adjustBias(double adjustment) {
        biasValue += adjustment;
    }

    /**
     * Method that sums the signals (values) received from connected synapses
     * together with the bias value. Stores this value internally.
     */
    protected void gatherInputs() {
        inputValue = 0; //reset input sum
        for(Synapse s : soma) {
            inputValue += s.getConductedValue();
        }
        inputValue += biasValue;
    }

    /**
     * Method that uses the activation function to transform the input signal
     * into the output signal.
     *
     * @see ActivationFunction
     */
    protected void generateOutput() {
        outputValue = activationFunction.compute(inputValue);
    }

    /**
     * Method that runs the neuron's behavior: runs {@link #gatherInputs()} and then {@link #generateOutput()}.
     */
    protected void process() {
        gatherInputs();
        generateOutput();
    }

    /**
     * Manually sets the input signal (value) for this neuron. This should be only done when a neuron
     * is not connected to any neurons in a previous layer, i.e., input neurons.
     * @param inputValue the input value to set
     */
    public void setInputValue(double inputValue) {
        this.inputValue = inputValue;
    }

    /**
     * Returns the current output error of the neuron. Used during training.
     * @return the current output error
     */
    public double getOutputErrorValue() {
        return outputErrorValue;
    }

    /**
     * Sets the current output error of the neuron. Used during training.
     * @param outputErrorValue the current output error
     */
    public void setOutputErrorValue(double outputErrorValue) {
        this.outputErrorValue = outputErrorValue;
    }

    /**
     * Returns the activation function of the neuron.
     * @return the activation function of the neuron
     */
    public final ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    @Override
    public String toString() {
        return String.format("[(%s;bias = %.2f)]",
                activationFunction.getClass().getSimpleName(),
                biasValue);
    }
}
