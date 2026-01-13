package it.unimi.dsi.fastutil.ints;

public interface IntHash {
   public interface Strategy {
      int hashCode(int var1);

      boolean equals(int var1, int var2);
   }
}
