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

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

/**
 * An implementation of the Component Plane visualization. It is a "sliced" version of the SelfOrganizingMap,
 * depicting the values of a specific component/variable/dimension of the prototypes.
 * <br/>
 * Each component plane corresponds to a single input dimension, and each neuron in the SOM is represented as a cell in this visualization.
 * The color of each cell in the visualization is determined by the value of the corresponding prototype component value.
 * <br/>
 * The component plane technique can be useful to identify the input dimensions that are most relevant to the SOM clustering.
 * For example, if some of the component planes show clear patterns or clusters of SOM neurons, this may indicate that
 * the input data has a strong correlation with these dimensions. Conversely, if some component planes show random or noisy patterns,
 * this may indicate that the corresponding input dimensions have little or no correlation with the SOM clustering.
 * <br/>
 * Overall, the combination of the U-matrix and the component planes can provide a powerful visualization tool to explore
 * the clustering structure of SOMs and to gain insights into the relationship between the input data and the SOM neurons.
 *
 * @see UMatrixVisualizationPanel
 *
 * @author brunomnsilva
 */
public class ComponentPlaneVisualizationPanel extends AbstractVisualizationPanel {

    private int index;

    /**
     * Default constructor. The <code>index</code> determines the component of the prototypes to visualize.
     * @see PrototypeNeuron
     * @param som the SelfOrganizingMap to visualize
     * @param index the index of the variable to visualize
     * @param name the name of the component/variable
     */
    public ComponentPlaneVisualizationPanel(SelfOrganizingMap som, int index, String name) {
        super(som, name);

        Args.requireInRange(index, "index", 0, som.getDimensionality() - 1);

        this.index = index;
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();
            double value = p.getPrototype().values()[index];

            grid.set(value, x, y);
        }
    }

    @Override
    protected String description() {
        return "Depicts the values of this component (feature/attribute) across neurons.";
    }
}
