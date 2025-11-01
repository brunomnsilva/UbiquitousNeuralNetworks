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
 * A console progress bar that displays the progress of a process as it advances towards a maximum value.
 *
 * @author brunomnsilva
 */
public class ConsoleProgressBar {

    private int maxValue;
    private int currentValue;
    private int barWidth;
    private char progressBarChar;

    /**
     * Creates a new ConsoleProgressBar object with the specified maximum value, progress bar width, and character for the progress bar.
     *
     * @param maxValue        the maximum value for the progress bar
     * @param barWidth        the width of the progress bar in characters
     * @param progressBarChar the character used to represent the progress in the progress bar
     */
    public ConsoleProgressBar(int maxValue, int barWidth, char progressBarChar) {
        this.maxValue = maxValue;
        this.barWidth = barWidth;
        this.progressBarChar = progressBarChar;
        this.currentValue = 0;
    }

    /**
     * Creates a new ConsoleProgressBar object with the specified maximum value and progress bar width, using the default progress bar character.
     *
     * @param maxValue the maximum value for the progress bar
     * @param barWidth the width of the progress bar in characters
     */
    public ConsoleProgressBar(int maxValue, int barWidth) {
        this(maxValue, barWidth, '░');
    }

    /**
     * Creates a new ConsoleProgressBar object with the specified maximum value and default progress bar width and character.
     *
     * @param maxValue the maximum value for the progress bar
     */
    public ConsoleProgressBar(int maxValue) {
        this(maxValue, 100);
    }

    /**
     * Updates the progress bar with the specified value.
     *
     * @param value the new value for the progress bar
     */
    public void update(int value) {
        this.currentValue = value;
        double progress = (double) currentValue / (double) maxValue;
        int progressBarLength = (int) (progress * barWidth);

        System.out.print("\r[");
        for (int i = 0; i < barWidth; i++) {
            if (i < progressBarLength) {
                System.out.print(progressBarChar);
            } else {
                System.out.print(" ");
            }
        }
        System.out.printf("] %d%%", (int) (progress * 100));
        if (currentValue == maxValue) {
            System.out.println();
        }
    }
}

