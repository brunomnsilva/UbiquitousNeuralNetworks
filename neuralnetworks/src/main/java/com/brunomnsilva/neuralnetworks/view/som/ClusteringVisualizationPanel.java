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

package com.brunomnsilva.neuralnetworks.view.som;

import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapClusteringResult;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import org.jfree.graphics2d.Args;

/**
 * An implementation of the visualization of a SelfOrganizingMapClusteringResult. This visualization merely depicts
 * the assigned prototype classes derived from the performed clustering.
 * <br/>
 * If the SelfOrganizingMapClusteringResult does not have any valid cluster information,
 * this visualization will show an error message.
 *
 * @see SelfOrganizingMapClusteringResult
 *
 * @author brunomnsilva
 */
public class ClusteringVisualizationPanel extends AbstractVisualizationPanel {

    /** Additional reference to the SelfOrganizingMapClusteringResult to visualize. */
    private final SelfOrganizingMapClusteringResult clustering;

    /**
     * Default constructor.
     * @param som the SelfOrganizingMap that originated the clustering
     * @param clustering the SelfOrganizingMapClusteringResult to visualize
     * @throws IllegalArgumentException if the <code>som</code> was not the originator of <code>clustering</code>
     */
    public ClusteringVisualizationPanel(SelfOrganizingMap som, SelfOrganizingMapClusteringResult clustering) {
        super(som, "Clustering of Prototypes");

        Args.nullNotPermitted(clustering, "clustering");

        if(clustering.getOriginator() != som) {
            throw new IllegalArgumentException("This 'clustering' was not obtained from this 'som'.");
        }

        this.clustering = clustering;

        if(!clustering.hasClusters()) {
            setErrorMessage("Clustering did not produce any clusters.");
        }
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        // If there are not clustering results to show, this was treated in the constructor
        // by setting an error message to the visualization. This code will not run with the error set.

        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();

            int clusterCode = clustering.getClusterAssignment(x, y);

            grid.set(clusterCode, x, y);
        }

        // Set number of levels, knowing each cluster has a number >= 1
        // It is expected for all prototypes to have an assigned cluster code != 0
        getColorScalePanel().setNumberLevels(clustering.getNumberClustersCodes());
    }

    @Override
    protected String description() {
        return "Depicts a clustering of the prototypes. Each color is a cluster, where zero means no cluster assignment.";
    }
}
