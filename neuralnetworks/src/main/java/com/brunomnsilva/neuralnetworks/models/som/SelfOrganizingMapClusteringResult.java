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

package com.brunomnsilva.neuralnetworks.models.som;

import com.brunomnsilva.neuralnetworks.core.Args;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a clustering result procedure for the prototypes of a {@link SelfOrganizingMap} by an implementation
 * of {@link SelfOrganizingMapClustering}.
 * <br/>
 * The clustering result is a <b>cluster code</b> set for each prototype (by {x,y} index).
 * <br/>
 * Please note that the cluster code <code>0 (zero)</code> has a special meaning: the prototype does not
 * belong to a cluster. Besides this restriction, all other cluster codes must be sequential natural numbers, starting at 1.
 *
 * @see SelfOrganizingMapClustering
 *
 * @author brunomnsilva
 */
public class SelfOrganizingMapClusteringResult {

    /** The originator of the clustering */
    private final SelfOrganizingMap originator;

    /** Cluster code assignments for all prototypes. */
    private final int[][] clusterCodeAssignment;

    /**
     * Default constructor. The internal structure to encode the prototype's cluster code
     * assignments is automatically initialized based on the size of the SelfOrganizingMap.
     * Hence, there is a 1-to-1 mapping of the (x,y) indices of the SelfOrganizingMap prototype's
     * and this internal structure, e.g., for {@link #setClusterAssignment(int, int, int)} and
     * {@link #getClusterAssignment(int, int)}.
     *
     * @param model the originator SelfOrganizingMap of the clustering
     */
    public SelfOrganizingMapClusteringResult(SelfOrganizingMap model) {
        this.originator = model;
        this.clusterCodeAssignment = new int[model.getWidth()][model.getHeight()];
    }

    /**
     * Sets the cluster assignment (code) for a prototype, given its lattice position
     * @param x the x-index of the prototype in the lattice
     * @param y the y-index of the prototype in the lattice
     */
    public void setClusterAssignment(int x, int y, int code) {
        Args.requireInRange(x, "x", 0, originator.getWidth() - 1);
        Args.requireInRange(x, "y", 0, originator.getHeight() - 1);

        clusterCodeAssignment[x][y] = code;
    }

    /**
     * Returns the cluster assignment (code) for a prototype, given its lattice position
     * @param x the x-index of the prototype in the lattice
     * @param y the y-index of the prototype in the lattice
     * @return the cluster assignment (code)
     */
    public int getClusterAssignment(int x, int y) {
        Args.requireInRange(x, "x", 0, originator.getWidth() - 1);
        Args.requireInRange(x, "y", 0, originator.getHeight() - 1);

        return clusterCodeAssignment[x][y];
    }

    /**
     * Returns the reference to the SelfOrganizingMap that originated the clustering
     * @return the SelfOrganizingMap that originated the clustering
     */
    public SelfOrganizingMap getOriginator() {
        return originator;
    }

    /**
     * Returns the number of unique cluster codes present.
     * @return number of cluster codes
     */
    public int getNumberClustersCodes() {
        // Find unique values inside 'clusterCodeAssignment' by using a Set.
        Set<Integer> unique = new HashSet<>();
        for(int i=0; i < clusterCodeAssignment.length; ++i) {
            for(int j=0; j < clusterCodeAssignment[0].length; ++j) {
                unique.add(clusterCodeAssignment[i][j]);
            }
        }
        return unique.size();
    }

    /**
     * Whether this clustering result contains valid cluster assignments.
     * In practical terms, if all cluster codes are zero (0), then the clustering algorithm did not produce any clusters.
     * @return true if this clustering result contains valid cluster assignments; false, otherwise.
     */
    public boolean hasClusters() {
        return getNumberClustersCodes() > 1;
    }
}
