package org.bouncycastle.util;

import java.util.Iterator;

public interface Iterable<T> extends java.lang.Iterable<T> {
   @Override
   Iterator<T> iterator();
}
