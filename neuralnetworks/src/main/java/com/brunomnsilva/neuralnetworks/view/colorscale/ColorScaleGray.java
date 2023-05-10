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
 * An implementation of a gray colorscale.
 *
 * @author brunomnsilva
 */
public class ColorScaleGray extends ColorScaleFromLevels {

    public ColorScaleGray() {
        super(new int[][]{
                {0, 0, 0},      // black
                {28, 28, 28},   // dark gray
                {56, 56, 56},   // medium gray
                {84, 84, 84},   // ...
                {112, 112, 112},
                {140, 140, 140},
                {168, 168, 168},
                {196, 196, 196},
                {224, 224, 224},  // light gray
                {244, 244, 244},  // very light gray
                {252, 252, 252},  // almost white
                {255, 255, 255}   // white
        });
    }

    @Override
    public String name() {
        return "Grayscale";
    }

}
