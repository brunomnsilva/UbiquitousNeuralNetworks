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
 * Utility class for reading and writing CSV files.
 *
 * @author brunomnsilva
 */
public final class CSVUtils {

    /**
     * Appends a sequence of <i>double</i> values to a CSV file, using the default (comma) value separator.
     * @param fw the file to write
     * @param values the sequence of values
     * @throws IOException if the file cannot be written to
     */
    public static void appendValuesToFile(FileWriter fw, double ...values) throws IOException {
        // By default values are comma-separated
        appendValuesToFile(fw, ',', values);
    }

    /**
     * Appends a sequence of <i>double</i> values to a CSV file, using the specified value separator.
     * @param fw the file to write
     * @param separator the value separator to use
     * @param values the sequence of values
     * @throws IOException if the file cannot be written to
     */
    public static void appendValuesToFile(FileWriter fw, char separator, double ...values) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < values.length; ++i) {
            sb.append(values[i]);
            if(i != values.length - 1) {
                sb.append(separator);
            }
        }
        sb.append("\n");
        fw.write(sb.toString());
    }

    /**
     * Appends a sequence of <i>String</i> values to a CSV file, using the default (comma) value separator.
     * @param fw the file to write
     * @param values the sequence of values
     * @throws IOException if the file cannot be written to
     */
    public static void appendValuesToFile(FileWriter fw, String ...values) throws IOException {
        // By default values are comma-separated
        appendValuesToFile(fw, ',', values);
    }

    /**
     * Appends a sequence of <i>String</i> values to a CSV file, using the default (comma) value separator.
     * @param fw the file to write
     * @param separator the value separator to use
     * @param values the sequence of values
     * @throws IOException if the file cannot be written to
     */
    public static void appendValuesToFile(FileWriter fw, char separator, String ...values) throws IOException {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < values.length; ++i) {
            sb.append(values[i]);
            if(i != values.length - 1) {
                sb.append(separator);
            }
        }
        sb.append("\n");
        fw.write(sb.toString());
    }
}
