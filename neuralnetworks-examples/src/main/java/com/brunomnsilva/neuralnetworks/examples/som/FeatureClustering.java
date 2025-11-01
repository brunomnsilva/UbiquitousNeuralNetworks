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
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetNormalization;
import com.brunomnsilva.neuralnetworks.dataset.InvalidDatasetFormatException;
import com.brunomnsilva.neuralnetworks.dataset.MinMaxNormalization;
import com.brunomnsilva.neuralnetworks.models.som.*;
import com.brunomnsilva.neuralnetworks.models.som.impl.BasicSOM;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.som.SelfOrganizingMapVisualizationFactory;
import com.brunomnsilva.yacl.core.Clusterable;
import com.brunomnsilva.yacl.hierarchical.Dendogram;
import com.brunomnsilva.yacl.hierarchical.HierarchicalClustering;
import com.brunomnsilva.yacl.hierarchical.HierarchicalClusteringResult;
import com.brunomnsilva.yacl.view.DendogramVisualization;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeatureClustering {
    public static void main(String[] args) {
        try {
            // Load a dataset and normalize it
            Dataset dataset = new Dataset("datasets/wine.data");

            DatasetNormalization normalization = new MinMaxNormalization(dataset);
            normalization.normalize(dataset);

            // Create basic SOM with random initialization of prototypes
            int width = 20;
            int height = 30;
            SelfOrganizingMap som = new BasicSOM(width, height, dataset.inputDimensionality());

            // Instantiate an offline training algorithm and train the SOM
            double iLearningRate = 0.5;
            double fLearningRate = 0.1;
            double iNeighRadius  = 2 * StrictMath.sqrt( som.getWidth()*som.getWidth() + som.getHeight()*som.getHeight());
            double fNeighRadius  = 1;
            int orderEpochs      = 100;
            int fineTuneEpochs   = 1000;

            // Instantiate a training algorithm (classic or batch)
            OfflineLearning learning = new ClassicLearning(iLearningRate, fLearningRate, iNeighRadius, fNeighRadius,
                    orderEpochs, fineTuneEpochs);
            learning.train(som, dataset);

            // Print statistics for model fitting
            SelfOrganizingMapStatistics statistics = SelfOrganizingMapStatistics.compute(som, dataset);
            System.out.println(statistics);

            showVisualizations(som, dataset);

            featureClustering(som, dataset.inputVariableNames());

        } catch (IOException | InvalidDatasetFormatException e) {
            e.printStackTrace();
        }
    }

    private static void featureClustering(SelfOrganizingMap som, String[] inputNames) {
        int dimensionality = som.getDimensionality();

        List<ComponentPlaneWrapper> components = new ArrayList<>();

        // Create the component planes
        for(int d=0; d < dimensionality; ++d) {
            ComponentPlane cp = ComponentPlane.fromSelfOrganizingMap(som, d, inputNames[d]);
            components.add( new ComponentPlaneWrapper(cp) );
        }
        // Hierarchical clustering usage
        HierarchicalClustering<ComponentPlaneWrapper> hclust = new HierarchicalClustering<>("complete");
        HierarchicalClusteringResult<ComponentPlaneWrapper> hclustResult = hclust.cluster(components);

        Dendogram<ComponentPlaneWrapper> dendogram = new Dendogram<>(hclustResult);

        DendogramVisualization<ComponentPlaneWrapper> viz = new DendogramVisualization<>(dendogram, DendogramVisualization.LabelType.LABEL);

        GenericWindow window = GenericWindow.horizontalLayout("Feature Clustering", viz);
        window.setPreferredSize(new Dimension(1000,800));
        window.pack();
        window.exitOnClose();
        window.setVisible(true);
    }

    private static void showVisualizations(SelfOrganizingMap som, Dataset dataset) {
        int dimensionality = som.getDimensionality();

        // Create a set of panels for U-Matrix + all component planes
        JPanel[] panels = new JPanel[dimensionality + 1];

        // Create the U-Matrix
        panels[0] = SelfOrganizingMapVisualizationFactory.createUMatrix(som);

        // Create the component planes
        int d;
        for(d=0; d < dimensionality; ++d) {
            panels[d+1] = SelfOrganizingMapVisualizationFactory.createComponentPlane(som, d, dataset.inputVariableNames()[d]);
        }

        GenericWindow window = GenericWindow.gridLayout("U-Matrix and Component Planes", 3, 6, panels);
        window.exitOnClose();
        window.setVisible(true);
    }

    /**
     * This class wraps a component plane instance and provides the clusterable behavior needed for the feature
     * clustering procedure, utilizing the YACL clustering library.
     */
    private static class ComponentPlaneWrapper implements Clusterable<ComponentPlaneWrapper> {

        private final ComponentPlane componentPlane;

        public ComponentPlaneWrapper(ComponentPlane componentPlane) {
            this.componentPlane = componentPlane;
        }

        @Override
        public double clusterableDistance(ComponentPlaneWrapper other) {
            // This must implement some kind of metric distance to be used by the hierarchical clustering
            // procedure. Below are three possible implementation examples you can choose from. You can derive your own.
            return pearsonDistance(this.componentPlane, other.componentPlane);
            //return euclideanDistance(this.componentPlane, other.componentPlane);
            //return cosineDistance(this.componentPlane, other.componentPlane);
        }

        @Override
        public double[] clusterablePoint() {
            return componentPlane.flatten();
        }

        @Override
        public String clusterableLabel() {
            return componentPlane.getName();
        }

        private static double pearsonDistance(ComponentPlane cp1, ComponentPlane cp2) {
            VectorN Ci = VectorN.fromArray( cp1.flatten() );
            VectorN Cj = VectorN.fromArray( cp2.flatten() );

            VectorN meanCi = VectorN.rep(Ci.dimensions(), Ci.mean());
            VectorN meanCj = VectorN.rep(Cj.dimensions(), Cj.mean());

            double stdCi = Ci.std();
            double stdCj = Cj.std();

            // adjust flattened vectors
            Ci.subtract(meanCi);
            Ci.divide(stdCi);

            Cj.subtract(meanCj);
            Cj.divide(stdCj);

            double dot = Ci.dot(Cj);

            double similarity = dot / Ci.dimensions(); // -1 to 1

            return 1 - similarity;
        }

        private static double euclideanDistance(ComponentPlane cp1, ComponentPlane cp2) {
            VectorN Ci = VectorN.fromArray(cp1.flatten());
            VectorN Cj = VectorN.fromArray(cp2.flatten());

            return Ci.distance(Cj);
        }

        private static double cosineDistance(ComponentPlane cp1, ComponentPlane cp2) {
            VectorN Ci = VectorN.fromArray(cp1.flatten());
            VectorN Cj = VectorN.fromArray(cp2.flatten());

            double dot = Ci.dot(Cj);
            double magCi = Ci.magnitude();
            double magCj = Cj.magnitude();

            double similarity = dot / (magCi * magCj); // -1 to 1

            return 1 - similarity;
        }

    }
}
