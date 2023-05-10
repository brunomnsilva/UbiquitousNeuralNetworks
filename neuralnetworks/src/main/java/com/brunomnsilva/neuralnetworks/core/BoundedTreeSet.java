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

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * A bounded implementation of the {@link java.util.TreeSet} class.
 * The set is guaranteed to not exceed a certain size limit,
 * and new elements are added while maintaining this limit
 * by removing elements from the set as needed.
 * <br/>
 * The elements that are to be removed are chosen based on comparison
 * between elements, given that the "lower" elements shall be removed when
 * necessary. The comparison criteria can be set with the respective constructor.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author brunomnsilva
 */
public class BoundedTreeSet<E> extends TreeSet<E> {
    private static final int DEFAULT_LIMIT = Integer.MAX_VALUE;

    private final int limit;

    /**
     * Constructs an empty {@code BoundedTreeSet} with an upper size limit of {@link Integer#MAX_VALUE}
     * and default comparator.
     */
    public BoundedTreeSet() {
        this(DEFAULT_LIMIT);
    }

    /**
     * Constructs an empty {@code BoundedTreeSet} with the specified upper size limit and default comparator.
     *
     * @param limit the upper size limit of the set
     */
    public BoundedTreeSet(int limit) {
        super();
        this.limit = limit;
    }

    /**
     * Constructs an empty {@code BoundedTreeSet} with the specified upper size limit and comparator.
     *
     * @param limit the upper size limit of the set
     * @param c     the comparator that will be used to order this set
     */
    public BoundedTreeSet(int limit, Comparator<? super E> c) {
        super(c);
        this.limit = limit;
    }

    private void adjust() {
        while (size() > limit) {
            remove(first());
        }
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * If this set already contains the element, the call leaves the set unchanged and returns {@code false}.
     * If the set is at full capacity after the addition of the element, the oldest element in the set is removed.
     *
     * @param item the element to be added to this set
     * @return {@code true} if this set did not already contain the specified element, or {@code false} otherwise
     */
    @Override
    public boolean add(E item) {
        boolean out = super.add(item);
        adjust();
        return out;
    }

    /**
     * Adds all the elements in the specified collection to this set if they're not already present.
     * If this set already contains all the elements, the call leaves the set unchanged and returns {@code false}.
     * If the set is at full capacity after the addition of the elements, the oldest elements in the set are removed.
     *
     * @param c the collection whose elements are to be added to this set
     * @return {@code true} if this set changed as a result of the call, or {@code false} otherwise
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean out = super.addAll(c);
        adjust();
        return out;
    }
}
