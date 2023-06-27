package com.brunomnsilva.neuralnetworks.view.som;


import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;

import java.util.Arrays;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Unified Distance Matrix (uMatrix).
 * <br/>
 * This class is one of the main visualization tools for the SOM. It is an exploratory cluster analysis visualization.
 * <br/>
 * The U-Matrix (Unified Distance Matrix) visualization is a popular way of visualizing a Self-Organizing Map (SOM).
 * It is a visual representation of the distances between SOM neurons. Visualizing the U-Matrix is a good way to get
 *  a sense of the topology of the SOM. Here we just calculate and keep the distance calculated
 * between adjacent neurons.
 *
 * <br/>
 *
 *  (refactored by @nmm on 2018-05-28)
 ** */


public class UMatrix {

    public enum Mode {MEDIAN, MEAN, MIN, MAX}

    private double[][] values;
    SelfOrganizingMap som;

    private Mode mode;
    private int Ux;
    private int Uy;
    public UMatrix(SelfOrganizingMap som) {
        int width = som.getWidth();
        int height = som.getHeight();

        Ux = 2 * width - 1;
        Uy = 2 * height - 1;
        this.values = new double[Ux][Uy];
        mode = Mode.MEAN;
        this.som = som;
    }

    public double get(int x, int y) {
        return values[x][y];
    }


    public void set(double value, int x, int y) {
        this.values[x][y] = value;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    private void calcVals(SelfOrganizingMap som, int My, int Mx, int j, int i) {
        double dz1;
        double dz2;
        final double sqrt_2 = Math.sqrt(2);

        if (i < Mx) {
            // Horizontal
            //uMatrix[2*i-1][2*j-2] = map[i-1][j-1].distance(map[i][j-1]);
            set(som.distanceBetweenPrototypes(som.get(i - 1, j - 1),
                            som.get(i, j - 1)),
                    2 * i - 1, 2 * j - 2);

        }
        if (j < My) {
            // Vertical
            //uMatrix[2*i-2][2*j-1] = map[i-1][j-1].distance(map[i-1][j]);
            set(som.distanceBetweenPrototypes(som.get(i - 1, j - 1), som.get(i - 1, j)),
                    2 * i - 2, 2 * j - 1);
        }
        if (j < My && i < Mx) {
            // Diagonals
            dz1 = som.distanceBetweenPrototypes(som.get(i - 1, j - 1), som.get(i, j));
            dz2 = som.distanceBetweenPrototypes(som.get(i - 1, j), som.get(i, j - 1));
            //uMatrix[2*i-1][2*j-1] = (dz1+dz2)/(2*sqrt_2);
            set((dz1 + dz2) / (2 * sqrt_2), 2 * i - 1, 2 * j - 1);
            ///grid.set((dz1+dz2)/(2* sqrt_2), 2* i -1, 2* j -1);
        }

    }

    public void updateValues(SelfOrganizingMap som) {
        int My = som.getHeight();
        int Mx = som.getWidth();


        double[] a;

        // U-Matrix computation. I don't quite remember what's the origin
        // of this algorithm, but I suspect it's an adaptation on Matlab's
        // algorithm. TODO: It remains to validate this against different lattices
        for (int j = 1; j <= My; j++) {
            for (int i = 1; i <= Mx; i++) {
                calcVals(som, My, Mx, j, i);
            }
        }

        // Values on the units
        for (int j = 1; j <= Uy; j += 2) {
            for (int i = 1; i <= Ux; i += 2) {
                if (i > 1 && j > 1 && i < Ux && j < Uy) //middle part of the map
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i, j - 1),
                            get(i - 2, j - 1),
                            get(i - 1, j)};
                else if (j == 1 && i > 1 && i < Ux) //upper edge
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i, j - 1),
                            get(i - 1, j)};
                else if (j == Uy && i > 1 && i < Ux) //lower edge
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i, j - 1),
                            get(i - 1, j - 2)};
                else if (i == 1 && j > 1 && j < Uy)
                    a = new double[]{
                            get(i, j - 1),
                            get(i - 1, j - 2),
                            get(i - 1, j)};
                else if (i == Ux && j > 1 && j < Uy)
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i - 1, j - 2),
                            get(i - 1, j)};
                else if (i == 1 && j == 1)
                    a = new double[]{
                            get(i, j - 1),
                            get(i - 1, j)};
                else if (i == Ux && j == 1)
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i - 1, j)};
                else if (i == 1 && j == Uy)
                    a = new double[]{
                            get(i, j - 1),
                            get(i - 1, j - 2)};
                else if (i == Ux && j == Uy)
                    a = new double[]{
                            get(i - 2, j - 1),
                            get(i - 1, j - 2)};
                else
                    a = new double[]{0.0};

                //uMatrix[i-1][j-1] = eval(a, 0);
                double v = eval(a);
                set(v, i - 1, j - 1);
            }
        }
    }

    private double eval(double[] v) {

        int len = v.length;
        if (len == 1) {
            //same value for all modes
            return v[0];
        } else if (len == 2) {
            if (mode == Mode.MEDIAN || mode == Mode.MEAN)
                return ((v[0] + v[1]) / 2);
            if (mode == Mode.MIN) return ((v[0] < v[1]) ? v[0] : v[1]);
            if (mode == Mode.MAX) return ((v[0] > v[1]) ? v[0] : v[1]);
        } else if (len == 3) {
            if (mode == Mode.MEAN) return ((v[0] + v[1] + v[2]) / 3);

            Arrays.sort(v);         //sort ascending
            if (mode == Mode.MEDIAN) return v[1];
            if (mode == Mode.MIN) return v[0];
            if (mode == Mode.MAX) return v[2];
        } else if (len == 4) {
            if (mode == Mode.MEAN) return ((v[0] + v[1] + v[2] + v[3]) / 4);

            Arrays.sort(v);         //sort ascending
            if (mode == Mode.MEDIAN) return ((v[1] + v[2]) / 2);
            if (mode == Mode.MIN) return v[0];
            if (mode == Mode.MAX) return v[3];
        }

        //not expecting more than 4 elements in vector
        return 0.0;

    }

    public void save(String fileName ) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));

            out.write("# U-Matrix date: " + new Date() + "\n");
            out.write("# xSize: " + Ux + "\n");
            out.write("# ySize: " + Uy + "\n");
            out.write("# mode: " + mode + "\n");
            //out.write("data:\n");
            for (int i = 0; i < Ux; i++) {
                out.write( String.valueOf(get(i, 0)) );
                for (int j = 1; j < Uy; j++)
                    out.write( ", "+get(i, j) );
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error saving U-Matrix to file " + fileName);
        }
    }

}