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
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.Point2D;

import java.awt.*;


/**
 * An implementation of a 2D input space depiction.
 *
 * @see AbstractSpatialVisualizationPanel
 *
 * @author brunomnsilva
 */
public class SpatialVisualization2DPanel extends AbstractSpatialVisualizationPanel {

    /** Padding relative to the bounds of the panel, where the painting should occur. */
    private static final int PADDING = 30;
    private static final int TICK_SIZE = 5;
    private static final int TICK_LABEL_OFFSET = 5;
    private static final int NUM_TICKS = 5;
    private static final int AXIS_LABEL_OFFSET = 10;
    private static final String X_AXIS_LABEL = "X";
    private static final String Y_AXIS_LABEL = "Y";

    /** Data set items point size. */
    private static final int PT_SIZE = 4;

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice.
     * <br/>
     * Data items can be later streamed with {@link #streamDatasetItem(DatasetItem)}.
     * @param som the SelfOrganizingMap to visualize
     */
    public SpatialVisualization2DPanel(SelfOrganizingMap som) {
        super(som);
    }

    /**
     * Creates a visualization that will depict the SelfOrganizingMap lattice and the Dataset input vectors.
     * @param som the SelfOrganizingMap to visualize
     * @param dataset the Dataset to visualize
     */
    public SpatialVisualization2DPanel(SelfOrganizingMap som, Dataset dataset) {
        super(som, dataset);
    }

    @Override
    protected final void paintInputSpace(Graphics g, int scrWidth, int scrHeight) {
        SelfOrganizingMap som = getSOM();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = som.getWidth();
        int height = som.getHeight();

        // Repaint canvas with background color
        g2.setColor(LookAndFeel.colorBackground);
        g2.fillRect(0, 0, scrWidth, scrHeight);

        // Draw axis
        paintAxis(g2, scrWidth, scrHeight);

        // Draw any dataset items available
        g.setColor(colorDataset);
        for (DatasetItem item : getDatasetItems()) {
            VectorN input = item.getInput();
            Point2D p = convertToAreaCoordinates(input.get(0), input.get(1), scrWidth, scrHeight);
            g.fillRect((int) p.x, (int) p.y, PT_SIZE, PT_SIZE);
        }

        // Draw SOM lattice
        g.setColor(colorLattice);
        for(int w=1; w < width; ++w) {
            for (int h = 1; h < height; ++h) {
                VectorN prototypeD = som.get(w, h).getPrototype();
                VectorN prototypeA = som.get(w - 1, h - 1).getPrototype();
                VectorN prototypeB = som.get(w, h - 1).getPrototype();
                VectorN prototypeC = som.get(w - 1, h).getPrototype();

                Point2D d = convertToAreaCoordinates(prototypeD.get(0), prototypeD.get(1), scrWidth, scrHeight);
                Point2D a = convertToAreaCoordinates(prototypeA.get(0), prototypeA.get(1), scrWidth, scrHeight);
                Point2D b = convertToAreaCoordinates(prototypeB.get(0), prototypeB.get(1), scrWidth, scrHeight);
                Point2D c = convertToAreaCoordinates(prototypeC.get(0), prototypeC.get(1), scrWidth, scrHeight);

                g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
                g.drawLine((int) a.x, (int) a.y, (int) c.x, (int) c.y);

                // Right and bottom grid lines
                if(w == width-1) {
                    g.drawLine((int) d.x, (int) d.y, (int) b.x, (int) b.y);
                }

                if(h == height-1) {
                    g.drawLine((int) d.x, (int) d.y, (int) c.x, (int) c.y);
                }
            }
        }

    }

    private void paintAxis(Graphics2D g2d, int scrWidth, int scrHeight) {
        int width = scrWidth;
        int height = scrHeight;

        double xMin = 0, xMax = 1, yMin = 0, yMax = 1; // TODO: can be used later on to set scaling values

        g2d.setFont(LookAndFeel.fontTextSmall);
        FontMetrics fm = g2d.getFontMetrics();

        // Draw x-axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING);
        double xTickInterval = (xMax - xMin) / (NUM_TICKS - 1);
        double xValue = xMin;
        for (int i = 0; i < NUM_TICKS; i++) {
            int x = (int) ((i * (width - 2 * PADDING) / (NUM_TICKS - 1)) + PADDING);
            g2d.setColor(Color.BLACK);
            g2d.drawLine(x, height - PADDING, x, height - PADDING - TICK_SIZE);
            String tickLabel = String.format("%.2f", xValue);
            int labelWidth = fm.stringWidth(tickLabel);
            g2d.setColor(Color.GRAY);
            g2d.drawString(tickLabel, x - labelWidth / 2, height - PADDING/2 + TICK_LABEL_OFFSET);
            xValue += xTickInterval;
        }
        int labelHeight = fm.getDescent();
        g2d.setColor(Color.BLACK);
        g2d.drawString(X_AXIS_LABEL, width - PADDING + AXIS_LABEL_OFFSET, height - PADDING + labelHeight);

        // Draw y-axis
        g2d.setColor(Color.BLACK);
        g2d.drawLine(PADDING, PADDING, PADDING, height - PADDING);
        double yTickInterval = (yMax - yMin) / (NUM_TICKS - 1);
        double yValue = yMin;
        for (int i = 0; i < NUM_TICKS; i++) {
            int y = (int) (((NUM_TICKS - 1 - i) * (height - 2 * PADDING) / (NUM_TICKS - 1)) + PADDING);
            g2d.setColor(Color.BLACK);
            g2d.drawLine(PADDING, y, PADDING + TICK_SIZE, y);
            String tickLabel = String.format("%.2f", yValue);
            int tickLabelWidth = fm.stringWidth(tickLabel);
            g2d.setColor(Color.GRAY);
            g2d.drawString(tickLabel, PADDING - TICK_LABEL_OFFSET - tickLabelWidth, y + fm.getAscent() / 2);
            yValue += yTickInterval;
        }
        int labelWidth = fm.stringWidth(Y_AXIS_LABEL);
        g2d.setColor(Color.BLACK);
        g2d.drawString(Y_AXIS_LABEL, PADDING - labelWidth/2, PADDING - AXIS_LABEL_OFFSET);
    }

    private Point2D convertToAreaCoordinates(double x, double y, int scrWidth, int scrHeight) {
        return new Point2D(PADDING + (x * (scrWidth - 2* PADDING)),
                Math.abs( (PADDING + (y * (scrHeight - 2* PADDING)) ) - scrHeight) );
    }
}
