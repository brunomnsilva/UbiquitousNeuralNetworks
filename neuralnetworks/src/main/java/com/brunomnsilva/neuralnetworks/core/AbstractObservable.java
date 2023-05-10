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

package com.brunomnsilva.neuralnetworks.core;

import java.util.HashSet;
import java.util.Set;

/**
 * A base implementation of {@link Observable}, which can be extended by other classes that intend to be observed.
 *
 * @author brunomnsilva
 */
public class AbstractObservable implements Observable {

    /** Holds the collection of Observers. */
    private Set<Observer> observerSet;

    /**
     * Default constructor. Initializes an empty collection of Observers.
     */
    public AbstractObservable() {
        observerSet = new HashSet<>();
    }

    @Override
    public void addObserver(Observer observer) {
        Args.nullNotPermitted(observer, "observer");

        observerSet.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        Args.nullNotPermitted(observer, "observer");

        observerSet.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observerSet) {
            o.onNotify(this);
        }
    }
}
