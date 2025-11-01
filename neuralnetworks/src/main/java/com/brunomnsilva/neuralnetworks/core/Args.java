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

package com.brunomnsilva.neuralnetworks.core;

/**
 * The Args class provides a collection of static methods for checking method parameters and throwing
 * IllegalArgumentExceptions if the parameters do not meet the specified requirements.
 *
 * @author brunomnsilva
 */
public final class Args {

    /**
     * Checks if the specified parameter is null and throws an IllegalArgumentException if it is.
     *
     * @param param the parameter to check for null
     * @param name the name of the parameter being checked
     * @throws IllegalArgumentException if the specified parameter is null
     */
    public static void nullNotPermitted(Object param, String name) {
        if (param == null) {
            throw new IllegalArgumentException("Null '" + name + "' argument.");
        }
    }

    /**
     * Checks if two specified integer values are equal and throws an IllegalArgumentException if they are not.
     *
     * @param value1 the first integer value to check
     * @param name1 the name of the first integer value being checked
     * @param value2 the second integer value to check
     * @param name2 the name of the second integer value being checked
     * @throws IllegalArgumentException if the two specified integer values are not equal
     */
    public static void requireEqual(int value1, String name1, int value2, String name2) {
        if(value1 != value2) {
            throw new IllegalArgumentException(String.format("Require %d and %d to be equal.",
                    name1, name2));
        }
    }

    /**
     * Checks if a specified integer value is greater than or equal to a specified minimum value and throws an
     * IllegalArgumentException if it is not.
     *
     * @param value the integer value to check
     * @param name the name of the integer value being checked
     * @param minValue the minimum value that the specified integer value must be greater than or equal to
     * @throws IllegalArgumentException if the specified integer value is less than the specified minimum value
     */
    public static void requireGreaterEqualThan(int value, String name, int minValue) {
        if(value < minValue) {
            throw new IllegalArgumentException(String.format("%s (%d) must be equal or greater than %d",
                    name, value, minValue));
        }
    }

    /**
     * Checks if a specified double value is greater than a specified minimum value and throws an IllegalArgumentException
     * if it is not. Uses a scaled comparison to avoid floating-point precision errors.
     *
     * @param value the double value to check
     * @param name the name of the double value being checked
     * @param minValue the minimum value that the specified double value must be greater than
     * @throws IllegalArgumentException if the specified double value is less than or equal to the specified minimum value
     */
    public static void requireGreaterThan(double value, String name, double minValue) {
        // Floating-point comparison is always dangerous
        long scaledValue = Math.round(value * 10000000);
        long scaledMinValue = Math.round(minValue * 10000000);
        if(scaledValue <= scaledMinValue) {
            throw new IllegalArgumentException(String.format("%s (%f) must be greater than %f",
                    name, value, minValue));
        }
    }

    /**
     * Checks if a specified integer value is non-negative and throws an IllegalArgumentException if it is not.
     *
     * @param value the integer value to check
     * @param name the name of the integer value being checked
     * @throws IllegalArgumentException if the specified integer value is negative
     */
    public static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be non-negative.");
        }
    }

    /**
     * Checks if a specified double value is non-negative and throws an IllegalArgumentException if it is not.
     *
     * @param value the double value to check
     * @param name the name of the double value being checked
     * @throws IllegalArgumentException if the specified double value is negative
     */
    public static void requireNonNegative(double value, String name) {
        if (value < 0.0) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be non-negative.");
        }
    }

    /**
     * This method checks if an integer value falls within a specified range.
     * If the value is less than the lower bound or greater than the upper bound, an IllegalArgumentException is thrown.
     * @param value the integer value to check
     * @param name the name of the value being checked
     * @param lowerBound the lower bound of the range (inclusive)
     * @param upperBound the upper bound of the range (inclusive)
     * @throws IllegalArgumentException if the value is outside the specified range
     */
    public static void requireInRange(int value, String name, int lowerBound, int upperBound) {
        if (value < lowerBound || value > upperBound) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be in the range " + lowerBound + " to " + upperBound);
        }
    }

    /**
     * Checks if the given double value is finite (i.e., not NaN or infinite).
     * @param value the double value to check for finiteness
     * @param name the name of the value being checked
     * @throws IllegalArgumentException if the value is not finite
     */
    public static void requireFinite(double value, String name) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Require '" + name + "' (" + value + ") to be finite.");
        }
    }
}
