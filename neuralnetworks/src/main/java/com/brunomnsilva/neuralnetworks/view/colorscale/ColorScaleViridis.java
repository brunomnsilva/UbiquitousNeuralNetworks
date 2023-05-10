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
 * An implementation of a viridis colorscale.
 *
 * @author brunomnsilva
 */
public class ColorScaleViridis extends ColorScaleFromLevels {

    /**
     * Default constructor.
     */
    public ColorScaleViridis() {
        super(new int[][]{
                {68, 1, 84},
                {72, 35, 116},
                {62, 67, 135},
                {47, 94, 150},
                {32, 120, 154},
                {24, 137, 147},
                {29, 152, 135},
                {50, 165, 120},
                {86, 174, 96},
                {132, 181, 69},
                {186, 186, 39},
                {253, 190, 2}
        });
    }

    @Override
    public String name() {
        return "Viridis";
    }

}
