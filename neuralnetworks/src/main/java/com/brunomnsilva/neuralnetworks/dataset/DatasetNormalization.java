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

package com.brunomnsilva.neuralnetworks.dataset;

import com.brunomnsilva.neuralnetworks.core.VectorN;

/**
 * A Dataset normalization.
 *
 * @author brunomnsilva
 */
public abstract class DatasetNormalization {

    /**
     * Normalizes the entire dataset.
     * @param dataset the dataset to normalize
     */
    public abstract void normalize(Dataset dataset);

    /**
     * Denormalizes the entire dataset.
     * @param dataset the dataset to denormalize
     */
    public abstract void denormalize(Dataset dataset);

    /**
     * Normalizes a DatasetItem according to the existing normalization data.
     * @param item a dataset item
     * @return a normalized dataset item
     */
    public abstract DatasetItem normalize(final DatasetItem item);

    /**
     * Denormalizes a DatasetItem according to the existing normalization data.
     * @param item a (normalized) dataset item
     * @return a denormalized dataset item
     */
    public abstract DatasetItem denormalize(final DatasetItem item);

    /**
     * Normalizes an input vector according to the existing normalization data.
     * @param input an input vector
     * @return a normalized input vector
     */
    public abstract VectorN normalizeInput(final VectorN input);

    /**
     * Normalizes a target output vector according to the existing normalization data.
     * @param output a target output vector
     * @return a normalized target output vector
     */
    public abstract VectorN normalizeOutput(final VectorN output);

    /**
     * Denormalizes an input vector according to the existing normalization data.
     * @param input an (normalized) input vector
     * @return a normalized input vector
     */
    public abstract VectorN denormalizeInput(final VectorN input);

    /**
     * Denormalizes a target output vector according to the existing normalization data.
     * @param output a (normalized) target output vector
     * @return a normalized target output vector
     */
    public abstract VectorN denormalizeOutput(final VectorN output);

}
