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

package com.brunomnsilva.neuralnetworks.view.art;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.models.art.MicroCategory;
import com.brunomnsilva.neuralnetworks.models.art.StreamART2A;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.Point2D;

import java.awt.*;

/**
 * An implementation of a visualization of StreamART2A instance's codebook.
 *
 * @author brunomnsilva
 */
public class CodebookVisualizationLayer implements LayerVisualization {

    private static final int PT_SIZE = 4;

    private StreamART2A streamART;

    /**
     * Default constructor.
     * @param streamART the StreamART2A instance to visualize
     */
    public CodebookVisualizationLayer(StreamART2A streamART) {
        Args.nullNotPermitted(streamART, "streamART");

        this.streamART = streamART;
    }

    @Override
    public void draw(Graphics2D g, int width, int height) {

        for (MicroCategory category : streamART.getCodebook()) {
            // Get first two features from the prototype
            double x = category.getPrototype().get(0);
            double y = category.getPrototype().get(1);

            // Create scaled point to area size; invert y
            Point2D p = new Point2D( x * width, Math.abs(y * height - height));

            double radius = streamART.vigilanceToDistance( category.getVigilanceRadius() );
            double rX = radius * width;
            double rY = radius * height;

            Point2D pArea = new Point2D(p.x - rX, p.y - rY);

            g.setColor(microCategoryAreaColor);
            g.drawOval((int) pArea.x, (int) pArea.y,
                    (int) (rX * 2), (int) (rY * 2));

            g.setColor(microCategoryColor);
            g.fillRect((int) p.x, (int) p.y, PT_SIZE, PT_SIZE);
            g.setColor(Color.black);
            g.setFont(LookAndFeel.fontTextSmall);
            g.drawString(String.valueOf(category.getWeight()), (int) p.x, (int) p.y);
        }
    }

    /////////////////////////////////////////////////////////////
    // COLORING

    private static Color microCategoryColor = Color.BLUE;
    private static Color microCategoryAreaColor = new Color(200, 200, 200, 100); // light gray with transparency

    public static void setMicroCategoryColor(Color microCategoryColor) {
        CodebookVisualizationLayer.microCategoryColor = microCategoryColor;
    }

    public static void setMicroCategoryAreaColor(Color microCategoryAreaColor) {
        CodebookVisualizationLayer.microCategoryAreaColor = microCategoryAreaColor;
    }
}
