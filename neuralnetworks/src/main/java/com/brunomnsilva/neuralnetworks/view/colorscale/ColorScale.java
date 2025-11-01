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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Defines the required behavior of a colorscale.
 *
 * @author brunomnsilva
 */
public interface ColorScale {

    /**
     * Returns the corresponding color of the ColorScale for a specific <i>value</i>,
     * given the <i>minimum</i> and <i>maximum</i> values that <i>value</i> can attain.
     *
     * @param value the value to be converted into a color
     * @param minimum lower bound value for <code>value</code>
     * @param maximum upper bound value for <code>value</code>
     * @return the corresponding color of the colorscale
     */
    Color valueToColor(double value, double minimum, double maximum);

    /**
     * Renders (draw/paint) a ColorScale inside a rectangle defined by
     * its top-left origin and size.
     *
     * @param g graphical context
     * @param x top-left x coordinate of the rectangle
     * @param y top-left y coordinate of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     */
    void draw(Graphics2D g, int x, int y, int width, int height);

    /**
     * Provides the name of the ColorScale.
     * @return the name of the colorscale
     */
    String name();

    /**
     * Creates a visual representation (image) of a ColorScale.
     * <br/>
     * The intent is to use the produced image to draw a ColorScale in a panel.
     *
     * @param scale the colorscale
     * @param width the produced image width
     * @param height the produced image width
     * @return an image of the colorscale
     */
    default  BufferedImage imageFromScale(ColorScale scale, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Note: Color scale values and y-axis of image are inverted
        for(int i=0; i<width; ++i) {
            for(int j=0; j<height; ++j) {
                Color levelColor = scale.valueToColor(j, 0, height);

                image.setRGB(i, height - 1 - j, levelColor.getRGB());
            }
        }
        return image;
    }
}
