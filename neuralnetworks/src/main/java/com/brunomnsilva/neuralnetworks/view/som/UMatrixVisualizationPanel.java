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
import com.brunomnsilva.neuralnetworks.models.som.Lattice;
import com.brunomnsilva.neuralnetworks.models.som.MetricDistance;
import com.brunomnsilva.neuralnetworks.models.som.PrototypeNeuron;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of the U-Matrix visualization. It is an exploratory cluster analysis visualization.
 * <br/>
 * The U-Matrix (Unified Distance Matrix) visualization is a popular way of visualizing a Self-Organizing Map (SOM).
 * It is a grayscale representation of the distances between SOM neurons.
 * The U-Matrix shows the distance between adjacent neurons, as well as between neurons that are not adjacent.
 * The U-Matrix is created by calculating the distance between every pair of neurons in the SOM, and then creating a
 * 2D matrix that represents these distances.
 * <br/>
 * The U-Matrix is often used to help visualize the topological properties of the SOM. It allows the user to see which neurons
 * are close to each other in the SOM, and which neurons are far away. Areas of the U-Matrix that are dark correspond to
 * regions of the SOM where neurons are close together, while areas that are light correspond to regions where neurons
 * are far apart. By looking at the U-Matrix, it is possible to identify clusters of neurons that are close together,
 * as well as areas of the SOM where there are discontinuities in the topology.
 * <br/>
 * The U-Matrix can be used to identify patterns in the data that are captured by the SOM.
 * For example, if the SOM is trained on a set of images, the U-Matrix can be used to identify clusters of similar images.
 * Similarly, if the SOM is trained on a set of documents, the U-Matrix can be used to identify clusters of documents
 * that are similar in content.
 * <br/>
 * Overall, the combination of the U-matrix and the component planes can provide a powerful visualization tool to explore
 * the clustering structure of SOMs and to gain insights into the relationship between the input data and the SOM neurons.
 *
 * @see ComponentPlaneVisualizationPanel
 *
 * @author brunomnsilva
 */
public class UMatrixVisualizationPanel extends AbstractVisualizationPanel {

    public enum Mode {MEDIAN, MEAN, MIN, MAX}

    private Mode mode;

    public UMatrixVisualizationPanel(SelfOrganizingMap som, Mode mode) {
        super(som, "U-Matrix", som.getWidth(), som.getHeight());

        this.mode = mode;

        // Add context menu entries
        addContextMenuEntries();
    }

    private void addContextMenuEntries() {
        addContextMenuAction("Minimum distances", e -> {
            setMode(Mode.MIN);
        });
        addContextMenuAction("Mean distances", e -> {
            setMode(Mode.MEAN);
        });
        addContextMenuAction("Median distances", e -> {
            setMode(Mode.MEDIAN);
        });
        addContextMenuAction("Maximum distances", e -> {
            setMode(Mode.MAX);
        });
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        super.update();
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        int w = som.getWidth();
        int h = som.getHeight();

        // Neighbors depend on the lattice structure
        Lattice lattice = som.getLattice();
        List<Double> neighborDistances = new ArrayList<>();
        // Use the same metric used to construct the model
        MetricDistance metricDistance = som.getMetricDistance();

        for (PrototypeNeuron neuron : som) {
            int x = neuron.getIndexX();
            int y = neuron.getIndexY();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue; // Skip the current prototype (no need to calculate distance to itself)
                    }

                    int neighborX = x + dx;
                    int neighborY = y + dy;

                    // Valid lattice coordinates
                    if(neighborX >= 0 && neighborX < w && neighborY >= 0 && neighborY < h) {
                        PrototypeNeuron neighbor = som.get(neighborX, neighborY);
                        // Are effectively neighbors
                        if(lattice.areNeighbors(neuron, neighbor)) {
                            VectorN neuronPrototype = neuron.getPrototype();
                            VectorN neighborPrototype = neighbor.getPrototype();

                            double dist = metricDistance.distanceBetween(neuronPrototype, neighborPrototype);
                            neighborDistances.add( dist );
                        }
                    }

                }
            }

            double neuronValue = computeNeuronValue(neighborDistances, this.mode);
            neighborDistances.clear();
            grid.set(neuronValue, x, y);
        }
    }

    private static double computeNeuronValue(List<Double> neighborDistances, Mode mode) {
        int size = neighborDistances.size();

        if(mode == Mode.MEAN) {
            double sum = 0;
            for (Double dist : neighborDistances) {
                sum += dist;
            }
            return sum / size;
        }

        Collections.sort(neighborDistances);

        if(mode == Mode.MIN) return neighborDistances.get(0);
        else if(mode == Mode.MAX) return neighborDistances.get( size - 1);
        else { // MEDIAN
            if(size % 2 == 0) {
                return neighborDistances.get( size / 2 );
            } else {
                int middle = size / 2;
                return (neighborDistances.get(middle) + neighborDistances.get(middle + 1)) / 2;
            }
        }
    }

    @Override
    protected String description() {
        return "Depicts distances between prototype's values. Useful to detect clusters.";
    }
}
