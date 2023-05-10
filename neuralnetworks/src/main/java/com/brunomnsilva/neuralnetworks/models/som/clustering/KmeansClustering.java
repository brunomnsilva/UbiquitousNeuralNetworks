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

package com.brunomnsilva.neuralnetworks.models.som.clustering;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.models.som.*;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a K-Means++ clustering of prototypes.
 * <br/>
 * It's simply a wrapper for the 'org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer'
 */
public class KmeansClustering extends SelfOrganizingMapClustering {

    /** The number of clusters of interest. */
    private final int numberClusters;

    /** Maximum number of iterations of the algorithm. */
    private final int maxIterations;

    /**
     * Creates a new instance of KmeansClustering.
     * @param som the SelfOrganizingMap to cluster its prototypes.
     * @param numberClusters the number of clusters of interest
     * @param maxIterations the maximum number of iterations of the algorithm
     */
    public KmeansClustering(SelfOrganizingMap som, int numberClusters, int maxIterations) {
        super(som);
        this.numberClusters = numberClusters;
        this.maxIterations = maxIterations;
    }

    @Override
    public SelfOrganizingMapClusteringResult cluster() {
        SelfOrganizingMap som = getSelfOrganizingMap();

        // Wrap prototypes
        List<PrototypeWrapper> clusterInput = new ArrayList<>(som.getWidth() * som.getHeight());
        for (PrototypeNeuron p : som) {
            clusterInput.add( new PrototypeWrapper(p) );
        }

        KMeansPlusPlusClusterer<PrototypeWrapper> clusterer = new KMeansPlusPlusClusterer<>(numberClusters, maxIterations);
        List<CentroidCluster<PrototypeWrapper>> clusteringResult = clusterer.cluster(clusterInput);

        SelfOrganizingMapClusteringResult clustering = new SelfOrganizingMapClusteringResult(som);

        // For each prototype of the SOM assign it to the closest k-means centroid
        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();

            double minDist = Double.MAX_VALUE;
            int clusterCode = 0;
            MetricDistance metric = new EuclideanDistance();
            for(int i=0; i < clusteringResult.size(); ++i) {
                VectorN centroid = VectorN.fromArray(clusteringResult.get(i).getCenter().getPoint());
                double curDist = metric.distanceBetween(centroid, p.getPrototype());
                if(curDist < minDist) {
                    minDist = curDist;
                    clusterCode = i + 1;
                }
            }

            clustering.setClusterAssignment(x, y, clusterCode);
        }

        return clustering;
    }

    private static class PrototypeWrapper implements Clusterable {
        private final PrototypeNeuron prototype;

        public PrototypeWrapper(PrototypeNeuron prototype) {
            this.prototype = prototype;
        }

        @Override
        public double[] getPoint() {
            return prototype.getPrototype().values();
        }
    }
}
