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
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;

/**
 * A possible state of the Ubiquitous Self-Organizing Map (UbiSOM).
 * <br/>
 * Each state computes the learning parameters differently.
 *
 * @see UbiSOMStateOrdering
 * @see UbiSOMStateConverging
 *
 * @author brunomnsilva
 */
public abstract class UbiSOMState {

    private UbiSOM model;

    /**
     * Default constructor.
     * @param model the state's context
     */
    public UbiSOMState(UbiSOM model) {
        this.model = model;
    }

    /**
     * Returns the context of this state.
     * @return the state's context
     */
    public UbiSOM getModel() {
        return model;
    }

    /**
     * Delegate method used by the UbiSOM model to process the current learning iteration.
     *
     * @param bmu the best matching-unit for <code>input</code>
     * @param input the <i>input</i> itself
     */
    public abstract void process(PrototypeNeuron bmu, VectorN input);
}
