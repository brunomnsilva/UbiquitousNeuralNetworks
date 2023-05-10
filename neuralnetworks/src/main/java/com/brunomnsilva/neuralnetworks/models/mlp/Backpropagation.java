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

import java.util.*;

import com.brunomnsilva.neuralnetworks.core.*;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;

/**
 * The backpropagation algorithm.
 *
 * @author brunomnsilva
 */
public class Backpropagation extends AbstractObservable implements Runnable {

    /** Number of epochs to use during training **/
    private final int numberEpochs;

    /** Minimum error to be achieved during training **/
    private final double minimumError;

    /** Learning rate to be used during training **/
    private final double learningRate;

    /** Whether to update the neuron's bias during training */
    private final boolean biasUpdate;

    /* Momentum to escape local minima */
    //private double momentum; // TODO: explore idea?

    /** Flag to signal premature end of training **/
    private volatile boolean stopTraining;

    /** Learning mean-squared error for all epochs of training **/
    private final TimeSeries meanSquaredError;

    /** The dataset iterator to be used during train **/
    private final Dataset dataset;

    /** The MLP network to train **/
    private final MLPNetwork network;

    /**
     * A builder for the Backpropagation algorithm. All instantiations of the algorithm are solely done through this
     * builder. It allows to set (or keep default values by omission) for the following hyper-parameters:
     * <ul>
     *     <li>learning rate (default = 0.1) - the learning rate to use during adjustment of network parameters (weights and biases)</li>
     *     <li>minimum error (default = 0) - the minimum error (loss function) to achieve during training. If the error goes
     *     below this threshold, the training is interrupted, despite the parameterized number of epochs.</li>
     *     <li>number of training epochs (default = 100) - the number of training epochs of backpropagation.</li>
     *     <li>bias update  (default = false) - whether to also update the biases during training.</li>
     * </ul>
     */
    public static class Builder {
        // Default values here
        private final MLPNetwork network;
        private final Dataset dataset;
        private double learningRate = 0.1; //default value
        private double minimumError = 0; //default value
        private int epochs = 100; //default value
        private boolean biasUpdate = false;

        /**
         * Default constructor with mandatory data
         * @param dataset the dataset to train the network with
         * @param network the network to be trained
         */
        public Builder(Dataset dataset, MLPNetwork network) {
            this.dataset = dataset;
            this.network = network;
        }

        /**
         * Specifies the learning rate to use during backpropagation.
         * @param learningRate the learning rate
         * @return the updated builder
         */
        public Builder withLearningRate(double learningRate) {
            this.learningRate = learningRate;
            return this;
        }

        /**
         * Sets that the biases are to be updated during training.
         * @return the updated builder
         */
        public Builder withBiasUpdate() {
            this.biasUpdate = true;
            return this;
        }

        /**
         * Sets the minimum error to achieve during training.
         * @param minimumError the minimum error threshold (must be finite and positive)
         * @return the updated builder
         */
        public Builder untilMinimumError(double minimumError) {
            Args.requireNonNegative(minimumError, "minimumError");
            this.minimumError = minimumError;
            return this;
        }

        /**
         * Sets the number of training epochs.
         * @param epochs the number of training epochs (must be >= 1)
         * @return the updated builder
         */
        public Builder forNumberEpochs(int epochs) {
            Args.requireGreaterEqualThan(epochs, "epochs", 1);
            this.epochs = epochs;
            return this;
        }

        /**
         * Instantiates a Backpropagation instance with the parameterized values.
         * @return a new Backpropagation instance
         */
        public Backpropagation build() {
            return new Backpropagation(dataset, network, learningRate, minimumError, epochs, biasUpdate);
        }
    }

    private Backpropagation(final Dataset dataset, MLPNetwork network, double learningRate,
                            double minimumError, int numberEpochs, boolean biasUpdate) {
        // Check if the dataset matches the network
        Args.requireEqual(network.getInputLayer().size(), "MLP input size",
                dataset.inputDimensionality(), "Dataset input dimensionality");

        Args.requireEqual(network.getOutputLayer().size(), "MLP output size",
                dataset.outputDimensionality(), "Dataset output dimensionality");

        this.dataset = dataset;
        this.network = network;
        this.meanSquaredError = new TimeSeries("Backpropagation, Mean Squared Error");

        this.learningRate = learningRate;
        this.minimumError = minimumError;
        this.numberEpochs = numberEpochs;
        this.biasUpdate = biasUpdate;
    }

    /**
     * Trains the network according to the current parameterization.
     * <br/>
     * The training is performed in a separate thread. Hence, the caller thread must
     * ensure that the training has ended before using the resulting model.
     */
    public void trainNetworkAsync() {
        new Thread(this).start();
    }

    /**
     * Trains the network according to the current parameterization.
     */
    public void trainNetwork() {
        run();
    }

    /**
     * Implements an iteration of the backpropagation algorithm for a given {input / target output}.
     */
    private double trainSample(final VectorN input, final VectorN targetOutput, HashMap<Neuron, Synapse[]> synapses) {

        // Feed the input and run it through the network
        network.feedInput(input);
        network.process();

        // Squared error
        double squaredError = 0;

        // Output layer
        Neuron[] outputNeuron = network.getOutputLayer().getMembers();
        for (int k = 0; k < outputNeuron.length; ++k) {
            Neuron unit = outputNeuron[k];

            double ok = unit.getOutputValue();
            double tk = targetOutput.get(k);

            double derivative = unit.getActivationFunction().derivative(ok);
            double delta_k = derivative * (tk - ok);

            unit.setOutputErrorValue(delta_k);

            if(biasUpdate) {
                unit.adjustBias( learningRate * delta_k);
            }

            // Compute loss function - squared error
            squaredError += (tk - ok) * (tk - ok);
        }

        // Hidden layers
        List<HiddenLayer> hiddenLayers = network.getHiddenLayers();
        for (int i = hiddenLayers.size() - 1; i >= 0; --i) {
            Neuron[] hiddenNeuron = hiddenLayers.get(i).getMembers();
            for (int h = 0; h < hiddenNeuron.length; ++h) {
                Neuron unit = hiddenNeuron[h];

                double oh = unit.getOutputValue();
                double delta_h = 0;

                Synapse[] outSynapses = synapses.get(unit);
                for (Synapse s : outSynapses) {
                    delta_h += s.getStrength() * s.getSink().getOutputErrorValue();
                }

                double derivative = unit.getActivationFunction().derivative(oh);

                delta_h *= derivative;

                unit.setOutputErrorValue(delta_h);

                if(biasUpdate) {
                    unit.adjustBias( learningRate * delta_h);
                }
            }
        }

        // Adjust all synapses
        Collection<Synapse[]> synapseList = synapses.values();
        for (Synapse[] sa : synapseList) {
            for (Synapse s : sa) {
                s.adjustStrength(learningRate * s.getSink().getOutputErrorValue() * s.getSource().getOutputValue());
            }
        }

        // Alert observers that the network has changed
        notifyObservers();

        return squaredError;
    }


    /**
     * Implements an iteration of the backpropagation algorithm.
     */
    private double trainSample(final VectorN input, final VectorN targetOutput) {
        return trainSample(input, targetOutput, network.getSynapsesFrom());
    }

    /**
     * Runs the backpropagation algorithm with the current parameterization.
     * <br/>
     * May be executed within its own thread if {@link #trainNetworkAsync()} is called.
     *
     * @see Runnable
     */
    public void run() {
        stopTraining = false;
        int currentEpoch = 1;
        double epochTrainError = 0;
        meanSquaredError.clear();

        // For performance reasons, we get all synapses beforehand
        HashMap<Neuron, Synapse[]> synapses = network.getSynapsesFrom();

        ConsoleProgressBar progress = new ConsoleProgressBar(numberEpochs);

        while (currentEpoch <= numberEpochs && !stopTraining ) {

            progress.update(currentEpoch);

            epochTrainError = 0;

            for (DatasetItem item : dataset) {

                double outputError = trainSample(item.getInput(), item.getTargetOutput(), synapses);

                epochTrainError += outputError;
            }

            currentEpoch++;
            epochTrainError /= dataset.size();

            // Add epoch error value to time series
            meanSquaredError.append(currentEpoch, epochTrainError);

            notifyObservers();

            // Break training if desired minimum error is achieved
            if( epochTrainError  <=  minimumError) {
                stopTraining();
                System.out.printf("\n[Training interrupted because minimumError=%.3f was achieved]\n", minimumError);
                break;
            }
        }

        stopTraining();
    }


    private void stopTraining() {
        stopTraining = true;
    }

    /**
     * Convenience method to check if the training has stopped when
     * using the {@link #trainNetworkAsync()} method.
     *
     * @return <i>true</i> is training has stopped; <i>false</i> otherwise
     */
    public boolean hasStoppedLearning() {
        return stopTraining;
    }

    /**
     * Returns a TimeSeries depicting the training error over time.
     * <br/>
     * It will only contain values after training is performed.
     *
     * @return training error time series
     *
     * @see TimeSeries
     */
    public TimeSeries getTrainMeanSquaredError() {
        return meanSquaredError;
    }

}
