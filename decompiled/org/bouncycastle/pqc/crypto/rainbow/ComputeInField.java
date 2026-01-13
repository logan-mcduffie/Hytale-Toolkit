package org.bouncycastle.pqc.crypto.rainbow;

class ComputeInField {
   public ComputeInField() {
   }

   public short[] solveEquation(short[][] var1, short[] var2) {
      if (var1.length != var2.length) {
         return null;
      } else {
         try {
            short[][] var3 = new short[var1.length][var1.length + 1];
            short[] var4 = new short[var1.length];

            for (int var5 = 0; var5 < var1.length; var5++) {
               System.arraycopy(var1[var5], 0, var3[var5], 0, var1[0].length);
               var3[var5][var2.length] = GF2Field.addElem(var2[var5], var3[var5][var2.length]);
            }

            this.gaussElim(var3);

            for (int var7 = 0; var7 < var3.length; var7++) {
               var4[var7] = var3[var7][var2.length];
            }

            return var4;
         } catch (RuntimeException var6) {
            return null;
         }
      }
   }

   public short[][] inverse(short[][] var1) {
      if (var1.length != var1[0].length) {
         throw new RuntimeException("The matrix is not invertible. Please choose another one!");
      } else {
         try {
            short[][] var3 = new short[var1.length][2 * var1.length];

            for (int var4 = 0; var4 < var1.length; var4++) {
               System.arraycopy(var1[var4], 0, var3[var4], 0, var1.length);

               for (int var5 = var1.length; var5 < 2 * var1.length; var5++) {
                  var3[var4][var5] = 0;
               }

               var3[var4][var4 + var3.length] = 1;
            }

            this.gaussElim(var3);
            short[][] var2 = new short[var3.length][var3.length];

            for (int var7 = 0; var7 < var3.length; var7++) {
               for (int var8 = var3.length; var8 < 2 * var3.length; var8++) {
                  var2[var7][var8 - var3.length] = var3[var7][var8];
               }
            }

            return var2;
         } catch (RuntimeException var6) {
            return null;
         }
      }
   }

   private void gaussElim(short[][] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         for (int var6 = var5 + 1; var6 < var1.length; var6++) {
            if (var1[var5][var5] == 0) {
               for (int var7 = var5; var7 < var1[0].length; var7++) {
                  var1[var5][var7] = GF2Field.addElem(var1[var5][var7], var1[var6][var7]);
               }
            }
         }

         short var3 = GF2Field.invElem(var1[var5][var5]);
         if (var3 == 0) {
            throw new RuntimeException("The matrix is not invertible");
         }

         var1[var5] = this.multVect(var3, var1[var5]);

         for (int var8 = 0; var8 < var1.length; var8++) {
            if (var5 != var8) {
               short var4 = var1[var8][var5];

               for (int var9 = var5; var9 < var1[0].length; var9++) {
                  short var2 = GF2Field.multElem(var1[var5][var9], var4);
                  var1[var8][var9] = GF2Field.addElem(var1[var8][var9], var2);
               }
            }
         }
      }
   }

   public short[][] multiplyMatrix(short[][] var1, short[][] var2) throws RuntimeException {
      if (var1[0].length != var2.length) {
         throw new RuntimeException("Multiplication is not possible!");
      } else {
         short var3 = 0;
         short[][] var4 = new short[var1.length][var2[0].length];

         for (int var5 = 0; var5 < var1.length; var5++) {
            for (int var6 = 0; var6 < var2.length; var6++) {
               for (int var7 = 0; var7 < var2[0].length; var7++) {
                  var3 = GF2Field.multElem(var1[var5][var6], var2[var6][var7]);
                  var4[var5][var7] = GF2Field.addElem(var4[var5][var7], var3);
               }
            }
         }

         return var4;
      }
   }

   public short[] multiplyMatrix(short[][] var1, short[] var2) throws RuntimeException {
      if (var1[0].length != var2.length) {
         throw new RuntimeException("Multiplication is not possible!");
      } else {
         short var3 = 0;
         short[] var4 = new short[var1.length];

         for (int var5 = 0; var5 < var1.length; var5++) {
            for (int var6 = 0; var6 < var2.length; var6++) {
               var3 = GF2Field.multElem(var1[var5][var6], var2[var6]);
               var4[var5] = GF2Field.addElem(var4[var5], var3);
            }
         }

         return var4;
      }
   }

   public short multiplyMatrix_quad(short[][] var1, short[] var2) throws RuntimeException {
      if (var1.length == var1[0].length && var1[0].length == var2.length) {
         short var3 = 0;
         short[] var4 = new short[var1.length];
         short var5 = 0;

         for (int var6 = 0; var6 < var1.length; var6++) {
            for (int var7 = 0; var7 < var2.length; var7++) {
               var3 = GF2Field.multElem(var1[var6][var7], var2[var7]);
               var4[var6] = GF2Field.addElem(var4[var6], var3);
            }

            var3 = GF2Field.multElem(var4[var6], var2[var6]);
            var5 = GF2Field.addElem(var5, var3);
         }

         return var5;
      } else {
         throw new RuntimeException("Multiplication is not possible!");
      }
   }

   public short[] addVect(short[] var1, short[] var2) {
      if (var1.length != var2.length) {
         throw new RuntimeException("Addition is not possible! vector1.length: " + var1.length + " vector2.length: " + var2.length);
      } else {
         short[] var3 = new short[var1.length];

         for (int var4 = 0; var4 < var3.length; var4++) {
            var3[var4] = GF2Field.addElem(var1[var4], var2[var4]);
         }

         return var3;
      }
   }

   public short[][] multVects(short[] var1, short[] var2) {
      if (var1.length != var2.length) {
         throw new RuntimeException("Multiplication is not possible!");
      } else {
         short[][] var3 = new short[var1.length][var2.length];

         for (int var4 = 0; var4 < var1.length; var4++) {
            for (int var5 = 0; var5 < var2.length; var5++) {
               var3[var4][var5] = GF2Field.multElem(var1[var4], var2[var5]);
            }
         }

         return var3;
      }
   }

   public short[] multVect(short var1, short[] var2) {
      short[] var3 = new short[var2.length];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = GF2Field.multElem(var1, var2[var4]);
      }

      return var3;
   }

   public short[][] multMatrix(short var1, short[][] var2) {
      short[][] var3 = new short[var2.length][var2[0].length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         for (int var5 = 0; var5 < var2[0].length; var5++) {
            var3[var4][var5] = GF2Field.multElem(var1, var2[var4][var5]);
         }
      }

      return var3;
   }

   public short[][] addMatrix(short[][] var1, short[][] var2) {
      if (var1.length == var2.length && var1[0].length == var2[0].length) {
         short[][] var3 = new short[var1.length][var1[0].length];

         for (int var4 = 0; var4 < var1.length; var4++) {
            for (int var5 = 0; var5 < var1[0].length; var5++) {
               var3[var4][var5] = GF2Field.addElem(var1[var4][var5], var2[var4][var5]);
            }
         }

         return var3;
      } else {
         throw new RuntimeException("Addition is not possible!");
      }
   }

   public short[][] addMatrixTranspose(short[][] var1) {
      if (var1.length != var1[0].length) {
         throw new RuntimeException("Addition is not possible!");
      } else {
         return this.addMatrix(var1, this.transpose(var1));
      }
   }

   public short[][] transpose(short[][] var1) {
      short[][] var2 = new short[var1[0].length][var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         for (int var4 = 0; var4 < var1[0].length; var4++) {
            var2[var4][var3] = var1[var3][var4];
         }
      }

      return var2;
   }

   public short[][] to_UT(short[][] var1) {
      if (var1.length != var1[0].length) {
         throw new RuntimeException("Computation to upper triangular matrix is not possible!");
      } else {
         short[][] var2 = new short[var1.length][var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2[var3][var3] = var1[var3][var3];

            for (int var4 = var3 + 1; var4 < var1[0].length; var4++) {
               var2[var3][var4] = GF2Field.addElem(var1[var3][var4], var1[var4][var3]);
            }
         }

         return var2;
      }
   }

   public short[][][] obfuscate_l1_polys(short[][] var1, short[][][] var2, short[][][] var3) {
      if (var2[0].length == var3[0].length && var2[0][0].length == var3[0][0].length && var2.length == var1[0].length && var3.length == var1.length) {
         short[][][] var5 = new short[var3.length][var3[0].length][var3[0][0].length];

         for (int var6 = 0; var6 < var2[0].length; var6++) {
            for (int var7 = 0; var7 < var2[0][0].length; var7++) {
               for (int var8 = 0; var8 < var1.length; var8++) {
                  for (int var9 = 0; var9 < var1[0].length; var9++) {
                     short var4 = GF2Field.multElem(var1[var8][var9], var2[var9][var6][var7]);
                     var5[var8][var6][var7] = GF2Field.addElem(var5[var8][var6][var7], var4);
                  }

                  var5[var8][var6][var7] = GF2Field.addElem(var3[var8][var6][var7], var5[var8][var6][var7]);
               }
            }
         }

         return var5;
      } else {
         throw new RuntimeException("Multiplication not possible!");
      }
   }
}
