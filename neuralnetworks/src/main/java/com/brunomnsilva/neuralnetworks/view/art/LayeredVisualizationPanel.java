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

package com.brunomnsilva.neuralnetworks.view.art;

import com.brunomnsilva.neuralnetworks.core.Observable;
import com.brunomnsilva.neuralnetworks.core.Observer;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A visualization panel that contains a stack of layered {@link LayerVisualization} instances.
 * <br/>
 * The order in which the layers are drawn is the order of adding them to this panel, e.g., the last added
 * LayerVisualization is the last to be drawn. You can use this knowledge to pick the intended overlapping of information.
 * <br/>
 * When this panel is notified of changes in its {@link Observable}, it draws itself again. Consequently, all included
 * layers are drawn again.
 *
 * @author brunomnsilva
 */
public class LayeredVisualizationPanel extends JPanel implements Observer {

    /** Collection of stacked visualizations. */
    private List<LayerVisualization> layers;

    private PanelWithDelegatedPaint inputSpacePanel;

    /**
     * Creates a new empty LayeredVisualizationPanel.
     */
    public LayeredVisualizationPanel() {
        super(true);
        layers = new ArrayList<>();

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setOpaque(true);
        this.setBackground(com.brunomnsilva.neuralnetworks.view.LookAndFeel.colorBackground);
        setPreferredSize(new Dimension(600, 600));

        // NORTH
        String title = "StreamART2A Codebook";
        JLabel nameLabel = new JLabel(title, JLabel.CENTER);
        nameLabel.setFont(LookAndFeel.fontTitle);
        nameLabel.setForeground(LookAndFeel.colorFontTitle);

        JLabel infoLabel = new JLabel(" \u24d8", JLabel.CENTER);
        infoLabel.setFont(LookAndFeel.fontTitle);
        infoLabel.setForeground(LookAndFeel.colorFontTitle);
        infoLabel.setToolTipText("Visualization of the input space summarization by micro-categories.");

        JPanel hbox = new JPanel(new FlowLayout());
        hbox.setBackground(LookAndFeel.colorBackground);
        hbox.add(nameLabel);
        hbox.add(infoLabel);
        add(hbox, BorderLayout.NORTH);

        // CENTER
        inputSpacePanel = new PanelWithDelegatedPaint();
        add(inputSpacePanel, BorderLayout.CENTER);
    }

    /**
     * Adds a new LayerVisualization to this panel.
     * @param layer the LayerVisualization
     */
    public void add(LayerVisualization layer) {
        if(!layers.contains(layer)) {
            // Add last. The layers will be called to paint from
            // the first added to the last
            layers.add(layers.size(), layer);
        }
    }

    @Override
    public void paint(Graphics g) {
        // Clear canvas
        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        super.paint(g);
    }

    @Override
    public void onNotify(Observable observable) {
        repaint();
    }

    private class PanelWithDelegatedPaint extends JPanel {
        public PanelWithDelegatedPaint() {
            super(true);
        }

        @Override
        public void paint(Graphics g) {
            // Ask layers to draw themselves
            for(LayerVisualization layer : layers) {
                layer.draw((Graphics2D)g, getWidth(), getHeight());
            }
        }
    }
}
