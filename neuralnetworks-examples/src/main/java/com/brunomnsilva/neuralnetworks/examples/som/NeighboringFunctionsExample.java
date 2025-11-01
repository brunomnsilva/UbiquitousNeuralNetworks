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

package com.brunomnsilva.neuralnetworks.examples.som;

import com.brunomnsilva.neuralnetworks.core.TimeSeries;
import com.brunomnsilva.neuralnetworks.models.som.NeighboringFunction;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;

public class NeighboringFunctionsExample {
    public static void main(String[] args) {

        TimeSeries gaussian = new TimeSeries("Gaussian");
        TimeSeries bubble = new TimeSeries("Bubble");
        TimeSeries pyramid = new TimeSeries("Pyramid");

        final int maxDistance = 100;
        final double sigma = 40;

        for(int t=0; t < maxDistance; ++t) {
            gaussian.append(t , NeighboringFunction.gaussian(t, sigma));
            bubble.append(t , NeighboringFunction.bubble(t, sigma));
            pyramid.append(t , NeighboringFunction.pyramid(t, sigma));
        }

        // Plot the signal and the filter's output
        Plot2D plot = new Plot2D.Builder()
                .title("Neighboring Functions")
                .linePlotFromTimeSeries(gaussian)
                .linePlotFromTimeSeries(bubble)
                .linePlotFromTimeSeries(pyramid)
                .xLabel("dist")
                .yLabel("h(dist)")
                .legend(true)
                .height(400)
                .width(600)
                .build();

        GenericWindow window = GenericWindow.horizontalLayout(plot.getTitle(), plot);
        window.setVisible(true);
    }
}
