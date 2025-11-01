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

/**
 * Observable is used to notify a group of {@link Observer} objects when a change
 * occurs. On creation, the set of observers is empty. After a change occurred,
 * the application can call the {@link #notifyObservers()} method. This will
 * cause the invocation of the {@link Observer#onNotify(Observable)} method of all registered
 * Observers. The order of invocation is not specified.
 *
 * @see Observer
 *
 * @author brunomnsilva
 */
public interface Observable {

    /**
     * Registers an observer that will be notified of changes.
     * @param observer the observer to register
     */
    void addObserver(Observer observer);

    /**
     * Unregisters an observer.
     *
     * If the <code>observer</code> wasn't previously registered, it will be ignored.
     *
     * @param observer the observer to unregister.
     */
    void removeObserver(Observer observer);

    /**
     * Notify registered observers of a change, in no particular order.
     *
     * For each registered observer, the method {@link Observer#onNotify(Observable)} will be called.
     */
    void notifyObservers();
}
