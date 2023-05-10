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

        // We have to first draw the synapses and then the neurons
        float diameter = DIAMETER;
        final int padding = PADDING;
        int numberLayers = network.getHiddenLayerCount() + 2; // + input and output

        int xSpacing = (getWidth() - padding*2) / (numberLayers - 1);

        // Two-pass algorithm:
        // - 1) Compute neuron's positioning on screen (and store them)
        // - 2 & 3) Draw synapses and neurons
        Map<Neuron, NeuronPosition> neuronPositionMap = new HashMap<>();

        // 1)
        List<NeuronLayer> orderedLayers = network.getOrderedLayers();

        int drawHeight = getHeight() - padding*2;
        int maxNeuronsInLayer = findMaxNumberNeuronsAcrossLayers(orderedLayers);

        // Adjust diameter if height is not enough for complete layer, using the default value
        if (maxNeuronsInLayer * diameter > drawHeight) {
            diameter = (float)drawHeight / maxNeuronsInLayer;
        }

        int x = padding;
        for (NeuronLayer layer : orderedLayers) {

            int y = padding;

            Neuron[] neurons = layer.getMembers();
            int numberNeurons = neurons.length;

            // When a layer has 1 or 2 neurons, the general even spacing does not look pretty. E.g.,
            // the single layer neuron will be at the top of the screen. Therefore, we treat these two situations
            // differently. If the layer only has one neuron, it will be positioned at the center; if there are two
            // there are going to be centered with some spacing. A "general" algorithm could be handy.
            if(numberNeurons == 1) {
                y = (drawHeight + padding*2) / 2;
                neuronPositionMap.put(neurons[0], new NeuronPosition(neurons[0], x, y));
            } else if(numberNeurons == 2) {
                y = (drawHeight + padding*2) / 4;
                neuronPositionMap.put(neurons[0], new NeuronPosition(neurons[0], x, y));
                y *= 3;
                neuronPositionMap.put(neurons[1], new NeuronPosition(neurons[1], x, y));
            } else {
                float ySpacing =  drawHeight / (numberNeurons - 1);
                for (Neuron neuron : neurons) {
                    neuronPositionMap.put(neuron, new NeuronPosition(neuron, x, y));
                    y += ySpacing;
                }
            }

            x += xSpacing;
        }

        // 2)
        double[] minMaxWeights = findMinMaxWeights();
        colorScalePanel.setScale(minMaxWeights[0], minMaxWeights[1]);

        HashMap<Neuron, Synapse[]> synapsesFrom = network.getSynapsesFrom(); // "outgoing" synapses
        for (Neuron neuron : neuronPositionMap.keySet()) {

            NeuronPosition neuronPosition = neuronPositionMap.get(neuron);
            Synapse[] outSynapses = synapsesFrom.get(neuron);

            if(outSynapses != null) {
                for (Synapse s : outSynapses) {
                    Neuron sink = (Neuron)s.getSink();
                    NeuronPosition sinkPosition = neuronPositionMap.get(sink);

                    double strength = s.getStrength();
                    Color strengthColor = colorScalePanel.valueToColor(strength);
                    g2.setPaint(strengthColor);

                    g2.drawLine((int)neuronPosition.x, (int)neuronPosition.y, (int)sinkPosition.x, (int)sinkPosition.y);
                }
            }
        }

        // 3)
        g2.setFont(LookAndFeel.fontTextSmall);
        FontMetrics fm = g2.getFontMetrics();
        int biasTextWidth = fm.stringWidth("X.XX");
        for (Neuron neuron : neuronPositionMap.keySet()) {
            NeuronPosition neuronPosition = neuronPositionMap.get(neuron);
            // Draw neuron
            Ellipse2D.Float circle = new Ellipse2D.Float(neuronPosition.x - diameter/2, neuronPosition.y - diameter/2,
                    diameter, diameter);
            g2.setPaint(neuronColor);
            g2.fill(circle);
            g2.setPaint(Color.BLACK);
            g2.draw(circle);

            if(showBias && (!(neuron instanceof InputNeuron))) {
                g2.setPaint(neuronTextColor);
                g2.drawString(String.format("%.2f", neuron.getBiasValue()),
                        neuronPosition.x - biasTextWidth/2,
                        neuronPosition.y);
            }

            // Draw input/output arrows
            int arrowLength = (int)Math.max(diameter, ARROW_LENGTH);
            g2.setPaint(neuronArrowColor);
            if(neuron instanceof InputNeuron) {
                drawArrow(g2, (int)(neuronPosition.x - arrowLength),
                        (int)neuronPosition.y,
                        (int)(neuronPosition.x - diameter/2),
                        (int)neuronPosition.y);
            } else if(neuron instanceof OutputNeuron) {
                drawArrow(g2, (int)(neuronPosition.x + diameter/2),
                        (int)neuronPosition.y,
                        (int)(neuronPosition.x + arrowLength),
                        (int)neuronPosition.y);
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
