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

package com.brunomnsilva.neuralnetworks.core;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A utility class for exporting TimeSeries.
 *
 * @author brunomnsilva
 */
public class TimeSeriesExport {

    /**
     * Exports a TimeSeries in CSV format, using the default (comma) separator.
     * @param timeSeries the TimeSeries to export
     * @param outputFilename the filename of the CSV file
     * @throws IOException if the file cannot be created
     */
    public static void toCSV(TimeSeries timeSeries, String outputFilename) throws IOException {
        toCSV(timeSeries, outputFilename, ',');
    }

    /**
     * Exports a TimeSeries in CSV format.
     * @param timeSeries the TimeSeries to export
     * @param outputFilename the filename of the CSV file
     * @param separator the data separator to use
     * @throws IOException if the file cannot be created
     */
    public static void toCSV(TimeSeries timeSeries, String outputFilename, char separator) throws IOException {
        FileWriter fw = new FileWriter(outputFilename);

        CSVUtils.appendValuesToFile(fw, separator, "Time", "Value");
        for (TimeValueTuple timeValue : timeSeries) {
            CSVUtils.appendValuesToFile(fw, separator, timeValue.getTime(), timeValue.getValue());
        }

        fw.close();
    }
}
