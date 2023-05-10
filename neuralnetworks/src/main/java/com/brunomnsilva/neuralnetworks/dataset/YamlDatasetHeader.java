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

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A data set header, in yaml format, that specifies some attributes, namely:
 * <ul>
 *     <li>Name - the name of the dataset;</li>
 *     <li>Description - a description of the dataset;</li>
 *     <li>Variables - a list of variables names of the inputs and target output vectors</li>
 *     <li>Number inputs - how many variables/components belong to the input data</li>
 *     <li>Number outputs - how many variables/components belong to the target output data</li>
 * </ul>
 * Provided below is an example of a yaml header for the IRIS dataset:
 * <code>
 *     ---
 * name: IRIS
 * description: Iris flower dataset from UCI repository
 * variables:
 *     - sepal_length # in cm
 *     - sepal_width # in cm
 *     - petal_length # in cm
 *     - petal_width # in cm
 *     - setosa # class
 *     - versicolor # class
 *     - virginica # class
 *
 * inputs: 4
 * outputs: 3
 * ---
 * </code>
 */
public class YamlDatasetHeader {

    private final String name;
    private final String description;
    private final List<String> variables;
    private final Integer numberInputs;
    private final Integer numberOutputs;

    private ExtractorInfo extractorInfo;

    /**
     * Creates a YamlDatasetHeader instance initialized with the arguments.
     * @param name the name of the dataset
     * @param description the description of the dataset
     * @param variables the collection of variables of the dataset
     * @param numberInputs the number of input variables
     * @param numberOutputs the number of target output variables
     */
    public YamlDatasetHeader(String name, String description, List<String> variables, int numberInputs, int numberOutputs) {
        this.name = name;
        this.description = description;
        this.variables = variables;
        this.numberInputs = numberInputs;
        this.numberOutputs = numberOutputs;

        this.extractorInfo = null;
    }

    /**
     * Returns the name of the dataset.
     * @return the name of the dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the dataset.
     * @return the description of the dataset
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the collection of variables of the dataset.
     * @return the collection of variables of the dataset
     */
    public List<String> getVariables() {
        return variables;
    }

    /**
     * Returns the number of input variables.
     * @return the number of input variables
     */
    public Integer getNumberInputs() {
        return numberInputs;
    }

    /**
     * Returns the number of target output variables.
     * @return the number of target output variables
     */
    public Integer getNumberOutputs() {
        return numberOutputs;
    }

    /**
     * After parsing a yaml header, returns the file line where the header starts.
     * @return starting line of the header
     */
    public int getParsedLineStart() {
        return (extractorInfo != null) ? extractorInfo.getStartLine() : -1;
    }

    /**
     * After parsing a yaml header, returns the file line where the header ends.
     * @return ending line of the header
     */
    public int getParsedLineEnd() {
        return (extractorInfo != null) ? extractorInfo.getEndLine() : -1;
    }

    private void attachExtractorInfo(ExtractorInfo info) {
        this.extractorInfo = info;
    }

    /**
     * Parses the yaml header according to the expected format.
     * <br/>
     * The extractor will process all text between a pair of <code>"---"</code> fences.
     * @param file the file from with to extract the header information
     * @return an instance of YamlDatasetHeader with parsed information
     * @throws InvalidDatasetFormatException if any required fields are missing or empty
     */
    public static YamlDatasetHeader fromParse(File file) throws InvalidDatasetFormatException {

        ExtractorInfo extractor = new ExtractorInfo(file);
        if(!extractor.isValid()) {
            String error = "Data set header not found or doesn't have a yaml format. Check documentation.";
            throw new InvalidDatasetFormatException(error);
        }

        String headerContents = extractor.getContents();
        Yaml yamlParser = new Yaml();
        Map<String, Object> yamlHeader = yamlParser.load(headerContents);

        // Check presence of all required information for the header

        String mandatory[] = new String[]{"name", "description", "variables", "inputs", "outputs"};
        for (String key : mandatory) {
            Object value = yamlHeader.get(key);
            if(value == null) {
                String error = String.format("Missing \"%s\" key/value in data set header. Please check the documentation.", key);
                throw new InvalidDatasetFormatException(error);
            }
        }

        // Here we know we have all the required keys, but some additional verifications
        // are required to check if theirs values are valid.
        String name, description;
        List<String> variables;
        Integer inputs, outputs;

        try {
            name = (String)yamlHeader.get("name");
            description = (String)yamlHeader.get("description");
            variables = (List<String>)yamlHeader.get("variables");
            inputs = (Integer)yamlHeader.get("inputs");
            outputs = (Integer)yamlHeader.get("outputs");
        } catch (ClassCastException e) {
            String error = String.format("Mismatch of key and expected value type in data set header. Please check the documentation.");
            throw new InvalidDatasetFormatException(error);
        }

        if(variables.isEmpty()) {
            String error = String.format("Missing variable names (sublist) in data set header. Please check the documentation.");
            throw new InvalidDatasetFormatException(error);
        }

        if(inputs <= 0) {
            String error = String.format("The value for \"inputs\" cannot be <= 0. Please check the documentation.");
            throw new InvalidDatasetFormatException(error);
        }

        if(outputs < 0) {
            String error = String.format("The value for \"outputs\" cannot be < 0. Please check the documentation.");
            throw new InvalidDatasetFormatException(error);
        }

        int variableCount = variables.size();
        int expectedCount = inputs + outputs;
        if(variableCount != expectedCount) {
            String error = String.format("The number of variables do not match the inputs/outputs count. Please check the documentation.");
            throw new InvalidDatasetFormatException(error);
        }

        for (String var : variables) {
            if(var == null) {
                String error = String.format("The name of a variable cannot be empty. Please check the documentation.");
                throw new InvalidDatasetFormatException(error);
            }
        }

        YamlDatasetHeader yamlDatasetHeader = new YamlDatasetHeader(name, description, variables, inputs, outputs);
        yamlDatasetHeader.attachExtractorInfo(extractor);
        return yamlDatasetHeader;
    }

    /**
     * Returns whether the content header is valid, i.e., all fields are present and/or not empty.
     * @return <i>true</i> if valid; <i>false</i> otherwise.
     */
    public boolean isContentValid() {
        return (
                (name != null && !name.isEmpty()) &&
                (description != null && !description.isEmpty()) &&
                (variables != null && variables.size() > 0) &&
                (numberInputs != null && numberInputs > 0) &&
                (numberOutputs != null && numberOutputs >= 0)
        );
    }

    /**
     * Returns the formatted header information.
     * @return the formatted header information
     * @throws IllegalStateException if the content is not valid
     */
    public String exportContents() throws IllegalStateException {
        if(!isContentValid()) throw new IllegalStateException("Invalid contents.");

        return toString();
    }

    @Override
    public String toString() {
        if(!isContentValid()) return "<Invalid content. Not all fields are set.>";

        StringBuilder sb = new StringBuilder();

        // Produce yaml output format
        sb.append("---").append("\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("description: ").append(description).append("\n");

        sb.append("variables: ").append("\n");
        for (String var : variables) {
            sb.append("    - ").append(var).append("\n"); // cannot use '\t'. This will fail in the parser later on.
        }

        sb.append("inputs: ").append(numberInputs).append("\n");
        sb.append("outputs: ").append(numberOutputs).append("\n");
        sb.append("---").append("\n");

        return sb.toString();
    }

    /* Extractor supporting class */

    /**
     * Finds and extracts the header part from a file.
     */
    private static class ExtractorInfo {
        private int startLine;
        private int endLine;
        private String contents;

        public ExtractorInfo(File file) {
            this.startLine = -1;
            this.endLine = -1;
            this.contents = "";

            extractLinesBetweenMarkers(file);
        }

        public boolean isValid() {
            return startLine != -1 && endLine != -1 && !contents.isEmpty();
        }

        public int getStartLine() {
            return startLine;
        }

        public int getEndLine() {
            return endLine;
        }

        public String getContents() {
            return contents;
        }

        @Override
        public String toString() {
            return "HeaderInfo{" +
                    "startLine=" + startLine +
                    ", endLine=" + endLine +
                    ", contents='" + contents + '\'' +
                    '}';
        }

        private void extractLinesBetweenMarkers(File file) {
            final StringBuilder headerLines = new StringBuilder();
            boolean markerFound = false;
            boolean markerFoundBeforeEnd = false;
            int lineNumber = 1, startLine = -1, endLine = -1;
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals("---")) {
                        if (markerFound) {
                            // end marker found, break the loop
                            markerFoundBeforeEnd = true;
                            endLine = lineNumber;
                            break;
                        } else {
                            // start marker found, start adding lines to the list
                            markerFound = true;
                            startLine = lineNumber;
                        }
                    } else if (markerFound) {
                        // add line to the header contents
                        headerLines.append(line).append("\n");
                    }

                    lineNumber++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(markerFoundBeforeEnd) {
                this.startLine = startLine;
                this.endLine = endLine;
                this.contents = headerLines.toString();
            }
        }
    }
}
