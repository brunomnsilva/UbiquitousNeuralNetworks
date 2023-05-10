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

package com.brunomnsilva.neuralnetworks.examples.mlp;

import com.brunomnsilva.neuralnetworks.core.TimeSeries;
import com.brunomnsilva.neuralnetworks.models.mlp.LinearActivation;
import com.brunomnsilva.neuralnetworks.models.mlp.ReLUActivation;
import com.brunomnsilva.neuralnetworks.models.mlp.SigmoidActivation;
import com.brunomnsilva.neuralnetworks.models.mlp.TanhActivation;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;

public class ActivationFunctionsExample {

    public static void main(String[] args) {

        TimeSeries linear = new TimeSeries("linear");
        TimeSeries sigmoid = new TimeSeries("sigmoid");
        TimeSeries tanh = new TimeSeries("tanh");
        TimeSeries relu = new TimeSeries("ReLU");

        final int pi = -5;
        final int pf = 5;

        LinearActivation linearActivation = new LinearActivation();
        SigmoidActivation sigmoidActivation = new SigmoidActivation();
        TanhActivation tanhActivation = new TanhActivation();
        ReLUActivation reLuActivation = new ReLUActivation();

        for(int x=pi; x <= pf; x += 1) {
            linear.append(x, linearActivation.compute(x));
            sigmoid.append(x, sigmoidActivation.compute(x));
            tanh.append(x, tanhActivation.compute(x));
            relu.append(x, reLuActivation.compute(x));
        }

        Plot2D plot = new Plot2D.Builder()
                .title("Activation Functions")
                .linePlotFromTimeSeries(linear)
                .linePlotFromTimeSeries(sigmoid)
                .linePlotFromTimeSeries(tanh)
                .linePlotFromTimeSeries(relu)
                .xLabel("x")
                .yLabel("f(x)")
                .legend(true)
                .height(400)
                .width(600)
                .build();

        GenericWindow window = GenericWindow.horizontalLayout(plot.getTitle(), plot);
        window.setVisible(true);
    }
}
