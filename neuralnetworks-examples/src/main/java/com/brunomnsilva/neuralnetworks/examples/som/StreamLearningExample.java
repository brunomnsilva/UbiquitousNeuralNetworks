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

package com.brunomnsilva.neuralnetworks.examples.som;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.*;
import com.brunomnsilva.neuralnetworks.models.som.impl.StreamingSOM;
import com.brunomnsilva.neuralnetworks.models.som.impl.UbiSOM;
import com.brunomnsilva.neuralnetworks.view.som.*;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;

import java.io.IOException;

public class StreamLearningExample {

    public static void main(String[] args) {
        try {
            //Dataset dataset = new Dataset("datasets/gaussian2d.data");
            //Dataset dataset = new Dataset("datasets/uniform2d.data");
            //Dataset dataset = new Dataset("datasets/complex_random.data");
            Dataset dataset = new Dataset("datasets/complex_density.data");
            //Dataset dataset = new Dataset("datasets/hepta.data");
            //Dataset dataset = new Dataset("datasets/sphere.data");
            //Dataset dataset = new Dataset("datasets/chainlink.data");
            //Dataset dataset = new Dataset("datasets/twoCloudsDriftSingle.data");
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            final int width = 20;
            final int height = 40;

            //StreamingSOM som = new PLSOM(width, height, dataset.inputDimensionality(),Math.sqrt(width * height) * 2 );
            //StreamingSOM som = new DSOM(width, height, dataset.inputDimensionality(),50, 0.65 );
            StreamingSOM som = new UbiSOM(width, height, dataset.inputDimensionality(), 0.1, 0.08, 0.6, 0.2, 0.7, 2000);

            // We'll feed the dataset items to the spatialVis panel during learning
            AbstractSpatialVisualizationPanel spatialViz = SelfOrganizingMapVisualizationFactory.createSpatialVisualizationPanel(som);
            som.addObserver(spatialViz);

            GenericWindow window = GenericWindow.horizontalLayout("Spatial Visualization", spatialViz);
            window.exitOnClose();
            window.setVisible(true);

            for (DatasetItem item : dataset) {
                VectorN input = item.getInput();

                spatialViz.streamDatasetItem(item);
                som.learn(input);
            }

            System.out.println("STREAM ENDED");

            if(som instanceof UbiSOM) {
                showUbiSOMVisualizations((UbiSOM)som, dataset);
            }

        } catch (IOException | InvalidDatasetFormatException e) {
            e.printStackTrace();
        }
    }

    private static void showUbiSOMVisualizations(UbiSOM som, Dataset dataset) {
        UMatrixVisualizationPanel uMatrix = SelfOrganizingMapVisualizationFactory.createUMatrix(som);
        NeuronActivityVisualizationPanel activity = SelfOrganizingMapVisualizationFactory.createNeuronActivity(som);
        BMUActivityVisualizationPanel bmu = SelfOrganizingMapVisualizationFactory.createBMUActivity(som);

        ComponentPlaneSelectVisualizationPanel cpSelect = SelfOrganizingMapVisualizationFactory.createComponentPlaneSelect(som, dataset.inputVariableNames());

        GenericWindow window = GenericWindow.horizontalLayout("Neuron and BMU Activity",
                uMatrix, cpSelect, activity, bmu);

        window.exitOnClose();
        window.setVisible(true);
    }

}
