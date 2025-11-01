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
 * A utility class for Self-Organizing Maps. Contains mainly data transformations.
 *
 * @author brunomnsilva
 */
public class SelfOrganizingMapUtils {

    /**
     * Returns the prototypes of a SelfOrganizingMap in the form of an array.
     * <br/>
     * The shape of the returned array is: <code>arr[nPrototypes][dimensionality]</code>
     * @param som the SelfOrganizingMap instance
     * @return the prototypes of a SelfOrganizingMap in the form of an array
     */
    public static double[][] prototypesTo2dArray(SelfOrganizingMap som) {
        int width = som.getWidth();
        int height = som.getHeight();
        int dimensionality = som.getDimensionality();
        double[][] data = new double[width * height][dimensionality];

        int i = 0;
        for (PrototypeNeuron p : som) {
            data[i++] = p.getPrototype().values();
        }

        return data;
    }

    /**
     * Returns a short description of a SelfOrganizingMap model.
     * @param som the SelfOrganizingMap model
     * @return a short description of a SelfOrganizingMap model
     */
    public static String generateShortDescription(SelfOrganizingMap som) {
        return String.format("%s (%d x %d x %d)", som.getImplementationName(),
                som.getWidth(), som.getHeight(), som.getDimensionality());
    }
}
