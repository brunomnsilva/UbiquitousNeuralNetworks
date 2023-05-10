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

package com.brunomnsilva.neuralnetworks.models.som;

import com.brunomnsilva.neuralnetworks.core.ArrayUtils;
import com.brunomnsilva.neuralnetworks.core.CSVUtils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A utility class for exporting SelfOrganizingMaps.
 *
 * @author brunomnsilva
 */
public class SelfOrganizingMapExport {

    /**
     * Exports a SelfOrganizingMap in CSV format, using the default (comma) separator.
     * @param som the SelfOrganizingMap to export
     * @param outputFilename the filename of the CSV file
     * @throws IOException if the file cannot be created
     */
    public static void toCSV(SelfOrganizingMap som, String outputFilename) throws IOException {
        toCSV(som, outputFilename, ',');
    }

    /**
     * Exports a SelfOrganizingMap in CSV format.
     * @param som the SelfOrganizingMap to export
     * @param outputFilename the filename of the CSV file
     * @param separator the data separator to use
     * @throws IOException if the file cannot be created
     */
    public static void toCSV(SelfOrganizingMap som, String outputFilename, char separator) throws IOException {
        FileWriter fw = new FileWriter(outputFilename);

        int dimensionality = som.getDimensionality();
        String[] dimensionNames = new String[dimensionality];
        for(int i=0; i < dimensionality; ++i) {
            dimensionNames[i] = "v" + (i+1);
        }

        String[] header = ArrayUtils.concatenate(new String[]{"x", "y"}, dimensionNames);
        CSVUtils.appendValuesToFile(fw, separator, header);

        for (PrototypeNeuron p : som) {
            double[] coords = new double[]{p.getIndexX(), p.getIndexY()};
            double[] values = p.getPrototype().values();

            double[] line = ArrayUtils.concatenate(coords, values);
            CSVUtils.appendValuesToFile(fw, separator, line);
        }

        fw.close();
    }
}
