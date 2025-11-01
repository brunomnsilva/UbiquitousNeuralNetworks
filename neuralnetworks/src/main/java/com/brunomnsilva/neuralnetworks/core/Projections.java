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

package com.brunomnsilva.neuralnetworks.core;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 * A utility class for data projections.
 *
 * @author brunomnsilva
 */
public class Projections {

    /**
     * Creates a new PCA data projection from <code>data</code>.
     * <br/>
     * The expected form of <code>data</code> is <code>data[nSamples][dimensionality]</code>.
     * @param data the data from which to create a PCA projection
     * @return a new PCA data projection
     */
    public static PCA PCAfrom2dArray(double[][] data) {
        Args.nullNotPermitted(data, "data");

        RealMatrix dataMatrix = MatrixUtils.createRealMatrix(data);

        // Compute mean vector
        Mean meanCalculator = new Mean();

        // Calculate the column means of the data
        double[] columnMeans = new double[dataMatrix.getColumnDimension()];
        for (int i = 0; i < dataMatrix.getColumnDimension(); i++) {
            double[] column = dataMatrix.getColumn(i);
            columnMeans[i] = meanCalculator.evaluate(column);
        }

        // Mean center data
        for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
            for (int j = 0; j < dataMatrix.getColumnDimension(); j++) {
                dataMatrix.setEntry(i, j, dataMatrix.getEntry(i, j) - columnMeans[j]);
            }
        }

        // Compute PCA
        Covariance covariance = new Covariance(dataMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition eigenDecomposition = new EigenDecomposition(covarianceMatrix);

        return new PCA(columnMeans, eigenDecomposition.getV());
    }


    /**
     * Represents a PCA data projection that can be used to project new data points.
     */
    public static class PCA {
        private double[] meanValues;
        private RealMatrix projectionMatrix;

        private PCA(double[] meanValues, RealMatrix projectionMatrix) {
            this.meanValues = meanValues;
            this.projectionMatrix = projectionMatrix;
        }

        /**
         * Using the current data projection information, projects <code>data</code> in <code>dimensions</code>.
         * <br/>
         * This is useful when you've created a PCA projection from e.g., N-dimensional data and you want to
         * project data in 2-, 3- dimensions.
         * @param data data points to project, e.g., <code>data[N][dim]</code>
         * @param dimensions number of dimensions to project the data
         * @return the projected data points, i.e., <code>arr[N][dimensions]</code>
         */
        public double[][] project(double[][] data, int dimensions) {
            Args.nullNotPermitted(data, "data");

            double[][] projected = new double[data.length][dimensions];

            for(int i=0; i < projected.length; ++i) {
                projected[i] = project(data[i], dimensions);
            }

            return projected;
        }

        /**
         * Using the current data projection information, projects a single <code>sample</code> in <code>dimensions</code>.
         * <br/>
         * This is useful when you've created a PCA projection from e.g., N-dimensional data and you want to
         * project data in 2-, 3- dimensions.
         * @param sample data point to project, e.g., <code>sample[dim]</code>
         * @param dimensions number of dimensions to project the data
         * @return the projected data points, i.e., <code>arr[dimensions]</code>
         */
        public double[] project(double[] sample, int dimensions) {
            Args.requireEqual(sample.length, "sample.length",
                    meanValues.length, "pca projection length");
            Args.requireInRange(dimensions, "dimensions", 1, meanValues.length);

            // mean center sample
            double[] centered = subtract(sample, meanValues);

            // Project onto specified 'dimensions'
            RealMatrix data = MatrixUtils.createRowRealMatrix(centered);
            RealMatrix projected = data.multiply(projectionMatrix.getSubMatrix(0, projectionMatrix.getRowDimension() - 1,
                    0, dimensions - 1));

            return projected.getRow(0);
        }
    }

    /**
     * Auxiliary method used for mean-centering data.
     * @param a first array
     * @param b second array
     * @return returns an array that is the pair-wise subtraction of [a - b]
     */
    private static double[] subtract(double[] a, double[] b) {
        Args.requireEqual(a.length, "a.length", b.length, "b.length");

        double[] new_vec = new double[a.length];
        for (int i = 0; i < new_vec.length; i++) {
            new_vec[i] = a[i] - b[i];
        }
        return new_vec;
    }

}
