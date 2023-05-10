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

package com.brunomnsilva.neuralnetworks.view.colorscale;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.*;

/**
 * Represents a wrapper around a colorscale with manipulation methods.
 * <br/>
 * Instances of this class can be used as graphical components.
 *
 * @author brunomnsilva
 */
public class ColorScalePanel extends JPanel {

    private static NumberFormat numberFormat = new DecimalFormat("###,###.###");
    private static final int X_OFFSET = 20;
    private static final int Y_OFFSET = 20;

    private double minValue;
    private double maxValue;

    private int numberLevels = 5;

    private final ColorScale scale;

    /**
     * Creates a new ColorScalePanel with the default color scale and range [0, 1].
     * @see ColorScaleFactory#createDefault()
     */
    public ColorScalePanel() {
        this(0, 1);
    }

    /**
     * Creates a new ColorScalePanel with the default color scale and specified range.
     * @see ColorScaleFactory#createDefault()
     * @param min minimum value
     * @param max maximum value
     */
    public ColorScalePanel(double min, double max) {
        this(min, max, ColorScaleFactory.createDefault());
    }

    /**
     * Creates a new ColorScalePanel with the specified color scale and range.
     * @param min minimum value
     * @param max maximum value
     * @param colorScale the underlying colorscale
     */
    public ColorScalePanel(double min, double max, ColorScale colorScale) {
        Args.nullNotPermitted(colorScale, "colorScale");

        this.scale = colorScale;
        this.minValue = min;
        this.maxValue = max;

        setBackground(LookAndFeel.colorBackground);
        setMaximumSize(new Dimension(33, 341)); // necessary?
    }

    /**
     * Changes the current interval of the scale
     * @param min minimum value
     * @param max maximum value
     */
    public void setScale(double min, double max) {
        minValue = min;
        maxValue = max;
        repaint();
    }

    /**
     * Returns the minimum value of the scale.
     * @return the minimum value of the scale
     */
    public double getMinimumValue() { return minValue; }

    /**
     * Returns the maximum value of the scale.
     * @return the maximum value of the scale
     */
    public double getMaximumValue() { return maxValue; }

    /**
     * Returns the number of levels (ticks) of the scale.
     * @return the number of levels (ticks) of the scale
     */
    public int getNumberLevels() {
        return numberLevels;
    }

    /**
     * Sets the number of levels (ticks) of the scale. Must be >= 2.
     * @param numberLevels  the number of levels (ticks) of the scale
     */
    public void setNumberLevels(int numberLevels) {
        // Does not make sense less than two levels
        Args.requireGreaterEqualThan(numberLevels, "numberLevels", 2);

        this.numberLevels = numberLevels;
    }

    /**
     * Returns the corresponding color for a value, using the underlying colorscale.
     * @param value the value to compute its color
     * @return the computed color for the colorscale
     */
    public Color valueToColor(double value) {
        return scale.valueToColor(value, minValue, maxValue);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        int width = this.getWidth();
        int height = this.getHeight();

        int halfWidth = width / 2;
        int drawRectWidth = halfWidth - X_OFFSET;
        int drawRectHeight = height - Y_OFFSET * 2;

        //if we have no area to draw, then do not attempt to draw anything.
        if(drawRectWidth <= 0 || drawRectHeight <= 0) return;

        //delegate the actual drawing of the scale colors
        scale.draw(g2, X_OFFSET, Y_OFFSET, drawRectWidth, drawRectHeight);

        g2.setPaint(LookAndFeel.colorFontText);
        g2.setFont(LookAndFeel.fontTextSmall);

        //surrounding-box, this code has better result in svg output than a simple rectangle:
        g2.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET + drawRectWidth, Y_OFFSET);
        g2.drawLine(X_OFFSET, Y_OFFSET, X_OFFSET, Y_OFFSET+drawRectHeight);
        g2.drawLine(X_OFFSET, Y_OFFSET + drawRectHeight, X_OFFSET + drawRectWidth, Y_OFFSET + drawRectHeight);
        g2.drawLine( X_OFFSET + drawRectWidth, Y_OFFSET + drawRectHeight, X_OFFSET + drawRectWidth, Y_OFFSET);

        int dy = (height - Y_OFFSET * 2) / (numberLevels - 1);
        double dValue = (maxValue - minValue) / (numberLevels - 1);

        int y = Y_OFFSET;
        double val = maxValue;

        //top of scale
        g2.drawLine(halfWidth, y, halfWidth+3, y);
        g2.drawString(numberFormat.format(val), halfWidth+6, y+5);
        //"middle" of scale
        y += dy;
        val -= dValue;
        
        for(int i = 1; i < numberLevels - 1; i++) {
            g2.drawLine(halfWidth, y, halfWidth+3, y);
            g2.drawString(numberFormat.format(val), halfWidth+6, y+5);

            y += dy;
            val -= dValue;
        }
        //bottom of scale
        g2.drawLine(halfWidth, drawRectHeight + Y_OFFSET, halfWidth+3, drawRectHeight + Y_OFFSET);
        g2.drawString(numberFormat.format(val), halfWidth+6, y+5);
    }
}