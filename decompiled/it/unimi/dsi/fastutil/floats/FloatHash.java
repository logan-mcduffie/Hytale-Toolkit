package it.unimi.dsi.fastutil.floats;

public interface FloatHash {
   public interface Strategy {
      int hashCode(float var1);

      boolean equals(float var1, float var2);
   }
}
