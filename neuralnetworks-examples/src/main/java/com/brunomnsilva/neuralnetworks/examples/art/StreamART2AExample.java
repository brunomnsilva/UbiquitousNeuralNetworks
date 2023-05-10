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

package com.brunomnsilva.neuralnetworks.examples.art;

import com.brunomnsilva.neuralnetworks.core.TimeSeries;
import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.dataset.DatasetNormalization;
import com.brunomnsilva.neuralnetworks.dataset.MinMaxNormalization;
import com.brunomnsilva.neuralnetworks.models.art.StreamART2A;
import com.brunomnsilva.neuralnetworks.models.art.StreamART2AWithConceptDrift;
import com.brunomnsilva.neuralnetworks.view.art.CodebookVisualizationLayer;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.art.LayeredVisualizationPanel;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;

public class StreamART2AExample {
    public static void main(String[] args) {
        try {
            Dataset dataset = new Dataset("datasets/complex_density.data");
            //Dataset dataset = new Dataset("datasets/twoCloudsDriftSingle.data");
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            int landmarkWindowSize = 1000;
            int q = 50;
            int K = 1000;
            double learningRate = 0.05;
            StreamART2A streamART = new /*StreamART2A*/StreamART2AWithConceptDrift(dataset.inputDimensionality(), 0, 1,
                    learningRate,
                    landmarkWindowSize, q, K);

            LayeredVisualizationPanel viz = new LayeredVisualizationPanel();
            CodebookVisualizationLayer codebookLayer = new CodebookVisualizationLayer(streamART);
            viz.add(codebookLayer);
            streamART.addObserver(viz);

            GenericWindow window = GenericWindow.horizontalLayout("StreamART Codebook", viz);
            window.exitOnClose();
            window.setVisible(true);

            // This may be way too fast to be able to visualize the model evolution
            // Put dataset iteration inside a thread and go... slower.
            new Thread(() -> {
                for (DatasetItem item : dataset) {
                    VectorN input = item.getInput();

                    streamART.learn(input);

                    // Comment sleep to go full speed
                    /*try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*/
                }
                System.out.println("STREAM ENDED");
                showQuantizationError(streamART);

            }).start();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void showQuantizationError(StreamART2A streamART2A) {
        if(streamART2A instanceof StreamART2AWithConceptDrift) {
            StreamART2AWithConceptDrift streamArt = (StreamART2AWithConceptDrift)streamART2A;

            Plot2D plot = new Plot2D.Builder().title("Quantization Error")
                    .linePlotFromTimeSeries(streamArt.getQuantizationErrorTimeSeries())
                    .xLabel("t")
                    .yLabel("E(t)")
                    .build();

            GenericWindow t = GenericWindow.horizontalLayout("Error Over Time", plot);
            t.setVisible(true);
        }

    }
}
