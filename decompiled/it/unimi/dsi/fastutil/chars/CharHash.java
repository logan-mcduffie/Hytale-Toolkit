package it.unimi.dsi.fastutil.chars;

public interface CharHash {
   public interface Strategy {
      int hashCode(char var1);

      boolean equals(char var1, char var2);
   }
}
