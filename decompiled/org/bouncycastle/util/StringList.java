package org.bouncycastle.util;

public interface StringList extends Iterable<String> {
   boolean add(String var1);

   String get(int var1);

   int size();

   String[] toStringArray();

   String[] toStringArray(int var1, int var2);
}
