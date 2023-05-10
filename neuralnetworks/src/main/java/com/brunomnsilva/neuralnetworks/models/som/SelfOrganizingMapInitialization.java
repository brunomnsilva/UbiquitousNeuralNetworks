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

import java.util.List;
import java.util.Random;

/**
 * Contains different variants/methods for the initialization of the
 * Self-Organizing Map (SOM) prototypes.
 * <br/>
 * Please note that when a SOM is created its prototypes are initially randomized
 * with its components between [0, 1].
 */
public class SelfOrganizingMapInitialization {

    /**
     * Performs a random initialization of the prototypes for a given SOM, but from
     * a random generator.
     *
     * If the generator is set with a fixed seed, then
     * the randomization of prototypes will always be the same; this may be ideal
     * for testing purposes of different kinds.
     *
     * @param som the self-organizing map to randomize its prototypes
     * @param rnd the random generator to use
     *
     * @throws IllegalArgumentException if <code>SOM</code> or <code>rnd</code> is <i>null</i>;
     */
    public static void randomFromGenerator(SelfOrganizingMap som, Random rnd) {
        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(rnd, "rnd");

        int dimensionality = som.getDimensionality();
        for(int w=0; w < som.getWidth(); ++w) {
            for(int h=0; h < som.getHeight(); ++h) {
                VectorN random = VectorN.random(dimensionality, rnd);
                som.get(w,h).setPrototype(random);
            }
        }
    }

    /**
     * Performs an initialization of the prototypes of a given SOM using a set of vectors.
     * <br/>
     * The vectors are randomly sampled from <code>vectors</code>.
     *
     * @param som the self-organizing map to randomize its prototypes
     * @param vectors the set of vectors to sample from
     *
     * @throws IllegalArgumentException if <code>SOM</code> or <code>vectors</code> is <i>null</i>;
     *                                  if the SOM and vectors have different dimensionality
     */
    public static void fromVectors(SelfOrganizingMap som, List<VectorN> vectors) {
        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(vectors, "vectors");
        Args.requireGreaterEqualThan(vectors.size(), "vectors.size()", 1);

        int vectorDimensionality = vectors.get(0).dimensions();
        Args.requireEqual(som.getDimensionality(), "som.getDimensionality()",
                vectorDimensionality, "vectors dimensionality");

        int vectorsSize = vectors.size();

        // Perform a random sampling from the list of vectors
        Random rnd = new Random();
        for(int w=0; w < som.getWidth(); ++w) {
            for(int h=0; h < som.getHeight(); ++h) {
                VectorN random = vectors.get( rnd.nextInt(vectorsSize) );
                som.get(w,h).setPrototype(random); // Checks dimensionality match
            }
        }
    }
}
