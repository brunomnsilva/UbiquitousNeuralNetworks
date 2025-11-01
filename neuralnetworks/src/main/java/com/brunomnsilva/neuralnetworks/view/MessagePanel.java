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

import javax.swing.*;
import java.awt.*;

/**
 * A panel that just displays a message.
 *
 * @author brunomnsilva
 */
public class MessagePanel extends JPanel {

    /**
     * Default constructor.
     * @param message the message to be displayed
     */
    public MessagePanel(String message) {

        setPreferredSize(new Dimension(400,500));
        setLayout(new BorderLayout());

        setOpaque(true);
        setBackground(LookAndFeel.colorBackground);

        JLabel label = new JLabel(message, JLabel.CENTER);
        label.setFont(LookAndFeel.fontTitle);
        label.setForeground(LookAndFeel.colorFontTitle);

        add(label, BorderLayout.CENTER);
    }
}
