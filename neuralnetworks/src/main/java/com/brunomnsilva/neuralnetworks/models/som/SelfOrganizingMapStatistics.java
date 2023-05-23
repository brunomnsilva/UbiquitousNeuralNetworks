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

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;

/**
 * Computes the standard SOM statistics:
 * <ul>
 *     <li>Quantization Error - is a measure of the average distance between each input vector and its best matching unit (BMU) on the map</li>
 *     <li>Topographic error - measures how often neighboring BMUs in the map are not also neighbors in the input space, indicating a distortion in the topology of the map.
 *     A topographic error of <code>0</code> means "perfect" topology, while <code>1</code> means that no topology ordering was achieved.</li>
 * </ul>
 * Instances of this class are solely created through {@link #compute(SelfOrganizingMap, Dataset)}.
 *
 * @author brunomnsilva
 */
public class SelfOrganizingMapStatistics {

    /** The computed quantization error. */
    private final double quantizationError;

    /** The computed topographic error. */
    private final double topographicError;

    /**
     * Private constructor that initializes a SelfOrganizingMapStatistics instance.
     * @param quantizationError the computed quantization error
     * @param topographicError the computed topographic error
     */
    private SelfOrganizingMapStatistics(double quantizationError, double topographicError) {
        this.quantizationError = quantizationError;
        this.topographicError = topographicError;
    }

    /**
     * Computes the statistics and returns an instance of SelfOrganizingMapStatistics.
     * @param som the self-organizing map to compute the statistics
     * @param dataset the dataset to compute the statistics
     * @return an instance of SelfOrganizingMapStatistics with computed values
     */
    public static SelfOrganizingMapStatistics compute(SelfOrganizingMap som, Dataset dataset) {
        double qe = 0, te = 0;

        for (DatasetItem item : dataset) {
            VectorN input = item.getInput();
            PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

            // Compute quantization error as the distance between
            // the input and the bmu's prototype
            double error = som.getMetricDistance().distanceBetween(bmu.getPrototype(), input);
            qe += error;

            // Compute the topographic error for the input
            te += topographicErrorForInput(input, som);
        }

        qe /= dataset.size();
        te /= dataset.size();

        return new SelfOrganizingMapStatistics(qe, te);
    }

    /**
     * Returns the computed quantization error.
     * @return the computed quantization error
     */
    public double getQuantizationError() {
        return quantizationError;
    }

    /**
     * Returns the computed topographic error.
     * @return the computed topographic error
     */
    public double getTopographicError() {
        return topographicError;
    }

    @Override
    public String toString() {
        return String.format("Quantization Error = %.3f | Topographic Error = %.3f",
                quantizationError, topographicError);
    }

    private static int topographicErrorForInput(VectorN input, SelfOrganizingMap som) {
        double minDist1 = Double.MAX_VALUE;
        double minDist2 = Double.MAX_VALUE;
        PrototypeNeuron closest1 = null;
        PrototypeNeuron closest2 = null;

        for (PrototypeNeuron p : som) {
            double dist = som.getMetricDistance().distanceBetween(p.getPrototype(), input);
            if (dist < minDist1) {
                minDist2 = minDist1;
                closest2 = closest1;
                minDist1 = dist;
                closest1 = p;
            } else if (dist < minDist2) {
                minDist2 = dist;
                closest2 = p;
            }
        }

        return som.getLattice().areNeighbors(closest1, closest2) ? 0 : 1;
    }
}
