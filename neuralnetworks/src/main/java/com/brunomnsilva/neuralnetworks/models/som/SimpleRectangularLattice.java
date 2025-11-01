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
 * An implementation of a simple rectangular lattice.
 * Neurons on one edge are not neighbors of the neurons in the opposite edge.
 *
 * @author brunomnsilva
 */
public class SimpleRectangularLattice extends RectangularLattice {
    @Override
    public double distanceBetween(PrototypeNeuron a, PrototypeNeuron b) {
        double deltaX = b.getIndexX() - a.getIndexX();
        double deltaY = b.getIndexY() - a.getIndexY();

        return StrictMath.sqrt((deltaX*deltaX) + (deltaY*deltaY));
    }

    @Override
    public boolean areNeighbors(PrototypeNeuron a, PrototypeNeuron b) {
        // In a rectangular lattice a cell has 8 neighbors
        int dx = Math.abs( b.getIndexX() - a.getIndexX() );
        int dy = Math.abs( b.getIndexY() - a.getIndexY() );
        return Math.max(dx, dy) <= 1;
    }
}
