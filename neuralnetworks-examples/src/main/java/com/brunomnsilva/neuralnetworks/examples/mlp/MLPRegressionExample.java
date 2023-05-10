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

import com.brunomnsilva.neuralnetworks.dataset.*;
import com.brunomnsilva.neuralnetworks.models.mlp.*;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.mlp.MLPNetworkVisualizationPanel;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author brunomnsilva
 */
public class MLPRegressionExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Dataset dataset = new Dataset("datasets/concrete.data");

            // Normalize the dataset - its important
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            DatasetTrainTestSplit datasetSplit = DatasetTrainTestSplit.split(dataset, 0.75);
            Dataset trainingSet = datasetSplit.getTrainingSet();
            Dataset testSet = datasetSplit.getTestSet();

            // Create network architecture -- it is problem dependent and results may vary
            // due to the random initialization of weights and dataset split
            MLPNetwork network = new MLPNetwork.Builder()
                    .addInputLayer(dataset.inputDimensionality())
                    .addHiddenLayer(12, ReLUActivation.class, 0)
                    .addOutputLayer(dataset.outputDimensionality(), LinearActivation.class, 0) // Linear activation for regression
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
                    .build();
            backpropagation.addObserver(networkViz);

            System.out.println("Training network with train dataset... ");
            backpropagation.trainNetwork();

            // Visualize convergence of training procedure
            viewTrainError(backpropagation);

            // Test the network
            System.out.println("Testing network with test dataset ...");
            double accuracy = testNetwork(network, testSet);
            System.out.println(String.format("-- R2-Score (1 is perfect fit): %.2f", accuracy) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double testNetwork(MLPNetwork network, Dataset testDataset) {

        List<Double> actual = new LinkedList<>();
        List<Double> predicted = new LinkedList<>();
        for (DatasetItem item : testDataset) {
            double networkOut = network.recall( item.getInput() ).get(0);
            double target = item.getTargetOutput().get(0);

            predicted.add(networkOut);
            actual.add(target);
        }

        double sumOfSquares = 0.0;
        double totalSumOfSquares = 0.0;
        double meanActual = 0.0;
        int n = actual.size();

        // calculate mean of actual values
        for (Double actualValue : actual) {
            meanActual += actualValue;
        }
        meanActual /= n;

        // calculate sum of squares of residuals and total sum of squares
        for (int i = 0; i < n; i++) {
            double residual = actual.get(i) - predicted.get(i);
            sumOfSquares += residual * residual;
            double deviation = actual.get(i) - meanActual;
            totalSumOfSquares += deviation * deviation;
        }

        // calculate R-squared score
        double rSquared = 1.0 - (sumOfSquares / totalSumOfSquares);
        return rSquared;
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
