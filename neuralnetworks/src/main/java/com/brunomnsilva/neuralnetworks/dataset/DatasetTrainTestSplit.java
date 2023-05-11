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

import com.brunomnsilva.neuralnetworks.core.Args;

/**
 * Performs a train/test split for a given dataset through {@link #split(Dataset, double)}.
 *
 * @author brunomnsilva
 */
public class DatasetTrainTestSplit {

    private final Dataset trainingSet;
    private final Dataset testSet;

    private DatasetTrainTestSplit(Dataset trainingSet, Dataset testSet) {
        this.trainingSet = trainingSet;
        this.testSet = testSet;
    }

    /**
     * Performs the dataset split. The <code>dataset</code> is not changed; we create separate {@link Dataset} instances
     * for the train and test sets.
     *
     * @param dataset the dataset to split.
     * @param trainSplit split factor in ]0,1[. This is the percentage of the <i>train</i> dataset;
     *              The remaining goes to the <i>test</i> dataset.
     * @return an instance of {@link DatasetTrainTestSplit}. See {@link DatasetTrainTestSplit#getTrainingSet()} and
     *                                                      {@link DatasetTrainTestSplit#getTestSet()}
     * @throws IllegalArgumentException if <code>dataset</code> is null.
     * @throws IllegalArgumentException if <code>dataset</code> has a size less than 2 (cannot be split).
     * @throws IllegalArgumentException if <code>split</code> is &ge; 0 or &le; 1.
     */
    public static DatasetTrainTestSplit split(final Dataset dataset, double trainSplit) {
        Args.nullNotPermitted(dataset, "dataset");

        if(dataset.size() < 2) {
            String error = String.format("Too small dataset to split (size = %d). Must be in > 2.", dataset.size());
            throw new IllegalArgumentException(error);
        }

        if(trainSplit <= 0 || trainSplit >= 1) {
            String error = String.format("Invalid split value (%.2f). Must be in ]0, 1[.", trainSplit);
            throw new IllegalArgumentException(error);
        }

        int total = dataset.size();
        int trainSize = (int)Math.ceil(total * trainSplit);

        Dataset copy = dataset.copy();
        copy.shuffle();

        Dataset training = copy.crop(0, trainSize);
        Dataset test = copy.crop(trainSize, total); //remaining items

        return new DatasetTrainTestSplit(training, test);
    }

    /**
     * Returns the training dataset, obtained from {@link DatasetTrainTestSplit#split(Dataset, double)} operation.
     * @return the training set.
     */
    public Dataset getTrainingSet() {
        return trainingSet;
    }

    /**
     * Returns the test dataset, obtained from {@link DatasetTrainTestSplit#split(Dataset, double)} operation.
     * @return the test set.
     */
    public Dataset getTestSet() {
        return testSet;
    }

}
