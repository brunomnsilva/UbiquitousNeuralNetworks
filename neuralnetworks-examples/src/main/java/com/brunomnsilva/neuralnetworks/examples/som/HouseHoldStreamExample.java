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
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.som.ComponentPlaneVisualizationPanel;
import com.brunomnsilva.neuralnetworks.view.som.UMatrixVisualizationPanel;
import com.brunomnsilva.neuralnetworks.view.som.SelfOrganizingMapVisualizationFactory;

import javax.swing.*;
import java.io.IOException;

public class HouseHoldStreamExample {

    public static void main(String[] args) {
        try {
            // Load dataset
            Dataset dataset = new Dataset("datasets/household_power_sensor.data");
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            // Instantiate model
            final int width = 20;
            final int height = 40;
            StreamingSOM ubiSOM = new UbiSOM(width, height, dataset.inputDimensionality(),
                    0.1, 0.08, 0.6, 0.2, 0.7, 2000);


            // Create window with grid layout
            GenericWindow window = GenericWindow.gridLayout("U-Matrix and Component Planes",
                    2, 4, createVisualization(ubiSOM, dataset));
            window.exitOnClose();
            window.setVisible(true);

            // Run stream full-speed
            for (DatasetItem item : dataset) {
                VectorN input = item.getInput();
                ubiSOM.learn(input);
            }

            System.out.println("STREAM ENDED");

        } catch (IOException | InvalidDatasetFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    private static JPanel[] createVisualization(StreamingSOM som, Dataset dataset) {
        // Instantiate visualizations
        int dimensionality = dataset.inputDimensionality();
        JPanel[] panels = new JPanel[dimensionality + 1];
        // Create U-Matrix
        UMatrixVisualizationPanel uMatrix = SelfOrganizingMapVisualizationFactory.createUMatrix(som);
        som.addObserver( uMatrix );
        panels[0] = uMatrix;
        // Create component planes
        for(int d=0; d < dimensionality; ++d) {
            ComponentPlaneVisualizationPanel cp = SelfOrganizingMapVisualizationFactory.createComponentPlane(som, d, dataset.inputVariableNames()[d]);
            som.addObserver(cp);
            panels[d+1] = cp;
        }
        return panels;
    }

}
