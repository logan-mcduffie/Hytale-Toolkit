package io.netty.util;

public interface Constant<T extends Constant<T>> extends Comparable<T> {
   int id();

   String name();
}
