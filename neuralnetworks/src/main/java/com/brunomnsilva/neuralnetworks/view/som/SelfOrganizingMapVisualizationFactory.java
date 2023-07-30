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

import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapClusteringResult;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.models.som.impl.UbiSOM;

/**
 * A factory of SelfOrganizingMap visualizations. The visualizations returned by the factory methods are
 * already synced with the underlying model/data, i.e., an initial call to the <code>update()</code> method.
 * <br/>
 * Also, when "dimensional" variants of a visualization are available, the best one is created, i.e., to
 * depict two- or three-dimensional input spaces. If the input space dimensionality is greater than 3, then
 * these methods will return the 3d variant; please note that these may not adequately represent the input space.
 *
 * @see UMatrixVisualizationPanel
 * @see ComponentPlaneVisualizationPanel
 * @see ComponentPlaneSelectVisualizationPanel
 * @see HitMapVisualizationPanel
 * @see TargetOutputVisualizationPanel
 * @see ClusteringVisualizationPanel
 * @see BMUActivityVisualizationPanel
 * @see NeuronActivityVisualizationPanel
 *
 * @author brunomnsilva
 */
public class SelfOrganizingMapVisualizationFactory {

    /**
     * Creates a new instance of the ComponentPlaneVisualizationPanel.
     * @see ComponentPlaneVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @param index index of the component plane to visualize
     * @param name name of the component plane
     * @return a new instance of the visualization
     */
    public static ComponentPlaneVisualizationPanel createComponentPlane(SelfOrganizingMap som, int index, String name) {
        ComponentPlaneVisualizationPanel panel = new ComponentPlaneVisualizationPanel(som, index, name);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of the ComponentPlaneSelectVisualizationPanel.
     * @see ComponentPlaneSelectVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @param names array with the names of the component planes
     * @return a new instance of the visualization
     */
    public static ComponentPlaneSelectVisualizationPanel createComponentPlaneSelect(SelfOrganizingMap som, String[] names) {
        ComponentPlaneSelectVisualizationPanel panel = new ComponentPlaneSelectVisualizationPanel(som, names);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of the ComponentPlaneSelectVisualizationPanel.
     * <br/>
     * The names of the component planes are automatically generated, i.e., "v1" (variable), "v2", etc.
     * @see ComponentPlaneSelectVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @return a new instance of the visualization
     */
    public static ComponentPlaneSelectVisualizationPanel createComponentPlaneSelect(SelfOrganizingMap som) {

        String[] names = new String[som.getDimensionality()];

        for(int i=0; i < som.getDimensionality(); ++i) {
            names[i] = String.format("v%d", (i+1));
        }

        ComponentPlaneSelectVisualizationPanel panel = new ComponentPlaneSelectVisualizationPanel(som, names);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of HitMapVisualizationPanel.
     * @see HitMapVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset with input data to project onto the SelfOrganizingMap
     * @return a new instance of the visualization
     */
    public static HitMapVisualizationPanel createHitMap(SelfOrganizingMap som, Dataset dataset) {
        HitMapVisualizationPanel panel = new HitMapVisualizationPanel(som, dataset);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of UMatrixVisualizationPanel.
     * @see UMatrixVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @return a new instance of the visualization
     */
    public static UMatrixVisualizationPanel createUMatrix(SelfOrganizingMap som) {
        UMatrixVisualizationPanel panel = new UMatrixVisualizationPanel(som, UMatrixVisualizationPanel.Mode.MEAN);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of TargetOutputVisualizationPanel.
     * @see TargetOutputVisualizationPanel
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset with input and target output data to project onto the SelfOrganizingMap
     * @return a new instance of the visualization
     */
    public static TargetOutputVisualizationPanel createTargetOutputProjection(SelfOrganizingMap som, Dataset dataset) {
        TargetOutputVisualizationPanel panel = new TargetOutputVisualizationPanel(som, dataset);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of NeuronActivityVisualizationPanel. This visualization is only available for the
     * {@link UbiSOM} model.
     * @see NeuronActivityVisualizationPanel
     * @param ubisom the UbiSOM to visualize
     * @return a new instance of the visualization
     */
    public static NeuronActivityVisualizationPanel createNeuronActivity(UbiSOM ubisom) {
        NeuronActivityVisualizationPanel panel = new NeuronActivityVisualizationPanel(ubisom);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of BMUActivityVisualizationPanel. This visualization is only available for the
     * {@link UbiSOM} model.
     * @see BMUActivityVisualizationPanel
     * @param ubisom the UbiSOM to visualize
     * @return a new instance of the visualization
     */
    public static BMUActivityVisualizationPanel createBMUActivity(UbiSOM ubisom) {
        BMUActivityVisualizationPanel panel = new BMUActivityVisualizationPanel(ubisom);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of ClusteringVisualizationPanel.
     * @param som the SelfOrganizingMap to visualize
     * @param clustering the SelfOrganizingMapClusteringResult to visualize
     * @return a new instance of the visualization
     */
    public static ClusteringVisualizationPanel createClustering(SelfOrganizingMap som, SelfOrganizingMapClusteringResult clustering) {
        ClusteringVisualizationPanel panel = new ClusteringVisualizationPanel(som, clustering);
        panel.update();
        return panel;
    }

    /**
     * Creates a new instance of AbstractSpatialVisualizationPanel. The best available implementation is automatically
     * chosen, i.e., {@link SpatialVisualization2DPanel} or {@link SpatialVisualization3DPanel}.
     * @see SpatialVisualization2DPanel
     * @see SpatialVisualization3DPanel
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset with input data to visualize
     * @return a new instance of the visualization
     */
    public static AbstractSpatialVisualizationPanel createSpatialVisualizationPanel(SelfOrganizingMap som, Dataset dataset) {
        AbstractSpatialVisualizationPanel spatialViz;
        if(som.getDimensionality() > 2) {
            spatialViz = new SpatialVisualization3DPanel(som, dataset);
        } else {
            spatialViz = new SpatialVisualization2DPanel(som, dataset);
        }
        return spatialViz;
    }

    /**
     * Creates a new instance of AbstractSpatialVisualizationPanel. The best available implementation is automatically
     * chosen, i.e., {@link SpatialVisualization2DPanel} or {@link SpatialVisualization3DPanel}.
     * <br/>
     * These variants of this visualization are made to stream input data into them, if necessary, via
     * {@link AbstractSpatialVisualizationPanel#streamDatasetItem(DatasetItem)}.
     * @see SpatialVisualization2DPanel
     * @see SpatialVisualization3DPanel
     * @param som the SelfOrganizingMap to visualize
     * @return a new instance of the visualization
     */
    public static AbstractSpatialVisualizationPanel createSpatialVisualizationPanel(SelfOrganizingMap som) {
        AbstractSpatialVisualizationPanel spatialViz;
        if(som.getDimensionality() > 2) {
            spatialViz = new SpatialVisualization3DPanel(som);
        } else {
            spatialViz = new SpatialVisualization2DPanel(som);
        }
        return spatialViz;
    }
}
