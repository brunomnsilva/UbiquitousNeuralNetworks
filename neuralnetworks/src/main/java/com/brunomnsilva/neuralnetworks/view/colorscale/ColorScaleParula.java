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
 * An implementation of a parula colorscale.
 *
 * @author brunomnsilva
 */
public class ColorScaleParula extends ColorScaleFromLevels {

    /**
     * Default constructor.
     */
    public ColorScaleParula() {
        super(new int[][]{
                {53, 42, 135},
                {15, 92, 221},
                {18, 125, 216},
                {33, 155, 202},
                {54, 180, 175},
                {79, 193, 140},
                {117, 199, 90},
                {165, 191, 38},
                {217, 168, 13},
                {254, 123, 7},
                {255, 72, 0},
                {213, 28, 0},
                {158, 1, 66}
        });
    }

    @Override
    public String name() {
        return "Parula";
    }

}
