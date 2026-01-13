package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

public class Interpolation {
   public static double linear(double valueA, double valueB, double weight) {
      if (!(weight < 0.0) && !(weight > 1.0)) {
         return valueA * (1.0 - weight) + valueB * weight;
      } else {
         throw new IllegalArgumentException("weight outside range");
      }
   }
}
