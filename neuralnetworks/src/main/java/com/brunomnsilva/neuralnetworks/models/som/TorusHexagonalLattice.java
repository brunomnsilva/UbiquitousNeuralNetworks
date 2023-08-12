package com.brunomnsilva.neuralnetworks.models.som;

/**
 * An implementation of a torus-shaped hexagonal lattice.
 * Neurons on one edge are neighbors of the neurons in the opposite edge.
 *
 * @author brunomnsilva
 */
public class TorusHexagonalLattice extends HexagonalLattice {
    @Override
    public double distanceBetween(PrototypeNeuron a, PrototypeNeuron b) {
        int dx = Math.abs(b.getIndexX() - a.getIndexX());
        int dy = Math.abs(b.getIndexY() - a.getIndexY());
        int wrappedDx = Math.min(dx, getWidth() - dx);
        int wrappedDy = Math.min(dy, getHeight() - dy);
        return Math.max(wrappedDx, wrappedDy);
    }

    @Override
    public boolean areNeighbors(PrototypeNeuron a, PrototypeNeuron b) {
        return distanceBetween(a, b) <= 1;
    }
}
