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

/**
 * A clustering of the SOM's prototypes. This abstract class is to be extended by concrete clustering algorithms.
 *
 * @author brunomnsilva
 */
public abstract class SelfOrganizingMapClustering {

    /** The instance of SelfOrganizingMap to be clustered. */
    private SelfOrganizingMap som;

    /**
     * Creates a new instance of a SelfOrganizingMapClustering.
     * @param som the instance of SelfOrganizingMap to be clustered
     */
    public SelfOrganizingMapClustering(SelfOrganizingMap som) {
        this.som = som;
    }

    /**
     * Returns the instance of SelfOrganizingMap to be clustered.
     * @return the instance of SelfOrganizingMap to be clustered
     */
    public SelfOrganizingMap getSelfOrganizingMap() {
        return som;
    }

    /**
     * Returns the SelfOrganizingMapClusteringResult.
     * @see SelfOrganizingMapClusteringResult
     * @return the SelfOrganizingMapClusteringResult
     */
    public abstract SelfOrganizingMapClusteringResult cluster();
}
