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

package com.brunomnsilva.neuralnetworks.view.chart;

import com.brunomnsilva.neuralnetworks.view.LookAndFeel;

import java.awt.*;
import java.text.NumberFormat;

/**
 * An extension of the {@link org.jfree.chart.axis.NumberAxis} class, with a Builder pattern.
 *
 * @author brunomnsilva
 */
public class NumberAxis extends org.jfree.chart.axis.NumberAxis {

    /**
     * The builder of the NumberAxis. Allows to set some values, while keeping others with default values.
     */
    public static class Builder {
        private String label;
        private Double min;
        private Double max;
        private NumberFormat numberFormat;
        private Font font;

        /**
         * Default constructor.
         * @param label the label of the axis
         */
        public Builder(String label) {
            this.label = label;
        }

        /**
         * Sets the (hard) limits of the axis values.
         * @param min minimum value
         * @param max maximum value
         * @return the updated builder
         */
        public Builder withLimits(Double min, Double max) {
            this.min = min;
            this.max = max;
            return this;
        }

        /**
         * Sets the number formatting of the number axis.
         * @param numberFormat the number format
         * @return the updated builder
         */
        public Builder withFormat(NumberFormat numberFormat) {
            this.numberFormat = numberFormat;
            return this;
        }

        /**
         * Sets the font of the number axis.
         * @param font the font
         * @return the updated builder
         */
        public Builder withFont(Font font) {
            this.font = font;
            return this;
        }

        /**
         * Creates a new instance of NumberAxis from the builder state.
         * @return a new instance of NumberAxis
         */
        public NumberAxis build() {
            NumberAxis axis = new NumberAxis();
            axis.setLabel(label);
            if(min != null && max != null) {
                axis.setRange(min, max);
                axis.setAutoRange(false);
            } else {
                axis.setAutoRange(true);
            }
            if(numberFormat != null) {
                axis.setNumberFormatOverride(numberFormat);
            }
            if(font != null) {
                axis.setTickLabelFont(font);
            }
            axis.setTickLabelFont(LookAndFeel.fontTextSmall);
            axis.setLabelFont(LookAndFeel.fontTextRegular);
            return axis;
        }
    }

    private NumberAxis() {
        // Empty. State is set by the builder.
    }
}
