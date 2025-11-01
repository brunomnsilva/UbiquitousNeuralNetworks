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

package com.brunomnsilva.neuralnetworks.examples.misc;

import com.brunomnsilva.neuralnetworks.dataset.*;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.dataset.DatasetVisualizationPanel;

import java.io.IOException;

public class DatasetExample {
    public static void main(String[] args) {

        Dataset dataset = null;
        try {
            dataset = new Dataset("datasets/household_power_sensor.data");
        } catch (IOException | InvalidDatasetFormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        DatasetSummary summary = new DatasetSummary(dataset);
        System.out.println(summary);

        DatasetVisualizationPanel panel = new DatasetVisualizationPanel(dataset);
        GenericWindow window = GenericWindow.horizontalLayout(dataset.getName(), panel);
        window.exitOnClose();
        window.setVisible(true);

        DatasetNormalization scaling = new MinMaxNormalization(dataset);
        scaling.normalize(dataset);

        DatasetSummary summary2 = new DatasetSummary(dataset);
        System.out.println(summary2);

        DatasetTrainTestSplit split = DatasetTrainTestSplit.split(dataset, 0.7);
        Dataset trainingData = split.getTrainingSet();
        Dataset testData = split.getTestSet();

        System.out.println(trainingData);
        System.out.println(testData);
    }
}
