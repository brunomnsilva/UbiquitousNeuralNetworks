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

package com.brunomnsilva.neuralnetworks.view.som;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the visualization of projecting target outputs of a {@link Dataset} onto a {@link SelfOrganizingMap}.
 * <br/>
 * This visualization can only be applied when the samples of a {@link Dataset} have assigned target outputs,
 * i.e., labelled data (can be class information, or not). Each input sample is projected onto the SOM and the BMU is found; the
 * corresponding target output (the label) is then assigned to the corresponding neuron cell of this visualization. In
 * case there are multiple assignments, the last one is the one that is visualized.
 * <br/>
 * Can be useful to visualize the composition of clusters, together with the U-matrix visualization.
 * <br/>
 * When the labels correspond to classes, it can also be useful to evaluate the quality of SOM clustering algorithms.
 * <br/>
 * If the Dataset does not have target output information, this visualization will show an error message.
 *
 * @see UMatrixVisualizationPanel
 * @see com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapClustering
 *
 * @author brunomnsilva
 */
public class TargetOutputVisualizationPanel extends AbstractVisualizationPanel {

    /** Additional reference to the Dataset held by this visualization. */
    private Dataset dataset;

    /** Map that translates all the different (hashed) target outputs to a numeric code value. */
    private final Map<Integer, Integer> targetOutputHashMapping;

    /** The projected target output code values. */
    /*private final Map<VectorN, Integer> targetOutputMapping;*/

    /**
     * Default constructor.
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset with input samples (and target outputs) to project onto the SelfOrganizingMap
     */
    public TargetOutputVisualizationPanel(SelfOrganizingMap som, Dataset dataset) {
        super(som, "Target Outputs");

        this.dataset = dataset;
        this.targetOutputHashMapping = new HashMap<>();
        /*this.targetOutputMapping = new HashMap<>();*/

        if(dataset.outputDimensionality() == 0) {
            setErrorMessage("No target outputs in the dataset");
        }
    }

    /*public Map<VectorN, Integer> getTargetOutputMapping() {
        return new HashMap<>( targetOutputMapping ); // return copy
    }*/

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        // If the dataset does not have target outputs, this was treated in the constructor
        // by setting an error message to the visualization. This code will not run with the error set.

        // We will consider that the dataset is immutable for this visualization.
        // Hence, we'll only compute labels/class mappings once
        if(targetOutputHashMapping.isEmpty()) {
            hashAndCodifyTargetOutputs(dataset);
        }

        for (DatasetItem item : dataset) {
            VectorN input = item.getInput();
            VectorN output = item.getTargetOutput();

            PrototypeNeuron bmu = som.bestMatchingUnitFor(input);
            int hash = Arrays.hashCode(output.values());
            int classValue = targetOutputHashMapping.get(hash);
            grid.set(classValue, bmu.getIndexX(), bmu.getIndexY());
        }

        // Set number of steps for the colorscale to the same number of
        // distinct classes
        int numberClasses = targetOutputHashMapping.size() + 1;
        getColorScalePanel().setNumberLevels(numberClasses);
    }

    @Override
    protected String description() {
        return "Depicts the neuron's corresponding target output class when selected as the BMU across the dataset.";
    }

    /**
     * Hashes and encodes the different target outputs found in the Dataset.
     * @param dataset the Dataset
     */
    private void hashAndCodifyTargetOutputs(Dataset dataset) {
        int classCode = 1;
        for (DatasetItem item : dataset) {
            VectorN target = item.getTargetOutput();
            int hash = Arrays.hashCode(target.values());

            if(!targetOutputHashMapping.containsKey(hash)) {
                targetOutputHashMapping.put(hash, classCode);

                classCode++;
            }
        }
    }
}
