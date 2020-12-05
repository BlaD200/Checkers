package org.ukma.vsynytsyn.utils;

import lombok.Data;

@Data
public class Tuple<A, B> {
    private final A a;
    private final B b;
}
