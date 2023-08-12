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
 * <br/>
 * The {@link SelfOrganizingMap} uses an instance of an implementing class to define
 * its internal lattice. Also, it "injects" the size of the model by calling {@link Lattice#setSize(int, int)}
 * in the constructor, so no need to call this method explicitly.
 *
 * @author brunomnsilva
 */
public abstract class Lattice {

    private int width = -1;
    private int height = -1;

    /**
     * Sets the size of the lattice. This may be needed for some topologies to compute
     * the required distances and neighbor checking.
     * @param width the width of the lattice
     * @param height the height of the lattice
     */
    protected void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of the lattice.
     * @return the width of the lattice
     * @throws IllegalStateException if the lattice size was not set
     */
    public int getWidth() {
        if(width < 0) {
            throw new IllegalStateException("Lattice height was not set.");
        }

        return width;
    }

    /**
     * Returns the height of the lattice.
     * @return the height of the lattice
     * @throws IllegalStateException if the lattice size was not set
     */
    public int getHeight() {
        if(height < 0) {
            throw new IllegalStateException("Lattice width was not set.");
        }

        return height;
    }

    /**
     * Calculates the distance between two prototype neurons in the lattice.
     *
     * @param a the first prototype neuron
     * @param b the second prototype neuron
     * @return the distance between the two neurons
     */
    public abstract double distanceBetween(PrototypeNeuron a, PrototypeNeuron b);

    /**
     * Checks if two prototype neurons are neighbors in the lattice.
     *
     * @param a the first prototype neuron
     * @param b the second prototype neuron
     * @return true if the two neurons are neighbors, false otherwise
     */
    public abstract boolean areNeighbors(PrototypeNeuron a, PrototypeNeuron b);
}
