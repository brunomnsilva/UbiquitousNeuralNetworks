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

/**
 * A factory class for creating color scales.
 *
 * @author brunomnsilva
 */
public class ColorScaleFactory {

    /**
     * An enumeration of the available color scales.
     */
    public enum Scale {GRAY, HUE, PARULA, VIRIDIS, ORANGE_BLUE };

    /**
     * The default color scale.
     */
    public static Scale defaultColorScale = Scale.VIRIDIS;

    /**
     * Sets the default color scale.
     *
     * @param defaultColorScale the new default color scale to set
     */
    public static void setDefaultColorScale(Scale defaultColorScale) {
        ColorScaleFactory.defaultColorScale = defaultColorScale;
    }

    /**
     * Creates a new color scale using the default color scale.
     *
     * @return a new color scale using the default color scale
     */
    public static ColorScale createDefault() {
        return create(defaultColorScale);
    }

    /**
     * Creates a new color scale of the specified type.
     *
     * @param type the type of color scale to create
     * @return a new color scale of the specified type
     */
    public static ColorScale create(Scale type) {
        switch (type) {
            case HUE: return new ColorScaleHue();
            case GRAY: return new ColorScaleGray();
            case PARULA: return new ColorScaleParula();
            case VIRIDIS: return new ColorScaleViridis();
            case ORANGE_BLUE: return new ColorScaleOrangeBlue();
        }

        // Default to.. default colorscale, e.g., if null
        return createDefault();
    }

    /**
     * Creates a new color scale of the specified name.
     *
     * @param name the name of the color scale to create
     * @return a new color scale of the specified name
     * @throws IllegalArgumentException if the specified name is not a valid color scale
     */
    public static ColorScale create(String name) throws IllegalArgumentException {
        Scale scale = Scale.valueOf(name.toUpperCase());
        return create(scale);
    }
}

