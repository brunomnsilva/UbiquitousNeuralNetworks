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
import com.brunomnsilva.neuralnetworks.models.som.NeighboringFunction;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of (improved) PLSOM, by me, from article.
 *
 * Link to article: https://link.springer.com/article/10.1007/s10489-008-0138-7
 *
 * @author brunomnsilva
 */
public class PLSOM extends StreamingSOM {

    //////////////////////////////////////////////////////////////////////////
    //IMPROVED PLSOM
    private List<VectorN> A;
    private double S;
    private int k;
    private final double neighborhoodRange;

    public PLSOM(int width, int height, int dimensionality, double neighborhoodRange) {
        super(width, height, dimensionality);

        this.neighborhoodRange = neighborhoodRange;

        initPLSOMParameters();
    }

    public PLSOM(int width, int height, int dimensionality, double neighborhoodRange,
                 Lattice lattice, MetricDistance metricDistance) {
        super(width, height, dimensionality, lattice, metricDistance);

        this.neighborhoodRange = neighborhoodRange;

        initPLSOMParameters();
    }

    private void initPLSOMParameters() {
        k = 1 + getDimensionality();
        A = new ArrayList<>();
        S = -1;
    }

    @Override
    public String getImplementationName() {
        return "PLSOM";
    }

    @Override
    public void learn(VectorN input) {
        PrototypeNeuron bmu = bestMatchingUnitFor(input);
        double quantizationError = getMetricDistance().distanceBetween(bmu.getPrototype(), input);

        double epsilon = epsilonValue(input, quantizationError);
        double radius = neighborhoodRange * Math.log( 1 + epsilon * (Math.E-1) );

        for (PrototypeNeuron p : this) {
            double dist = latticeDistanceBetween(bmu, p);

            double neigh = NeighboringFunction.gaussian(dist, radius);

            // Expecting a finite [0, 1] neighborhood function value.
            // We discard updates when the function value is < 0.01 - this has a negligible
            // influence the on final result and is much more performant
            if (neigh > 1 || neigh < 0.01 || Double.isInfinite(neigh) || Double.isInfinite(-neigh)) {
                continue;
            }

            VectorN adjust = input.copy();
            adjust.subtract(p.getPrototype());
            adjust.multiply(epsilon * neigh);

            p.getPrototype().add(adjust);
        }

        prototypesUpdated();
    }

    //////////////////////////////////////////////////////////////////////////
    //IMPROVED PLSOM HELPER METHODS

    private double epsilonValue(VectorN input, double bmuQuantizationError) {
        double s = diameterOfSetUnionWith(input);
        if( s > S) {
            S = s;
            contractSet(input);
            addSet(input);
        }

        return Math.min(bmuQuantizationError / S, 0.5);
    }

    private double diameterOfSetUnionWith(VectorN input) {
        // Calculates the largest distance between any two members
        MetricDistance metricDistance = getMetricDistance();

        List<VectorN> union = new ArrayList<>();
        union.addAll(A);
        union.add(input);

        double maxDist = Double.MIN_VALUE;

        int setLen = union.size();

        if(setLen == 1)
            return 0;

        for(int i=0; i<setLen-1; i++) {
            for(int j=i+1; j<setLen; j++) {
                double dist = metricDistance.distanceBetween(union.get(i), union.get(j));
                if( dist > maxDist) {
                    maxDist = dist;
                }
            }
        }

        return maxDist;
    }

    private void contractSet(VectorN sample) {
        while( A.size() >= k) {
            VectorN closest = closestMemberOfSet(sample);
            A.remove(closest);
        }
    }

    private VectorN closestMemberOfSet(VectorN input) {
        double minDist = Double.MAX_VALUE;
        VectorN closest = null;
        MetricDistance metricDistance = getMetricDistance();

        for (VectorN v : A) {
            double curDist = metricDistance.distanceBetween(v, input);
            if( curDist < minDist) {
                minDist = curDist;
                closest = v;
            }
        }

        return closest;
    }

    private void addSet(VectorN input) {
        A.add(input);
    }

}
