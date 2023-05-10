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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brunomnsilva.neuralnetworks.examples.som;

import com.brunomnsilva.neuralnetworks.core.*;
import com.brunomnsilva.neuralnetworks.models.som.impl.StreamingSOM;
import com.brunomnsilva.neuralnetworks.models.som.impl.UbiSOM;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.chart.Plot2D;
import com.brunomnsilva.neuralnetworks.view.som.SelfOrganizingMapVisualizationFactory;
import com.brunomnsilva.neuralnetworks.view.som.UMatrixVisualizationPanel;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import com.brunomnsilva.neuralnetworks.view.Point2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author brunomnsilva
 */
public class InteractiveGaussianClouds extends JPanel implements MouseListener, MouseMotionListener,
        MouseWheelListener, KeyListener, Observer {

    public static void main(String[] args) {
        final InteractiveGaussianClouds interaction = new InteractiveGaussianClouds();

        UMatrixVisualizationPanel umat = SelfOrganizingMapVisualizationFactory.createUMatrix(interaction.model);
        //NeuronActivityVisualizationPanel activity = SelfOrganizingMapVisualizationFactory.createNeuronActivity((UbiSOM) interaction.model);

        final DriftPlot driftPlot = new DriftPlot();

        interaction.getSOM().addObserver(umat);
        //interaction.getSOM().addObserver(activity);
        interaction.getSOM().addObserver(driftPlot);

        JFrame window = new JFrame("UbiSOM Interactive Gaussian Clouds");
        window.getContentPane().setLayout(new BorderLayout(5,5));
        JLabel instructionsLabel = new JLabel(instructions, JLabel.CENTER);
        instructionsLabel.setFont(LookAndFeel.fontTextRegular);
        window.getContentPane().add(instructionsLabel, BorderLayout.NORTH);
        window.getContentPane().add(interaction, BorderLayout.CENTER);

        JPanel hbox = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        hbox.add(umat);
        //hbox.add(activity);

        window.getContentPane().add(hbox, BorderLayout.EAST);
        window.getContentPane().add(driftPlot, BorderLayout.SOUTH);

        window.getContentPane().setBackground(LookAndFeel.colorBackground);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                interaction.exit();
            }
        };

        window.addWindowListener(exitListener);
    }

    /** generate sample rate */
    public final int SAMPLE_RATE_SEC = 500;
    /** constant that defines the maximum number of samples to hold for drawing */
    private static final int MAX_DRAW_POINTS = 1000;
    /** max permitted clouds */
    private static final int MAX_CLOUDS_TOTAL = 20;
    /** max initial clouds */
    private static final int MAX_CLOUDS_INIT = 3;
    /** width of window */
    private static final int WIDTH = 800;
    /** height of window */
    private static final int HEIGHT = 500;
    /** Help instructions */
    private static final String instructions = "\u24d8 LClick to drag; "
            + "Ctrl+LClick to delete; Shit+LClick bring forward; "
            + "RClick to add new cloud; Scroll to (in/de)crease variance. F12 to change theme.";

    /** Theme array */
    private final Theme[] themes = new Theme[] {
            new Theme(Color.white, Color.black, Color.red, Color.black, Color.orange, Color.black),
            new Theme(Color.darkGray, Color.white, Color.red, Color.darkGray, Color.blue, Color.white),
            new Theme(Color.darkGray, Color.green, Color.CYAN, Color.white, Color.red, Color.white)
    };
    private int currentThemeIndex = 0;

    /** UbiSOM instance */
    private final StreamingSOM model;

    /** Normalization instance */
    public final DataStreamFeatureRange normalization;

    /** Auxiliary list of generated points for drawing */
    private BoundedQueue<double[]> generatedPoints; // TODO: generate points as VectorN to keep things homogeneous?

    /** List of Cloud elements **/
    private final List<Cloud> cloudCollection;

    /** Worker thread */
    private WorkerThread workerThread;



    /** dragging an element in progress? **/
    private boolean dragging = false;
    /** the element that is being dragged **/
    private Cloud dragElement;
    /** The distance from the upper left corner of the
    dragElement to the point where the user clicked
    the element.  This offset is maintained as the
    element is dragged. **/
    private int offsetX,  offsetY;

    public InteractiveGaussianClouds() {
        
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);
        this.setFocusable(true);
        
        cloudCollection = new LinkedList<>();
        
        // Place random initial clouds
        int max = Math.max( Math.min(MAX_CLOUDS_INIT, MAX_CLOUDS_TOTAL), 0);

        Random r = new Random();
        for(int i=0; i< max; ++i) {
            int x = r.nextInt(WIDTH);
            int y = r.nextInt(HEIGHT);
            
            Cloud c = new Cloud(x, y);
            
            cloudCollection.add(c);
        }
        
        //Create UbiSOM ///////////////////////////////////////////////////////

        int width = 20;
        int height = 40;
        double alpha_0 = 0.1;
        double alpha_f = 0.02;
        double sigma_0 = 0.6;
        double sigma_f = 0.2;
        double beta = 0.8;
        int T = 2000;

        int samplesPerSec = SAMPLE_RATE_SEC;
        
        model = new UbiSOM(width,
                            height,
                            2,
                            alpha_0,
                            alpha_f,
                            sigma_0,
                            sigma_f,
                            beta,
                            T
        );

        normalization = new DataStreamFeatureRange(new double[]{0,0},
                new double[]{InteractiveGaussianClouds.WIDTH, InteractiveGaussianClouds.HEIGHT});

        // Create limited-sized queue
        generatedPoints = new BoundedQueue<>(MAX_DRAW_POINTS);
        
        //Create worker thread and add listener
        workerThread = new WorkerThread(samplesPerSec);
        workerThread.addObserver(this);
        
        Thread t = new Thread(workerThread);
        t.start();
    }
    
    public StreamingSOM getSOM() {
        return model;
    }
    
    private void cycleTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % themes.length;
    }
    
    public Theme getTheme() {
        return themes[currentThemeIndex];
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent evt) {
        // If already dragging, ignore. Defensive programming.
        if (dragging) return;
        
        int mouseX = evt.getX();
        int mouseY = evt.getY();        
        
        if (SwingUtilities.isRightMouseButton(evt)) { // User right-clicked
            // Check if "cloud limit" is reached
            if(isMaximumCloudsReached()) return;

            // Create a new Cloud
            Cloud c = new Cloud(mouseX, mouseY);
            cloudCollection.add(c);

        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            // Further actions require mouse over an element

            // Check if mouse is over any element
            Cloud cloud = findCloudAt(mouseX, mouseY);

            // Bail out if not over any element
            if(cloud == null) return;

            if (evt.isShiftDown()) { // User shift-clicked.
                bringToFront(cloud);
            } else if (evt.isAltDown()) { // User alt-clicked

            } else if (evt.isControlDown()) { // User ctrl-clicked
                // Remove cloud only if more than 1 exists
                if(cloudCollection.size() > 1) {
                    cloudCollection.remove(cloud);
                }
            } else {
                // This is a simple left-press.  Start dragging the
                // element that the user clicked (if any).
                dragElement = findCloudAt(evt.getX(), evt.getY());
                if (dragElement != null) {
                    dragging = true;   // Begin a drag operation.
                    offsetX = evt.getX() - (int)dragElement.getX();
                    offsetY = evt.getY() - (int)dragElement.getY();
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent evt) {
        // Continue the drag operation if one is in progress.
        // Move the element that is being dragged to the current
        // mouse position.  But clamp it so that it can't
        // be more than halfway off the screen.

        if (dragging) {
            dragElement.moveXY(evt.getX() - offsetX, evt.getY() - offsetY);
            
            /* Clamp (x,y) to a permitted range, as described above. */
      
            if (dragElement.getX() < -Cloud.SIZE / 2) {
                dragElement.setX(-Cloud.SIZE / 2);
            } else if (dragElement.getX() + Cloud.SIZE / 2 > getSize().width) {
                dragElement.setX(getWidth() - Cloud.SIZE / 2);
            }
            if (dragElement.getY() < -Cloud.SIZE / 2) {
                dragElement.setY(-Cloud.SIZE / 2);
            } else if (dragElement.getY() + Cloud.SIZE / 2 > getSize().height) {
                dragElement.setY(getHeight() - Cloud.SIZE / 2);
            }
        }
        /* Redraw the canvas */
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        
        Cloud c = findCloudAt(mouseX, mouseY);
        
        if(c != null) {
            int wheelRotation = e.getWheelRotation();
            if(wheelRotation < 0) {
                c.decreaseVariance();
            } else if(wheelRotation > 0) {
                c.increaseVariance();
            }
        }
    }
    
    private boolean isMaximumCloudsReached() {
        return cloudCollection.size() == MAX_CLOUDS_TOTAL;
    }

    private Cloud findCloudAt(int x, int y) {
        for (Cloud cloud : cloudCollection) {
            if (cloud.containsPoint(x, y)) {
                return cloud;
            }
        }
        return null;
    }
    
    private void bringToFront(Cloud cloud) {
        if (cloud != null) {
            cloudCollection.remove(cloud);  // Remove element from current position.
            cloudCollection.add(cloud);     // Put element in the Vector in last position.

            repaint();
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        
        Theme theme = getTheme();
        
        g2.setPaint(theme.background);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Comment for performance enhancement
        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw "fading" sampled points
        float fadeIncrement = 1f / generatedPoints.size();
        float alpha = 0;

        for (double[] point : generatedPoints) {

            Color tc = theme.point;
            Color c = new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), (int)(alpha*255));

            alpha += fadeIncrement;
            if(alpha > 1) alpha = 1;
            
            g2.setPaint(c);
            g2.fillRect((int)point[0], (int)point[1], 2, 2);
            
        }
        
        // Draw UbiSOM model
        draw2DSOM(g2, getWidth(), getHeight());
        
        // Draw cloud centers
        for (Cloud e : cloudCollection) {
            e.paint(g2);
        }

        //print title
        g2.setFont(LookAndFeel.fontButton);
        g2.setPaint(theme.text);
        g2.drawString(String.format("Clouds: current = %d | min = 1 | max = %d", cloudCollection.size(), MAX_CLOUDS_TOTAL), 20, 20);
    }

    private Point2D convertToAreaCoordinates(double x, double y, int scrWidth, int scrHeight) {
        double[] denormalize = normalization.denormalize(new double[]{x,y});
        return new Point2D(denormalize[0],denormalize[1]);
    }
    
    private void draw2DSOM(Graphics2D g, int scrWidth, int scrHeight) {        
        int width = model.getWidth();
        int height = model.getHeight();
        Theme theme = getTheme();
        //accept only first 2 dimensions of prototype vectors
        // Draw SOM lattice
        g.setColor(theme.lattice);
        for(int w=1; w < width; ++w) {
            for (int h = 1; h < height; ++h) {
                VectorN prototypeD = model.get(w, h).getPrototype();
                VectorN prototypeA = model.get(w - 1, h - 1).getPrototype();
                VectorN prototypeB = model.get(w, h - 1).getPrototype();
                VectorN prototypeC = model.get(w - 1, h).getPrototype();

                Point2D d = convertToAreaCoordinates(prototypeD.get(0), prototypeD.get(1), scrWidth, scrHeight);
                Point2D a = convertToAreaCoordinates(prototypeA.get(0), prototypeA.get(1), scrWidth, scrHeight);
                Point2D b = convertToAreaCoordinates(prototypeB.get(0), prototypeB.get(1), scrWidth, scrHeight);
                Point2D c = convertToAreaCoordinates(prototypeC.get(0), prototypeC.get(1), scrWidth, scrHeight);

                g.drawLine((int) a.x, (int) a.y, (int) b.x, (int) b.y);
                g.drawLine((int) a.x, (int) a.y, (int) c.x, (int) c.y);

                // Right and bottom grid lines
                if(w == width-1) {
                    g.drawLine((int) d.x, (int) d.y, (int) b.x, (int) b.y);
                }

                if(h == height-1) {
                    g.drawLine((int) d.x, (int) d.y, (int) c.x, (int) c.y);
                }
            }
        }
    }
    
    @Override
    public void onNotify(Observable o) {
        this.repaint();
    }
    
    public void exit() {
        workerThread.exit = true;
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        // Cycle theme on F12 key press
        int keyCode = evt.getKeyCode();
        if (keyCode == KeyEvent.VK_F12) {
            cycleTheme();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
       
    /**
     * Worker thread that generates samples from existing clouds and feeds
     * them to the UbiSOM model.
     */
    private class WorkerThread extends AbstractObservable implements Runnable {

       private final Random rand;
       public volatile boolean exit = false;
       private final long samplesPerSec;
       
        public WorkerThread(long samplesPerSec) {
            this.samplesPerSec = samplesPerSec;
            this.rand = new Random();
        }
        
        @Override
        public void run() {
            
            while(!exit) {
                // Pick random cloud
                int N = cloudCollection.size();
                int i = rand.nextInt(N);
                // Sample point
                Cloud c = cloudCollection.get(i);
                if(c != null) {
                    double[] nextSample = c.nextSample();

                    generatedPoints.add(nextSample);

                    double[] normalize = normalization.normalize(nextSample);

                    VectorN input = VectorN.fromArray(normalize);
                    model.learn(input);

                    notifyObservers();
                }

                try {
                    Thread.sleep( 1000 / samplesPerSec);
                } catch (Exception e) {
                }
            }
        }
        
    }

    /**
     * Class that represents (and generates) a Gaussian cloud of points.
     */
    private class Cloud {
        /** size of cloud center */
        static final float SIZE = 10;
        /** use a java2d ellipse (circle) as interaction element */
        private final Ellipse2D.Float center;
        /** gaussian distribution generator */
        private MultivariateNormalDistribution gaussian;
        
        private double variance = 70;
        private double[][] coVarianceMatrix;
        
        public Cloud(float x, float y) {
            center = new Ellipse2D.Float(x - SIZE/2, y - SIZE/2, SIZE, SIZE);
            
            coVarianceMatrix = new double[][]{{variance,0},{0,variance}};
            adjustGaussian();
        }
        
        public double[] nextSample() {
            return gaussian.sample();
        }

        public void moveXY(float x, float y) {
            center.x = x;
            center.y = y;

            adjustGaussian();
        }
        
        public void increaseVariance() {
            double newVariance = variance += 10;
            setVariance(newVariance);
        }
        
        private void adjustGaussian() {
            gaussian = new MultivariateNormalDistribution(new double[]{center.x + SIZE/2, center.y + SIZE/2}, coVarianceMatrix);
        }
        
        public void decreaseVariance() {
            double newVariance = variance -= 10;
            setVariance(newVariance);
        }
        
        private double setVariance(double v) {
            variance = (v > 150) ? 150 : (v < 10) ? 10 : v;

            coVarianceMatrix = new double[][]{{variance,0},{0,variance}};
            adjustGaussian();
            
            return v;
        }
        
        public float getX() { return center.x; }
        public float getY() { return center.y; }
        public void setX(float x) { center.x = x;}
        public void setY(float y) { center.y = y; }
        
        public boolean containsPoint(float x, float y) {
            return center.contains(x, y);
        }
        
        public void paint(Graphics2D g) {
            Theme theme = getTheme();
            
            g.setPaint(theme.centroidFill);
            g.fill(center);
            g.setPaint(theme.centroidDraw);
            g.draw(center);
            
            // Draw circle to indicate where gaussian points may lie
            double diameter  = variance;
            double cx = (center.x + SIZE / 2) - diameter/2;
            double cy = (center.y + SIZE / 2) - diameter/2;
            g.setPaint(theme.centroidFill);
            g.drawOval((int)cx, (int)cy, (int)diameter, (int)diameter);
        }
    }

    /**
     * Class that holds a Theme configuration.
     */
    private static class Theme {
        private final Color background;
        private final Color lattice;
        private final Color centroidFill;
        private final Color centroidDraw;
        private final Color point;
        private final Color text;

        public Theme(Color background, Color lattice, Color centroidFill, Color centroidDraw, Color point, Color text) {
            this.background = background;
            this.lattice = lattice;
            this.centroidFill = centroidFill;
            this.centroidDraw = centroidDraw;
            this.point = point;
            this.text = text;
        }
    }

    /**
     * The Drift Function plot panel class.
     */
    private static class DriftPlot extends JPanel implements Observer {
        private Plot2D thePlot;

        public DriftPlot() {
            this.setLayout(new BorderLayout());
            TimeSeries ts = new TimeSeries("Drift Function");

            thePlot = new Plot2D.Builder()
                    //.title("Drift Function")
                    .linePlotFromTimeSeries(ts)
                    .xLabel("Learning Iterations")
                    .yLabel("Drift Function")
                    .withSlidingWindow(4000, true)
                    .width(500)
                    .height(300)
                    .build();

            this.add(thePlot, BorderLayout.CENTER);
        }

        @Override
        public void onNotify(Observable o) {
            if(o instanceof UbiSOM) {
                UbiSOM model = (UbiSOM) o;

                double lastDriftValue = model.getCurrentDriftValue();
                thePlot.append(lastDriftValue);
            }
        }
    }

    private class DataStreamFeatureRange {

        private double[] minValues;
        private double[] maxValues;
        private int d;

        public DataStreamFeatureRange(final double[] minValues, final double[] maxValues) {
            int dmin = minValues.length;
            int dmax = maxValues.length;
            if (dmin != dmax) {
                throw new IllegalArgumentException(String.format("Mismatch between array lengths: %d vs %d", dmin, dmax));
            }
            this.minValues = Arrays.copyOf(minValues, dmin);
            this.maxValues = Arrays.copyOf(maxValues, dmax);
            this.d = dmin;

        }

        public double[] normalize(double[] x) {
            if (x.length != d) {
                throw new IllegalArgumentException(String.format("Mismatch between array lengths: %d vs %d", d, x.length));
            }
            double[] normalized = new double[d];
            for (int i = 0; i < d; i++) {
                double denominator = maxValues[i] - minValues[i];
                normalized[i] = (denominator != 0)
                        ? ((x[i] - minValues[i]) / denominator) : 0;
            }
            return normalized;
        }

        public double[] denormalize(double[] x) {
            if (x.length != d) {
                throw new IllegalArgumentException(String.format("Mismatch between array lenghts: %d vs %d", d, x.length));
            }
            double[] denormalized = new double[d];
            for (int i = 0; i < d; i++) {
                denormalized[i] = minValues[i] + x[i] * (maxValues[i] - minValues[i]);
            }
            return denormalized;
        }

    }

}
