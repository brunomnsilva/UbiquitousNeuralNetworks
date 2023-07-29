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

import java.util.Arrays;
import java.util.Random;

/**
 * Represents a vector in an N-dimensional space.
 *
 * @author brunomnsilva
 */
public class VectorN implements Comparable<VectorN> {

    /**
     * Random number generator for creating random vectors.
     */
    private static final Random rng = new Random();

    /**
     * The components of the vector.
     */
    private final double[] vector;

    /**
     * Private constructor used by the static factory methods.
     *
     * @param array the components of the vector
     */
    private VectorN(double[] array) {
        this.vector = new double[array.length];
        System.arraycopy(array, 0, this.vector, 0, array.length);
    }

    /**
     * Creates a new vector from an array of components.
     *
     * @param array the components of the vector
     * @return the new vector
     */
    public static VectorN fromArray(double[] array) {
        return new VectorN(array);
    }

    /**
     * Creates a new vector from a sequence of components.
     *
     * @param values the components of the vector
     * @return the new vector
     */
    public static VectorN fromValues(double... values) {
        return new VectorN(values);
    }

    /**
     * Creates a new zero vector with the given dimensions.
     *
     * @param dimensions the number of dimensions of the new vector
     * @return the new zero vector
     */
    public static VectorN zeros(int dimensions) {
        Args.requireNonNegative(dimensions, "dimensions");

        double[] zeros = new double[dimensions];
        return fromArray(zeros);
    }

    /**
     * Creates a new vector with the given dimensions, where all elements are set to the specified value.
     *
     * @param dimensions the number of dimensions of the new vector
     * @param val        the value to be replicated in all elements of the vector
     * @return the new vector with replicated elements
     */
    public static VectorN rep(int dimensions, double val) {
        Args.requireNonNegative(dimensions, "dimensions");
        Args.requireFinite(val, "val");

        double[] values = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            values[i] = val;
        }
        return fromArray(values);
    }

    /**
     * Creates a new random vector with the given dimensions, using the default
     * random number generator.
     *
     * @param dimensions the number of dimensions of the new vector
     * @return the new random vector
     */
    public static VectorN random(int dimensions) {
        Args.requireNonNegative(dimensions, "dimensions");

        double[] values = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            values[i] = rng.nextDouble();
        }
        return fromArray(values);
    }

    /**
     * Creates a new random vector with the given dimensions, using the given
     * random number generator.
     *
     * @param dimensions the number of dimensions of the new vector
     * @param rnd the random number generator to use
     * @return the new random vector
     */
    public static VectorN random(int dimensions, Random rnd) {
        Args.requireNonNegative(dimensions, "dimensions");

        double[] values = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            values[i] = rnd.nextDouble();
        }
        return fromArray(values);
    }

    /**
     * Returns a new vector with the absolute value of the minimum component of
     * each corresponding pair of components in the given vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the new vector with the absolute minimum pairwise values
     * @throws IllegalArgumentException if the vectors have different
     *         dimensions
     */
    public static VectorN absMin(final VectorN v1, final VectorN v2) {
        if (v1.dimensions() != v2.dimensions())
            throw new IllegalArgumentException("Vectors differ in size.");

        final double[] vValues = v1.values();
        final double[] minValues = v2.values();

        for (int i = 0; i < vValues.length; i++) {
            if (Math.abs(vValues[i]) < Math.abs(minValues[i]))
                minValues[i] = Math.abs(vValues[i]);
        }
        return VectorN.fromArray(minValues);
    }

    /**
     * Returns a new vector with the minimum component of
     * each corresponding pair of components in the given vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the new vector with the minimum pairwise values
     * @throws IllegalArgumentException if the vectors have different
     *         dimensions
     */
    public static VectorN min(final VectorN v1, final VectorN v2) {
        Args.requireEqual(v1.dimensions(), "v1.dimensions()", v2.dimensions(), "v2.dimensions()");

        final double[] vValues = v1.values();
        final double[] minValues = v2.values();

        for (int i = 0; i < vValues.length; i++) {
            if (vValues[i] < minValues[i])
                minValues[i] = vValues[i];
        }
        return VectorN.fromArray(minValues);
    }

    /**
     * Returns a new vector with the maximum component of
     * each corresponding pair of components in the given vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the new vector with the maximum pairwise values
     * @throws IllegalArgumentException if the vectors have different
     *         dimensions
     */
    public static VectorN max(final VectorN v1, final VectorN v2) {
        Args.requireEqual(v1.dimensions(), "v1.dimensions()", v2.dimensions(), "v2.dimensions()");

        final double[] vValues = v1.values();
        final double[] maxValues = v2.values();

        for (int i = 0; i < vValues.length; i++) {
            if (vValues[i] > maxValues[i])
                maxValues[i] = vValues[i];
        }
        return VectorN.fromArray(maxValues);
    }

    /**
     * Computes the argmax of this vector.
     * <br />
     * E.g., if <code>v = [1,5,3,2]</code>, then the method returns <code>[0, 1, 0, 0]</code>
     *
     * @param v the vector
     * @return a new vector where only the maximum value is set to 1 and all other values are set to 0
     */
    public static VectorN argmax(VectorN v) {
        if(v.dimensions() == 0) return VectorN.zeros(0);

        // Find index of maximum
        int maxIndex = 0;
        for(int i=1; i < v.dimensions(); ++i) {
            if(v.get(i) > v.get(maxIndex)) {
                maxIndex = i;
            }
        }
        // Return zeroed vector with 1 at maximum index
        VectorN argmax = VectorN.zeros(v.dimensions());
        argmax.vector[maxIndex] = 1;
        return argmax;
    }

    public VectorN copy() {
        return new VectorN(this.vector);
    }

    public void fill(double value) {
        // No Arrays.fill(double[], double) method. Must do it manually
        for (int i = 0; i < vector.length; i++) {
            vector[i] = value;
        }
    }

    /**
     * Randomizes the components of this vector with values in <code>[0, 1]</code>.
     */
    public void randomize() {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = rng.nextDouble();
        }
    }

    /**
     * Returns the components of this vector.
     * <br/>
     * Changes to the returned array are not reflected in the vector.
     *
     * @return an array with the components of this vector.
     */
    public double[] values() {
        return Arrays.copyOf(vector, vector.length);
    }

    /**
     * Returns a component of this vector.
     *
     * @param index index of the component
     * @return the component value
     * @throws ArrayIndexOutOfBoundsException if index is invalid for this vector
     */
    public double get(int index) {
        return vector[index];
    }

    /**
     * Returns the number of components (dimensions) of this vector.
     *
     * @return the number of dimensions of this vector
     */
    public int dimensions() {
        return vector.length;
    }

    /**
     * Adds the specified value to every component of the vector.
     * <br>
     * You can use this method to subtract, if the specified value is negative.
     * @param val the value to add to every component of the vector
     */
    public void add(double val) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] += val;
        }
    }
    
    /**
     * Component-wise addition with another vector.
     *
     * @param v the vector to add
     */
    public void add(VectorN v) {
        Args.nullNotPermitted(v, "v");

        add(v.vector);
    }

    /**
     * Add this vector component-wise by the given array.
     *
     * @param array the array to apply to this vector
     * @throws IllegalArgumentException if the length of the given array does not match
     *         the number of dimensions in this vector
     */
    public void add(double[] array) {
        Args.requireEqual(this.dimensions(), "this.dimensions()",
                array.length, "array.length");

        for (int i = 0; i < array.length; i++) {
            vector[i] += array[i];
        }
    }

    /**
     * Component-wise subtraction with another vector.
     *
     * @param v the vector to subtract
     */
    public void subtract(VectorN v) {
        Args.nullNotPermitted(v, "v");
        subtract(v.vector);
    }

    /**
     * Subtract this vector component-wise by the given array.
     *
     * @param array the array to apply to this vector
     * @throws IllegalArgumentException if the length of the given array does not match
     *         the number of dimensions in this vector
     */
    public void subtract(double[] array) {
        Args.requireEqual(this.dimensions(), "this.dimensions()",
                array.length, "array.length");

        for (int i = 0; i < array.length; i++) {
            vector[i] -= array[i];
        }
    }

    /**
     * Divides every component of the vector by the specified divider.
     *
     * @param divider the value to divide every component by
     */
    public void divide(double divider) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] /= divider;
        }
    }

    /**
     * Component-wise division with another vector.
     *
     * @param v the vector to divide by
     */
    public void divide(VectorN v) {
        Args.nullNotPermitted(v, "v");

        divide(v.vector);
    }

    /**
     * Divides this vector component-wise by the given array of dividers.
     *
     * @param dividers the array of dividers to apply to this vector
     * @throws IllegalArgumentException if the length of the given array does not match
     *         the number of dimensions in this vector
     */
    public void divide(double[] dividers) {
        Args.requireEqual(this.dimensions(), "this.dimensions()",
                dividers.length, "dividers.length");

        for (int i = 0; i < dividers.length; i++) {
            vector[i] /= dividers[i];
        }
    }

    /**
     * Multiplies every component of the vector by the specified multiplier.
     *
     * @param multiplier the value to multiply every component by
     */
    public void multiply(double multiplier) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] *= multiplier;
        }
    }

    /**
     * Component-wise multiplication with another vector.
     *
     * @param v the vector to multiply
     */
    public void multiply(VectorN v) {
        Args.nullNotPermitted(v, "v");

        multiply(v.vector);
    }

    /**
     * Multiplies this vector component-wise by the given array of multipliers.
     *
     * @param multipliers the array of multipliers to apply to this vector
     * @throws IllegalArgumentException if the length of the given array does not match
     *         the number of dimensions in this vector
     */
    public void multiply(double[] multipliers) {
        Args.requireEqual(this.dimensions(), "this.dimensions()", multipliers.length, "multipliers.length");

        for (int i = 0; i < multipliers.length; i++) {
            vector[i] *= multipliers[i];
        }
    }

    /**
     * Raises each component of the vector to the power of <code>exp</code>.
     *
     * @param exp the exponent to raise each component of the vector to
     * @throws IllegalArgumentException if the exponent is not a finite number
     */
    public void pow(double exp) {
        Args.requireFinite(exp, "exp");

        for (int i = 0; i < vector.length; i++) {
            vector[i] = StrictMath.pow(vector[i], exp) ;
        }
    }

    /**
     * Square root each component of the vector.
     * <br/>
     * This operation will fail if any of the components of the vector is negative.
     * In this case the vector will remain unchanged.
     * @throws IllegalStateException if the vector has negative components
     */
    public void sqrt() {
        // Validate components
        VectorN copy = this.copy();
        for (int i = 0; i < copy.vector.length; ++i) {
            if( copy.vector[i] < 0)
                throw new IllegalStateException("The vector has negative components.");

            copy.vector[i] = StrictMath.sqrt( copy.vector[i] ) ;
        }

        for (int i=0; i < vector.length; ++i) {
            vector[i] = copy.vector[i];
        }
    }

    /**
     * Computes the mean value of the components of the vector.
     * @return the mean value of its components
     */
    public double mean() {
        double sum = 0;
        for(int i=0; i < vector.length; ++i) {
            sum += vector[i];
        }

        return sum / vector.length;
    }

    /**
     * Computes the standard deviation of the components of the vector.
     * @return the standard deviation of its components
     */
    public double std() {
        double mean = mean();

        double sumdiffs = 0;
        for(int i=0; i < vector.length; ++i) {
            sumdiffs += (vector[i] - mean) * (vector[i] - mean);
        }

        return Math.sqrt( sumdiffs / vector.length );
    }


    /**
     * Computes the dot product with another vector.
     *
     * @param v the other vector
     * @return the dot product
     */
    public double dot(VectorN v) {
        Args.nullNotPermitted(v, "v");
        Args.requireEqual(this.dimensions(), "this.dimensions()",
                v.dimensions(), "v.dimensions()");

        double result = 0;
        for (int i = 0; i < vector.length; i++) {
            result += vector[i] * v.vector[i];
        }
        return result;
    }

    /**
     * Computes the magnitude of this vector.
     *
     * @return the magnitude of this vector
     */
    public double magnitude() {
        return Math.sqrt(dot(this));
    }

    /**
     * Computes the (euclidean) distance between this vector and another.
     *
     * @param v the other vector
     * @return the distance between the two vectors
     */
    public double distance(VectorN v) {
        Args.nullNotPermitted(v, "v");

        int len = v.dimensions();
        double distance = 0;
        for(int i=0; i < len; i++) {
            distance += (v.get(i) - this.get(i)) * (v.get(i) - this.get(i));
        }

        return StrictMath.sqrt( distance ) ;
    }

    /**
     * Returns a new VectorN that is this vector with its magnitude normalized to 1.
     *
     * @return a unit vector
     */
    public VectorN normalize() {
        double magnitude = magnitude();
        if (magnitude == 0) {
            return this.copy();
        }
        double[] newValues = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            newValues[i] = vector[i] / magnitude;
        }
        return VectorN.fromArray(newValues);
    }

    @Override
    public String toString() {
        return Arrays.toString(vector);
    }

    @Override
    public int compareTo(VectorN other) {
        Args.requireEqual(this.dimensions(), "this.dimensions()",
                          other.dimensions(), "other.dimensions()");

        return Arrays.compare(vector, other.vector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorN vectorN = (VectorN) o;
        return Arrays.equals(vector, vectorN.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }
}
