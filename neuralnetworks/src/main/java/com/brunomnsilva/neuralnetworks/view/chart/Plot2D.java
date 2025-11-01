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

package com.brunomnsilva.neuralnetworks.view.chart;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.TimeSeries;
import com.brunomnsilva.neuralnetworks.core.TimeValueTuple;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Utility class to generate 2D plots, using JFreeChart plots.
 *
 * Known problem: sometimes, specially with windowed-sized plots, java.lang.IndexOutOfBoundsException exceptions
 *            are randomly thrown; these are recoverable. I have yet to resolve the issue, but I believe the underlying
 *            implementation (JFreeChart) isn't robust enough to handle a high cadence of adding points to a series,
 *            while having to update the plots.
 *
 *            I think I would have to override the Renderer and use a lock to synchronize the operations of adding points
 *            and repainting.
 *
 * @author brunomnsilva
 */
public class Plot2D extends JPanel  {

    private enum PlotType {UNSET, LINE, SCATTER}

    /**
     * The Plot2D builder.
     * <br/>
     * The default values are:
     * <ul>
     *     <li><code>width = 500</code></li>
     *     <li><code>height = 300</code></li>
     *     <li><code>windowSize = -1 (no moving window)</code></li>
     *     <li><code>yMinMaxKeep = false (only for moving windows)</code></li>
     *     <li><code>xLabel = "x"</code></li>
     *     <li><code>yLabel = "y"</code></li>
     *     <li><code>xFormat = "###,###.###"</code></li>
     *     <li><code>yFormat = "###.###"</code></li>
     *     <li><code>plotTitle = "" (none)</code></li>
     *     <li><code>xMin, xMax, yMin, yMax - unset (automatic)</code></li>
     *     <li><code>legend = false</code></li>
     * </ul>
     */
    public static class Builder {

        private PlotType plotType = PlotType.UNSET;
        private int width = 500; // Default plot width
        private int height = 300; // Default plot height
        private int windowSize = -1; // Default moving window size, -1 signals no moving window
        private boolean yMinMaxKeep = false; // In moving windows, whether to keep min and max y values

        private String xLabel = "x"; // Default x-axis label
        private String yLabel = "y"; // Default y-axis label

        private String xFormat = "###,###.###"; // Default format for x values
        private String yFormat = "###.###"; // Default format for y values

        private Double xMin, xMax, yMin, yMax; // Fixed min and max values for x and y-axis

        private String plotTitle = ""; // Default plot title (none)

        private boolean legend = false; // Whether to include legend of multiple series

        private XYSeriesCollection dataset = new XYSeriesCollection();

        /**
         * Default constructor of builder with default values.
         */
        public Builder() {

        }

        /**
         * Sets the title of the plot.
         * @param title the title of the plot
         * @return the update builder
         */
        public Builder title(String title) {
            Args.nullNotPermitted(title, "name");

            this.plotTitle = title;
            return this;
        }

        /**
         * Sets the width of the plot panel.
         * @param width the width of the plot panel
         * @return
         */
        public Builder width(int width) {
            Args.requireNonNegative(width, "width");
            this.width = width;
            return this;
        }

        /**
         * Sets the height of the plot panel.
         * @param height the height of the plot panel
         * @return the update builder
         */
        public Builder height(int height) {
            Args.requireNonNegative(height, "height");
            this.height = height;
            return this;
        }

        /**
         * Sets the label of the x-axis.
         * @param label the label of the x-axis
         * @return the update builder
         */
        public Builder xLabel(String label) {
            Args.nullNotPermitted(label, "label");
            this.xLabel = label;
            return this;
        }

        /**
         * Sets the label of the y-axis.
         * @param label the label of the y-axis
         * @return the update builder
         */
        public Builder yLabel(String label) {
            Args.nullNotPermitted(label, "label");
            this.yLabel = label;
            return this;
        }

        /**
         * Sets the (hard) limits of the x-axis values.
         * <br/>
         * If not set, the limits are computed automatically from the plot data.
         * @param min the minimum value
         * @param max the maximum value
         * @return the update builder
         */
        public Builder xLimits(double min, double max) {
            this.xMin = min;
            this.xMax = max;
            return this;
        }

        /**
         * Sets the (hard) limits of the y-axis values.
         * <br/>
         * If not set, the limits are computed automatically from the plot data.
         * @param min the minimum value
         * @param max the maximum value
         * @return the update builder
         */
        public Builder yLimits(double min, double max) {
            this.yMin = min;
            this.yMax = max;
            return this;
        }

        /**
         * Turns on the legends for the series of the plot
         * @param flag whether to turn on legends
         * @return the update builder
         */
        public Builder legend(boolean flag) {
            this.legend = flag;
            return this;
        }

        /**
         * Configures the plot to use a moving window over data.
         * <br/>
         * Please note that this is only valid when the plot has a single data series
         * and new samples are added through the {@link Plot2D#append(double)} or
         * {@link Plot2D#append(double, double)} methods.
         * @param size the size of the moving window
         * @param yMinMaxKeep whether to keep min and max values for incoming samples
         * @return the update builder
         */
        public Builder withSlidingWindow(int size, boolean yMinMaxKeep) {
            Args.requireNonNegative(size, "size");
            this.windowSize = size;
            this.yMinMaxKeep = yMinMaxKeep;
            return this;
        }

        /**
         * Adds a new line plot series to the plot.
         * The domain (x) values start at 1 and are incremented for each value in <code>values</code>.
         * @param name the name of the series
         * @param values the y values of the series
         * @return the update builder
         */
        public Builder linePlotFromArray(String name, double[] values) {
            Args.nullNotPermitted(name, "name");
            Args.nullNotPermitted(values, "values");

            // If UNSET, it must be of the same type as set
            if(plotType != PlotType.UNSET && plotType != PlotType.LINE) {
                throw new IllegalStateException("Cannot change type of plot. Current = " + plotType);
            }

            XYSeries series = Plot2D.seriesFromDoubleArray(name, values);
            this.dataset.addSeries(series);
            this.plotType = PlotType.LINE;

            return this;
        }

        /**
         * Adds a new line plot series to the plot from a TimeSeries instance.
         * Domain (x) values are the <i>time</i> values
         * Range (y) values are the <i>value</i> values
         * @param timeSeries the TimeSeries
         * @return the update builder
         */
        public Builder linePlotFromTimeSeries(TimeSeries timeSeries) {
            Args.nullNotPermitted(timeSeries, "timeSeries");

            // If UNSET, it must be of the same type as set
            if(plotType != PlotType.UNSET && plotType != PlotType.LINE) {
                throw new IllegalStateException("Cannot change type of plot. Current = " + plotType);
            }

            XYSeries series = Plot2D.seriesFromTimeseries(timeSeries);
            this.dataset.addSeries(series);
            this.plotType = PlotType.LINE;

            return this;
        }

        /**
         * Adds a new line plot series to the plot.
         * This version receives the x and y values in the <code>data</code> array. The array is expected to have the
         * form <code>data[nSamples][2]</code> with x and y values in the latter dimension.
         * @param name the name of the series
         * @param data the data for the series
         * @return the update builder
         */
        public Builder scatterPlotFrom2dArray(String name, double[][] data) {
            Args.nullNotPermitted(name, "name");
            Args.nullNotPermitted(data, "data");
            Args.requireEqual(data[0].length, "data[0].length", 2, "xy data");

            // If UNSET, it must be of the same type as set
            if(plotType != PlotType.UNSET && plotType != PlotType.SCATTER) {
                throw new IllegalStateException("Cannot change type of plot. Current = " + plotType);
            }

            XYSeries series = Plot2D.seriesFrom2dArray(name, data);
            this.dataset.addSeries(series);
            this.plotType = PlotType.SCATTER;

            return this;
        }

        /**
         * Creates a new Plot2D instance from the builder parameterization.
         * @return a new Plot2D instance
         * @throws IllegalStateException if no series were added
         */
        public Plot2D build() {
            if(plotType == PlotType.UNSET) {
                throw new IllegalStateException("No data added to plot.");
            }

            Plot2D plot = new Plot2D(plotTitle, dataset, xLabel, xFormat, xMin, xMax,
                    yLabel, yFormat, yMin, yMax,
                    windowSize, yMinMaxKeep, plotType, legend, width, height);
            return plot;
        }
    }

    private final XYSeriesCollection dataset;
    private final int windowSize;

    private boolean yMinMaxKeep;
    private double fixMin = Double.MAX_VALUE;
    private double fixMax = Double.MIN_VALUE;

    private final NumberAxis xAxis;
    private final NumberAxis yAxis;

    private final String title;

    private Plot2D(String title, XYSeriesCollection dataset, String xLabel, String xFormat, Double xMin, Double xMax,
                   String yLabel, String yFormat, Double yMin, Double yMax,
                   int windowSize, boolean yMinMaxKeep, PlotType plotType, boolean legend, int width, int height) {

        this.setLayout(new BorderLayout());

        this.title = title;
        this.dataset = dataset;
        this.windowSize = windowSize;
        this.yMinMaxKeep = yMinMaxKeep;

        // TODO: (refactored) fix this, although it ain't causing problems yet
        //if(series.getItemCount() > 1) {
        //    fixMin = series.getMinY();
        //    fixMax = series.getMaxY();
        //}

        DecimalFormat xF = new DecimalFormat(xFormat);
        DecimalFormat yF = new DecimalFormat(yFormat);

        this.xAxis = new NumberAxis.Builder(xLabel)
                .withFormat(xF)
                .withLimits(xMin, xMax)
                .build();

        this.yAxis = new NumberAxis.Builder(yLabel)
                .withFormat(yF)
                .withLimits(yMin, yMax)
                .build();

        boolean lines = plotType == PlotType.LINE;

        XYItemRenderer renderer = new XYLineAndShapeRenderer(lines, !lines);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

        // Use first default colors with transparency when scatter plotting multiple series
        if(plotType == PlotType.SCATTER && dataset.getSeriesCount() > 1) {
            Color[] defaultColors = new Color[]{
                new Color(255, 0, 0, 128),
                new Color(0,  0, 255, 128),
                new Color(0, 255, 0, 128),
                new Color(0, 255, 255, 128),
                new Color(255, 255, 0, 128),
            };

            int seriesCount = dataset.getSeriesCount();
            for(int i=0; i < seriesCount; i++) {
                Color color = defaultColors[i];
                renderer.setSeriesPaint(i, color);
            }
        }

        StandardChartTheme currentTheme = new StandardChartTheme("JFree");
        currentTheme.setChartBackgroundPaint(Color.WHITE);
        currentTheme.setRegularFont(LookAndFeel.fontTextRegular);
        currentTheme.setSmallFont(LookAndFeel.fontTextSmall);
        currentTheme.setLargeFont(LookAndFeel.fontTextLarge);
        currentTheme.setExtraLargeFont(LookAndFeel.fontTitle);

        JFreeChart chart = new JFreeChart(title, LookAndFeel.fontTitle, plot, legend);
        currentTheme.apply(chart);

        // Must do this after applying the theme, or it'll be overridden
        plot.getDomainAxis().setTickLabelFont(LookAndFeel.fontTextSmall);
        plot.getRangeAxis().setTickLabelFont(LookAndFeel.fontTextSmall);

        chart.setTextAntiAlias(true);

        /* Wrapper for user-interactivity */
        ChartPanel thePanel = new ChartPanel(chart, true, true, true, true, true);
        thePanel.setMouseWheelEnabled(true);
        thePanel.setMouseZoomable(true);
        thePanel.setRangeZoomable(true);

        this.setPreferredSize(new Dimension(width, height));

        this.add(thePanel, BorderLayout.CENTER);
    }

    public String getTitle() {
        return title;
    }

    /**
     *
     * @param y
     */
    public void append(double y) {
        if(this.dataset.getSeriesCount() > 1) {
            throw new IllegalStateException("Cannot call this operation with multiple series on plot.");
        }
        XYSeries series = this.dataset.getSeries(0);
        double nextX = series.getMaxX();

        if(Double.isNaN(nextX))  {
            nextX = 0; //the series might be initially empty
        }

        append(nextX + 1 , y);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void append(double x, double y) {
        if(this.dataset.getSeriesCount() > 1) {
            throw new IllegalStateException("Cannot call this operation with multiple series on plot.");
        }
        XYSeries series = this.dataset.getSeries(0);

        if(yMinMaxKeep) {
            if(y < fixMin) { fixMin = y; }
            if(y > fixMax) { fixMax = y; }
        }

        if(windowSize > 0 && x > windowSize) {
            //discard series items fifo-style, since they will
            //not be displayed when using a sliding window
            series.setMaximumItemCount(windowSize - 1);

            this.xAxis.setRange(x - windowSize, x);

            if(yMinMaxKeep) {
                this.yAxis.setRange(fixMin, fixMax);
            }
        }

        series.add(x, y, true); // this will trigger a plot update internally
    }

    private static XYSeries seriesFromDoubleArray(String name, double[] arr) {
        XYSeries series = new XYSeries(name);
        int index = 1;
        for (double v : arr) {
            series.add(index++, v);
        }
        return series;
    }

    private static XYSeries seriesFromDoubleArray(String name, Double[] arr) {
        XYSeries series = new XYSeries(name);
        int index = 1;
        for (double v : arr) {
            series.add(index++, v);
        }
        return series;
    }

    private static XYSeries seriesFrom2dArray(String name, double[][] array) {
        Args.requireEqual(array[0].length, "array[0].length", 2 , "2");

        XYSeries series = new XYSeries(name);
        for(int i=0; i < array.length; ++i) {
            series.add( array[i][0], array[i][1] );
        }
        return series;
    }

    private static XYSeries seriesFromTimeseries(TimeSeries ts) {
        XYSeries series = new XYSeries(ts.getName());

        for (TimeValueTuple tv : ts) {
            series.add(tv.getTime(), tv.getValue());
        }
        return series;
    }
}
