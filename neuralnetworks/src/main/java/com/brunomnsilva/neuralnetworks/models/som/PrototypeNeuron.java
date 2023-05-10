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
import com.brunomnsilva.neuralnetworks.core.VectorN;

import java.util.Random;

/**
 * Represents a neuron in a self-organizing map lattice with a prototype vector.
 * The prototype vector is a multidimensional point that represents the neuron's position in the input space.
 *
 * @author brunomnsilva
 */
public class PrototypeNeuron {
    /**
     * The x index of this neuron in the lattice.
     */
    private int xIndex;

    /**
     * The y index of this neuron in the lattice.
     */
    private int yIndex;

    /**
     * The prototype vector of this neuron that represents its position in the input space.
     */
    private VectorN prototype;

    /**
     * Constructs a new prototype neuron with a randomly initialized prototype vector.
     *
     * @param xIndex the x index of this neuron in the lattice.
     * @param yIndex the y index of this neuron in the lattice.
     * @param dimensionality the dimensionality of the input space.
     */
    public PrototypeNeuron(int xIndex, int yIndex, int dimensionality) {
        Args.requireNonNegative(xIndex, "xIndex");
        Args.requireNonNegative(yIndex, "yIndex");
        Args.requireNonNegative(dimensionality, "dimensionality");

        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.prototype = VectorN.random(dimensionality);
    }

    /**
     * Constructs a new prototype neuron with a prototype vector that is initialized using the specified random number generator.
     *
     * @param xIndex the x index of this neuron in the lattice.
     * @param yIndex the y index of this neuron in the lattice.
     * @param dimensionality the dimensionality of the input space.
     * @param rnd the random number generator to use for initializing the prototype vector.
     */
    public PrototypeNeuron(int xIndex, int yIndex, int dimensionality, Random rnd) {
        Args.requireNonNegative(xIndex, "xIndex");
        Args.requireNonNegative(yIndex, "yIndex");
        Args.requireNonNegative(dimensionality, "dimensionality");

        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.prototype = VectorN.random(dimensionality, rnd);
    }

    /**
     * Returns the x index of this neuron in the lattice.
     *
     * @return the x index of this neuron in the lattice.
     */
    public int getIndexX() {
        return xIndex;
    }

    /**
     * Returns the y index of this neuron in the lattice.
     *
     * @return the y index of this neuron in the lattice.
     */
    public int getIndexY() {
        return yIndex;
    }

    /**
     * Returns the prototype vector of this neuron that represents its position in the input space.
     *
     * @return the prototype vector of this neuron.
     */
    public VectorN getPrototype() {
        return prototype;
    }

    /**
     * Sets the prototype vector of this neuron that represents its position in the input space.
     * <br/>
     * This method ensures that the new prototype vector has the same dimensionality as the old one.
     *
     * @param prototype the new prototype vector of this neuron.
     */
    public void setPrototype(VectorN prototype) {
        Args.requireEqual(prototype.dimensions(), "prototype.dimensions()",
                this.prototype.dimensions(), "current dimensionality");

        // Avoid pitfall of using the same VectorN reference for multiple prototypes
        this.prototype = prototype.copy();
    }

    @Override
    public String toString() {
        return String.format("(x = %2d, y = %2d) - %s", xIndex, yIndex, prototype.toString());
    }
}
