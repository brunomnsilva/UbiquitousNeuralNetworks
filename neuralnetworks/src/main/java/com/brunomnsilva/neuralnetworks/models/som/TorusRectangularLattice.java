package com.brunomnsilva.neuralnetworks.models.som;

/**
 * An implementation of a torus-shaped rectangular lattice.
 * Neurons on one edge are neighbors of the neurons in the opposite edge.
 *
 * @author brunomnsilva
 */
public class TorusRectangularLattice extends RectangularLattice {

    @Override
    public double distanceBetween(PrototypeNeuron a, PrototypeNeuron b) {
        double deltaX = Math.min(Math.abs(b.getIndexX() - a.getIndexX()), getWidth() - Math.abs(b.getIndexX() - a.getIndexX()));
        double deltaY = Math.min(Math.abs(b.getIndexY() - a.getIndexY()), getHeight() - Math.abs(b.getIndexY() - a.getIndexY()));

        return StrictMath.sqrt((deltaX * deltaX) + (deltaY * deltaY));
    }

    @Override
    public boolean areNeighbors(PrototypeNeuron a, PrototypeNeuron b) {
        int dx = Math.min(Math.abs(b.getIndexX() - a.getIndexX()), getWidth() - Math.abs(b.getIndexX() - a.getIndexX()));
        int dy = Math.min(Math.abs(b.getIndexY() - a.getIndexY()), getHeight() - Math.abs(b.getIndexY() - a.getIndexY()));
        return dx <= 1 && dy <= 1;
    }
}
