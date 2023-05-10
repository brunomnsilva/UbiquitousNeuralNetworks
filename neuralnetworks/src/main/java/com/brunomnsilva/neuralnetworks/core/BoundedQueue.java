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

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * A bounded queue implementation backed by a linked blocking deque with a fixed capacity limit.
 * Elements are added to the end of the queue and if the capacity is reached, the oldest element is removed from the front.
 * This implementation is thread-safe.
 * @param <E> the type of elements held in this queue
 *
 * @author brunomnsilva
 */
public class BoundedQueue<E> extends LinkedBlockingDeque<E> {
    /**
     * The maximum capacity of the queue.
     */
    private final int limit;

    /**
     * Creates a new bounded queue with the specified capacity limit.
     * @param limit the maximum capacity of the queue
     */
    public BoundedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        super.addLast(o);
        while (size() > limit) { super.removeFirst(); }
        return true;

    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public Iterator<E> iterator() {
        return super.iterator();
    }
}