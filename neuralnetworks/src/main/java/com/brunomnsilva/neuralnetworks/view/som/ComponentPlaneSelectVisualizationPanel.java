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
 * An aggregator panel for all possible {@link ComponentPlaneVisualizationPanel} instances of a SelfOrganizingMap.
 * <br/>
 * Only one component is visible at any given time, but the component plane of interest can be selected through the
 * context menu.
 *
 * @author brunomnsilva
 */
public class ComponentPlaneSelectVisualizationPanel extends AbstractVisualizationPanel {

    /** The names of all components. */
    private final String[] names;

    /** Index of the component currently being shown. */
    private int currentIndex;

    /**
     * Default constructor.
     * @param som the SelfOrganizingMap to visualize
     * @param names the names of all components. Must match the som dimensionality.
     */
    public ComponentPlaneSelectVisualizationPanel(SelfOrganizingMap som, String[] names) {
        super(som, "Component Planes");

        Args.nullNotPermitted(names, "names");
        Args.requireEqual(som.getDimensionality(), "som.getDimensionality()", names.length, "names.length");

        this.names = names;
        this.currentIndex = 0;

        // Add context menu entries
        addContextMenuEntries();
    }

    private void addContextMenuEntries() {
        for(int i=0; i < names.length; ++i) {
            // Name of the context menu item is the component plane name
            String name = names[i];
            // And the index of the component plane to draw is the corresponding one
            int index = i;

            addContextMenuAction(name, e -> {
                // Select the component plane index and request update
                currentIndex = index;
                super.update();
            });
        }
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();
            double value = p.getPrototype().values()[currentIndex];

            grid.set(value, x, y);
        }
    }

    @Override
    protected String description() {
        return "Right-click to select a component plane.";
    }
}
