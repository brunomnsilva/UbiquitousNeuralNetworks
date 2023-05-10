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

import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.models.som.impl.UbiSOM;

/**
 * An implementation of the neuron activity visualization panel. It depicts the recency of adjustment for each prototype.
 * <br/>
 * More details can be found in my PhD thesis <a href="http://hdl.handle.net/10362/19974">here</a> at pp. 177.
 */
public class NeuronActivityVisualizationPanel extends AbstractVisualizationPanel {

    /**
     * Default constructor.
     * @param som the SelfOrganizingMap to visualize
     */
    public NeuronActivityVisualizationPanel(SelfOrganizingMap som) {
        super(som, "Neuron Activity");

        if(!(som instanceof UbiSOM)) {
            throw new IllegalArgumentException("Argument 'som' must be of type UbiSOM for this visualization.");
        }
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        UbiSOM ubisom = (UbiSOM) som;

        for (PrototypeNeuron p : ubisom) {
            int x = p.getIndexX();
            int y = p.getIndexY();
            double value = ubisom.getTimestampActivity(x, y);

            grid.set(value, x, y);
        }
    }

    @Override
    protected String description() {
        return "Depicts the recency of neuron prototype adjustments.";
    }
}
