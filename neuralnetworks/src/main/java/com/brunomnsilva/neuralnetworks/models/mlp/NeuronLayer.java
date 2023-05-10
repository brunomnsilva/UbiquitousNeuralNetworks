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
 * A MLP layer of neurons.
 *
 * @author brunomnsilva
 */
public abstract class NeuronLayer {

    private final ArrayList<Neuron> members;

    public NeuronLayer() {
        members = new ArrayList<>();
    }

    /**
     * Checks to see if the neuron can be added to this layer.
     * Must be of the same type of the layer.
     * @param neuron the neuron to check
     */
    public abstract boolean isValidNeuron(Neuron neuron);

    /**
     * Processes all the neurons from this layer
     */
    public void process() {
        for(Neuron neuron : members)
            neuron.process();
    }

    /**
     * Returns the size of the layer.
     * @return the size of the layer
     */
    public int size() {
        return members.size();
    }

    /**
     * Method to obtain the neurons of this layer in array-fashion.
     *
     * @return an array with all neurons of this layer
     */
    public Neuron[] getMembers() {
        Neuron[] list = new Neuron[members.size()];
        return members.toArray(list);
    }

    /**
     * Adds a neuron to this layer.
     * @param neuron the neuron to add
     */
    public void addNeuron(Neuron neuron) {
        if(!isValidNeuron(neuron))
            throw new IllegalArgumentException("Neuron is not compatible with layer type");

        members.add(neuron);
    }

    /**
     * Removes a neuron from this layer.
     * @param neuron the neuron to remove
     */
    public void removeNeuron(Neuron neuron) {
        members.remove(neuron);
    }

    /**
     * Returns the output of the members of this layer
     * @return the output
     */
    public double[] getOutput() {
        double[] out = new double[members.size()];
        for(int i=0; i<out.length;i++) {
            out[i] = members.get(i).getOutputValue();
        }
        return out;
    }



    @Override
    public String toString() {
        // In our implementation, each layer is composed by the same "type" of neurons.
        // Hence, we output a summary "<LayerType> : <number> x <neuronStr>"
        if( members.isEmpty() )
            return String.format("%-12s : (empty layer)",  this.getClass().getSimpleName());
        else {
            Neuron neuronExample = members.get(0);
            return String.format("%-12s : %3d x [%s; bias = %.2f]",
                    this.getClass().getSimpleName(),
                    members.size(),
                    neuronExample.getActivationFunction().getClass().getSimpleName(),
                    neuronExample.getBiasValue());
        }

    }
}
