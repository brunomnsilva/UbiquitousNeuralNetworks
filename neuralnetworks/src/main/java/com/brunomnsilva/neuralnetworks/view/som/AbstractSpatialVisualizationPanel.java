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
import com.brunomnsilva.neuralnetworks.core.BoundedQueue;
import com.brunomnsilva.neuralnetworks.core.Observable;
import com.brunomnsilva.neuralnetworks.core.Observer;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMapUtils;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * This abstract class models a base spatial visualization of SelfOrganizingMap lattice and, optionally, data set items
 * from a Dataset (or manually "streamed") in the input space.
 * <br/>
 * This class has only basic functionality and color configurations, leaving to the implementing classes to depict
 * the lattice of the SelfOrganizingMap and the data points in a spatial coordinate system, e.g., 2D and 3D.
 * <br/>
 * It is expected that implementing classes always draw the lattice of the SelfOrganizingMap, depicting the topology of the map.
 * <br/>
 * The mode depiction of input space data points should be selected through the constructors.
 * The {@link #AbstractSpatialVisualizationPanel(SelfOrganizingMap, Dataset)} constructor will force the entire data set
 * to be drawn (compute-intensive, for large data sets), while the {@link #AbstractSpatialVisualizationPanel(SelfOrganizingMap)}
 * will only draw input space data points that are fed to the visualization by the {@link #streamDatasetItem(DatasetItem)}. In
 * the latter case, there is an imposed limit on the quantity of streamed data items that are shown in FIFO style.
 *
 * <br/>
 * The base functionality can automatically update the visualization by observing changes to the underlying
 * SelfOrganizingMap; see {@link SelfOrganizingMap#addObserver(Observer)}
 *
 * @author brunomnsilva
 */
public abstract class AbstractSpatialVisualizationPanel extends JPanel implements Observer {

    private static final int DATASET_ITEM_MAX = 2000;


    /** The underlying SelfOrganizingMap of the visualization. */
    private SelfOrganizingMap som;

    /** The underlying Dataset of the visualization. May stay null if streaming data items. */
    private Dataset dataset;

    /** Bounded queue to hold streamed data items. */
    private BoundedQueue<DatasetItem> datasetItemQueue;

    private PanelWithDelegatedPaint inputSpacePanel;

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice and the Dataset input vectors.
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset to visualize
     */
    public AbstractSpatialVisualizationPanel(SelfOrganizingMap som, Dataset dataset) {
        super(true);

        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(dataset, "dataset");

        this.som = som;
        this.dataset = dataset;

        initComponents(); // must be here, after this.som initialization
    }

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice.
     * <br/>
     * Data items can be later streamed with {@link #streamDatasetItem(DatasetItem)}.
     * @param som the SelfOrganizingMap to visualize
     */
    public AbstractSpatialVisualizationPanel(SelfOrganizingMap som) {
        super(true);

        Args.nullNotPermitted(som, "som");

        this.som = som;
        // Streaming queued data items mode
        this.datasetItemQueue = new BoundedQueue<>(DATASET_ITEM_MAX);

        initComponents(); // must be here, after this.som initialization
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setOpaque(true);
        this.setBackground(LookAndFeel.colorBackground);
        setPreferredSize(new Dimension(600, 600));

        // NORTH
        String somDescription = SelfOrganizingMapUtils.generateShortDescription(this.som);
        JLabel nameLabel = new JLabel(somDescription, JLabel.CENTER);
        nameLabel.setFont(com.brunomnsilva.neuralnetworks.view.LookAndFeel.fontTitle);
        nameLabel.setForeground(LookAndFeel.colorFontTitle);

        JLabel infoLabel = new JLabel(" \u24d8", JLabel.CENTER);
        infoLabel.setFont(LookAndFeel.fontTitle);
        infoLabel.setForeground(LookAndFeel.colorFontTitle);
        infoLabel.setToolTipText("Spatial projection of lattice and input data.");

        JPanel hbox = new JPanel(new FlowLayout());
        hbox.setBackground(LookAndFeel.colorBackground);
        hbox.add(nameLabel);
        hbox.add(infoLabel);
        add(hbox, BorderLayout.NORTH);

        // CENTER
        inputSpacePanel = new PanelWithDelegatedPaint();
        add(inputSpacePanel, BorderLayout.CENTER);
    }

    /**
     * Implementing classes should use this method to paint the input space depiction.
     * @param g the graphics context to use
     * @param scrWidth the width of the region to paint
     * @param scrHeight the height of the region to paint
     */
    protected abstract void paintInputSpace(Graphics g, int scrWidth, int scrHeight);

    /**
     * Returns the panel where the input space is to be depicted (painted).
     * @return the panel where the input space is to be depicted
     */
    protected final JPanel getInputSpacePanel() {
        return inputSpacePanel;
    }

    /**
     * Returns the underlying SelfOrganizingMap of the visualization.
     * @return the underlying SelfOrganizingMap of the visualization
     */
    protected final SelfOrganizingMap getSOM() { return som; }

    /**
     * Returns the DatasetItems to be depicted in the visualization.
     * @return the DatasetItems to be depicted in the visualization
     */
    protected final Iterable<DatasetItem> getDatasetItems() {
        return dataset != null ? dataset : datasetItemQueue;
    }

    /**
     * Returns the number of DatasetItems that are to be depicted in the visualization.
     * @return the number of DatasetItems that are to be depicted in the visualization
     */
    protected int getDatasetItemsCount() {
        return dataset != null ? dataset.size() : datasetItemQueue.size();
    }

    /**
     * Add a new DatasetItem to be depicted in the visualization.
     * Note that this change will only be reflected in the visualization after calling {@link #update()} or
     * when using the Observer pattern, i.e., when an instance of this class is an observer of a SelfOrganizingMap;
     * see {@link SelfOrganizingMap#addObserver(Observer)}.
     *
     * @param item the additional DatasetItem to depict in the visualization
     */
    public final void streamDatasetItem(DatasetItem item) {
        if(dataset != null) {
            throw new IllegalStateException("Visualization instantiated to draw an entire Dataset. See Javadoc.");
        }

        datasetItemQueue.add(item);
    }

    /**
     * Forces an update to this visualization depicting the current state of the underlying SelfOrganizingMap,
     * together with any data items.
     */
    public final void update() {
        this.repaint();
    }

    @Override
    public void onNotify(Observable observable) {
        if(observable instanceof SelfOrganizingMap) {
            SelfOrganizingMap refSOM = (SelfOrganizingMap)observable;
            if(getSOM() != refSOM) return;

            // SOM state changed, so update the visualization
            update();
        }
    }

    /////////////////////////////////////////////////////////////
    // COLORING to maintain coherence by implementing classes

    protected static Color colorLattice = Color.BLACK;
    protected static Color colorDataset = Color.RED;

    public static void setColorLattice(Color colorLattice) {
        AbstractSpatialVisualizationPanel.colorLattice = colorLattice;
    }

    public static void setColorDataset(Color colorDataset) {
        AbstractSpatialVisualizationPanel.colorDataset = colorDataset;
    }

    /////////////////////////////////////////////////////////////
    // SPATIAL VISUALIZATION PANEL

    private class PanelWithDelegatedPaint extends JPanel {
        public PanelWithDelegatedPaint() {
            super(true);
        }

        @Override
        public void paint(Graphics g) {
            paintInputSpace(g, getWidth(), getHeight());
        }
    }
}
