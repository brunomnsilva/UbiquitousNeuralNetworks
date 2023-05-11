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

import com.brunomnsilva.neuralnetworks.core.Args;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * A data set, where each item/observation/entry is of type {@link DatasetItem}.
 * Datasets are loaded from text files that are required to have a yaml header, describing
 * the details of the dataset, see {@link YamlDatasetHeader}.
 *
 * This dataset is {@link Iterable} over {@link DatasetItem} elements. Different traversals
 * can be obtained by shuffling the dataset, see {@link #shuffle()} and {@link #shuffle(int)}.
 *
 * A dataset will not allow adding or removing of items, once it is loaded. We can, however,
 * obtain only "parts" of the dataset through, e.g., {@link #crop(int, int)}.
 *
 * @see YamlDatasetHeader
 * @see DatasetItem
 *
 * @author brunomnsilva
 */
public class Dataset implements Iterable<DatasetItem> {
    /** The default data delimiter when importing data sets from files */
    private static final String DELIMITER_DEFAULT = ",";

    /** The underlying File */
    private File underlyingFile;

    /** The delimiter of the data when importing data sets*/
    private String delimiter;

    /** The parsed yaml header */
    private YamlDatasetHeader header;

    /** Collection that holds in memory all data set items */
    private List<DatasetItem> items;

    /** Constant that limits the output of data items in the toString method */
    private static final int ITEM_TOSTRING_LIMIT = 100;

    /**
     * Imports a dataset from the file specified in <code>filePath</code> using default data delimiter (comma).
     * The file must have a valid Yaml header.
     * @param filePath the file path of the file to import
     * @throws IOException if the file does not exist
     * @throws InvalidDatasetFormatException if the Yaml header is invalid or if the data set is empty
     */
    public Dataset(String filePath)
            throws IOException, InvalidDatasetFormatException {
        
        this(filePath, DELIMITER_DEFAULT);
    }

    /**
     * Imports a dataset from the file specified in <code>filePath</code> using the data <code>delimiter</code>.
     * The file must have a valid Yaml header.
     * @param filePath the file path of the file to import
     * @param delimiter the data delimiter to use
     * @throws IOException if the file does not exist
     * @throws InvalidDatasetFormatException if the Yaml header is invalid or if the data set is empty
     */
    public Dataset(String filePath, String delimiter)
            throws IOException, InvalidDatasetFormatException {
        Args.nullNotPermitted(filePath, "filePath");
        Args.nullNotPermitted(delimiter, "delimiter");

        this.underlyingFile = new File(filePath);
        if(!this.underlyingFile.exists()){
            throw new FileNotFoundException(String.format("The file %s does not exist.", filePath));
        }

        this.delimiter = delimiter;
        this.items = new ArrayList<>();

        parse(); // may throw InvalidDatasetFormatException
    }

    private Dataset(File underlyingFile, String delimiter, YamlDatasetHeader header, List<DatasetItem> items) {
        this.underlyingFile = underlyingFile;
        this.delimiter = delimiter;
        this.header = header;
        this.items = items;
    }

    /**
     * Returns a deep-copy of the Dataset.
     * @return a copy of the Dataset
     */
    public Dataset copy() {
        //perform deep copy of items
        List<DatasetItem> itemsCopy = new ArrayList<>();
        for (DatasetItem item : this.items) {
            itemsCopy.add( item.copy() );
        }

        Dataset copy = new Dataset(underlyingFile, delimiter, header, itemsCopy);
        return copy;
    }

    /**
     * Returns a portion of the Dataset (the items are deep-copied) by index values.
     * @param fromIndex start index (inclusive)
     * @param toIndex end index (exclusive)
     * @return the specified portion of the Dataset
     * @throws IllegalArgumentException if any of the indices are invalid
     */
    public Dataset crop(int fromIndex, int toIndex) {
        if(fromIndex < 0 || toIndex > size() || fromIndex >= toIndex) {
            String error = String.format("Invalid indices for crop operation: fromIndex = %d; toIndex = %d", fromIndex, toIndex);
            throw new IllegalArgumentException(error);
        }

        Dataset copy = this.copy();
        copy.items = copy.items.subList(fromIndex, toIndex);
        return copy;
    }

    /**
     * Returns a new dataset with <code>sampleSize</code> random dataset items (the items are deep-copied).
     * @param sampleSize the number of random items
     * @return a sampled dataset
     */
    public Dataset sample(int sampleSize) {
        Args.requireInRange(sampleSize, "sampleSize", 1, this.size());

        Dataset copy = copy();
        copy.shuffle();
        return copy.crop(0, sampleSize - 1);
    }

    /**
     * Obtains the name defined for this Dataset in its Yaml header.
     * @return the name of the dataset
     */
    public String getName() {
        return header.getName();
    }

    /**
     * Obtains the description defined for this Dataset in its Yaml header.
     * @return the description of the dataset
     */
    public String getDescription() {
        return header.getDescription();
    }
    /**
     * Returns the size of the data set, i.e., total number of items.
     * @return the size of data set
     */
    public int size() {
        return this.items.size();
    }

    /**
     * Returns the dimensionality of the input vectors.
     * @return the dimensionality of the input vectors
     */
    public int inputDimensionality() {
        return this.items.get(0).getInput().dimensions();
    }

    /**
     * Returns the dimensionality of the target output vectors.
     * @return the dimensionality of the target output vectors
     */
    public int outputDimensionality() {
        return this.items.get(0).getTargetOutput().dimensions();
    }

    /**
     * Returns the names of the components of the inputs, obtained from the yaml header.
     * @return the names of the components of the inputs
     */
    public String[] inputVariableNames() {
        return header.getVariables()
                .subList(0, header.getNumberInputs())
                .toArray(new String[]{});
    }

    /**
     * Returns the names of the components of the target outputs, obtained from the yaml header.
     * @return the names of the components of the target outputs
     */
    public String[] outputVariableNames() {
        return header.getVariables()
                .subList(header.getNumberInputs(), header.getVariables().size())
                .toArray(new String[]{});
    }

    /**
     * Obtains the <b>reference</b> of the {@link DatasetItem} at <code>index</code>.
     * @param index the internal index of the item, in [0, {@link #size()} - 1].
     * @return the dataset item.
     * @throws IndexOutOfBoundsException if the index is not valid.
     */
    public DatasetItem get(int index) {
        if(index < 0 || index >= size()) {
            String error = String.format("Invalid index (%d). Must be in [0, %d].", index, size()-1);
            throw new IndexOutOfBoundsException(error);
        }
        return items.get(index);
    }

    /**
     * Shuffles the data set items using the default random generator.
     * <br/>
     * After this operation {@link #iterator()} will follow a new ordering.
     */
    public void shuffle() {
        Collections.shuffle(items);
    }

    /**
     * Shuffles the data set items using java random generator with specified seed.
     * <br/>
     * After this operation {@link #iterator()} will follow a new ordering.
     * @param seed a seed number to initialize random generator
     */
    public void shuffle(int seed) {
        Collections.shuffle(items, new Random(seed));
    }

    @Override
    public Iterator<DatasetItem> iterator() {
        return items.iterator();
    }

    private void parse() throws IOException, InvalidDatasetFormatException {

        this.header = YamlDatasetHeader.fromParse(this.underlyingFile); //throws InvalidDatasetFormatException

        // We should have a valid dataset here.

        int discardLines = header.getParsedLineEnd();
        int totalColumns = header.getNumberInputs() + header.getNumberOutputs();

        BufferedReader reader = new BufferedReader(new FileReader(this.underlyingFile));
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;

            if(lineNumber <= discardLines) continue;

            if(line.trim().isEmpty()) continue;

            // Process data row
            String[] data = line.split(this.delimiter);

            if(data.length != totalColumns) {
                String msg = String.format("Column mismatch in data set (expected: %d, actual: %d) -> line %d", totalColumns, data.length, lineNumber);
                throw new InvalidDatasetFormatException(msg);
            }


            double[] doubleValues = Arrays.stream(data)
                    .mapToDouble(Double::parseDouble)
                    .toArray();

            items.add(new DatasetItem(doubleValues, header.getNumberInputs(), header.getNumberOutputs()));
        }

        // Makes no sense to have an empty data set, without items.
        // We do not allow adding items, at least not for this type of instance

        if(items.isEmpty()) {
            String msg = String.format("The data set cannot be empty.");
            throw new InvalidDatasetFormatException(msg);
        }
    }


    @Override
    public String toString() {
        final NumberFormat numberFormat = new DecimalFormat("###,###.###");
        StringBuilder sb = new StringBuilder();

        sb.append(header.toString());

        items.stream().limit(ITEM_TOSTRING_LIMIT).forEach(e -> {
            sb.append(e).append("\n");
        });

        String formattedSize = String.format("(Total %s)", numberFormat.format(size()));
        if(size() > ITEM_TOSTRING_LIMIT) {
            sb.append(String.format("...\n> Limit of %d reached. Omitting remaining items %s\n",
                    ITEM_TOSTRING_LIMIT, formattedSize));
        } else {
            sb.append(String.format("> %s \n", formattedSize));
        }

        return sb.toString();
    }

}
