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
import com.brunomnsilva.neuralnetworks.models.mlp.*;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.colorscale.ColorScalePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A visualization of a MLP network depicting the various layers, neurons, synapses and bias values.
 * <br/>
 * This panel requires a ColorScalePanel from which it will obtain the colors to paint the synapses according to their
 * relative strengths. It also adjusts the limits of the colorscale based on the min and max strength values found.
 * <br/>
 * The visualization of bias can be toggled from a context menu.
 *
 * @author brunomnsilva
 */
public class MLPNetworkLayersPanel extends JPanel {

    private static final int MAX_VISIBLE_NEURONS = 32; // adjust as needed

    private static final int PADDING = 50;
    private static final int DIAMETER = 40;

    private static final int ARROW_LENGTH = 40;
    private static final int ARROW_HEAD_SIZE = 8;

    private final ColorScalePanel colorScalePanel;
    private final MLPNetwork network;

    private boolean showBias = false;

    /**
     * Creates a new MLPNetworkLayersPanel.
     * @param network the MLP network to visualize
     * @param colorScalePanel the ColorScalePanel to use
     */
    public MLPNetworkLayersPanel(MLPNetwork network, ColorScalePanel colorScalePanel) {
        super(true);

        Args.nullNotPermitted(network, "network");
        Args.nullNotPermitted(colorScalePanel, "colorScalePanel");

        this.network = network;
        this.colorScalePanel = colorScalePanel;

        setPreferredSize(new Dimension(800, 600));
        setBackground(LookAndFeel.colorBackground);

        initMouseActions();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int padding = PADDING;
        float diameter = DIAMETER;

        List<NeuronLayer> orderedLayers = network.getOrderedLayers();
        int numberLayers = orderedLayers.size();
        int xSpacing = (getWidth() - 2 * padding) / (numberLayers - 1);
        int drawHeight = getHeight() - 2 * padding;

        int maxNeuronsInLayer = findMaxNumberNeuronsAcrossLayers(orderedLayers);

        // Adjust diameter for tallest layer
        if (maxNeuronsInLayer * diameter > drawHeight) {
            diameter = (float) drawHeight / maxNeuronsInLayer;
        }

        Map<Neuron, NeuronPosition> neuronPositionMap = new HashMap<>();
        int x = padding;

        // --- 1) Compute neuron positions ---
        for (NeuronLayer layer : orderedLayers) {
            Neuron[] neurons = layer.getMembers();
            int numberNeurons = neurons.length;

            float ySpacing;
            float yStart;

            // Compress layer if it exceeds MAX_VISIBLE_NEURONS
            if (numberNeurons > MAX_VISIBLE_NEURONS) {
                ySpacing = drawHeight / (MAX_VISIBLE_NEURONS - 1f);
                yStart = padding;
                float scale = (float) MAX_VISIBLE_NEURONS / numberNeurons;

                for (int i = 0; i < numberNeurons; i++) {
                    float y = yStart + i * ySpacing * scale;
                    neuronPositionMap.put(neurons[i], new NeuronPosition(neurons[i], x, y));
                }
            } else if (numberNeurons == 1) {
                float y = padding + drawHeight / 2f;
                neuronPositionMap.put(neurons[0], new NeuronPosition(neurons[0], x, y));
            } else if (numberNeurons == 2) {
                float y1 = padding + drawHeight / 4f;
                float y2 = padding + 3 * drawHeight / 4f;
                neuronPositionMap.put(neurons[0], new NeuronPosition(neurons[0], x, y1));
                neuronPositionMap.put(neurons[1], new NeuronPosition(neurons[1], x, y2));
            } else {
                ySpacing = drawHeight / (numberNeurons - 1f);
                ySpacing = Math.max(ySpacing, diameter + 2);
                float totalLayerHeight = ySpacing * (numberNeurons - 1);
                yStart = padding + (drawHeight - totalLayerHeight) / 2f;

                float y = yStart;
                for (Neuron neuron : neurons) {
                    neuronPositionMap.put(neuron, new NeuronPosition(neuron, x, y));
                    y += ySpacing;
                }
            }
            x += xSpacing;
        }

        // --- 2) Draw synapses ---
        double[] minMaxWeights = findMinMaxWeights();
        colorScalePanel.setScale(minMaxWeights[0], minMaxWeights[1]);
        HashMap<Neuron, Synapse[]> synapsesFrom = network.getSynapsesFrom();

        for (Neuron neuron : neuronPositionMap.keySet()) {
            NeuronPosition neuronPosition = neuronPositionMap.get(neuron);
            Synapse[] outSynapses = synapsesFrom.get(neuron);
            if (outSynapses != null) {
                for (Synapse s : outSynapses) {
                    Neuron sink = (Neuron) s.getSink();
                    NeuronPosition sinkPos = neuronPositionMap.get(sink);
                    if (sinkPos != null) {
                        Color strengthColor = colorScalePanel.valueToColor(s.getStrength());
                        g2.setPaint(strengthColor);
                        g2.drawLine((int) neuronPosition.x, (int) neuronPosition.y,
                                (int) sinkPos.x, (int) sinkPos.y);
                    }
                }
            }
        }

        // --- 3) Draw neurons ---
        g2.setFont(LookAndFeel.fontTextSmall);
        FontMetrics fm = g2.getFontMetrics();
        int biasTextWidth = fm.stringWidth("X.XX");

        for (Neuron neuron : neuronPositionMap.keySet()) {
            NeuronPosition pos = neuronPositionMap.get(neuron);
            Ellipse2D.Float circle = new Ellipse2D.Float(
                    pos.x - diameter / 2, pos.y - diameter / 2, diameter, diameter);
            g2.setPaint(neuronColor);
            g2.fill(circle);
            g2.setPaint(Color.BLACK);
            g2.draw(circle);

            if (showBias && (!(neuron instanceof InputNeuron))) {
                g2.setPaint(neuronTextColor);
                g2.drawString(String.format("%.2f", neuron.getBiasValue()),
                        pos.x - biasTextWidth / 2, pos.y);
            }

            int arrowLength = (int) Math.max(diameter, ARROW_LENGTH);
            g2.setPaint(neuronArrowColor);

            if (neuron instanceof OutputNeuron) {
                drawArrow(g2, (int) (pos.x + diameter / 2), (int) pos.y,
                        (int) (pos.x + arrowLength), (int) pos.y);
            }
        }
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);

        // Since we'll be always drawing arrows horizontally, then we can
        // simplify this

        int arrowSize = ARROW_HEAD_SIZE; // Change this value to adjust the arrowhead size
        int[] arrowX = {x2, x2 - arrowSize, x2 - arrowSize};
        int[] arrowY = {y2, y2 - arrowSize/2, y2 + arrowSize/2};
        g.fillPolygon(arrowX, arrowY, 3);

        /*
        double angle = Math.atan2(y2 - y1, x2 - x1);
        Polygon arrowhead = new Polygon();
        int arrowSize = 8; // Change this value to adjust the arrowhead size
        arrowhead.addPoint((int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6)),
                (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6)));
        arrowhead.addPoint(x2, y2);
        arrowhead.addPoint((int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6)),
                (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6)));
        g.fillPolygon(arrowhead);
        */
    }

    private double[] findMinMaxWeights() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Synapse s : network.getSynapses()) {
            double w = s.getStrength();
            if(w > max) max = w;
            if(w < min) min = w;
        }
        return new double[]{min, max};
    }

    private int findMaxNumberNeuronsAcrossLayers(List<NeuronLayer> layers) {
        int max = layers.get(0).size();
        for(int i=1; i < layers.size(); ++i) {
            int current = layers.get(i).size();
            if(current > max) {
                max = current;
            }
        }
        return max;
    }

    private class NeuronPosition {
        Neuron neuron;
        float x, y;

        public NeuronPosition(Neuron neuron, float x, float y) {
            this.neuron = neuron;
            this.x = x;
            this.y = y;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // MOUSE LISTENER AND POP-UP MENU

    private JPopupMenu contextMenu;

    private void initMouseActions() {
        // Menu lazy initialization
        if(contextMenu == null) {
            contextMenu = new JPopupMenu();
            final JPanel thisPanel = this;
            this.addMouseListener(new MouseClickListener(this));
        }

        // Add menu item
        JMenuItem item = new JMenuItem("Toggle Neuron Bias");
        item.addActionListener(e -> {
            showBias = !showBias;
            repaint();
        });
        contextMenu.add( item );
    }
    private class MouseClickListener extends MouseAdapter {

        private MLPNetworkLayersPanel panel;

        public MouseClickListener(MLPNetworkLayersPanel panel) {
            this.panel = panel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(panel.contextMenu == null) return;

            if(e.getButton() == MouseEvent.BUTTON3) {
                contextMenu.show(panel , e.getX(), e.getY());
            }
        }
    }

    /////////////////////////////////////////////////////////////
    // COLORING

    private static Color neuronColor = Color.GREEN;
    private static Color neuronTextColor = Color.BLACK;
    private static Color neuronArrowColor = Color.BLACK;

    public static void setNeuronColor(Color neuronColor) {
        MLPNetworkLayersPanel.neuronColor = neuronColor;
    }

    public static void setNeuronTextColor(Color neuronTextColor) {
        MLPNetworkLayersPanel.neuronTextColor = neuronTextColor;
    }

    public static void setNeuronArrowColor(Color neuronArrowColor) {
        MLPNetworkLayersPanel.neuronArrowColor = neuronArrowColor;
    }
}
