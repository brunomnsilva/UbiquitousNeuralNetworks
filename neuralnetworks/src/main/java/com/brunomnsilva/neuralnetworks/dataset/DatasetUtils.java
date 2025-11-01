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

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for working with Dataset. Contains mainly data transformations.
 *
 * @author brunomnsilva
 */
public class DatasetUtils {

    /**
     * Returns a deep-copy of the inputs present in a dataset.
     * @param dataset the dataset from where to draw inputs
     * @return a deep-copy of the inputs present in a dataset
     */
    public static List<VectorN> inputsToList(Dataset dataset) {
        List<VectorN> data = new ArrayList<>(dataset.size());

        for (DatasetItem item : dataset) {
            data.add( item.getInput().copy() );
        }

        return data;
    }

    /**
     * Returns a deep-copy of the target outputs present in a dataset.
     * @param dataset the dataset from where to draw target outputs
     * @return a deep-copy of the target outputs present in a dataset
     */
    public static List<VectorN> targetOutputsToList(Dataset dataset) {
        List<VectorN> data = new ArrayList<>(dataset.size());

        for (DatasetItem item : dataset) {
            data.add( item.getTargetOutput().copy() );
        }

        return data;
    }

    /**
     * Returns the inputs present in a dataset in array format.
     * @param dataset the dataset from where to draw inputs
     * @return the inputs present in a dataset
     */
    public static double[][] inputsTo2dArray(Dataset dataset) {
        int dataSize = dataset.size();
        int dataDim = dataset.inputDimensionality();

        double[][] data = new double[dataSize][dataDim];

        for(int i=0; i < dataSize; ++i) {
            data[i] = dataset.get(i).getInput().values();
        }

        return data;
    }

    /**
     * Returns the target outputs present in a dataset in array format.
     * @param dataset the dataset from where to draw target outputs
     * @return the target outputs present in a dataset
     */
    public static double[][] targetOutputsTo2dArray(Dataset dataset) {
        int dataSize = dataset.size();
        int dataDim = dataset.outputDimensionality();

        double[][] data = new double[dataSize][dataDim];

        for(int i=0; i < dataSize; ++i) {
            data[i] = dataset.get(i).getTargetOutput().values();
        }

        return data;
    }
}
