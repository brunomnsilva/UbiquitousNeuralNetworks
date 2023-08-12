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

import com.brunomnsilva.neuralnetworks.core.AbstractObservable;
import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.VectorN;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A Self-Organizing Map is composed by a 2d grid (lattice) of data prototypes (neurons).
 * <br/>
 * All neurons have neighborhood relationships and this will result in the topological
 * ordering of the data prototypes during training.
 * <br/>
 * Each data prototype is of the same dimensionality of the training data; during training
 * a high-dimensionality mapping (of training data) to 2-dimensions (the lattice) is performed,
 * with the following properties:
 *  - topological ordering of the data prototypes;
 *  - density mapping of the training data underlying distribution.
 * <br/>
 * More details can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 17.
 *
 * @author brunomnsilva
 */
public abstract class SelfOrganizingMap extends AbstractObservable
        implements Iterable<PrototypeNeuron> {

    protected int width, height, dimensionality;

    // Both structures will contain the same prototypes (references)
    // One is more useful for iterations (List), while the other (2d-array)
    // is more efficient for index-based direct access
    protected List<PrototypeNeuron> prototypesList;
    protected PrototypeNeuron[][] prototypeGrid;

    private final MetricDistance metricDistance;

    private final Lattice lattice;

    /**
     * Instantiates a new self-organizing map (SOM).
     * @param width the width of the 2d SOM lattice
     * @param height the height of the 2d SOM lattice
     * @param dimensionality the dimensionality of the SOM prototypes
     * @param lattice the lattice shape
     * @param metricDistance the metric distance to use to compute the best matching unit
     * @throws IllegalArgumentException if width, height or dimensionality are not greater than 0;
     *                                  if lattice or metricDistance are null.
     */
    public SelfOrganizingMap(int width, int height, int dimensionality,
                             Lattice lattice, MetricDistance metricDistance) {
        Args.requireGreaterEqualThan(width, "width", 0);
        Args.requireGreaterEqualThan(height, "height", 0);
        Args.requireGreaterEqualThan(dimensionality, "dimensionality", 0);

        this.width = width;
        this.height = height;
        this.dimensionality = dimensionality;

        this.lattice = lattice;
        // 'Inject' lattice size
        this.lattice.setSize(width, height);

        this.metricDistance = metricDistance;

        this.prototypesList = new ArrayList<>();
        this.prototypeGrid = new PrototypeNeuron[width][height];

        for(int w=0; w < width; ++w) {
            for(int h=0; h < height; ++h) {
                // The prototype constructor performs a random initialization (0,1)
                // Better than to leave them with zeros initially
                PrototypeNeuron p = new PrototypeNeuron(w, h, dimensionality);
                prototypeGrid[w][h] = p;
                prototypesList.add(p);
            }
        }
    }

    /**
     * Instantiates a new self-organizing map (SOM) with default hexagonal shape lattice
     * and euclidean metric distance.
     *
     * @param width the width of the SOM lattice
     * @param height the height of the SOM lattice
     * @param dimensionality the dimensionality of the SOM prototypes
     * @throws IllegalArgumentException if width, height or dimensionality are not greater than 0
     */
    public SelfOrganizingMap(int width, int height, int dimensionality) {
        this(width, height, dimensionality, new SimpleHexagonalLattice(), new EuclideanDistance());
    }

    /**
     * Returns the prototype neuron (reference) at a lattice location.
     * @param xIndex x grid index in [0, width[.
     * @param yIndex y grid index in [0, height[.
     * @return the prototype
     * @throws IllegalArgumentException if any of the indices are invalid.
     */
    public PrototypeNeuron get(int xIndex, int yIndex) {
        Args.requireInRange(xIndex, "xIndex", 0, width);
        Args.requireInRange(yIndex, "yIndex", 0, height);

        return prototypeGrid[xIndex][yIndex];
    }

    /**
     * Returns the current lattice shape.
     * @return the lattice
     */
    public Lattice getLattice() {
        return lattice;
    }

    /**
     * Returns the current metric distance.
     * @return the metric distance
     */
    public MetricDistance getMetricDistance() {
        return metricDistance;
    }

    /**
     * Computes the lattice distance between two prototype neurons.
     * It delegates the computation to the current <i>lattice</i> shape.
     * @param a prototype neuron #1
     * @param b prototype neuron #2
     * @return the lattice distance
     */
    public final double latticeDistanceBetween(PrototypeNeuron a, PrototypeNeuron b) {
        return lattice.distanceBetween(a, b);
    }

    /**
     * Computes the distance between two prototype's vectors.
     * It delegates the computation to the current <i>metric distance</i>.
     * @param a prototype neuron #1
     * @param b prototype neuron #2
     * @return the vector distance
     */
    public final double distanceBetweenPrototypes(PrototypeNeuron a, PrototypeNeuron b) {
        return metricDistance.distanceBetween(a.getPrototype(), b.getPrototype());
    }

    /**
     * Computes the Best Matching Unit (BMU) for an <code>input</code>.
     * This is the prototype neuron that is closest to the <i>input</i>
     * according to the current <i>distance metric</i>.
     * @param input the input
     * @return the BMU for the input
     */
    public final PrototypeNeuron bestMatchingUnitFor(VectorN input) {
        PrototypeNeuron bmu = prototypesList.get(0);
        double minDist = metricDistance.distanceBetween(bmu.getPrototype(), input);

        for (PrototypeNeuron p : prototypesList) {
            double dist = metricDistance.distanceBetween(p.getPrototype(), input);
            if(dist < minDist) {
                bmu = p;
                minDist = dist;
            }
        }
        return bmu;
    }

    /**
     * Returns the width of the 2d SOM lattice.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the 2d SOM lattice.
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the dimensionality of the prototypes.
     * @return the dimensionality
     */
    public int getDimensionality() {
        return dimensionality;
    }

    /**
     * Returns a name describing this implementation/variant of the SelfOrganizingMap.
     * @return a name describing this implementation/variant of the SelfOrganizingMap
     */
    public abstract String getImplementationName();

    @Override
    public final Iterator<PrototypeNeuron> iterator() {
        return prototypesList.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%s (%d x %d x %d) | %s | %s: \n",
                getImplementationName(), width, height, dimensionality,
                lattice.getClass().getSimpleName(),
                metricDistance.getClass().getSimpleName()));
        for (PrototypeNeuron p : prototypesList) {
            sb.append(p).append("\n");
        }

        return sb.toString();
    }

    /**
     * Used by a caller to signal that the SOM has changed its state.
     * This will notify all registered observers.
     * <br/>
     * No verification is made to check if anything has really changed.
     */
    public void prototypesUpdated() { //TODO: change name of method?
        notifyObservers();
    }
}
