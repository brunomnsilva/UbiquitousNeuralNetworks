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

package com.brunomnsilva.neuralnetworks.dataset;

import com.brunomnsilva.neuralnetworks.core.VectorN;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A Dataset summary. Provides minimum, maximum and mean values across all variables.
 *
 * @author brunomnsilva
 */
public class DatasetSummary {
    private VectorN inputMinimumValues;
    private VectorN inputMaximumValues;
    private VectorN outputMinimumValues;
    private VectorN outputMaximumValues;
    private VectorN inputMeanValues;
    private VectorN outputMeanValues;

    private final Dataset dataset;

    /**
     * Default constructor.
     * @param dataset the dataset to generate the summary from
     */
    public DatasetSummary(final Dataset dataset) {
        int inputLen = dataset.get(0).getInput().dimensions();
        int outputLen = dataset.get(0).getTargetOutput().dimensions();

        inputMinimumValues = dataset.get(0).getInput().copy();
        inputMaximumValues = dataset.get(0).getInput().copy();
        outputMinimumValues = dataset.get(0).getTargetOutput().copy();
        outputMaximumValues = dataset.get(0).getTargetOutput().copy();

        inputMeanValues = VectorN.zeros(inputLen);
        outputMeanValues = VectorN.zeros(outputLen);

        for (DatasetItem item : dataset) {
            inputMinimumValues = VectorN.min(item.getInput(), inputMinimumValues);
            inputMaximumValues = VectorN.max(item.getInput(), inputMaximumValues);
            outputMinimumValues = VectorN.min(item.getTargetOutput(), outputMinimumValues);
            outputMaximumValues = VectorN.max(item.getTargetOutput(), outputMaximumValues);

            inputMeanValues.add(item.getInput());
            outputMeanValues.add(item.getTargetOutput());
        }

        inputMeanValues.divide(dataset.size());
        outputMeanValues.divide(dataset.size());

        this.dataset = dataset;
    }

    /**
     * Returns a vector with the minimum values across the input variables.
     * @return a vector with the minimum values across the input variables
     */
    public VectorN inputMinimumValues() {
        return inputMinimumValues.copy();
    }

    /**
     * Returns a vector with the maximum values across the input variables.
     * @return a vector with the maximum values across the input variables
     */
    public VectorN inputMaximumValues() {
        return inputMaximumValues.copy();
    }

    /**
     * Returns a vector with the minimum values across the target output variables.
     * @return a vector with the minimum values across the target output variables
     */
    public VectorN outputMinimumValues() {
        return outputMinimumValues.copy();
    }

    /**
     * Returns a vector with the maximum values across the target output variables.
     * @return a vector with the maximum values across the target output variables
     */
    public VectorN outputMaximumValues() {
        return outputMaximumValues.copy();
    }

    /**
     * Returns a vector with the mean values across the input variables.
     * @return a vector with the mean values across the input variables
     */
    public VectorN inputMeanValues() {
        return inputMeanValues.copy();
    }

    /**
     * Returns a vector with the mean values across the target output variables.
     * @return a vector with the mean values across the target output variables
     */
    public VectorN outputMeanValues() {
        return outputMeanValues.copy();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');
        NumberFormat formatter = new DecimalFormat("###,###.###", otherSymbols);

        int totalVariables = dataset.inputDimensionality() + dataset.outputDimensionality();

        sb.append(String.format("%-10s", ""));
        for(int i=0; i < totalVariables; i++) {
            String name = i < dataset.inputDimensionality() ? inputVariableName(i) : outputVariableName(totalVariables - 1 - i);
            name = abbreviate(name, 17);
            String type = i < dataset.inputDimensionality() ? "(in)" : "(out)";
            sb.append(String.format("%-20s\t", (name+type)));
        }
        sb.append("\n");

        appendStatistics("Min.:", inputMinimumValues.values(), outputMinimumValues.values(), sb, formatter);
        appendStatistics("Max.:", inputMaximumValues.values(), outputMaximumValues.values(), sb, formatter);
        appendStatistics("Mean:", inputMeanValues.values(), outputMeanValues.values(), sb, formatter);

        sb.append(String.format("\nTotal: %s observations", formatter.format(dataset.size()))).append("\n");

        return sb.toString();
    }

    private String inputVariableName(int index) {
        return dataset.inputVariableNames()[index];
    }

    private String outputVariableName(int index) {
        return dataset.outputVariableNames()[index];
    }

    private void appendStatistics(String name, double[] inValues, double[] outValues,
                                  StringBuilder sb, NumberFormat formatter) {

        int totalVariables = inValues.length + outValues.length;
        sb.append(String.format("%-10s", name));
        for(int i=0; i < totalVariables; i++) {
            double val = i < inValues.length ? inValues[i] : outValues[totalVariables - 1 - i];
            sb.append(String.format("%-20s\t", formatter.format(val)));
        }
        sb.append("\n");
    }

    private static String abbreviate(String input, int length) {
        return ( input.length () > length ) ? input.substring ( 0 , length - 1 ).concat ( "…" ) : input;
    }

}
