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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A ColorScale from an array of colors (levels). Other necessary colors will be interpolated between
 * these levels.
 *
 * @author brunomnsilva
 */
public abstract class ColorScaleFromLevels implements ColorScale {
    private final int[][] levels;
    private final BufferedImage image;

    /**
     * Constructor that initializes the levels of this ColorScale.
     * <br/>
     * <code>levels</code> must have the form of <code>levels[nColors][3]</code>, where the
     * RGB components (0, 255) are set for each color.
     * <br/>
     * The color at <code>levels[0]</code> will be used for the lowest value, while the last
     * will be used for the highest value of the ColorScale.
     * @param levels the color levels to use to determine the colorscale
     */
    public ColorScaleFromLevels(final int[][] levels) {
        Args.nullNotPermitted(levels, "levels");
        Args.requireEqual(levels[0].length, "levels[0].length", 3, "RGB values");

        this.levels = levels;
        this.image = imageFromScale(this, 1, 255);
    }

    @Override
    public final Color valueToColor(double value, double minimum, double maximum) {
        if(value > maximum) value = maximum;
        else if(value < minimum) value = minimum;

        // Normalize the value to the range [0, 1]
        double normalizedValue = (value - minimum) / (maximum - minimum);

        // Determine the indices of the two nearest colors in the scale
        int colorIndex1 = (int) Math.floor(normalizedValue * (levels.length - 1));
        int colorIndex2 = (colorIndex1 >= levels.length - 1) ? colorIndex1 : colorIndex1 + 1;

        // Determine the relative distance between the two nearest colors
        double colorDistance = normalizedValue * (levels.length - 1) - colorIndex1;

        // Interpolate between the two nearest colors based on the relative distance
        int red = (int) Math.round(levels[colorIndex1][0] + colorDistance * (levels[colorIndex2][0] - levels[colorIndex1][0]));
        int green = (int) Math.round(levels[colorIndex1][1] + colorDistance * (levels[colorIndex2][1] - levels[colorIndex1][1]));
        int blue = (int) Math.round(levels[colorIndex1][2] + colorDistance * (levels[colorIndex2][2] - levels[colorIndex1][2]));

        // Create and return the color object
        return new Color(red, green, blue);
    }

    @Override
    public final void draw(Graphics2D g, int x, int y, int width, int height) {
        g.drawImage(image, x, y, width, height, null);
    }

}
