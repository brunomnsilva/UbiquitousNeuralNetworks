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

package com.brunomnsilva.neuralnetworks.models.art;

import com.brunomnsilva.neuralnetworks.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The StreamART2A model. Input samples are fed over time and the model summarizes the underlying distribution
 * by using micro-categories. These micro-categories are extensions of the adaptive resonance theory perceptive
 * fields, where fields respond to inputs if they are "close enough" (the input lies within a perceptive field around
 * a data prototype) and are adjusted, if necessary; for inputs that are not within any perceptive field, new perceptive
 * fields are created. In this description, a micro-category can be seen as a prototype of data that establishes a
 * perceptive field, augmented with time information and weight ("importance", i.e., how many inputs does it represent.
 * <br/>
 * The model maintains a codebook that is dynamic over time, i.e., it adjusts itself to a possibly drifting underlying
 * distribution by managing micro-categories within landmark windows and storing them in the model's codebook, while
 * adding new micro-categories and removing old ones.
 * <br/>
 * The details of the algorithm can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 72.
 *
 * @author brunomnsilva
 */
public class StreamART2A extends AbstractObservable {

    /**
     * Current codebook for <code>landmarkWindowSize</code>, limited by <code>q</code> size.
     */
    private final List<MicroCategory> codebook;

    /**
     * Codebook storage (the model), limited by <code>K</code> size.
     */
    private final BoundedTreeSet<MicroCategory> codebookStorage;

    /**
     * The size of each landmark window.
     */
    private final int landmarkWindowSize;

    /**
     * Determines the maximum amount of micro-categories to hold within a landmark window.
     */
    private final int q;

    /**
     * Determines the maximum size of the <code>codebookStorage</code>.
     */
    private final int K;

    /**
     * Holds the vigilance value the current landmark window.
     */
    private double vigilance;

    /**
     * Input normalization factor, based on minimum and maximum values of the input components.
     */
    protected final double inputManifold;

    /**
     * Holds the learning rate to use when adjusting the micro-categories prototypes.
     */
    private final double learningRate;

    /**
     * Holds the total amount of input samples processed.
     */
    protected int learnInputCount;

    /**
     * Creates a new StreamART2A model.
     * @param dimensionality the dimensionality of the input space
     * @param dmin the minimum value of all input space variables
     * @param dmax the maximum value of all input space variables
     * @param learningRate the learning rate for prototype adjustments
     * @param landmarkWindowSize the landmark window size
     * @param q the parameter <code>q</code> - maximum number of micro-categories within a landmark window
     * @param K the parameter <code>K</code> - maximum size of the model (in micro-categories)
     */
    public StreamART2A(int dimensionality, double dmin, double dmax, double learningRate, int landmarkWindowSize, int q, int K) {

        this.inputManifold = (dmax - dmin) * StrictMath.sqrt(dimensionality);
        this.learningRate = learningRate;
        this.landmarkWindowSize = landmarkWindowSize;
        this.q = q;
        this.K = K;

        this.vigilance = 1;
        this.learnInputCount = 0;

        this.codebook = new ArrayList<>();

        // The bounded set should remove "oldest" micro-categories when its limit is reached.
        // Hence, we provide the collection a comparator that achieves that
        this.codebookStorage = new BoundedTreeSet<>(K, (m1, m2) -> (int)(m1.getTimestamp() - m2.getTimestamp()));
    }

    /**
     * Returns all micro-categories present in the model.
     * @return all micro-categories present in the model
     */
    public Collection<MicroCategory> getCodebook() {
        return getCodebookBetween(0, learnInputCount);
    }

    /**
     * Returns the micro-categories present in the model, until a specific <code>timestampHorizon</code>.
     * The parameter establishes a time in the past, after which the micro-categories are of interest.
     * @param timestampHorizon the past horizon timestamp
     * @return the micro-categories after the specified horizon
     */
    public Collection<MicroCategory> getCodebookUntil(long timestampHorizon) {
        return getCodebookBetween(timestampHorizon, learnInputCount);
    }

    /**
     * Returns the micro-categories present in the model, between two timestamps.
     * @param iTimestamp the lower timestamp
     * @param fTimestamp the higher timestamp
     * @return the micro-categories present in the model, between the specified timestamps
     */
    public Collection<MicroCategory> getCodebookBetween(long iTimestamp, long fTimestamp) {
        Args.requireGreaterEqualThan((int)fTimestamp, "fTimestamp", (int)iTimestamp);

        List<MicroCategory> horizonCodebook = new ArrayList<>();

        // Prevent race condition with moveCodebookToStorageAndReset
        synchronized (this) {
            // The codebookStorage set is iterated in ascending order
            for (MicroCategory c : codebookStorage) {
                if (c.getTimestamp() < iTimestamp) continue;
                if (c.getTimestamp() > fTimestamp) break;

                horizonCodebook.add(c.copy());
            }
        }
        return horizonCodebook;
    }

    /**
     * Passes a new input vector by the model. This method effectively implements the StreamART2A algorithm.
     * //TODO: url of thesis.
     * @param input the input vector.
     */
    public void learn(VectorN input) {
        learnInputCount++;

        // If a landmark is reached, move codebook to storage and reset the model; proceed.
        if( learnInputCount % landmarkWindowSize == 0) {
            moveCodebookToStorageAndReset();
        }

        // Constrain codebook through merging of micro-categories; proceed.
        if( codebook.size() > this.q) {
            mergeClosestCategories();
        }

        // If codebook is empty, add the new micro-category; no vigilance check required.
        if( codebook.isEmpty() ) {
            codebook.add( new MicroCategory(input, learnInputCount) );
            return;
        }

        // Search stage of non-empty codebook
        SearchResult searchResult = searchClosestMicroCategoryTo(input);

        MicroCategory category = searchResult.category;
        double distance = searchResult.distance;

        double similarity = distanceToSimilarity(distance);

        // Check for "match", i.e., the similarity is greater or equal than the vigilance
        // If so, make ART "resonate", i.e., category adjustment;
        // Otherwise, add a new category with center at 'input'
        if(matches(similarity)) {
            // The update rule is:
            // W(t + 1) = learningRate * X + (1 - learningRate) * W(t)
            VectorN inputDelta = input.copy();
            inputDelta.multiply(learningRate);

            category.getPrototype().multiply( 1 - learningRate);
            category.getPrototype().add(inputDelta);

            category.incrementWeight();
            category.setTimestamp(learnInputCount);

        } else {
            codebook.add( new MicroCategory(input, learnInputCount) );
        }
    }

    /**
     * Convenience method to convert vigilance to radius in a normalized input space.
     * @param vigilance the vigilance to convert
     * @return the converted radius
     */
    public double vigilanceToDistance(double vigilance) {
        return inputManifold * (1 - vigilance);
    }

    private boolean matches(double similarity) {
        //boolean match = similarity >= vigilance;
        // TODO: check later if the original code is really better
        boolean match = true;
        if( similarity < vigilance) {
            match = false;
            // Ignore possible round-off error. Does using Double.compare yield the same?
            if( Math.abs( similarity - vigilance) < 0.0000000000001 )
                match = true;
        }
        return match;
    }

    private void mergeClosestCategories() {
        // TODO: refactor original code
        int k = codebook.size();
        int c1 = 0, c2 = 0;
        double similarity = Double.MIN_VALUE;

        double newVigilance = Double.MIN_VALUE;
        boolean updateVigilance = false;
        // Find the closest pair of categories
        for(int i=0; i < k-1; ++i) {
            for(int j=i+1; j < k; ++j) {

                double s = distanceToSimilarity( distanceBetween(codebook.get(i).getPrototype(),
                        codebook.get(j).getPrototype()) );

                if(s > similarity) {
                    similarity = s;
                    c1 = i;
                    c2 = j;
                }
            }
        }

        // TODO: what was really the purpose of 'new_vigilance' ?
        if(similarity < vigilance) {
            if(similarity > newVigilance) {
                updateVigilance = true;
                newVigilance = similarity;
            }
        }

        MicroCategory category1 = codebook.get(c1);
        MicroCategory category2 = codebook.get(c2);

        // Merge prototypes
        int category1Weight = category1.getWeight();
        int category2Weight = category2.getWeight();
        double sumWeights = category1Weight + category2Weight;

        VectorN prototype1 = category1.getPrototype().copy();
        VectorN prototype2 = category2.getPrototype().copy();

        // New micro-category center is the sum of both weighed category prototypes
        prototype1.multiply(category1Weight/sumWeights);
        prototype2.multiply(category2Weight/sumWeights);
        prototype1.add(prototype2);

        MicroCategory mergedCategory = new MicroCategory(prototype1, learnInputCount, (int)sumWeights);

        // Update vigilance, if we need to
        if(updateVigilance) {
            this.vigilance = newVigilance;
        }

        //merge into categories
        codebook.remove( category1 );
        codebook.remove( category2 );

        codebook.add(mergedCategory);
    }

    private void moveCodebookToStorageAndReset() {
        // Store the final vigilance used for this batch of codebooks.
        for (MicroCategory c : codebook) {
            c.setVigilanceRadius( vigilance );
        }

        // Move to storage and clear the codebook

        // Prevent race condition with getCodebookBetween
        synchronized (this) {
            codebookStorage.addAll( codebook );
        }
        codebook.clear();

        // Reset vigilance parameter
        this.vigilance = 1;

        // Notify observers
        notifyObservers();
    }

    private SearchResult searchClosestMicroCategoryTo(VectorN input) {
        SearchResult result = new SearchResult(null, Double.MAX_VALUE);

        for (MicroCategory category : codebook) {
            double curDist = distanceBetween(category.getPrototype(), input);
            if( curDist < result.distance ) {
                result.category = category;
                result.distance = curDist;
            }
        }

        return result;
    }

    private double distanceToSimilarity(double distance) {
        return 1 - ( distance / inputManifold );
    }

    private static double distanceBetween(VectorN a, VectorN b) {
        Args.requireEqual(a.dimensions(), "a.dimensions()", b.dimensions(), "b.dimensions()");

        // This is the euclidean distance. The use of other distance metrics is not studied.
        // This is why we do not use a strategy pattern for the distance calculation and made
        // it hard-coded in this implementation.

        int len = a.dimensions();
        double distance = 0;
        for(int i=0; i < len; i++) {
            distance += (a.get(i) - b.get(i)) * (a.get(i) - b.get(i));
        }

        return StrictMath.sqrt( distance) ;
    }

    @Override
    public String toString() {
        return "StreamART2A{" +
                "codebookSize=" + codebook.size() +
                ", codebookStorageSize=" + codebookStorage.size() +
                ", inputManifold=" + inputManifold +
                ", learningRate=" + learningRate +
                ", landmarkWindowSize=" + landmarkWindowSize +
                ", q=" + q +
                ", K=" + K +
                ", vigilance=" + vigilance +
                ", learnInputCount=" + learnInputCount +
                '}';
    }

    private static final class SearchResult {
        private MicroCategory category;
        private double distance;

        public SearchResult(MicroCategory category, double distance) {
            this.category = category;
            this.distance = distance;
        }
    }

}
