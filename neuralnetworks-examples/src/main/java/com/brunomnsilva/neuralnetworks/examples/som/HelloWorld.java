package com.brunomnsilva.neuralnetworks.examples.som;

import com.brunomnsilva.neuralnetworks.models.som.SelfOrganizingMap;
import com.brunomnsilva.neuralnetworks.models.som.impl.BasicSOM;
import com.brunomnsilva.neuralnetworks.view.ExportSVGWrapperPanel;
import com.brunomnsilva.neuralnetworks.view.GenericWindow;
import com.brunomnsilva.neuralnetworks.view.som.ComponentPlaneVisualizationPanel;
import com.brunomnsilva.neuralnetworks.view.som.SelfOrganizingMapVisualizationFactory;

public class HelloWorld {
    public static void main(String[] args) {

        SelfOrganizingMap som = new BasicSOM(10, 20, 3);

        ComponentPlaneVisualizationPanel cpViz = SelfOrganizingMapVisualizationFactory.createComponentPlane(som, 0, "v1");

        GenericWindow window = GenericWindow.horizontalLayout("Component Plane", new ExportSVGWrapperPanel(cpViz));
        window.exitOnClose();
        window.setVisible(true);

    }
}
