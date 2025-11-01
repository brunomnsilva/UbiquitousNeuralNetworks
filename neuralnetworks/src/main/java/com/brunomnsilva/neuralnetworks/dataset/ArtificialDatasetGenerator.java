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

import cern.jet.random.Normal;
import cern.jet.random.Uniform;
import cern.jet.random.engine.DRand;
import cern.jet.random.engine.RandomEngine;
import com.brunomnsilva.neuralnetworks.core.CSVUtils;
import com.brunomnsilva.neuralnetworks.core.VectorN;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static java.lang.StrictMath.*;

public class ArtificialDatasetGenerator {

    /**
     * Generates an artificial uniform distribution in 2d-space.
     * @param outputFilename the resulting filename
     * @param N number of data points to generate
     * @throws IOException if file cannot be created
     */
    public static void uniform(String outputFilename, int N) throws IOException {

        Locale.setDefault(Locale.US); //'.' as decimal separator

        String name = "Uniform Dataset";
        String description = String.format("Dataset with %d points describing an uniform distribution (square) in 2d-space with values in [0,1]. Generated on %s.",
                N, getCurrentDate());
        List<String> variables = new ArrayList<>();
        variables.add("x");
        variables.add("y");
        int inputs = 2;
        int outputs = 0;

        YamlDatasetHeader header = new YamlDatasetHeader(name, description, variables, inputs, outputs);

        FileWriter fw = new FileWriter(outputFilename);

        fw.write(header.exportContents());

        Random r = new Random(12345);
        for(int i=0; i<N; i++) {

            float[] sample = new float[]{r.nextFloat(), r.nextFloat()};

            CSVUtils.appendValuesToFile(fw, sample[0], sample[1]);
        }

        fw.close();
    }

    /**
     * Generates an artificial gaussian distribution in 2d-space.
     * @param outputFilename the resulting filename
     * @param N number of data points to generate
     * @param mean mean of the distribution
     * @param variance variance of the distribution
     * @throws IOException if file cannot be created
     */
    public static void gaussian(String outputFilename, int N, double mean, double variance) throws IOException {

        Locale.setDefault(Locale.US); //'.' as decimal separator

        String name = "2D Gaussian Dataset";
        String description = String.format("A gaussian cloud in 2d-space with %d points in [0,1], with mean = %.2f and variance = %.2f. Generated on %s.",
                N, mean, variance, getCurrentDate());
        List<String> variables = new ArrayList<>();
        variables.add("x");
        variables.add("y");
        int inputs = 2;
        int outputs = 0;

        YamlDatasetHeader header = new YamlDatasetHeader(name, description, variables, inputs, outputs);

        FileWriter fw = new FileWriter(outputFilename);

        fw.write(header.exportContents());

        RandomEngine engine = new DRand((int)System.currentTimeMillis());
        Normal normal = new Normal(mean, variance, engine);

        for(int i=0; i<N; i++) {

            float[] sample = new float[]{(float)normal.nextDouble(), (float)normal.nextDouble()};

            CSVUtils.appendValuesToFile(fw, sample[0], sample[1]);
        }

        fw.close();
    }

    /**
     * Generates an artificial distribution in 3d-space representing a sphere.
     * @param outputFilename the resulting filename
     * @param N number of data points to generate
     * @param radius radius of the sphere
     * @throws IOException if file cannot be created
     */
    public static void sphere(String outputFilename, int N, double radius) throws IOException {

        Locale.setDefault(Locale.US); //'.' as decimal separator

        String name = "Sphere Dataset";
        String description = String.format("Dataset with %d points describing a sphere around (0,0,0) with radius %.2f. Generated on %s.",
                N, radius, getCurrentDate());
        List<String> variables = new ArrayList<>();
        variables.add("x");
        variables.add("y");
        variables.add("z");
        int inputs = 3;
        int outputs = 0;

        YamlDatasetHeader header = new YamlDatasetHeader(name, description, variables, inputs, outputs);

        FileWriter fw = new FileWriter(outputFilename);
        fw.write(header.exportContents());

        Uniform randZ = new Uniform(-radius, radius, 12345);
        Uniform randPhi = new Uniform(0, 2*PI, 54321);

        double x, y, z, theta, phi;
        for(int i=0; i<N; i++) {

            z = randZ.nextDouble();
            phi = randPhi.nextDouble();

            // To find the latitude (theta) of this point, note that z=R*sin(theta),
            // so theta=sin-1(z/R); its longitude is (surprise!) phi.
            // In rectilinear coordinates, x=R*cos(theta)*cos(phi), y=R*cos(theta)*sin(phi),
            // z= R*sin(theta) = (surprise!) z.

            theta = asin( z / radius);
            x = radius * cos(theta)* cos(phi);
            y = radius * cos(theta) * sin(phi);

            CSVUtils.appendValuesToFile(fw, x, y, z);
        }

        fw.close();
    }

    /**
     * Creates a data set from an input colored (file) image.
     * <br/>
     * Please note that the background color of the image must be (pure) white, i.e., (255,255,255).
     * Otherwise, it will be considered as a cluster color.
     * <br/>
     * This algorithm will ensure that N data points are generated. There is no guarantee that the point
     * density of each cluster will be the same. It is a stochastic algorithm.
     *
     * @param outputFilename the resulting filename
     * @param N number of data points to generate
     * @param inputFilename filepath of input image
     * @param minMaxScaling whether data points are normalized into <code>[0,1]</code>
     * @throws IOException
     */
    public static void fromColorImageStochastic(String outputFilename, int N, String inputFilename, boolean minMaxScaling) throws IOException {
        // This algorithm will ensure that N data points are generated. There is no guarantee that the point
        // density of each cluster will be the same. It is a stochastic algorithm.

        final int bgColor = Color.WHITE.getRGB();

        BufferedImage img = ImageIO.read(new File(inputFilename));
        int w = img.getWidth();
        int h = img.getHeight();

        // Must temporarily save all data points, to compute the header info later
        Map<VectorN, Integer> observations = new HashMap<>(N); // Key: Vector, Value: assignedCluster

        Random rand = new Random();

        Map<Integer, Integer> colorToCode = new HashMap<>();
        int clusterCode = 1;

        // We are trying to guarantee that exactly N unique data points are generated.
        // But we cannot be certain that they can be extracted, e.g., a value of N very close to the
        // number of colored pixels in a large image. We shall track the number of 'failed' uniqueness generation
        // and, when these exceed the predefined threshold (a high number), bail-out.
        // This is to ensure that this algorithm doesn't enter an infinite loop.
        final int failThreshold = 500000;
        int failCount = 0;
        while(observations.size() < N) {

            int x = rand.nextInt(w);
            int y = rand.nextInt(h);

            int pixelColor = img.getRGB(x, y);

            // bgColor pixels are invalid for point generation
            if(pixelColor == bgColor) continue;

            Integer assignedClusterCode;

            if(colorToCode.containsKey(pixelColor)) {
                assignedClusterCode = colorToCode.get(pixelColor);
            } else {
                assignedClusterCode = clusterCode++;
                colorToCode.put(pixelColor, assignedClusterCode);
            }

            //create data point, but invert y coordinate (screen to natural coordinates)
            VectorN sample;
            if(minMaxScaling) {
                double xscaled = x / (double)w;
                double yscaled = Math.abs(y - h) / (double)h;
                sample = VectorN.fromValues(xscaled, yscaled);
            } else {
                sample = VectorN.fromValues(x, Math.abs(y - h));
            }

            if(observations.containsKey(sample)) {
                failCount++;
            } else {
                observations.put(sample, assignedClusterCode);
            }

            // Quit after the fail threshold is achieved
            if(failCount >= failThreshold) break;
        }

        // Create dataset header
        String name = String.format("Image Dataset (%s)", inputFilename);
        String description = String.format("Dataset with %d points from %d clusters, created from a colored image. Generated on %s.",
                observations.size(), colorToCode.size(), getCurrentDate());
        List<String> variables = new ArrayList<>();
        variables.add("x");
        variables.add("y");
        variables.add("class");
        int inputs = 2;
        int outputs = 1;

        YamlDatasetHeader header = new YamlDatasetHeader(name, description, variables, inputs, outputs);

        FileWriter fw = new FileWriter(outputFilename);
        fw.write(header.exportContents());

        // Export observations
        for (VectorN v : observations.keySet()) {
            Integer vClass = observations.get(v);

            double[] array = v.values();
            array = Arrays.copyOf(array, array.length + 1);
            array[array.length - 1] = vClass;
            CSVUtils.appendValuesToFile(fw, array);
        }

        fw.close();
    }

    /**
     * Creates a data set from an input colored (file) image.
     * <br/>
     * Please note that the background color of the image must be (pure) white, i.e., (255,255,255).
     * Otherwise, it will be considered as a cluster color.
     * <br/>
     * This algorithm will not ensure that N data points are generated. The point density will be
     * proportional to the size of the clusters in the image.
     *
     * @param outputFilename the resulting filename
     * @param N number of data points to generate
     * @param inputFilename filepath of input image
     * @throws IOException
     */
    public static void fromColorImageWithRelativeDensity(String outputFilename, int N, String inputFilename) throws IOException {
        // This algorithm will ensure that at least N data points are generated. There is no guarantee that the point
        // density of each cluster will be the same. It is a stochastic algorithm.

        final int bgColor = Color.WHITE.getRGB(); // White ignored as a cluster

        BufferedImage img = ImageIO.read(new File(inputFilename));
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        // The algorithm will proceed as follows:
        // 1 - Gather all data points with their colors as cluster members (inside a dictionary)
        // 2 - Each cluster will be sampled according to its relative density (size) in the image.

        int totalColoredPixels = 0;
        Map<Integer, List<VectorN>> colorPixels = new HashMap<>(imgWidth * imgHeight);
        Map<Integer, Integer> colorToCode = new HashMap<>();
        int colorCode = 1;
        for(int x=0; x < imgWidth; ++x) {
            for(int y=0; y < imgHeight; ++y) {

                int pixelColor = img.getRGB(x, y);

                if(pixelColor == bgColor) continue;

                List<VectorN> members = colorPixels.get(pixelColor);
                if(members == null) { // New cluster found
                    members = new ArrayList<>();
                    members.add(VectorN.fromValues(x, y));
                    colorPixels.put(pixelColor, members);
                    colorToCode.put(pixelColor, colorCode++);
                } else {
                    members.add(VectorN.fromValues(x, y));
                }

                totalColoredPixels++;
            }
        }

        if(totalColoredPixels < N) {
            System.err.println("Not enough colored pixels to generate unique N points.");
            // TODO: Check undesirable situations? Nevertheless, uniqueness of data points is not guaranteed
        }

        // Sample each cluster with relative density

        // This would give relative to cluster size:
        //int numberClusters = colorPixels.size();
        //int density = (int)Math.ceil(N / (double)numberClusters);
        int density;

        Random rnd = new Random();
        List<VectorN> observations = new ArrayList<>(N);
        for (Integer color : colorPixels.keySet()) {
            List<VectorN> members = colorPixels.get(color);
            int size = members.size();
            int classCode = colorToCode.get(color);

            double relativeDensityPercentage = size / (double)totalColoredPixels;
            density = (int)Math.ceil(N * relativeDensityPercentage);

            int generated = 0;
            while(generated < density) {
                int rndIndex = rnd.nextInt(size);

                VectorN sample = members.get(rndIndex);

                // Save the point together with its assigned cluster code
                double x = sample.get(0);
                double y = sample.get(1);
                double xscaled = x / (double)imgWidth;
                double yscaled = Math.abs(y - imgHeight) / (double)imgHeight;

                VectorN observation = VectorN.fromValues( xscaled, yscaled, classCode);
                observations.add(observation);

                generated++;
            }
        }

        // Shuffle observations
        Collections.shuffle(observations);


        // Create dataset header
        String name = String.format("Image Dataset (%s)", inputFilename);
        String description = String.format("Dataset with %d points from %d clusters (equal density), created from a colored image. Generated on %s.",
                observations.size(), colorToCode.size(), getCurrentDate());
        List<String> variables = new ArrayList<>();
        variables.add("x");
        variables.add("y");
        variables.add("class");
        int inputs = 2;
        int outputs = 1;

        YamlDatasetHeader header = new YamlDatasetHeader(name, description, variables, inputs, outputs);

        FileWriter fw = new FileWriter(outputFilename);
        fw.write(header.exportContents());

        // Export observations
        for (VectorN v : observations) {
            CSVUtils.appendValuesToFile(fw, v.values());
        }

        fw.close();
    }

    /**
     * Returns the current system date in the format <code>"yyyy/MM/dd"</code>.
     * @return
     */
    private static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return currentDate.format(formatter);
    }

    /**
     * Example usage of artificial generators.
     * @param args (none)
     */
    public static void main(String[] args) {
        try {
            //uniform("datasets/uniform2d.data", 100000);
            gaussian("datasets/gaussian2d.data", 100000, 0, 1);
            //sphere("datasets/sphere.data", 50000, 4);
            //fromColorImageStochastic("datasets/complex_random.data", 100000, "datasets/complex_dataset.png", true);
            //fromColorImageWithRelativeDensity("datasets/complex_density.data", 100000, "datasets/complex_dataset.png");

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
