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

package com.brunomnsilva.neuralnetworks.view.mlp;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.Observable;
import com.brunomnsilva.neuralnetworks.core.Observer;
import com.brunomnsilva.neuralnetworks.models.mlp.Backpropagation;
import com.brunomnsilva.neuralnetworks.models.mlp.MLPNetwork;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.colorscale.ColorScaleFactory;
import com.brunomnsilva.neuralnetworks.view.colorscale.ColorScalePanel;

import javax.swing.*;
import java.awt.*;

/**
 * A full visualization of a MLP network. It produces a panel with a {@link MLPNetworkLayersPanel}
 * and a {@link ColorScalePanel} side by side.
 * <br/>
 * When this panel is notified of changes in its {@link Observable}, it draws itself again. This is useful to visualize
 * the evolution of the network parameters during training.
 *
 * @author brunomnsilva
 */
public class MLPNetworkVisualizationPanel extends JPanel implements Observer {

    private final MLPNetwork network;
    private ColorScalePanel colorScalePanel;
    private MLPNetworkLayersPanel layersPanel;

    /**
     * Creates a new MLPNetworkVisualizationPanel
     * @param network the MLP network to visualize
     */
    public MLPNetworkVisualizationPanel(MLPNetwork network) {
        Args.nullNotPermitted(network, "network");

        this.network = network;

        initComponents();
    }

    private void initComponents() {
        JLabel nameLabel = new JLabel("MLP Network", JLabel.CENTER);
        nameLabel.setFont(LookAndFeel.fontTitle);
        nameLabel.setForeground(LookAndFeel.colorFontTitle);

        // Information about the visualization while hovering over a (i) symbol.
        JLabel infoLabel = new JLabel(" \u24d8", JLabel.CENTER);
        infoLabel.setFont(LookAndFeel.fontTitle);
        infoLabel.setForeground(LookAndFeel.colorFontTitle);

        String description = "Depicts the network architecture and synapse weights.";
        infoLabel.setToolTipText(description);

        colorScalePanel = new ColorScalePanel(-1, 1,
                ColorScaleFactory.create(ColorScaleFactory.Scale.ORANGE_BLUE));

        layersPanel = new MLPNetworkLayersPanel(network, colorScalePanel);

        setLayout(new BorderLayout());

        // Put label and info at top
        JPanel hbox = new JPanel(new FlowLayout());
        hbox.setBackground(LookAndFeel.colorBackground);
        hbox.add(nameLabel);
        hbox.add(infoLabel);
        add(hbox, BorderLayout.NORTH);

        Dimension colorScalePanelDim = new Dimension(100, 100);
        colorScalePanel.setMinimumSize(colorScalePanelDim);
        colorScalePanel.setMaximumSize(colorScalePanelDim);
        colorScalePanel.setPreferredSize(colorScalePanelDim);

        add(layersPanel, BorderLayout.CENTER);
        add(colorScalePanel, BorderLayout.EAST);

        this.setOpaque(true);
        this.setBackground(LookAndFeel.colorBackground);

        setPreferredSize(new Dimension(900, 600));
    }

    @Override
    public void onNotify(Observable observable) {
        if(observable instanceof Backpropagation) {
            repaint();
        }
    }
}
