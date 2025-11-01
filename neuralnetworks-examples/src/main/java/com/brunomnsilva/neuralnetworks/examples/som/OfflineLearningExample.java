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

import com.brunomnsilva.neuralnetworks.core.Projections;
import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.*;
import com.brunomnsilva.neuralnetworks.models.som.*;
import com.brunomnsilva.neuralnetworks.models.som.clustering.DBSCANClustering;
import com.brunomnsilva.neuralnetworks.models.som.impl.BasicSOM;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.som.*;

import javax.swing.*;
import java.io.IOException;

public class OfflineLearningExample {

    public static void main(String[] args) {

        try {
            // Load a dataset and normalize it

            //Dataset dataset = new Dataset("datasets/gaussian2d.data");
            //Dataset dataset = new Dataset("datasets/uniform2d.data");
            //Dataset dataset = new Dataset("datasets/complex_density.data");
            Dataset dataset = new Dataset("datasets/hepta.data");
            //Dataset dataset = new Dataset("datasets/sphere.data");
            //Dataset dataset = new Dataset("datasets/chainlink.data");
            //Dataset dataset = new Dataset("datasets/wine.data");
            //Dataset dataset = new Dataset("datasets/cactus.data");
            //Dataset dataset = new Dataset("datasets/iris.data");

            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            // Create basic SOM with random initialization of prototypes
            int width = 20;
            int height = 30;
            SelfOrganizingMap som = new BasicSOM(width, height, dataset.inputDimensionality());

            // Use (alternatively) input-based initialization of prototypes? If so, uncomment
            //SelfOrganizingMapInitialization.fromVectors(som, DatasetUtils.inputsToList(dataset));

            // Let's visualize the training process (we're limited to 2d/3d view without any projection)
            // The dataset may be large, we don't want to overload the graphical drawing, so sample the dataset
            Dataset datasetSample = dataset.sample(Math.min(dataset.size(), 2000));
            AbstractSpatialVisualizationPanel spatialViz = SelfOrganizingMapVisualizationFactory.createSpatialVisualizationPanel(som, datasetSample);
            som.addObserver(spatialViz);

            GenericWindow window = GenericWindow.horizontalLayout("Spatial Visualization", spatialViz);
            window.exitOnClose(); //we can stop/interrupt the training by closing this window
            window.setVisible(true);

            // Instantiate an offline training algorithm and train the SOM
            // The parameters should be tuned for each variant/dataset in order to get the best results
            // The number of epochs should be higher for smaller datasets
            // Note: The Batch training algorithm does not depend on the alpha (learning rate) parameter.
            double iAlpha       = 0.2;
            double fAlpha       = 0.005;
            double iSigma       = 2 * StrictMath.sqrt( som.getWidth()*som.getWidth() + som.getHeight()*som.getHeight());
            double fSigma       = 0.1;
            int orderEpochs     = 5;
            int fineTuneEpochs  = 50;

            // Instantiate a training algorithm (classic or batch)
            OfflineLearning learning = new ClassicLearning(iAlpha, fAlpha, iSigma, fSigma, orderEpochs, fineTuneEpochs);
            //OfflineLearning learning = new BatchLearning(iSigma, fSigma, orderEpochs, fineTuneEpochs);

            learning.train(som, dataset);

            // Print statistics for model fitting
            SelfOrganizingMapStatistics statistics = SelfOrganizingMapStatistics.compute(som, dataset);
            System.out.println(statistics);

            showUMatrixComponentPlanes(som, dataset.inputVariableNames());
            showHitMapClasses(som, dataset);
            showDataProjection(som, dataset, normalization);

            // Attempt a clustering of the prototypes. If the dataset includes target classes, we can
            // compare the results afterwards with the visualizations

            //SelfOrganizingMapClusteringResult clustering = new KmeansClustering(som, 7, 10000).cluster();
            int dim = dataset.inputDimensionality();
            int minPts = (dim > 2 ? 4 * dim : 2 * dim); // rule-of-thumb, may not work for all cases
            SelfOrganizingMapClusteringResult clustering = new DBSCANClustering(som, 0.05, minPts).cluster();
            showClusteringResult(som, clustering);

            // Export the SOM to CSV?
            //SelfOrganizingMapExport.toCSV(som, "som.csv");

        } catch (IOException | InvalidDatasetFormatException e) {
            e.printStackTrace();
        }
    }

    private static void showClusteringResult(SelfOrganizingMap som, SelfOrganizingMapClusteringResult clustering) {
        ClusteringVisualizationPanel clusterViz = SelfOrganizingMapVisualizationFactory.createClustering(som, clustering);

        GenericWindow window = GenericWindow.horizontalLayout( "Clustering Results", clusterViz);
        window.exitOnClose();
        window.setVisible(true);
    }

    private static void showUMatrixComponentPlanes(SelfOrganizingMap som, String[] inputNames) {
        int dimensionality = som.getDimensionality();

        // Create a set of panels for U-Matrix + all component planes
        JPanel[] panels = new JPanel[dimensionality + 1];

        // Create the U-Matrix
        panels[0] = SelfOrganizingMapVisualizationFactory.createUMatrix(som);

        // Create the component planes
        for(int d=0; d < dimensionality; ++d) {
            panels[d+1] = SelfOrganizingMapVisualizationFactory.createComponentPlane(som, d, inputNames[d]);
        }

        // We could alternatively use a ComponentPlaneSelectVisualizationPanel, but only one component plane
        // would be visible at a time.

        GenericWindow window = GenericWindow.horizontalLayout("U-Matrix and Component Planes", panels);
        window.exitOnClose();
        window.setVisible(true);
    }

    private static void showHitMapClasses(SelfOrganizingMap som, Dataset dataset) {
        HitMapVisualizationPanel hitMapViz = SelfOrganizingMapVisualizationFactory.createHitMap(som, dataset);
        TargetOutputVisualizationPanel targetOutputViz = SelfOrganizingMapVisualizationFactory.createTargetOutputProjection(som, dataset);

        GenericWindow window = GenericWindow.horizontalLayout("HitMap and Target Output Projection",
                hitMapViz, targetOutputViz);
        window.exitOnClose();
        window.setVisible(true);
    }

    private static void showDataProjection(SelfOrganizingMap som, Dataset dataset, DatasetNormalization norm) {
        // Project dataset and prototype through PCA projection in 2d
        double[][] data = DatasetUtils.inputsTo2dArray(dataset);
        double[][] prototypes = SelfOrganizingMapUtils.prototypesTo2dArray(som);

        // Plot just a sample of the dataset
        Dataset datasetSample = dataset.sample( Math.min( dataset.size(), 1000));
        double[][] dataSample = DatasetUtils.inputsTo2dArray(datasetSample);

        // If any MinMax normalization was applied previously, this has
        // a severe effect on the PCA projection. Hence, we must denormalize
        // the data beforehand.
        if(norm instanceof MinMaxNormalization) {
            data = denormalizeRows(data, norm);
            prototypes = denormalizeRows(prototypes, norm);
            dataSample = denormalizeRows(dataSample, norm);
        }

        Projections.PCA pca = Projections.PCAfrom2dArray(data);

        double[][] projectedDataset = pca.project(dataSample, 2);
        double[][] projectedPrototypes = pca.project(prototypes, 2);

        Plot2D plot = new Plot2D.Builder()
                .title("PCA Projection")
                .scatterPlotFrom2dArray(dataset.getName(), projectedDataset)
                .scatterPlotFrom2dArray("SOM Prototypes", projectedPrototypes)
                .xLabel("Component 1")
                .yLabel("Component 2")
                .legend(true)
                .width(500)
                .height(500)
                .build();

        GenericWindow window = GenericWindow.horizontalLayout("2D Data Projection", plot);
        window.exitOnClose();
        window.setVisible(true);
    }

    private static double[][] denormalizeRows(double[][] data, DatasetNormalization norm) {
        int rows = data.length;
        for(int i=0; i < rows; ++i) {
            VectorN v = VectorN.fromArray(data[i]);
            data[i] = norm.denormalizeInput(v).values();
        }
        return data;
    }

}
