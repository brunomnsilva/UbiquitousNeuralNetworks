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

/**
 * Holds the values of a self-organizing map component plane.
 * <br/>
 * Instances of this class can only be created from an existing SOM model.
 * <br/>
 * It may be useful for further processing of component planes, e.g., for feature clustering.
 *
 * @author brunomnsilva
 */
public class ComponentPlane {

    protected double[][] values;
    protected String name;

    /**
     * Constructor to initialize instance.
     * @param values the values for the component plane
     * @param name the name for the component plane
     */
    public ComponentPlane(double[][] values, String name) {
        Args.nullNotPermitted(values, "values");
        Args.nullNotPermitted(name, "name");

        this.values = values;
        this.name = name;
    }

    /**
     * Creates a component plane instance by extracting a plane from an existing self-organizing map model.
     * @param som the self-organizing map model
     * @param componentIndex the index of the plane from the self-organizing map model
     * @param name the name for the component plane
     * @return a component plane
     */
    public static ComponentPlane fromSelfOrganizingMap(SelfOrganizingMap som, int componentIndex, String name) {
        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(name, "name");
        Args.requireNonNegative(componentIndex, "componentIndex");

        double[][] values = new double[som.getWidth()][som.getHeight()];

        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();
            double value = p.getPrototype().values()[componentIndex];

            values[x][y] = value;
        }

        return new ComponentPlane(values, name);
    }

    /**
     * Returns the value at the specified coordinates.
     * @param x the x-coordinate in [0, getWidth [
     * @param y the y-coordinate in [0, getHeight [
     * @return the value at the specified coordinates
     */
    public double getValue(int x, int y) {
        return values[x][y];
    }

    /**
     * Returns the name of the component plane.
     * @return the name of the component plane
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the width of the component plane.
     * @return the width of the component plane
     */
    public int getWidth() {
        return values.length;
    }

    /**
     * Returns the height of the component plane.
     * @return the height of the component plane
     */
    public int getHeight() {
        return values[0].length;
    }

    /**
     * Flattens the component plane, i.e., converts the underlying two-dimensional array into a one-dimensional array.
     * <br/>
     * Involves arranging all the elements of the component plane into a single row, disregarding the original rows and columns structure.
     * The resulting array will have a linear sequence of elements, and the order of elements will follow a row-major order.
     * @return a one-dimensional array of the component plane values
     */
    public double[] flatten() {
        int w = getWidth();
        int h = getHeight();
        double[] array = new double[w * h];

        int index = 0;
        for(int j=0; j < h; ++j) {
            for(int i=0; i < w; ++i) {
                array[index++] = values[i][j];
            }
        }

        return array;
    }
}
