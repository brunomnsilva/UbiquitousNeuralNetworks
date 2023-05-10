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

package com.brunomnsilva.neuralnetworks.view;

/**
 * A simple 2D point with x and y coordinates.
 *
 * @author brunomnsilva
 */
public class Point2D {

    /**
     * The x-coordinate of the point.
     */
    public double x;

    /**
     * The y-coordinate of the point.
     */
    public double y;

    /**
     * Constructs a new point with the given x and y coordinates.
     *
     * @param x the x-coordinate of the point.
     * @param y the y-coordinate of the point.
     */
    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the point.
     *
     * @return the x-coordinate of the point.
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the point.
     *
     * @param x the new x-coordinate of the point.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the point.
     *
     * @return the y-coordinate of the point.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the point.
     *
     * @param y the new y-coordinate of the point.
     */
    public void setY(double y) {
        this.y = y;
    }
}

