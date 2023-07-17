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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.brunomnsilva.neuralnetworks.view;

import com.brunomnsilva.neuralnetworks.core.Args;

import java.awt.*;
import javax.swing.*;

/**
 * A utility class for creating generic windows with different layouts.
 *
 * @author brunomnsilva
 */
public class GenericWindow extends JFrame {

    protected GenericWindow() {}

    /**
     * Creates a new GenericWindow with a horizontal layout.
     *
     * @param title  the window title
     * @param panels an array of JPanels to add to the window
     * @return a new GenericWindow instance
     */
    public static GenericWindow horizontalLayout(String title, JPanel... panels) {
        return new GenericWindow(title, 1, 0, panels);
    }

    /**
     * Creates a new GenericWindow with a vertical layout.
     *
     * @param title  the window title
     * @param panels an array of JPanels to add to the window
     * @return a new GenericWindow instance
     */
    public static GenericWindow verticalLayout(String title, JPanel... panels) {
        return new GenericWindow(title, 0, 1, panels);
    }

    /**
     * Creates a new GenericWindow with a grid layout.
     *
     * @param title      the window title
     * @param layoutRows the number of rows in the grid layout
     * @param layoutCols the number of columns in the grid layout
     * @param panels     an array of JPanels to add to the window
     * @return a new GenericWindow instance
     */
    public static GenericWindow gridLayout(String title, int layoutRows, int layoutCols, JPanel... panels) {
        return new GenericWindow(title, layoutRows, layoutCols, panels);
    }

    /**
     * Creates a new GenericWindow with the specified layout and panels.
     *
     * @param title      the window title
     * @param layoutRows the number of rows in the layout
     * @param layoutCols the number of columns in the layout
     * @param panels     an array of JPanels to add to the window
     */
    private GenericWindow(String title, int layoutRows, int layoutCols, JPanel... panels) {
        Args.nullNotPermitted(title, "title");
        Args.nullNotPermitted(panels, "panels");
        Args.requireNonNegative(layoutRows, "layoutRows");
        Args.requireNonNegative(layoutCols, "layoutCols");

        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Can be overridden by setter

        GridLayout layout = new GridLayout(layoutRows, layoutCols);
        getContentPane().setBackground(LookAndFeel.colorBackground);
        getContentPane().setLayout(layout);

        for (JPanel p : panels) {
            if (p != null)
                getContentPane().add(p);
        }

        pack(); // Compute content's preferred sizes and resize window
        setLocationRelativeTo(null); // Center on screen
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        pack(); // adjust contents to preferred size
    }

    /**
     * Sets the window to exit the application on close and displays a confirmation dialog.
     */
    public void exitOnClose() {
        final JFrame window = this;
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(window,
                        "Are you sure you want to exit the application?", "Exit Application?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

}
