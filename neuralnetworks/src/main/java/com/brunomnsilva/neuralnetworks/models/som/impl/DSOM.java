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

package com.brunomnsilva.neuralnetworks.models.som.impl;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.models.som.Lattice;
import com.brunomnsilva.neuralnetworks.models.som.MetricDistance;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;

/**
 * Implementation of Dynamic SOM (DSOM), by me, from article.
 *
 * Link to article: https://www.sciencedirect.com/science/article/abs/pii/S0925231211000713
 *
 * @author brunomnsilva
 */
public class DSOM extends StreamingSOM {

    /** The Plasticity parameter. */
    private final double plasticity;

    /** The Epsilon parameter. */
    private final double epsilon;

    public DSOM(int width, int height, int dimensionality, double plasticity, double epsilon) {
        super(width, height, dimensionality);
        this.plasticity = plasticity;
        this.epsilon = epsilon;
    }

    public DSOM(int width, int height, int dimensionality, double plasticity, double epsilon,
                Lattice lattice, MetricDistance metricDistance) {
        super(width, height, dimensionality, lattice, metricDistance);
        this.plasticity = plasticity;
        this.epsilon = epsilon;
    }


    @Override
    public String getImplementationName() {
        return "DSOM";
    }

    @Override
    public void learn(VectorN input) {
        PrototypeNeuron bmu = bestMatchingUnitFor(input);

        for (PrototypeNeuron p : this) {
            double distGrid = latticeDistanceBetween(bmu, p);
            double distPrototype = distanceBetweenPrototypes(bmu, p);
            double quantizationError = getMetricDistance().distanceBetween(bmu.getPrototype(), input);

            double neigh = Math.exp( (-(distGrid * distGrid) / (quantizationError * quantizationError))
                    * (1/(plasticity * plasticity)));

            // Expecting a finite [0, 1] neighborhood function value.
            // We discard updates when the function value is < 0.01 - this has a negligible
            // influence the on final result and is much more performant
            if (neigh > 1 || neigh < 0.01 || Double.isInfinite(neigh) || Double.isInfinite(-neigh)) {
                continue;
            }

            double scaling = epsilon * distPrototype * neigh;

            VectorN adjust = input.copy();
            adjust.subtract(p.getPrototype());
            adjust.multiply(scaling);

            p.getPrototype().add(adjust);
        }

        prototypesUpdated();
    }
}
