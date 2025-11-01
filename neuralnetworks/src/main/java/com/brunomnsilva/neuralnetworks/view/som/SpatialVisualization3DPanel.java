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
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import org.math.plot.Plot3DPanel;
import org.math.plot.plots.Plot;
import org.math.plot.plots.ScatterPlot;
import org.math.plot.render.AbstractDrawer;

import java.awt.*;


/**
 * An implementation of a 3D input space depiction.
 *
 * @see AbstractSpatialVisualizationPanel
 *
 * @author brunomnsilva
 */
public class SpatialVisualization3DPanel extends AbstractSpatialVisualizationPanel {

    /** The actual 3d plot. The actual painting is delegated to this panel. */
    private Plot3DPanel plot3DPanel;

    /**  Holds converted som prototypes plot data. */
    private SOMPlot3D plotSOM = null;

    /** Holds converted dataset items plot data. */
    private Plot plotData = null;

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice.
     * <br/>
     * Data items can be later streamed with {@link #streamDatasetItem(DatasetItem)}.
     * @param som the SelfOrganizingMap to visualize
     */
    public SpatialVisualization3DPanel(SelfOrganizingMap som) {
        super(som);
        initComponents();
    }

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice and the Dataset input vectors.
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset to visualize
     */
    public SpatialVisualization3DPanel(SelfOrganizingMap som, Dataset dataset) {
        super(som, dataset);
        initComponents();
    }

    private void initComponents() {
        plot3DPanel = new Plot3DPanel("SOUTH");
        plot3DPanel.setBackground(LookAndFeel.colorBackground);

        getInputSpacePanel().setLayout(new BorderLayout());
        getInputSpacePanel().add(plot3DPanel.plotCanvas, BorderLayout.CENTER);
    }

    @Override
    protected final void paintInputSpace(Graphics g, int scrWidth, int scrHeight) {
        // We merely compose the data to feed to the Plot3D and then delegate the paint to it (see last instruction)

        /////////////////////////////////////////////////////////
        // DRAW DATA ITEMS
        int itemCount = getDatasetItemsCount();
        double[][] data = new double[itemCount][3];
        int i=0;
        for (DatasetItem item : getDatasetItems()) {
            // There may be a disparity between the size reported and the items
            // returned by the iterator, due to concurrent modification. This is to avoid
            // those situations. TODO: other "cleverer" way to avoid this?
            if(i == itemCount) break;

            VectorN input = item.getInput();
            data[i][0] = input.get(0);
            data[i][1] = input.get(1);
            data[i][2] = input.get(2);
            ++i;
        }

        if(plotData == null) {
            plotData = new ScatterPlot("dataset", colorDataset, data);
            plot3DPanel.addPlot(plotData);
        } else {
            plotData.setData(data);
        }

        /////////////////////////////////////////////////////////
        //DRAW SELF-ORGANIZING MAP
        SelfOrganizingMap som = getSOM();
        int width = som.getWidth();
        int height = som.getHeight();

        double[][][] map = new double[width][height][3];

        for (PrototypeNeuron p : som) {
            int x = p.getIndexX();
            int y = p.getIndexY();
            map[x][y][0] = p.getPrototype().get(0);
            map[x][y][1] = p.getPrototype().get(1);
            map[x][y][2] = p.getPrototype().get(2);
        }

        if(plotSOM == null) {
            plotSOM = new SOMPlot3D("SOM", colorLattice, map);
            plot3DPanel.addPlot(plotSOM);
        } else {
            plotSOM.setData(map);
        }

        // Delegate the paint to the plot panel
        plot3DPanel.plotCanvas.paint(g);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // CUSTOM PLOT 3D (jMathPlot library)

    public class SOMPlot3D extends Plot { // TODO: needs TLC
        double[][][] MAP;
        public boolean draw_lines;
        public boolean fill_shape;
        protected double[][] XYZ_list;
        int sizeX;
        int sizeY;
        boolean fillGrid;

        public SOMPlot3D(final String n, final Color c, final double[][][] _MAP) {
            super(n, c);
            this.draw_lines = true;
            this.fill_shape = true;
            this.XYZ_list = null;
            this.fillGrid = true;
            this.MAP = _MAP;
            this.sizeX = this.MAP.length;
            this.sizeY = this.MAP[0].length;
            this.buildXYZ_list();
        }

        private void buildXYZ_list() {
            this.XYZ_list = new double[this.sizeX * this.sizeY][3];
            for (int i = 0; i < this.sizeX; ++i) {
                for (int j = 0; j < this.sizeY; ++j) {
                    this.XYZ_list[i + j * this.sizeX][0] = this.MAP[i][j][0];
                    this.XYZ_list[i + j * this.sizeX][1] = this.MAP[i][j][1];
                    this.XYZ_list[i + j * this.sizeX][2] = this.MAP[i][j][2];
                }
            }
        }

        @Override
        public void plot(final AbstractDrawer draw, final Color c) {
            if (!this.visible) {
                return;
            }
            draw.setColor(c);
            for (int i = 0; i < this.sizeX - 1; ++i) {
                for (int j = 0; j < this.sizeY - 1; ++j) {
                    draw.drawPolygon(new double[][] { { this.MAP[i][j][0], this.MAP[i][j][1], this.MAP[i][j][2] }, { this.MAP[i + 1][j][0], this.MAP[i + 1][j][1], this.MAP[i + 1][j][2] }, { this.MAP[i + 1][j + 1][0], this.MAP[i + 1][j + 1][1], this.MAP[i + 1][j + 1][2] }, { this.MAP[i][j + 1][0], this.MAP[i][j + 1][1], this.MAP[i][j + 1][2] } });
                    if (this.fillGrid) {
                        draw.fillPolygon(0.2f, new double[][] { { this.MAP[i][j][0], this.MAP[i][j][1], this.MAP[i][j][2] }, { this.MAP[i + 1][j][0], this.MAP[i + 1][j][1], this.MAP[i + 1][j][2] }, { this.MAP[i + 1][j + 1][0], this.MAP[i + 1][j + 1][1], this.MAP[i + 1][j + 1][2] }, { this.MAP[i][j + 1][0], this.MAP[i][j + 1][1], this.MAP[i][j + 1][2] } });
                    }
                }
            }
        }

        @Override
        public double[] isSelected(final int[] screenCoordTest, final AbstractDrawer draw) {
            return null;
        }

        public void setData(final double[][][] _MAP) {
            this.MAP = _MAP;
            this.sizeX = this.MAP.length;
            this.sizeY = this.MAP[0].length;
            this.buildXYZ_list();
        }

        @Override
        public double[][] getData() {
            return this.XYZ_list;
        }

        @Override
        public void setData(double[][] d) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
