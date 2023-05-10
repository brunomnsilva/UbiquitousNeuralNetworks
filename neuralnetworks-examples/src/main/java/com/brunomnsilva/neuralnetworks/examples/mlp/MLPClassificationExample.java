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

package com.brunomnsilva.neuralnetworks.examples.mlp;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.*;
import com.brunomnsilva.neuralnetworks.models.mlp.*;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.mlp.MLPNetworkVisualizationPanel;

/**
 *
 * @author brunomnsilva
 */
public class MLPClassificationExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //Dataset dataset = new Dataset("datasets/iris.data");
            Dataset dataset = new Dataset("datasets/wine.data");

            // Normalize the dataset - its important
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            DatasetTrainTestSplit datasetSplit = DatasetTrainTestSplit.split(dataset, 0.7);
            Dataset trainingSet = datasetSplit.getTrainingSet();
            Dataset testSet = datasetSplit.getTestSet();

            // Create network architecture -- it is problem dependent and results may vary
            // due to the random initialization of weights and dataset split
            MLPNetwork network = new MLPNetwork.Builder()
                    .addInputLayer(dataset.inputDimensionality())
                    .addHiddenLayer(10, ReLUActivation.class, 0)
                    .addOutputLayer(dataset.outputDimensionality(), SigmoidActivation.class, 0) // Our target outputs are in [0,1]
                    .weightsInitializedBetween(-0.3, 0.3)
                    .build();

            System.out.println( network );

            // Visualize the network, even during training
            MLPNetworkVisualizationPanel networkViz = new MLPNetworkVisualizationPanel(network);
            GenericWindow window = GenericWindow.horizontalLayout("MLP Network", networkViz);
            window.exitOnClose();
            window.setVisible(true);

            // Train the model
            Backpropagation backpropagation = new Backpropagation.Builder(trainingSet, network)
                    .withLearningRate(0.03)
                    .withBiasUpdate()
                    .forNumberEpochs(2000)
                    .untilMinimumError(0.01)
                    .build();
            backpropagation.addObserver(networkViz);

            System.out.println("Training network with train dataset... ");
            backpropagation.trainNetwork();

            // Visualize convergence of training procedure
            viewTrainError(backpropagation);

            // Test the network
            System.out.println("Testing network with test dataset ...");
            double accuracy = testNetwork(network, testSet);
            System.out.println(String.format("-- Network Accuracy: %.2f %%", accuracy) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double testNetwork(MLPNetwork network, Dataset testDataset) {

        int accurate = 0;
        for (DatasetItem item : testDataset) {

            VectorN networkOut = network.recall( item.getInput() );
            VectorN target = item.getTargetOutput();

            // Test recall accuracy with argmax
            if( target.compareTo(VectorN.argmax(networkOut)) == 0) {
                accurate++;
            } else {
                System.out.printf("[FAILED] Desired vs. Network output: %s vs. %s \n", target, networkOut);
            }
        }
        // Compute dataset accuracy (%)
        return (accurate/(double)testDataset.size()) * 100;
    }

    private static void viewTrainError(Backpropagation bp) {

        Plot2D plot = new Plot2D.Builder().title("Training error")
                .linePlotFromTimeSeries(bp.getTrainMeanSquaredError())
                .xLabel("Epoch")
                .yLabel("MSE")
                .build();

        GenericWindow t = GenericWindow.horizontalLayout("Error Over Time", plot);
        t.setVisible(true);
    }

}
