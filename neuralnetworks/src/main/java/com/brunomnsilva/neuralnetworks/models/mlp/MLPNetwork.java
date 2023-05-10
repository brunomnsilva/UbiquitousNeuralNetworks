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

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.VectorN;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * This class represents a MLP (Multilayer Perceptron) network.
 *
 * @author brunomnsilva
 */
public class MLPNetwork {

    /**
     * A builder for the MLPNetwork. All instances of MLPNetwork are created solely through this builder.
     * <br/>
     * It is mandatory that the network has at least one input layer and one output layer.
     */
    public static class Builder {

        private LayerInfo inputLayerInfo;
        private LayerInfo outputLayerInfo;
        private final List<LayerInfo> hiddenLayersInfo;
        private double synapseMinInitialWeight = Synapse.MIN_INIT_STRENGTH;
        private double synapseMaxInitialWeight = Synapse.MAX_INIT_STRENGTH;

        /**
         * Default constructor that initializes the builder.
         */
        public Builder() {
            hiddenLayersInfo = new ArrayList<>();
        }

        /**
         * Adds an input layer to the network.
         * @param size the number of neurons of the layer
         * @return the updated builder
         */
        public Builder addInputLayer(int size) {
            Args.requireGreaterEqualThan(size, "size", 1);

            if(inputLayerInfo != null)
                throw new IllegalArgumentException("Only one input layer allowed.");

            inputLayerInfo = new LayerInfo(size, LinearActivation.class, 0);

            return this;
        }

        /**
         * Adds a hidden layer to the network.
         * <br/>
         * Hidden layers are added by successive calls to this method. The ordering of the layers in the network shall
         * follow the ordering of the calls to this method.
         * @param size the number of neurons of the layer
         * @param neuronActivation the activation function for all neurons of this network
         * @param neuronBias the initial bias for the neurons of this network
         * @return the updated builder
         */
        public Builder addHiddenLayer(int size, Class<? extends ActivationFunction> neuronActivation, double neuronBias) {
            Args.requireGreaterEqualThan(size, "size", 1);

            LayerInfo layerInfo = new LayerInfo(size, neuronActivation, neuronBias);

            hiddenLayersInfo.add(layerInfo);

            return this;
        }

        /**
         * Adds an output layer to the network.
         * @param size the number of neurons of the layer
         * @param neuronActivation the activation function for all neurons of this network
         * @param neuronBias the initial bias for the neurons of this network
         * @return the updated builder
         */
        public Builder addOutputLayer(int size, Class<? extends ActivationFunction> neuronActivation, double neuronBias) {
            Args.requireGreaterEqualThan(size, "size", 1);

            if(outputLayerInfo != null)
                throw new IllegalArgumentException("Only one output layer allowed.");

            outputLayerInfo = new LayerInfo(size, neuronActivation, neuronBias);

            return this;
        }

        /**
         * Sets interval of values from which the synapses weights are to be initialized.
         * @param min minimum value
         * @param max maximum value
         * @return the updated builder
         */
        public Builder weightsInitializedBetween(double min, double max) {
            Args.requireFinite(min, "min");
            Args.requireFinite(max, "max");
            Args.requireGreaterThan(max, "max", min);

            synapseMinInitialWeight = min;
            synapseMaxInitialWeight = max;

            return this;
        }

        /**
         * Creates a new MLPNetwork with the specified architecture.
         * @return a new MLPNetwork with the specified architecture
         */
        public MLPNetwork build() {
            if(inputLayerInfo == null)
                throw new IllegalStateException("An input layer is mandatory.");
            if(outputLayerInfo == null)
                throw new IllegalStateException("An input layer is mandatory.");

            try {
                return new MLPNetwork(inputLayerInfo, hiddenLayersInfo, outputLayerInfo,
                        synapseMinInitialWeight, synapseMaxInitialWeight);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /** The input layer. Only one exists. **/
    private final InputLayer inputLayer;

    /** The list of hidden layers. No limit is imposed. **/
    private final ArrayList<HiddenLayer> hiddenLayers;

    /** The output layer. Only one exists. **/
    private final OutputLayer outputLayer;

    private MLPNetwork(LayerInfo input, List<LayerInfo> hidden, LayerInfo output, double weightsMin, double weightsMax)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // Initialize network architecture
        // Input layer
        int inputSize = input.neuronCount;
        inputLayer = new InputLayer();
        for (int i = 0; i < inputSize; ++i) {
            inputLayer.addNeuron(new InputNeuron()); // IdentityActivation and bias = 0
        }

        // Hidden layers
        hiddenLayers = new ArrayList<>();
        for (LayerInfo layerInfo : hidden) {

            HiddenLayer layer = new HiddenLayer();
            for (int j = 0; j < layerInfo.neuronCount; j++) {
                ActivationFunction func = layerInfo.neuronActivation.getConstructor().newInstance();
                HiddenNeuron hiddenNeuron = new HiddenNeuron(func, layerInfo.neuronBias);
                layer.addNeuron(hiddenNeuron);
            }

            hiddenLayers.add(layer);
        }

        int outputSize = output.neuronCount;
        outputLayer = new OutputLayer();
        for (int i = 0; i < outputSize; i++) {
            ActivationFunction func = output.neuronActivation.getConstructor().newInstance();
            outputLayer.addNeuron(new OutputNeuron(func, output.neuronBias));
        }

        // Fully connected network
        connectLayers(weightsMin, weightsMax);
    }

    /**
     * Generates a fully-connected network by creating the necessary synapses between all layers.
     */
    private void connectLayers(double weightsMin, double weightsMax) {
        Random rand = new Random();

        List<NeuronLayer> layers = getOrderedLayers();

        for (int i = 0; i < layers.size() - 1; i++) {
            Neuron[] sources = layers.get(i).getMembers();
            Neuron[] targets = layers.get(i + 1).getMembers();

            for (Neuron source : sources) {
                for (Neuron target : targets) {
                    // Create and weigh the synapse with a uniform random value
                    double weight = (weightsMax - weightsMin) * rand.nextDouble() + weightsMin;
                    Synapse synapse = new Synapse(source, target, weight);
                    target.connectSynapse(synapse);
                }
            }

        }
    }

    /**
     * Returns an ordered collection of the network layers.
     * <br/>
     * The order of the layers in the returned list follows the network architecture, from a single input layer, through
     * all hidden layers, until the single output layer.
     * @return an ordered collection of the network layers
     */
    public List<NeuronLayer> getOrderedLayers() {
        ArrayList<NeuronLayer> layers = new ArrayList<>();
        layers.add(inputLayer);
        layers.addAll(hiddenLayers); // by internal order
        layers.add(outputLayer);
        return layers;
    }

    /**
     * Returns the number of hidden layers of this network.
     * @return the number of hidden layers of this network
     */
    public int getHiddenLayerCount() {
        return hiddenLayers.size();
    }

    /**
     * Returns a collection of the network hidden layers.
     * @return a collection of the network hidden layers
     */
    public List<HiddenLayer> getHiddenLayers() {
        return new ArrayList<>(hiddenLayers);
    }

    /**
     * Returns a dictionary with mappings <code>{neuron : outgoing synapses}</code>.
     *
     * @return a dictionary with mappings <code>{neuron : outgoing synapses}</code>
     */
    public HashMap<Neuron, Synapse[]> getSynapsesFrom() {
        HashMap<Neuron, Synapse[]> synapsesFrom = new HashMap<>();

        for (Neuron source : getNeurons()) {
            ArrayList<Synapse> synapsesFromSource = new ArrayList<>();

            for (NeuronLayer layer : getOrderedLayers()) {
                for (Neuron neuronInLayer : layer.getMembers()) {

                    for (Synapse synapse : neuronInLayer.getSynapses()) {
                        if( synapse.getSource() == source)
                            synapsesFromSource.add(synapse);
                    }

                }
            }
            synapsesFrom.put(source, synapsesFromSource.toArray(new Synapse[]{}));
        }

        return synapsesFrom;
    }

    /**
     * Returns a collection of all network neurons.
     * @return a collection of all network neurons
     */
    public List<Neuron> getNeurons() {
        ArrayList<Neuron> neurons = new ArrayList<>();
        for (NeuronLayer layer : getOrderedLayers()) {
            Collections.addAll(neurons, layer.getMembers());
        }
        return neurons;
    }

    /**
     * Returns a collection of all network synapses.
     * @return a collection of all network synapses
     */
    public List<Synapse> getSynapses() {
        ArrayList<Synapse> synapses = new ArrayList<>();
        for (NeuronLayer layer : getOrderedLayers()) {
            if( layer instanceof InputLayer)
                continue; //does not have (inbound) synapses

            for (Neuron n : layer.getMembers()) {
                Collections.addAll(synapses, n.getSynapses());
            }
        }
        return synapses;
    }
    
    /**
     * Feeds an input to the input layer. This effectively maps each variable of the input to a neuron of the input layer.
     * @param input the input vector
     * @throws IllegalArgumentException if input mismatch relative to the network input layer
     * @see Neuron#setInputValue(double)
     */
    public void feedInput(VectorN input)  {
        Args.requireEqual(input.dimensions(), "input.dimensions()", inputLayer.size(), "input layer size");

        Neuron[] inputNeuron = inputLayer.getMembers();
        for (int i = 0; i < input.dimensions(); i++) {
            inputNeuron[i].setInputValue(input.get(i));
        }
    }

    /**
     * Processes the current input through all layers in a feed-forward fashion.
     * @see NeuronLayer#process()
     */
    public void process() {
        List<NeuronLayer> layers = getOrderedLayers();
        for (NeuronLayer l : layers) {
            l.process();
        }
    }

    /**
     * Returns the current network output state.
     *
     * @see #feedInput(VectorN)
     * @see #process()
     *
     * @return the current network output
     */
    public VectorN getCurrentOutput() {
        int nOutputs = outputLayer.size();
        Neuron[] outputNeuron = outputLayer.getMembers();
        double[] output = new double[nOutputs];
        for (int i = 0; i < nOutputs; i++) {
            output[i] = outputNeuron[i].getOutputValue();
        }

        return VectorN.fromArray(output);
    }

    /**
     * Returns the output of the network for an input.
     *
     * @param input the input
     * @return network output
     */
    public VectorN recall(VectorN input)  {
        feedInput(input);
        process();
        return getCurrentOutput();
    }

    /**
     * Returns the input layer.
     * @return the input layer
     */
    public InputLayer getInputLayer() {
        return inputLayer;
    }

    /**
     * Returns the output layer.
     * @return the output layer
     */
    public OutputLayer getOutputLayer() {
        return outputLayer;
    }

    @Override
    public String toString() {
        List<NeuronLayer> layers = getOrderedLayers();

        StringBuilder sb = new StringBuilder();
        for (NeuronLayer layer : layers) {
            sb.append( layer.toString() ).append("\n");
        }
        return sb.toString();
    }

    /**
     * Necessary information to create a layer afterwards.
     * @see NeuronLayer
     * @see Neuron
     * @see ActivationFunction
     */
    private static class LayerInfo {
        private final int neuronCount;
        private final Class<? extends ActivationFunction> neuronActivation;
        private final double neuronBias;

        public LayerInfo(int neuronCount, Class<? extends ActivationFunction> neuronActivation, double neuronBias) {
            this.neuronCount = neuronCount;
            this.neuronActivation = neuronActivation;
            this.neuronBias = neuronBias;
        }

    }
}
