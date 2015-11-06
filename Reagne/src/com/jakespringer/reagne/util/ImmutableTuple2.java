package com.jakespringer.reagne.util;

public class ImmutableTuple2<L, R> {
    public final L left;
    public final R right;
    
    public ImmutableTuple2(final L l, final R r) {
        left = l;
        right = r;
    }
}
