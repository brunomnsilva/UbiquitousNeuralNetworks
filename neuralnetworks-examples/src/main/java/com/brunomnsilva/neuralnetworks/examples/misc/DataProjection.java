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

import com.brunomnsilva.neuralnetworks.core.Projections;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetUtils;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;

public class DataProjection {

    public static void main(String[] args) {
        try {
            //Dataset dataset = new Dataset("datasets/hepta.data");
            //Dataset dataset = new Dataset("datasets/iris.data");
            Dataset dataset = new Dataset("datasets/wine.data");

            double[][] data = DatasetUtils.inputsTo2dArray(dataset);

            Projections.PCA pca = Projections.PCAfrom2dArray(data);

            double[][] projectedData = pca.project(data, 2);

            Plot2D plot = new Plot2D.Builder()
                    .scatterPlotFrom2dArray("dataset", projectedData)
                    .height(400)
                    .width(400)
                    .title("PCA Projection")
                    .xLabel("1st component")
                    .yLabel("2nd component")
                    .build();

            GenericWindow window = GenericWindow.horizontalLayout("PCA", plot);
            window.exitOnClose();
            window.setVisible(true);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
