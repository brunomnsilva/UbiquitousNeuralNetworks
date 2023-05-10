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
 * A lattice is a set of neurons organized in a spatial arrangement.
 *
 * @author brunomnsilva
 */
public interface Lattice {

    /**
     * Calculates the distance between two prototype neurons in the lattice.
     *
     * @param a the first prototype neuron
     * @param b the second prototype neuron
     * @return the distance between the two neurons
     */
    double distanceBetween(PrototypeNeuron a, PrototypeNeuron b);

    /**
     * Checks if two prototype neurons are neighbors in the lattice.
     *
     * @param a the first prototype neuron
     * @param b the second prototype neuron
     * @return true if the two neurons are neighbors, false otherwise
     */
    boolean areNeighbors(PrototypeNeuron a, PrototypeNeuron b);
}