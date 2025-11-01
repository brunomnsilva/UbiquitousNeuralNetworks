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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brunomnsilva.neuralnetworks.view;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A simple SVG wrapper for an existing JPanel.
 * <br/>
 * This panels adds an "Export SVG" button to the bottom of the panel. When clicked it exports
 * the current contents of the panel to an SVG file, whose name is prompted to the user.
 *
 * @author brunomnsilva
 */
public class ExportSVGWrapperPanel extends JPanel {

    private final JPanel content;

    /**
     * Creates a wrapper panel.
     * @param wrapped the panel to wrap
     */
    public ExportSVGWrapperPanel(JPanel wrapped) {
        this.content = wrapped;

        BorderLayout borderLayout = new BorderLayout(0, 0);
        setLayout(borderLayout);
        setBackground(LookAndFeel.colorBackground);
        
        add(wrapped, BorderLayout.CENTER);
        
        JButton export = new JButton("Export SVG");
        export.setFont(LookAndFeel.fontButton);

        JPanel hbox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        hbox.setBackground(LookAndFeel.colorBackground);
        hbox.add(export);
        
        add(hbox, BorderLayout.SOUTH);
        
        export.addActionListener(e -> {

            String filename = JOptionPane.showInputDialog(null,
                    "Input filename (.svg will be appended automatically)",
                    "Export SVG",
                    JOptionPane.QUESTION_MESSAGE);

            if(filename == null || filename.trim().isEmpty()) return;

            try {
                exportSVG(filename+".svg");
                JOptionPane.showMessageDialog(null, "SVG successfully exported.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "A problem occurred. Try again with a different filename.");
            }
        });
                
    }
    
    private void exportSVG(String filename) throws IOException {
        SVGGraphics2D g2 = new SVGGraphics2D(content.getWidth(), content.getHeight());

        content.paint(g2);
        File f = new File(filename);
        SVGUtils.writeToSVG(f, g2.getSVGElement());
    }

}
