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

package com.brunomnsilva.neuralnetworks.view.som;

import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

import java.util.Arrays;

/**
 * An implementation of the U-Matrix visualization. It is an exploratory cluster analysis visualization.
 * <br/>
 * The U-Matrix (Unified Distance Matrix) visualization is a popular way of visualizing a Self-Organizing Map (SOM).
 * It is a grayscale representation of the distances between SOM neurons.
 * The U-Matrix shows the distance between adjacent neurons, as well as between neurons that are not adjacent.
 * The U-Matrix is created by calculating the distance between every pair of neurons in the SOM, and then creating a
 * 2D matrix that represents these distances.
 * <br/>
 * The U-Matrix is often used to help visualize the topological properties of the SOM. It allows the user to see which neurons
 * are close to each other in the SOM, and which neurons are far away. Areas of the U-Matrix that are dark correspond to
 * regions of the SOM where neurons are close together, while areas that are light correspond to regions where neurons
 * are far apart. By looking at the U-Matrix, it is possible to identify clusters of neurons that are close together,
 * as well as areas of the SOM where there are discontinuities in the topology.
 * <br/>
 * The U-Matrix can be used to identify patterns in the data that are captured by the SOM.
 * For example, if the SOM is trained on a set of images, the U-Matrix can be used to identify clusters of similar images.
 * Similarly, if the SOM is trained on a set of documents, the U-Matrix can be used to identify clusters of documents
 * that are similar in content.
 * <br/>
 * Overall, the combination of the U-matrix and the component planes can provide a powerful visualization tool to explore
 * the clustering structure of SOMs and to gain insights into the relationship between the input data and the SOM neurons.
 *
 * @see ComponentPlaneVisualizationPanel
 *
 * @author brunomnsilva
 */
public class UMatrixVisualizationPanel extends AbstractVisualizationPanel {

    public enum Mode {MEDIAN, MEAN, MIN, MAX};

    private Mode mode;

    public UMatrixVisualizationPanel(SelfOrganizingMap som, Mode mode) {
        super(som, "U-Matrix", som.getWidth() * 2 - 1, som.getHeight() * 2 - 1);

        this.mode = mode;

        // Add context menu entries
        addContextMenuEntries();
    }

    private void addContextMenuEntries() {
        addContextMenuAction("Minimum distances", e -> {
            setMode(Mode.MIN);
        });
        addContextMenuAction("Mean distances", e -> {
            setMode(Mode.MEAN);
        });
        addContextMenuAction("Median distances", e -> {
            setMode(Mode.MEDIAN);
        });
        addContextMenuAction("Maximum distances", e -> {
            setMode(Mode.MAX);
        });
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        super.update();
    }

    @Override
    protected void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid) {
        double sqrt_2 = Math.sqrt(2);

        int My = som.getHeight();
        int Mx = som.getWidth();
        int Ux = 2*Mx-1;
        int Uy = 2*My-1;

        double[] a;
        double dz1;
        double dz2;

        // U-Matrix computation. I don't quite remember what's the origin
        // of this algorithm, but I suspect it's an adaptation on Matlab's
        // algorithm. TODO: It remains to validate this against different lattices
        for(int j=1; j<=My; j++) {
            for(int i=1; i<=Mx; i++) {
                if(i<Mx) {
                    // Horizontal
                    //uMatrix[2*i-1][2*j-2] = map[i-1][j-1].distance(map[i][j-1]);
                    double v = som.distanceBetweenPrototypes(som.get(i-1, j-1), som.get(i, j-1));
                    grid.set(v, 2*i-1, 2*j-2);
                }
                if(j<My) {
                    // Vertical
                    //uMatrix[2*i-2][2*j-1] = map[i-1][j-1].distance(map[i-1][j]);
                    double v = som.distanceBetweenPrototypes(som.get(i-1, j-1), som.get(i-1, j));
                    grid.set(v, 2*i-2, 2*j-1);
                }
                if(j<My && i<Mx) {
                    // Diagonals
                    dz1 = som.distanceBetweenPrototypes(som.get(i-1, j-1), som.get(i, j));
                    dz2 = som.distanceBetweenPrototypes(som.get(i-1, j), som.get(i, j-1));
                    //uMatrix[2*i-1][2*j-1] = (dz1+dz2)/(2*sqrt_2);
                    grid.set((dz1+dz2)/(2*sqrt_2), 2*i-1, 2*j-1);
                }
            }
        }

        // Values on the units
        for(int j=1; j<=Uy; j+=2) {
            for(int i=1; i<=Ux; i+=2) {
                if(i>1 && j>1 && i<Ux && j<Uy) //middle part of the map
                    a = new double[]{
                            grid.get(i-2,j-1),
                            grid.get(i,j-1),
                            grid.get(i-2, j-1),
                            grid.get(i-1, j) };
                else if(j==1 && i>1 && i<Ux) //upper edge
                    a = new double[]{
                            grid.get(i-2, j-1),
                            grid.get(i, j-1),
                            grid.get(i-1, j) };
                else if(j==Uy && i>1 && i<Ux) //lower edge
                    a = new double[]{
                            grid.get(i-2, j-1),
                            grid.get(i, j-1),
                            grid.get(i-1, j-2) };
                else if(i==1 && j>1 && j<Uy)
                    a = new double[]{
                            grid.get(i, j-1),
                            grid.get(i-1, j-2),
                            grid.get(i-1, j) };
                else if(i==Ux && j>1 && j<Uy)
                    a = new double[]{
                            grid.get(i-2, j-1),
                            grid.get(i-1, j-2),
                            grid.get(i-1, j) };
                else if(i==1 && j==1)
                    a = new double[]{
                            grid.get(i, j-1),
                            grid.get(i-1, j) };
                else if(i==Ux && j==1)
                    a = new double[]{
                            grid.get(i-2, j-1),
                            grid.get(i-1, j) };
                else if(i==1 && j==Uy)
                    a = new double[]{
                            grid.get(i, j-1),
                            grid.get(i-1, j-2) };
                else if(i==Ux && j==Uy)
                    a = new double[]{
                            grid.get(i-2, j-1),
                            grid.get(i-1, j-2) };
                else
                    a = new double[]{0.0};

                //uMatrix[i-1][j-1] = eval(a, 0);
                double v = eval(a, this.mode);
                grid.set(v, i-1, j-1);
            }
        }
    }

    private static double eval(double[] v, Mode mode) {

        int len = v.length;
        if(len == 1) {
            //same value for all modes
            return v[0];
        }
        else if(len==2) {
            if(mode == Mode.MEDIAN || mode == Mode.MEAN) return ( (v[0]+v[1])/2 );
            if(mode == Mode.MIN)    return ( (v[0] < v[1]) ? v[0] : v[1] );
            if(mode == Mode.MAX)    return ( (v[0] > v[1]) ? v[0] : v[1] );
        }
        else if(len == 3) {
            if(mode == Mode.MEAN)   return ((v[0]+v[1]+v[2])/3);

            Arrays.sort(v);         //sort ascending
            if(mode == Mode.MEDIAN) return v[1];
            if(mode == Mode.MIN)    return v[0];
            if(mode == Mode.MAX)    return v[2];
        }
        else if(len==4) {
            if(mode == Mode.MEAN)   return ((v[0]+v[1]+v[2]+v[3])/4);

            Arrays.sort(v);         //sort ascending
            if(mode == Mode.MEDIAN) return ((v[1]+v[2])/2);
            if(mode == Mode.MIN)    return v[0];
            if(mode == Mode.MAX)    return v[3];
        }

        //not expecting more than 4 elements in vector
        return 0.0;
    }

    @Override
    protected String description() {
        return "Depicts distances between prototype's values. Useful to detect clusters.";
    }
}
