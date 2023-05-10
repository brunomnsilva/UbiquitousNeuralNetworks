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

package com.brunomnsilva.neuralnetworks.view.dataset;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.dataset.Dataset;
import com.brunomnsilva.neuralnetworks.dataset.DatasetItem;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * A simple Dataset visualization panel that shows the data set in tabular format with a scroll bar.
 *
 * @author brunomnsilva
 */
public class DatasetVisualizationPanel extends JPanel {
    private JTable table;
    private Dataset dataset;

    /**
     * Creates a new instance of DatasetVisualizationPanel
     * @param dataset the Dataset to show in tabular format
     */
    public DatasetVisualizationPanel(Dataset dataset) {
        Args.nullNotPermitted(dataset, "dataset");

        this.dataset = dataset;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setPreferredSize(new Dimension(800, 600));

        DefaultTableModel model = createTableModel();

        table = new JTable(model);
        table.getTableHeader().setFont(LookAndFeel.fontTextRegular);
        table.setFont(LookAndFeel.fontTextSmall);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane);
    }

    private DefaultTableModel createTableModel() {
        // Concat header (inputs + target outputs)
        String[] inputNames = dataset.inputVariableNames();
        String[] outputNames = dataset.outputVariableNames();
        String[] header = new String[inputNames.length + outputNames.length];
        System.arraycopy(inputNames, 0, header, 0, inputNames.length);
        System.arraycopy(outputNames, 0, header, inputNames.length, outputNames.length);

        // Create model
        DefaultTableModel model = new DefaultTableModel(header, dataset.size()) {
            // Disable editing of cells
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        int numInputs = dataset.inputDimensionality();
        int numOutputs = dataset.outputDimensionality();
        for(int row=0; row < dataset.size(); ++row) {
            DatasetItem item = dataset.get(row);

            for(int col = 0; col < numInputs; ++col) {
                model.setValueAt( item.getInput().get(col) , row, col);
            }

            for(int col = 0; col < numOutputs; ++col) {
                model.setValueAt( item.getTargetOutput().get(col) , row, col + numInputs);
            }
        }

        return model;
    }
}
