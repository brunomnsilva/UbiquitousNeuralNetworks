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
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.colorscale.ColorScalePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This class represents a generic grid panel to produce {@link com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap}
 * visualizations. It holds a reference to a {@link ColorScalePanel} from which it derives
 * the cell colors. It does not hold any reference to a SelfOrganizingMap; all grid values are set manually and the
 * colorscale range adjusted automatically.
 * <br/>
 * The visualized grid can either be rectangular or hexagonal.
 * <br/>
 * The panel implements a mouse listener: when hovering over a cell its current value in shown in a tooltip.
 *
 * @author brunomnsilva
 */
public class GenericGridPanel extends JPanel {

    /** Possible lattice types. */
    public enum LatticeType {RECTANGULAR, HEXAGONAL};

    /** The lattice type to draw. */
    private final LatticeType gridLatticeType;

    /** Whether the size of the cells are relative to their values. */
    private boolean valuesToSize;

    /** The size of the grid. */
    private final int width, height;

    /** The values to draw are drawn from this array. */
    private final double[][] values;

    /** The ColorScalePanel to translate values to cell colors. */
    private final ColorScalePanel colorScale;

    /**
     * Default constructor.
     * @param colorScale the ColorScalePanel instance to translate values to colors
     * @param gridLatticeType the lattice type to be drawn.
     * @param width the width of the grid
     * @param height the height of the grid
     */
    public GenericGridPanel(ColorScalePanel colorScale, LatticeType gridLatticeType, int width, int height) {
        super(true);

        Args.nullNotPermitted(colorScale, "colorScale");
        Args.nullNotPermitted(gridLatticeType, "gridLatticeType");
        Args.requireGreaterEqualThan(width, "width", 1);
        Args.requireGreaterEqualThan(height, "height", 1);

        this.gridLatticeType = gridLatticeType;
        this.width = width;
        this.height = height;
        this.colorScale = colorScale;

        this.values = new double[width][height];

        this.valuesToSize = false;

        setBackground(LookAndFeel.colorBackground);

        addMouseMotionListener(new GridMouseListener());
    }

    /**
     * Sets the value of a grid cell at (xIndex, yIndex).
     * <br/>
     * Note that changes made by calling this method will only be reflected in the visualization after calling
     * the {@link #update()} method.
     * @param value the value to assign to the cell
     * @param xIndex the x-index of grid cell. Must be valid, i.e., in [0, gridWidth - 1].
     * @param yIndex the y-index of grid cell. Must be valid, i.e., in [0, gridHeight - 1].
     */
    public void set(double value, int xIndex, int yIndex) {
        Args.requireFinite(value, "value");
        Args.requireInRange(xIndex, "xIndex", 0, width - 1);
        Args.requireInRange(yIndex, "yIndex", 0, height - 1);

        values[xIndex][yIndex] = value;
    }

    /**
     * Returns the current value set for a grid cell.
     * @param xIndex the x-index of grid cell. Must be valid, i.e., in [0, gridWidth - 1].
     * @param yIndex the y-index of grid cell. Must be valid, i.e., in [0, gridHeight - 1].
     * @return the current value of cell at (xIndex, yIndex)
     */
    public double get(int xIndex, int yIndex) {
        return values[xIndex][yIndex];
    }

    /**
     * Sets whether the size of the cells are relative to their values.
     * @param valuesToSize flag
     */
    public void setValuesToSize(boolean valuesToSize) {
        this.valuesToSize = valuesToSize;
    }

    /**
     * Updates the visualization, reflecting any possible changes made through {@link #set(double, int, int)}.
     */
    public void update() {
        synchronized(this) {
            checkLimits();
            repaint();
            colorScale.repaint();
        }
    }

    /**
     * Resets all cell's assigned values to 0 (zero).
     * <br/>
     * Note that changes made by calling this method will only be reflected in the visualization after calling
     * the {@link #update()} method.
     */
    public void resetValuesToZero() {
        for(int x=0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                values[x][y] = 0;
            }
        }
    }

    /**
     * Updates the colorscale limits, according to the cell's values.
     * @return true if there is a valid range of values (i.e., they are not all equal); false, otherwise.
     */
    private boolean checkLimits() {
        double min = values[0][0];
        double max = values[0][0];

        for(int x=0; x < width; ++x) {
            for(int y=0; y < height; ++y) {
                double val = values[x][y];

                if(val < min) { min = val; }
                if(val > max) { max = val; }
            }
        }

        colorScale.setScale(min, max);

        return !nearZeroDifference(min, max);
    }

    /**
     * Utility method to check if the difference between two double values is negligible.
     * @param a first value
     * @param b second value
     * @return if the difference between two double values is negligible
     */
    private boolean nearZeroDifference(double a, double b) {
        return ( Math.abs(a-b) < 0.0000001 );
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // DRAWING

    private static final int X_OFFSET = 20;
    private static final int Y_OFFSET = 20;

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(gridLatticeType == LatticeType.RECTANGULAR) {
            drawRectangularGrid(g2);
        } else {
            drawHexagonalGrid(g2);
        }
    }

    private void drawHighlightedCell(Graphics2D g2) {
        if(highlightedCell.hasCellCoordinates() && highlightedCell.cellShape != null) {
            Stroke original = g2.getStroke();
            g2.setStroke(new BasicStroke(2));
            g2.setPaint(Color.RED); //TODO: Create a color scale "accent" color?
            g2.draw(highlightedCell.cellShape);
            g2.setStroke(original);

            setToolTipText(String.format("(%d,%d) = %.3f",
                    highlightedCell.xNeuron, highlightedCell.yNeuron,
                    highlightedCell.cellValue));
        } else {
            setToolTipText(null);
        }
    }

    private void drawHexagonalGrid(Graphics2D g2) {
        int w = getWidth() - X_OFFSET * 2;
        int h = getHeight() - Y_OFFSET * 2;

        float hex_width = (float)w / (width);
        float hex_height = (float)h / (height) * 1.25f;

        float dx = hex_width;
        float dy = hex_height * 0.75f;

        for(int y=0; y < height; ++y) {

            float yPos = y * dy + Y_OFFSET;

            for(int x=0; x < width; ++x) {

                //“even-r” horizontal layout
                // shoves even rows right
                boolean evenRow = y % 2 == 0;
                float xPos = x * dx + ( evenRow ? hex_width/2 : 0) + X_OFFSET;

                // In the visualization the neuron at (0,0) is in the bottom-left corner
                // of the grid. Hence, we have to flip the y values.
                double value = values[x][height - y - 1];

                drawHexagon(g2, value, xPos, yPos, hex_width, hex_height, x + 1, height - y);
            }
        }

        // Draw highlighted cell
        drawHighlightedCell(g2);

        g2.setPaint(Color.BLACK);
        // Draw some grid coordinates
        g2.setFont(LookAndFeel.fontTextSmall);
        drawString(String.valueOf(height), g2, Color.black, new Rectangle2D.Float(0, 0, X_OFFSET, Y_OFFSET));
        drawString("1", g2, Color.black, new Rectangle2D.Float(0, Y_OFFSET+h, X_OFFSET, Y_OFFSET));
        drawString(String.valueOf(width), g2, Color.black, new Rectangle2D.Float(X_OFFSET+w, Y_OFFSET+h, X_OFFSET, Y_OFFSET));

        // Draw axis labels
        drawString("H", g2, Color.black, new Rectangle2D.Float(0, 0, X_OFFSET, getHeight()));
        drawString("W", g2, Color.black, new Rectangle2D.Float(0, getHeight()-Y_OFFSET, getWidth(), Y_OFFSET));

    }

    private void drawRectangularGrid(Graphics2D g2) {
        int w = getWidth() - X_OFFSET * 2;
        int h = getHeight() - Y_OFFSET * 2;

        g2.setPaint(Color.black);
        g2.setStroke(new BasicStroke(1));

        //Draw boundaries
        float dx_inc;
        float dy_inc;
        float r_width = (float)w / (width);
        float r_height = (float)h / (height);
        dx_inc = r_width / 2;
        dy_inc = r_height / 2;

        float xpos;
        float ypos = -dy_inc + Y_OFFSET + h;

        for (int y = 0; y < height; ++y) {
            xpos = dx_inc + X_OFFSET;

            for (int x = 0; x < width; ++x) {
                // In the visualization the neuron at (0,0) is in the bottom-left corner
                // Hence, we have to flip the y values
                double value = values[x][height - y - 1];

                drawRectangle(g2, value, xpos, ypos, r_width, r_height, x + 1, height - y);
                xpos += 2 * dx_inc;
            }
            ypos -= 2 * dy_inc;
        }

        // Draw highlighted cell
        drawHighlightedCell(g2);

        // Draw surrounding box
        g2.setPaint(Color.BLACK);
        g2.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET+w, Y_OFFSET);
        g2.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET, Y_OFFSET+h);
        g2.drawLine(X_OFFSET, Y_OFFSET+h, X_OFFSET+w, Y_OFFSET+h);
        g2.drawLine( X_OFFSET+w, Y_OFFSET+h, X_OFFSET+w, Y_OFFSET);

        // Draw some grid coordinates
        g2.setFont(LookAndFeel.fontTextSmall);
        drawString(String.valueOf(height), g2, Color.black, new Rectangle2D.Float(0, 0, X_OFFSET, Y_OFFSET));
        drawString("1", g2, Color.black, new Rectangle2D.Float(0, Y_OFFSET+h, X_OFFSET, Y_OFFSET));
        drawString(String.valueOf(width), g2, Color.black, new Rectangle2D.Float(X_OFFSET+w, Y_OFFSET+h, X_OFFSET, Y_OFFSET));

        // Draw axis labels
        drawString("H", g2, Color.black, new Rectangle2D.Float(0, 0, X_OFFSET, getHeight()));
        drawString("W", g2, Color.black, new Rectangle2D.Float(0, getHeight()-Y_OFFSET, getWidth(), Y_OFFSET));
    }

    private void drawRectangle(Graphics2D g, double value, float x, float y,
                               float width, float height, int xNeuron, int yNeuron) {

        Color color = colorScale.valueToColor(value);

        Shape rect = new Rectangle2D.Float(x-(width/2),y-(height/2),
                width,
                height);

        drawCell(g, rect, x, y, color, value);

        // Check mouse position to highlight
        checkCellHover(rect, value, xNeuron, yNeuron);
    }

    private void drawHexagon(Graphics2D g, double value, float x, float y,
                             float width, float height, int xNeuron, int yNeuron) {

        // Pointy-top hexagon
        double halfWidth = width / 2;
        double quarterHeight = height / 4;
        // counter clock-wise ordered points, starting at the pointy top
        // we consider (x,y) the coordinate of the top-left enclosing rectangle
        double[] xPoints = {x + halfWidth, x, x, x + halfWidth, x + width, x + width};
        double[] yPoints = {y, y + quarterHeight, y + quarterHeight*3, y + height, y + quarterHeight*3, y + quarterHeight};

        Color color = colorScale.valueToColor(value);

        Path2D hexagon = new Path2D.Float();

        hexagon.moveTo(xPoints[0], yPoints[0]);
        for(int i = 1; i < xPoints.length; ++i) {
            hexagon.lineTo(xPoints[i], yPoints[i]);
        }
        hexagon.closePath();

        drawCell(g, hexagon, x, y, color, value);

        // Check mouse position to highlight
        checkCellHover(hexagon, value, xNeuron, yNeuron);
    }

    private void drawCell(Graphics2D g, Shape shape, double centerX, double centerY, Color color, double value) {
        if(valuesToSize) {
            // We draw the original shape, then we paint a scaled version
            // of the shape
            g.setPaint(Color.BLACK);
            g.draw(shape);

            double scaleFactor = scaleFactor(value);
            Shape scaledShape = scaleShape(shape, centerX, centerY, scaleFactor);

            g.setPaint(color);
            g.fill(scaledShape);
        } else {
            // "Normal" operation: just pain the shape
            g.setPaint(color);
            g.fill(shape);
        }
    }

    private double scaleFactor(double value) {
        double max = colorScale.getMaximumValue();
        double min = colorScale.getMinimumValue();
        double range = max - min;
        double scaledValue = value - min;

        double factor = (scaledValue / range);

        // We don't want "blank" cells, so the colorscale still applies
        // correclty. So we establish a lower threshold of 0.1
        if(factor < 0.1) factor = 0.1;

        return factor;
    }

    private static Shape scaleShape(Shape shape, double centerX, double centerY, double scaleFactor) {
        Point2D center = getCenter(shape);

        AffineTransform transform = AffineTransform.getTranslateInstance((1.0 - scaleFactor) * center.getX(),
                (1.0 - scaleFactor) * center.getY());

        transform.scale(scaleFactor, scaleFactor);

        return transform.createTransformedShape(shape);
    }

    private static Point2D getCenter(Shape shape) {
        double centerX = shape.getBounds2D().getCenterX();
        double centerY = shape.getBounds2D().getCenterY();
        return new Point2D.Double(centerX, centerY);
    }

    private void checkCellHover(Shape shape, double value, int xNeuron, int yNeuron) {
        if(highlightedCell.hasCellCoordinates()) {
            if(shape.contains( highlightedCell.xScrCoord, highlightedCell.yScrCoord) ) {
                highlightedCell.cellValue = value;
                highlightedCell.cellShape = shape;
                highlightedCell.xNeuron = xNeuron;
                highlightedCell.yNeuron = yNeuron;
            }
        }
    }

    private void drawString(String s, Graphics2D g, Color c, Rectangle2D rect) {

        FontMetrics fm = g.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(s, g);

        int x = (int) ( (rect.getWidth() - r.getWidth()) / 2 );
        int y = (int) ( (rect.getHeight() - r.getHeight()) / 2 + fm.getAscent() );

        g.drawString(s, (int)(rect.getX() + x), (int)(rect.getY() + y));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // MOUSE LISTENER AND USER INTERACTION

    private HighlightedCell highlightedCell = new HighlightedCell();
    private class GridMouseListener extends MouseAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            // These are the relative coordinates inside the panel
            int x = e.getX();
            int y = e.getY();

            highlightedCell.trackMousePosition(x, y);
        }
    }

    // Due to how a panel is painted, we must delay the highlighting until
    // the end of painting the entire grid. Therefore, this class also holds
    // a reference to the shape that is to be drawn later on and its grid value.
    private class HighlightedCell {
        int xScrCoord = -1;
        int yScrCoord = -1;

        private double cellValue;
        private Shape cellShape;
        private int xNeuron, yNeuron;

        boolean hasCellCoordinates() { return xScrCoord != -1; }
        void markNoCellHover() {
            if(xScrCoord != -1) {
                // Going out of cell bounds. Repaint
                // to clear highlighting
                repaint();
            }
            xScrCoord = -1;
        }

        void trackMousePosition(int x, int y) {
            // TODO: Improve these bounds with hexagonal lattice?
            //       Its not so easy, so maybe it will stay like this for the foreseeable future.

            // Ignore when mouse is outside the "drawable" area of the grid
            if(x < X_OFFSET || x > getWidth() - X_OFFSET || y < Y_OFFSET || y > getHeight() - Y_OFFSET)
                markNoCellHover();
            // This will cause the highlighting of the (x,y) grid cell
            else {
                xScrCoord = x;
                yScrCoord = y;
                repaint();
            }
        }
    }
}
