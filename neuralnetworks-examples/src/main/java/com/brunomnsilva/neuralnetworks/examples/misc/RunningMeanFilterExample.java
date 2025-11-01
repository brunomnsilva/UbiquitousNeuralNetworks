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

package com.brunomnsilva.neuralnetworks.examples.misc;

import com.brunomnsilva.neuralnetworks.core.*;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;

import java.io.IOException;

public class RunningMeanFilterExample {
    public static void main(String[] args) throws IOException {

        // Create a signal with noise
        double[] signal = SignalGenerator.generateWaveSignal(10000, 10, 0.01, 20, 5);

        // Instantiate filters
        AbstractRunningMeanFilter simpleRM = new SimpleRunningMeanFilter("Simple", 50);
        AbstractRunningMeanFilter tripleRM = new TripleCascadedMeanFilter("Triple", 50);

        // Pass the signal through the filter and track its values
        TimeSeries simpleSeries = new TimeSeries(simpleRM.getName() + simpleRM.getWindowSize());
        TimeSeries tripleSeries = new TimeSeries(tripleRM.getName() + tripleRM.getWindowSize());

        int time = 1;
        for (double val : signal) {
            double output = simpleRM.filter(val);
            simpleSeries.append(time, output);

            output = tripleRM.filter(val);
            tripleSeries.append(time, output);

            time++;
        }

        // We can export the filtered values, if we want
        //TimeSeriesExport.toCSV(simpleSeries, "filter.csv");

        // Plot the signal and the filter's output
        Plot2D plot = new Plot2D.Builder()
                .title("Signal and Filter")
                .linePlotFromTimeSeries(simpleSeries)
                .linePlotFromTimeSeries(tripleSeries)
                .linePlotFromArray("Signal", signal)
                .xLabel("time")
                .yLabel("value")
                .legend(true)
                .height(400)
                .width(600)
                .build();

        GenericWindow window = GenericWindow.horizontalLayout(plot.getTitle(), plot);
        window.setVisible(true);
    }
}
