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

import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapClustering;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapClusteringResult;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a DBSCAN clustering of prototypes.
 * <br/>
 * It's simply a wrapper for the 'org.apache.commons.math3.ml.clustering.DBSCANClusterer'.
 */
public class DBSCANClustering extends SelfOrganizingMapClustering {

    /** Epsilon parameter. */
    private final double eps;
    /** minPoints parameter. */
    private final int minPts;

    /**
     * Creates a new instance of DBSCANClustering.
     * @param som the SelfOrganizingMap to cluster its prototypes.
     * @param eps the value of the <i>epsilon</i> parameter
     * @param minPts the value of the <i>minPoints</i> parameter
     */
    public DBSCANClustering(SelfOrganizingMap som, double eps, int minPts) {
        super(som);
        this.eps = eps;
        this.minPts = minPts;
    }

    @Override
    public SelfOrganizingMapClusteringResult cluster() {
        SelfOrganizingMap som = getSelfOrganizingMap();
        // Wrap prototypes
        List<PrototypeWrapper> clusterInput = new ArrayList<>(som.getWidth() * som.getHeight());
        for (PrototypeNeuron p : som) {
            clusterInput.add( new PrototypeWrapper(p) );
        }

        DBSCANClusterer<PrototypeWrapper> clusterer = new DBSCANClusterer<>(eps, minPts);
        List<Cluster<PrototypeWrapper>> clusteringResult = clusterer.cluster(clusterInput);

        SelfOrganizingMapClusteringResult clustering = new SelfOrganizingMapClusteringResult(som);

        // Note that unassigned prototype's cluster codes wil retain the initialized value of zero (0).
        // This signals that the prototype was not clustered and was considered 'noise' by this clustering algorithm.
        for(int i=0; i < clusteringResult.size(); ++i) {
            Cluster<PrototypeWrapper> result = clusteringResult.get(i);

            List<PrototypeWrapper> members = result.getPoints();
            for (PrototypeWrapper pw : members) {
                int x = pw.prototype.getIndexX();
                int y = pw.prototype.getIndexY();

                clustering.setClusterAssignment(x, y, (i + 1));
            }
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
