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

import com.brunomnsilva.neuralnetworks.core.Args;

import javax.swing.*;
import java.awt.*;

/**
 * A utility class for creating generic windows with different layouts and with an SVG export option.
 *
 * @author brunomnsilva
 */
public class GenericWindowWithSVGExport extends GenericWindow {
    /**
     * Creates a new GenericWindow with a horizontal layout.
     *
     * @param title  the window title
     * @param panels an array of JPanels to add to the window
     * @return a new GenericWindow instance
     */
    public static GenericWindow horizontalLayout(String title, JPanel... panels) {
        return new GenericWindowWithSVGExport(title, 1, 0, panels);
    }

    /**
     * Creates a new GenericWindow with a vertical layout.
     *
     * @param title  the window title
     * @param panels an array of JPanels to add to the window
     * @return a new GenericWindow instance
     */
    public static GenericWindow verticalLayout(String title, JPanel... panels) {
        return new GenericWindowWithSVGExport(title, 0, 1, panels);
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
        return new GenericWindowWithSVGExport(title, layoutRows, layoutCols, panels);
    }

    /**
     * Creates a new GenericWindowWithSVGExport with the specified layout and panels.
     *
     * @param title      the window title
     * @param layoutRows the number of rows in the layout
     * @param layoutCols the number of columns in the layout
     * @param panels     an array of JPanels to add to the window
     */
    private GenericWindowWithSVGExport(String title, int layoutRows, int layoutCols, JPanel... panels) {
        Args.nullNotPermitted(title, "title");
        Args.nullNotPermitted(panels, "panels");
        Args.requireNonNegative(layoutRows, "layoutRows");
        Args.requireNonNegative(layoutCols, "layoutCols");

        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Can be overridden by setter

        JPanel content = new JPanel(new GridLayout(layoutRows, layoutCols));
        content.setBackground(LookAndFeel.colorBackground);

        for (JPanel p : panels) {
            if (p != null)
                content.add(p);
        }

        JPanel svgPanel = new ExportSVGWrapperPanel(content);

        this.setLayout(new BorderLayout());
        this.setBackground(LookAndFeel.colorBackground);
        this.getContentPane().add(svgPanel, BorderLayout.CENTER);


        pack(); // Compute content's preferred sizes and resize window
        setLocationRelativeTo(null); // Center on screen
    }
}
