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

package com.brunomnsilva.neuralnetworks.examples.artsom;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.dataset.DatasetNormalization;
import com.brunomnsilva.neuralnetworks.dataset.MinMaxNormalization;
import com.brunomnsilva.neuralnetworks.models.art.MicroCategory;
import com.brunomnsilva.neuralnetworks.models.art.StreamART2A;
import com.brunomnsilva.neuralnetworks.models.artsom.MicroCategoryBatchLearning;
import com.brunomnsilva.neuralnetworks.models.som.impl.BasicSOM;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.som.*;

import java.util.Collection;

public class StreamART2A_BatchSOMExample {

    public static void main(String[] args) {
        try {
            //Dataset dataset = new Dataset("datasets/complex_density.data");
            Dataset dataset = new Dataset("datasets/twoCloudsDriftSingle.data");
            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            int landmarkWindowSize = 1000;
            int q = 50;
            int K = 100000; //very high value to capture the entire stream horizon
            double learningRate = 0.05;
            StreamART2A streamART = new StreamART2A(dataset.inputDimensionality(), 0, 1,
                    learningRate,
                    landmarkWindowSize, q, K);

            for (DatasetItem item : dataset) {
                VectorN input = item.getInput();

                streamART.learn(input);

            }
            System.out.println("STREAM ENDED");

            // Get StreamART codebook for a past timestamp horizon and train a SOM with the codebook
            // for exploratory cluster analysis
            long ti = dataset.size() / 4; //halfway through the dataset
            long tf = dataset.size() / 3;
            Collection<MicroCategory> codebook = streamART.getCodebookBetween(ti, tf);

            // Create Self-Organizing Map
            SelfOrganizingMap som = new BasicSOM(20, 30, dataset.inputDimensionality());

            // Train the SOM from the StreamART codebook
            double iSigma =  StrictMath.sqrt( som.getWidth()*som.getWidth() + som.getHeight()*som.getHeight());
            MicroCategoryBatchLearning learning = new MicroCategoryBatchLearning(iSigma, 0.5, 10, 100);
            learning.train(som, codebook);

            // Show SOM visualizations
            UMatrixVisualizationPanel umatrix = SelfOrganizingMapVisualizationFactory.createUMatrix(som);
            AbstractSpatialVisualizationPanel spatialViz;
            if(dataset.inputDimensionality() > 2) {
                spatialViz = new SpatialVisualization3DPanel(som);
            } else {
                spatialViz = new SpatialVisualization2DPanel(som);
            }

            GenericWindow window = GenericWindow.horizontalLayout("Self-Organizing Map", umatrix, spatialViz);
            window.exitOnClose();
            window.setVisible(true);


        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
