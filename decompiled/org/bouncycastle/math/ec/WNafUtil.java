package org.bouncycastle.math.ec;

import java.math.BigInteger;

public abstract class WNafUtil {
   public static final String PRECOMP_NAME = "bc_wnaf";
   private static final int[] DEFAULT_WINDOW_SIZE_CUTOFFS = new int[]{13, 41, 121, 337, 897, 2305};
   private static final int MAX_WIDTH = 16;
   private static final byte[] EMPTY_BYTES = new byte[0];
   private static final int[] EMPTY_INTS = new int[0];
   private static final ECPoint[] EMPTY_POINTS = new ECPoint[0];

   public static void configureBasepoint(ECPoint var0) {
      ECCurve var1 = var0.getCurve();
      if (null != var1) {
         BigInteger var2 = var1.getOrder();
         int var3 = null == var2 ? var1.getFieldSize() + 1 : var2.bitLength();
         final int var4 = Math.min(16, getWindowSize(var3) + 3);
         var1.precompute(var0, "bc_wnaf", new PreCompCallback() {
            @Override
            public PreCompInfo precompute(PreCompInfo var1) {
               WNafPreCompInfo var2x = var1 instanceof WNafPreCompInfo ? (WNafPreCompInfo)var1 : null;
               if (null != var2x && var2x.getConfWidth() == var4) {
                  var2x.setPromotionCountdown(0);
                  return var2x;
               } else {
                  WNafPreCompInfo var3x = new WNafPreCompInfo();
                  var3x.setPromotionCountdown(0);
                  var3x.setConfWidth(var4);
                  if (null != var2x) {
                     var3x.setPreComp(var2x.getPreComp());
                     var3x.setPreCompNeg(var2x.getPreCompNeg());
                     var3x.setTwice(var2x.getTwice());
                     var3x.setWidth(var2x.getWidth());
                  }

                  return var3x;
               }
            }
         });
      }
   }

   public static int[] generateCompactNaf(BigInteger var0) {
      if (var0.bitLength() >>> 16 != 0) {
         throw new IllegalArgumentException("'k' must have bitlength < 2^16");
      } else if (var0.signum() == 0) {
         return EMPTY_INTS;
      } else {
         BigInteger var1 = var0.shiftLeft(1).add(var0);
         int var2 = var1.bitLength();
         int[] var3 = new int[var2 >> 1];
         BigInteger var4 = var1.xor(var0);
         int var5 = var2 - 1;
         int var6 = 0;
         int var7 = 0;

         for (int var8 = 1; var8 < var5; var8++) {
            if (!var4.testBit(var8)) {
               var7++;
            } else {
               int var9 = var0.testBit(var8) ? -1 : 1;
               var3[var6++] = var9 << 16 | var7;
               var7 = 1;
               var8++;
            }
         }

         var3[var6++] = 65536 | var7;
         if (var3.length > var6) {
            var3 = trim(var3, var6);
         }

         return var3;
      }
   }

   public static int[] generateCompactWindowNaf(int var0, BigInteger var1) {
      if (var0 == 2) {
         return generateCompactNaf(var1);
      } else if (var0 >= 2 && var0 <= 16) {
         if (var1.bitLength() >>> 16 != 0) {
            throw new IllegalArgumentException("'k' must have bitlength < 2^16");
         } else if (var1.signum() == 0) {
            return EMPTY_INTS;
         } else {
            int[] var2 = new int[var1.bitLength() / var0 + 1];
            int var3 = 1 << var0;
            int var4 = var3 - 1;
            int var5 = var3 >>> 1;
            boolean var6 = false;
            int var7 = 0;
            int var8 = 0;

            while (var8 <= var1.bitLength()) {
               if (var1.testBit(var8) == var6) {
                  var8++;
               } else {
                  var1 = var1.shiftRight(var8);
                  int var9 = var1.intValue() & var4;
                  if (var6) {
                     var9++;
                  }

                  var6 = (var9 & var5) != 0;
                  if (var6) {
                     var9 -= var3;
                  }

                  int var10 = var7 > 0 ? var8 - 1 : var8;
                  var2[var7++] = var9 << 16 | var10;
                  var8 = var0;
               }
            }

            if (var2.length > var7) {
               var2 = trim(var2, var7);
            }

            return var2;
         }
      } else {
         throw new IllegalArgumentException("'width' must be in the range [2, 16]");
      }
   }

   public static byte[] generateJSF(BigInteger var0, BigInteger var1) {
      int var2 = Math.max(var0.bitLength(), var1.bitLength()) + 1;
      byte[] var3 = new byte[var2];
      BigInteger var4 = var0;
      BigInteger var5 = var1;
      int var6 = 0;
      byte var7 = 0;
      byte var8 = 0;
      int var9 = 0;

      while ((var7 | var8) != 0 || var4.bitLength() > var9 || var5.bitLength() > var9) {
         int var10 = (var4.intValue() >>> var9) + var7 & 7;
         int var11 = (var5.intValue() >>> var9) + var8 & 7;
         int var12 = var10 & 1;
         if (var12 != 0) {
            var12 -= var10 & 2;
            if (var10 + var12 == 4 && (var11 & 3) == 2) {
               var12 = -var12;
            }
         }

         int var13 = var11 & 1;
         if (var13 != 0) {
            var13 -= var11 & 2;
            if (var11 + var13 == 4 && (var10 & 3) == 2) {
               var13 = -var13;
            }
         }

         if (var7 << 1 == 1 + var12) {
            var7 ^= 1;
         }

         if (var8 << 1 == 1 + var13) {
            var8 ^= 1;
         }

         if (++var9 == 30) {
            var9 = 0;
            var4 = var4.shiftRight(30);
            var5 = var5.shiftRight(30);
         }

         var3[var6++] = (byte)(var12 << 4 | var13 & 15);
      }

      if (var3.length > var6) {
         var3 = trim(var3, var6);
      }

      return var3;
   }

   public static byte[] generateNaf(BigInteger var0) {
      if (var0.signum() == 0) {
         return EMPTY_BYTES;
      } else {
         BigInteger var1 = var0.shiftLeft(1).add(var0);
         int var2 = var1.bitLength() - 1;
         byte[] var3 = new byte[var2];
         BigInteger var4 = var1.xor(var0);

         for (int var5 = 1; var5 < var2; var5++) {
            if (var4.testBit(var5)) {
               var3[var5 - 1] = (byte)(var0.testBit(var5) ? -1 : 1);
               var5++;
            }
         }

         var3[var2 - 1] = 1;
         return var3;
      }
   }

   public static byte[] generateWindowNaf(int var0, BigInteger var1) {
      if (var0 == 2) {
         return generateNaf(var1);
      } else if (var0 >= 2 && var0 <= 8) {
         if (var1.signum() == 0) {
            return EMPTY_BYTES;
         } else {
            byte[] var2 = new byte[var1.bitLength() + 1];
            int var3 = 1 << var0;
            int var4 = var3 - 1;
            int var5 = var3 >>> 1;
            boolean var6 = false;
            int var10 = 0;
            int var8 = 0;

            while (var8 <= var1.bitLength()) {
               if (var1.testBit(var8) == var6) {
                  var8++;
               } else {
                  var1 = var1.shiftRight(var8);
                  int var9 = var1.intValue() & var4;
                  if (var6) {
                     var9++;
                  }

                  var6 = (var9 & var5) != 0;
                  if (var6) {
                     var9 -= var3;
                  }

                  var10 += var10 > 0 ? var8 - 1 : var8;
                  var2[var10++] = (byte)var9;
                  var8 = var0;
               }
            }

            if (var2.length > var10) {
               var2 = trim(var2, var10);
            }

            return var2;
         }
      } else {
         throw new IllegalArgumentException("'width' must be in the range [2, 8]");
      }
   }

   public static int getNafWeight(BigInteger var0) {
      if (var0.signum() == 0) {
         return 0;
      } else {
         BigInteger var1 = var0.shiftLeft(1).add(var0);
         BigInteger var2 = var1.xor(var0);
         return var2.bitCount();
      }
   }

   public static WNafPreCompInfo getWNafPreCompInfo(ECPoint var0) {
      return getWNafPreCompInfo(var0.getCurve().getPreCompInfo(var0, "bc_wnaf"));
   }

   public static WNafPreCompInfo getWNafPreCompInfo(PreCompInfo var0) {
      return var0 instanceof WNafPreCompInfo ? (WNafPreCompInfo)var0 : null;
   }

   public static int getWindowSize(int var0) {
      return getWindowSize(var0, DEFAULT_WINDOW_SIZE_CUTOFFS, 16);
   }

   public static int getWindowSize(int var0, int var1) {
      return getWindowSize(var0, DEFAULT_WINDOW_SIZE_CUTOFFS, var1);
   }

   public static int getWindowSize(int var0, int[] var1) {
      return getWindowSize(var0, var1, 16);
   }

   public static int getWindowSize(int var0, int[] var1, int var2) {
      int var3 = 0;

      while (var3 < var1.length && var0 >= var1[var3]) {
         var3++;
      }

      return Math.max(2, Math.min(var2, var3 + 2));
   }

   public static WNafPreCompInfo precompute(final ECPoint var0, final int var1, final boolean var2) {
      final ECCurve var3 = var0.getCurve();
      return (WNafPreCompInfo)var3.precompute(
         var0,
         "bc_wnaf",
         new PreCompCallback() {
            @Override
            public PreCompInfo precompute(PreCompInfo var1x) {
               WNafPreCompInfo var2x = var1x instanceof WNafPreCompInfo ? (WNafPreCompInfo)var1x : null;
               int var3x = Math.max(2, Math.min(16, var1));
               int var4 = 1 << var3x - 2;
               if (this.checkExisting(var2x, var3x, var4, var2)) {
                  var2x.decrementPromotionCountdown();
                  return var2x;
               } else {
                  WNafPreCompInfo var5 = new WNafPreCompInfo();
                  ECPoint[] var6 = null;
                  ECPoint[] var7 = null;
                  ECPoint var8 = null;
                  if (null != var2x) {
                     int var9 = var2x.decrementPromotionCountdown();
                     var5.setPromotionCountdown(var9);
                     int var10 = var2x.getConfWidth();
                     var5.setConfWidth(var10);
                     var6 = var2x.getPreComp();
                     var7 = var2x.getPreCompNeg();
                     var8 = var2x.getTwice();
                  }

                  var3x = Math.min(16, Math.max(var5.getConfWidth(), var3x));
                  var4 = 1 << var3x - 2;
                  int var18 = 0;
                  if (null == var6) {
                     var6 = WNafUtil.EMPTY_POINTS;
                  } else {
                     var18 = var6.length;
                  }

                  if (var18 < var4) {
                     var6 = WNafUtil.resizeTable(var6, var4);
                     if (var4 == 1) {
                        var6[0] = var0.normalize();
                     } else {
                        int var19 = var18;
                        if (var18 == 0) {
                           var6[0] = var0;
                           var19 = 1;
                        }

                        ECFieldElement var11 = null;
                        if (var4 == 2) {
                           var6[1] = var0.threeTimes();
                        } else {
                           ECPoint var12 = var8;
                           ECPoint var13 = var6[var19 - 1];
                           if (null == var8) {
                              var12 = var6[0].twice();
                              var8 = var12;
                              if (!var12.isInfinity() && ECAlgorithms.isFpCurve(var3) && var3.getFieldSize() >= 64) {
                                 switch (var3.getCoordinateSystem()) {
                                    case 2:
                                    case 3:
                                    case 4:
                                       var11 = var12.getZCoord(0);
                                       var12 = var3.createPoint(var12.getXCoord().toBigInteger(), var12.getYCoord().toBigInteger());
                                       ECFieldElement var14 = var11.square();
                                       ECFieldElement var15 = var14.multiply(var11);
                                       var13 = var13.scaleX(var14).scaleY(var15);
                                       if (var18 == 0) {
                                          var6[0] = var13;
                                       }
                                 }
                              }
                           }

                           while (var19 < var4) {
                              var6[var19++] = var13 = var13.add(var12);
                           }
                        }

                        var3.normalizeAll(var6, var18, var4 - var18, var11);
                     }
                  }

                  if (var2) {
                     int var20;
                     if (null == var7) {
                        var20 = 0;
                        var7 = new ECPoint[var4];
                     } else {
                        var20 = var7.length;
                        if (var20 < var4) {
                           var7 = WNafUtil.resizeTable(var7, var4);
                        }
                     }

                     while (var20 < var4) {
                        var7[var20] = var6[var20].negate();
                        var20++;
                     }
                  }

                  var5.setPreComp(var6);
                  var5.setPreCompNeg(var7);
                  var5.setTwice(var8);
                  var5.setWidth(var3x);
                  return var5;
               }
            }

            private boolean checkExisting(WNafPreCompInfo var1x, int var2x, int var3x, boolean var4) {
               return null != var1x
                  && var1x.getWidth() >= Math.max(var1x.getConfWidth(), var2x)
                  && this.checkTable(var1x.getPreComp(), var3x)
                  && (!var4 || this.checkTable(var1x.getPreCompNeg(), var3x));
            }

            private boolean checkTable(ECPoint[] var1x, int var2x) {
               return null != var1x && var1x.length >= var2x;
            }
         }
      );
   }

   public static WNafPreCompInfo precomputeWithPointMap(ECPoint var0, final ECPointMap var1, final WNafPreCompInfo var2, final boolean var3) {
      ECCurve var4 = var0.getCurve();
      return (WNafPreCompInfo)var4.precompute(
         var0,
         "bc_wnaf",
         new PreCompCallback() {
            @Override
            public PreCompInfo precompute(PreCompInfo var1x) {
               WNafPreCompInfo var2x = var1x instanceof WNafPreCompInfo ? (WNafPreCompInfo)var1x : null;
               int var3x = var2.getWidth();
               int var4x = var2.getPreComp().length;
               if (this.checkExisting(var2x, var3x, var4x, var3)) {
                  var2x.decrementPromotionCountdown();
                  return var2x;
               } else {
                  WNafPreCompInfo var5 = new WNafPreCompInfo();
                  var5.setPromotionCountdown(var2.getPromotionCountdown());
                  ECPoint var6 = var2.getTwice();
                  if (null != var6) {
                     ECPoint var7 = var1.map(var6);
                     var5.setTwice(var7);
                  }

                  ECPoint[] var11 = var2.getPreComp();
                  ECPoint[] var8 = new ECPoint[var11.length];

                  for (int var9 = 0; var9 < var11.length; var9++) {
                     var8[var9] = var1.map(var11[var9]);
                  }

                  var5.setPreComp(var8);
                  var5.setWidth(var3x);
                  if (var3) {
                     ECPoint[] var12 = new ECPoint[var8.length];

                     for (int var10 = 0; var10 < var12.length; var10++) {
                        var12[var10] = var8[var10].negate();
                     }

                     var5.setPreCompNeg(var12);
                  }

                  return var5;
               }
            }

            private boolean checkExisting(WNafPreCompInfo var1x, int var2x, int var3x, boolean var4x) {
               return null != var1x
                  && var1x.getWidth() >= var2x
                  && this.checkTable(var1x.getPreComp(), var3x)
                  && (!var4x || this.checkTable(var1x.getPreCompNeg(), var3x));
            }

            private boolean checkTable(ECPoint[] var1x, int var2x) {
               return null != var1x && var1x.length >= var2x;
            }
         }
      );
   }

   private static byte[] trim(byte[] var0, int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(var0, 0, var2, 0, var2.length);
      return var2;
   }

   private static int[] trim(int[] var0, int var1) {
      int[] var2 = new int[var1];
      System.arraycopy(var0, 0, var2, 0, var2.length);
      return var2;
   }

   private static ECPoint[] resizeTable(ECPoint[] var0, int var1) {
      ECPoint[] var2 = new ECPoint[var1];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      return var2;
   }
}
