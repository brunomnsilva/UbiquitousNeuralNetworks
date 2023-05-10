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
import com.brunomnsilva.neuralnetworks.models.som.DecayFunction;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;

public class DecayFunctionsExample {
    public static void main(String[] args) {

        TimeSeries exponential = new TimeSeries("Exponential");
        TimeSeries linear = new TimeSeries("Linear");
        TimeSeries inverseTime = new TimeSeries("Inverse-Time");

        final int T = 2000;
        final double pi = 0.1;
        final double pf = 0.01;

        // Also check what happens when t > T (for 100 iterations)
        for(int t=0; t < T + 100; ++t) {
            exponential.append(t , DecayFunction.exponential(pi, pf, t, T));
            linear.append(t , DecayFunction.linear(pi, pf, t, T));
            inverseTime.append(t , DecayFunction.inverseTime(pi, pf, 10000, t, T));
        }

        // Plot the signal and the filter's output
        Plot2D plot = new Plot2D.Builder()
                .title("Decay Functions")
                .linePlotFromTimeSeries(exponential)
                .linePlotFromTimeSeries(linear)
                .linePlotFromTimeSeries(inverseTime)
                .xLabel("t")
                .yLabel("f(t)")
                .legend(true)
                .height(400)
                .width(600)
                .build();

        GenericWindow window = GenericWindow.horizontalLayout(plot.getTitle(), plot);
        window.setVisible(true);
    }
}
