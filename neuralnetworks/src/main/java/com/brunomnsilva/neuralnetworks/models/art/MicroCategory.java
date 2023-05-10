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

package com.brunomnsilva.neuralnetworks.models.art;

import com.brunomnsilva.neuralnetworks.core.VectorN;

/**
 * Represents a micro-category (with a data prototype) in the StreamART2A algorithm.
 * <br/>
 * A micro-category extends the data prototype concept
 * to include a <i>timestamp</i> and a <i>weight</i>, i.e., how many inputs
 * does it represent.
 * <br/>
 * The <code>vigilanceRadius</code> establishes a perceptive field.
 *
 * @see StreamART2A
 *
 * @author brunomnsilva
 */
public class MicroCategory {

    /**
     * The data prototype.
     */
    private final VectorN prototype;

    /**
     * The timestamp, i.e., when was it last modified.
     */
    private long timestamp;

    /**
     * The weight, i.e., how many inputs does it "represent" inside its perceptive field.
     */
    private int weight;

    /**
     * The vigilance radius of the perceptive field.
     */
    private double vigilanceRadius;

    /**
     * Constructs a new MicroCategory object with the given prototype, and default values for timestamp, weight and vigilanceRadius.
     *
     * @param prototype the prototype vector of the MicroCategory.
     */
    public MicroCategory(VectorN prototype) {
        this(prototype, 0, 1, 1);
    }

    /**
     * Constructs a new MicroCategory object with the given prototype, timestamp, and default values for weight and vigilanceRadius.
     *
     * @param prototype the prototype vector of the MicroCategory.
     * @param timestamp the timestamp of the MicroCategory.
     */
    public MicroCategory(VectorN prototype, long timestamp) {
        this(prototype, timestamp, 1, 1);
    }

    /**
     * Constructs a new MicroCategory object with the given prototype, timestamp, weight, and default value for vigilanceRadius.
     *
     * @param prototype the prototype vector of the MicroCategory.
     * @param timestamp the timestamp of the MicroCategory.
     * @param weight the weight of the MicroCategory.
     */
    public MicroCategory(VectorN prototype, long timestamp, int weight) {
        this(prototype, timestamp, weight, 1);
    }

    /**
     * Constructs a new MicroCategory object with the given prototype, timestamp, weight, and vigilanceRadius.
     *
     * @param prototype the prototype vector of the MicroCategory.
     * @param timestamp the timestamp of the MicroCategory.
     * @param weight the weight of the MicroCategory.
     * @param vigilanceRadius the vigilance radius of the MicroCategory.
     */
    private MicroCategory(VectorN prototype, long timestamp, int weight, double vigilanceRadius) {
        this.prototype = prototype;
        this.timestamp = timestamp;
        this.weight = weight;
        this.vigilanceRadius = vigilanceRadius;
    }

    /**
     * Creates and returns a new MicroCategory object that is a copy of this instance.
     *
     * @return a copy of this MicroCategory object.
     */
    public MicroCategory copy() {
        return new MicroCategory(this.prototype.copy(), this.timestamp, this.weight, this.vigilanceRadius);
    }

    /**
     * Returns the prototype of this MicroCategory.
     *
     * @return the prototype of this MicroCategory.
     */
    public VectorN getPrototype() {
        return prototype;
    }

    /**
     * Returns the timestamp of this MicroCategory.
     *
     * @return the timestamp of this MicroCategory.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of this MicroCategory.
     *
     * @param timestamp the new timestamp value to be set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the weight of this MicroCategory.
     *
     * @return the weight of this MicroCategory.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Increments the weight of this MicroCategory by 1.
     */
    public void incrementWeight() {
        weight += 1;
    }

    /**
     * Returns the vigilance radius of this MicroCategory.
     *
     * @return the vigilance radius of this MicroCategory.
     */
    public double getVigilanceRadius() {
        return vigilanceRadius;
    }

    /**
     * Sets the vigilance radius of this MicroCategory.
     *
     * @param vigilanceRadius the new vigilance radius value to be set.
     */
    public void setVigilanceRadius(double vigilanceRadius) {
        this.vigilanceRadius = vigilanceRadius;
    }

    @Override
    public String toString() {
        return "MicroCategory{" +
                "prototype=" + prototype +
                ", timestamp=" + timestamp +
                ", weight=" + weight +
                ", vigilanceRadius=" + vigilanceRadius +
                '}';
    }
}
