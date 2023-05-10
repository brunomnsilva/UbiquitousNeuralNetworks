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

package com.brunomnsilva.neuralnetworks.view.som;

import com.brunomnsilva.neuralnetworks.core.Args;
import com.brunomnsilva.neuralnetworks.core.Observable;
import com.brunomnsilva.neuralnetworks.core.Observer;
import com.brunomnsilva.neuralnetworks.models.som.RectangularLattice;
import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.view.LookAndFeel;
import com.brunomnsilva.neuralnetworks.view.MessagePanel;
import com.brunomnsilva.neuralnetworks.view.colorscale.ColorScalePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This abstract class models a base visualization for the SelfOrganizingMap, where neurons in the lattice are
 * visualized in a grid and colored based on set values. The lattice that is shown matches the lattice that was
 * parameterized in the instantiation of the SelfOrganizingMap.
 * <br/>
 * This visualization includes a title, a {@link GenericGridPanel} and a {@link ColorScalePanel}.
 * Implementing classes can create different visualizations by solely implementing the
 * {@link #updateGridValues(SelfOrganizingMap, GenericGridPanel)}, while retaining the overall same look between
 * visualizations.
 * <br/>
 * Implementing classes can attach context menu actions to perform specific actions.
 * <br/>
 * The base functionality can automatically update the visualization by observing changes to the underlying
 * SelfOrganizingMap; see {@link SelfOrganizingMap#addObserver(Observer)}.
 *
 * @see GenericGridPanel
 * @see ColorScalePanel
 * @see SelfOrganizingMap
 *
 * @author brunomnsilva
 */
public abstract class AbstractVisualizationPanel extends JPanel implements Observer {

    /** The underlying SelfOrganizingMap of the visualization. */
    private final SelfOrganizingMap som;

    /** The title of the panel. */
    private final String title;
    
    /** The GenericGridPanel to depict values. */
    private GenericGridPanel gridPanel;
    
    /** The ColorScalePanel from where to derive the depicted value's colors. */
    private ColorScalePanel colorScalePanel;

    /** Context menu for user interaction. */
    private JPopupMenu contextMenu;

    /** Error message (optional) */
    private String errorMessage;

    /**
     * The content displayed in this panel, besides the title.
     * Typically, a [grid | colorscale], but may be swapped by an error panel. */
    private JPanel contentPanel;

    /**
     * Default constructor. The size of the {@link GenericGridPanel} will be automatically.
     * derived from the size of the SelfOrganizingMap.
     * @param som the SelfOrganizingMap to visualize
     * @param title the title of the panel
     */
    public AbstractVisualizationPanel(SelfOrganizingMap som, String title) {
        this(som, title, som.getWidth(), som.getHeight());
    }

    /**
     * Constructor that allows to set the dimensions of the {@link GenericGridPanel}.
     * @param som the SelfOrganizingMap to visualize
     * @param title the title of the panel
     * @param gridPanelWidth the width of the {@link GenericGridPanel}
     * @param gridPanelHeight the height of the {@link GenericGridPanel}
     */
    public AbstractVisualizationPanel(SelfOrganizingMap som, String title, int gridPanelWidth, int gridPanelHeight) {
        super(true);

        Args.nullNotPermitted(som, "som");
        Args.nullNotPermitted(title, "name");

        this.som = som;
        this.title = title;
        this.errorMessage = null;

        initComponents(gridPanelWidth, gridPanelHeight);
    }

    /**
     * For a caller to request an update to this visualization. Necessary to reflect any changes to the
     * underlying SelfOrganizingMap.
     */
    public final void update() {
        if(errorMessage != null) return;

        updateGridValues(this.som, this.gridPanel);
        gridPanel.update();
    }

    /**
     * If set, the <i>message</i> will be displayed instead of the grid and colorscale.
     * The message should be set by the implementing classes, in case of any error that does not allow
     * the visualization to be produced.
     * <br/>
     * For performance reasons this method should be called only once.
     *
     * @param message the error message
     */
    protected final void setErrorMessage(String message) {
        this.errorMessage = message;

        // Replace the contents with a MessagePanel
        invalidate();
        remove(contentPanel);
        add(new MessagePanel(message), BorderLayout.CENTER);
        validate();
    }
    /**
     * Updates the values to be shown on the underlying GenericGridPanel, effectively implementing the visualization.
     * @param som the SelfOrganizingMap to visualize
     * @param grid the instance of GenericGridPanel to manipulate
     */
    protected abstract void updateGridValues(SelfOrganizingMap som, GenericGridPanel grid);

    /**
     * Returns a short description of the visualization. It will be shown in the panel's title (tooltip).
     * @return a short description of the visualization
     */
    protected abstract String description();

    /**
     * Can be used by implementing classes to add context menu actions.
     * @param menuItemName the menu item name
     * @param action the action to execute
     */
    protected final void addContextMenuAction(String menuItemName, ActionListener action) {
        // Menu lazy initialization
        if(contextMenu == null) {
            contextMenu = new JPopupMenu();
            final JPanel thisPanel = this;
            this.addMouseListener(new MouseClickListener(this));
        }
        // Add menu item
        JMenuItem item = new JMenuItem(menuItemName);
        item.addActionListener(action);
        contextMenu.add( item );
    }

    private void initComponents(int gridPanelWidth, int gridPanelHeight) {
        setLayout(new BorderLayout());
        this.setOpaque(true);
        this.setBackground(LookAndFeel.colorBackground);
        setPreferredSize(new Dimension(400, 500));

        // NORTH
        JLabel nameLabel = new JLabel(this.title, JLabel.CENTER);
        nameLabel.setFont(LookAndFeel.fontTitle);
        nameLabel.setForeground(LookAndFeel.colorFontTitle);

        // Information about the visualization while hovering over a (i) symbol.
        JLabel infoLabel = new JLabel(" \u24d8", JLabel.CENTER);
        infoLabel.setFont(LookAndFeel.fontTitle);
        infoLabel.setForeground(LookAndFeel.colorFontTitle);

        String description = description();
        if(description != null) {
            infoLabel.setToolTipText(description);
        } else {
            infoLabel.setToolTipText("No available information.");
        }

        // Put label and info at top
        JPanel hbox = new JPanel(new FlowLayout());
        hbox.setBackground(LookAndFeel.colorBackground);
        hbox.add(nameLabel);
        hbox.add(infoLabel);
        add(hbox, BorderLayout.NORTH);

        // CENTER

        colorScalePanel = new ColorScalePanel();

        GenericGridPanel.LatticeType gridLatticeType = (som.getLattice() instanceof RectangularLattice) ?
                GenericGridPanel.LatticeType.RECTANGULAR : GenericGridPanel.LatticeType.HEXAGONAL;

        gridPanel = new GenericGridPanel(colorScalePanel,
                gridLatticeType,
                gridPanelWidth,
                gridPanelHeight);

        // Process clicks in the grid panel
        gridPanel.addMouseListener(new MouseClickListener(this));

        Dimension colorScalePanelDim = new Dimension(80, 100);
        colorScalePanel.setMinimumSize(colorScalePanelDim);
        colorScalePanel.setMaximumSize(colorScalePanelDim);
        colorScalePanel.setPreferredSize(colorScalePanelDim);

        contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Put grid at left - 90% space
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.9;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 1;
        contentPanel.add(gridPanel, c);

        // Put colorscale at right - 10% space
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.1;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 1;
        contentPanel.add(colorScalePanel, c);

        add(contentPanel, BorderLayout.CENTER);
    }

    protected final ColorScalePanel getColorScalePanel() {
        return colorScalePanel;
    }

    @Override
    public void onNotify(Observable observable) {
        if(observable instanceof SelfOrganizingMap) {
            SelfOrganizingMap refSOM = (SelfOrganizingMap)observable;
            if(refSOM != som) return;

            this.update();
        }
    }

    /**
     * A mouse listener implementation to trigger the context menu.
     */
    private class MouseClickListener extends MouseAdapter {

        private AbstractVisualizationPanel originator;

        public MouseClickListener(AbstractVisualizationPanel originator) {
            this.originator = originator;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(originator.contextMenu == null) return;

            if(e.getButton() == MouseEvent.BUTTON3) {
                contextMenu.show(originator , e.getX(), e.getY());
            }
        }
    }
}
