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

package com.brunomnsilva.neuralnetworks.models.som;

import com.brunomnsilva.neuralnetworks.core.Args;

/**
 * Provides different decay functions for some parameter.
 *
 * @author brunomnsilva
 */
public class DecayFunction {

    /**
     * Provides the decreasing exponential function decay.
     * @param pi initial value
     * @param pf final value
     * @param t current iteration
     * @param T total iterations
     * @return the decay function value for iteration <code>t</code>
     */
    public static double exponential(double pi, double pf, int t, int T) {
        Args.requireNonNegative(t, "t");
        Args.requireNonNegative(T, "T");

        if(t >= T) return pf;

        double exp = (double)t / T;
        return pi * (float)Math.pow((pf/pi), exp);
    }

    /**
     * Provides the decreasing linear function decay.
     * @param pi initial value
     * @param pf final value
     * @param t current iteration
     * @param T total iterations
     * @return the decay function value for iteration <code>t</code>
     */
    public static double linear(double pi, double pf, int t, int T) {
        Args.requireNonNegative(t, "t");
        Args.requireNonNegative(T, "T");

        if(t >= T) return pf;

        return pi + (pf - pi) * t / T;
    }

    /**
     * Provides the decreasing inverse-time function decay.
     *
     * The rate of decay of this function is controlled by the constant <code>C</code>, which
     * must be empirically estimated based on the value of <code>T</code>.
     *
     * Higher values of <code>C</code> yield slower decays.
     *
     * @param pi initial value
     * @param pf final value
     * @param C constant to use
     * @param t current iteration
     * @param T total iterations
     * @return the decay function value for iteration <code>t</code>
     */
    public static double inverseTime(double pi, double pf, double C, int t, int T) {
        Args.requireNonNegative(t, "t");
        Args.requireNonNegative(T, "T");
        Args.requireNonNegative(C, "C");

        double c = T / C;
        return ((pi - pf) / (1 + c * t)) + pf;
    }
}
