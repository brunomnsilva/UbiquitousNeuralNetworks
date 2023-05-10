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

/**
 * An implementation of the Hit-map visualization, where samples from a {@link Dataset} are projected onto a
 * {@link SelfOrganizingMap} and a "hit" means a BMU activation for a prototype. Hence, this visualization depicts
 * how many times the prototypes were chosen as the BMU for the input samples.
 *
 * @author brunomnsilva
 */
public class HitMapVisualizationPanel extends AbstractVisualizationPanel {

    /** Additional reference to the Dataset held by this visualization. */
    private final Dataset dataset;

    /**
     * Default constructor.
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset with input samples to project onto the SelfOrganizingMap
     */
    public HitMapVisualizationPanel(SelfOrganizingMap som, Dataset dataset) {
        super(som, "Hit Map");

        this.dataset = dataset;
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        // If recalled, the existing values will be incremented
        // Should be initially set to zero
        grid.resetValuesToZero();

        for (DatasetItem item : dataset) {
            VectorN input = item.getInput();
            PrototypeNeuron bmu = som.bestMatchingUnitFor(input);

            int x = bmu.getIndexX();
            int y = bmu.getIndexY();

            double count = grid.get(x, y);
            grid.set(count + 1, x, y);
        }

        // This will ensure that cells are filled proportionally
        // to their values. This is specific to this type of visualization.
        grid.setValuesToSize(true);
    }

    @Override
    protected String description() {
        return "Depicts how many times a neuron is selected as the BMU across the dataset.";
    }
}
