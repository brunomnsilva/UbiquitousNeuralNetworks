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
 * Library-wide look and feel configurations.
 *
 * @author brunomnsilva
 */
public class LookAndFeel {
    public static Font fontTextSmall = new Font("SansSerif", Font.PLAIN, 10);
    public static Font fontTextRegular = new Font("SansSerif", Font.PLAIN, 12);
    public static Font fontTextLarge = new Font("SansSerif", Font.PLAIN, 14);
    public static Font fontTitle = new Font("SansSerif", Font.BOLD, 15);
    public static Font fontButton = new Font("SansSerif", Font.BOLD, 10);

    public static Color colorBackground = Color.WHITE;
    public static Color colorFontText = Color.BLACK;    // TODO: use these colors in drawn text and labels
    public static Color colorFontTitle = Color.BLACK;

    public static void setFontTextSmall(Font fontTextSmall) {
        LookAndFeel.fontTextSmall = fontTextSmall;
    }

    public static void setFontTextRegular(Font fontTextRegular) {
        LookAndFeel.fontTextRegular = fontTextRegular;
    }

    public static void setFontTextLarge(Font fontTextLarge) {
        LookAndFeel.fontTextLarge = fontTextLarge;
    }

    public static void setFontTitle(Font fontTitle) {
        LookAndFeel.fontTitle = fontTitle;
    }

    public static void setFontButton(Font fontButton) {
        LookAndFeel.fontButton = fontButton;
    }

    public static void setColorBackground(Color colorBackground) {
        LookAndFeel.colorBackground = colorBackground;
    }

    public static void setColorFontText(Color colorFontText) {
        LookAndFeel.colorFontText = colorFontText;
    }

    public static void setColorFontTitle(Color colorFontTitle) {
        LookAndFeel.colorFontTitle = colorFontTitle;
    }

    static {
        try {
            // Set System L&F
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
