package it.unimi.dsi.fastutil.doubles;

public interface DoubleHash {
   public interface Strategy {
      int hashCode(double var1);

      boolean equals(double var1, double var3);
   }
}
