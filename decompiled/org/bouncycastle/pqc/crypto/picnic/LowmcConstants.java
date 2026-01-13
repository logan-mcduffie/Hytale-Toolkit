package org.bouncycastle.pqc.crypto.picnic;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Properties;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.encoders.Hex;

abstract class LowmcConstants {
   protected int[] linearMatrices;
   protected int[] roundConstants;
   protected int[] keyMatrices;
   protected KMatrices LMatrix;
   protected KMatrices KMatrix;
   protected KMatrices RConstants;
   protected int[] linearMatrices_full;
   protected int[] keyMatrices_full;
   protected int[] keyMatrices_inv;
   protected int[] linearMatrices_inv;
   protected int[] roundConstants_full;
   protected KMatrices LMatrix_full;
   protected KMatrices LMatrix_inv;
   protected KMatrices KMatrix_full;
   protected KMatrices KMatrix_inv;
   protected KMatrices RConstants_full;

   static int[] readArray(DataInputStream var0) throws IOException {
      int[] var1 = new int[var0.readInt()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = var0.readInt();
      }

      return var1;
   }

   static int[] ReadFromProperty(Properties var0, String var1, int var2) {
      String var3 = var0.getProperty(var1);
      byte[] var4 = Hex.decode(removeCommas(var3));
      int[] var5 = new int[var2];

      for (int var6 = 0; var6 < var4.length / 4; var6++) {
         var5[var6] = Pack.littleEndianToInt(var4, var6 * 4);
      }

      return var5;
   }

   private static byte[] removeCommas(String var0) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      for (int var2 = 0; var2 != var0.length(); var2++) {
         if (var0.charAt(var2) != ',') {
            var1.write(var0.charAt(var2));
         }
      }

      return var1.toByteArray();
   }

   private KMatricesWithPointer GET_MAT(KMatrices var1, int var2) {
      KMatricesWithPointer var3 = new KMatricesWithPointer(var1);
      var3.setMatrixPointer(var2 * var3.getSize());
      return var3;
   }

   protected KMatricesWithPointer LMatrix(PicnicEngine var1, int var2) {
      if (var1.stateSizeBits == 128) {
         return this.GET_MAT(this.LMatrix, var2);
      } else if (var1.stateSizeBits == 129) {
         return this.GET_MAT(this.LMatrix_full, var2);
      } else if (var1.stateSizeBits == 192) {
         return var1.numRounds == 4 ? this.GET_MAT(this.LMatrix_full, var2) : this.GET_MAT(this.LMatrix, var2);
      } else if (var1.stateSizeBits == 255) {
         return this.GET_MAT(this.LMatrix_full, var2);
      } else {
         return var1.stateSizeBits == 256 ? this.GET_MAT(this.LMatrix, var2) : null;
      }
   }

   protected KMatricesWithPointer LMatrixInv(PicnicEngine var1, int var2) {
      if (var1.stateSizeBits == 129) {
         return this.GET_MAT(this.LMatrix_inv, var2);
      } else if (var1.stateSizeBits == 192 && var1.numRounds == 4) {
         return this.GET_MAT(this.LMatrix_inv, var2);
      } else {
         return var1.stateSizeBits == 255 ? this.GET_MAT(this.LMatrix_inv, var2) : null;
      }
   }

   protected KMatricesWithPointer KMatrix(PicnicEngine var1, int var2) {
      if (var1.stateSizeBits == 128) {
         return this.GET_MAT(this.KMatrix, var2);
      } else if (var1.stateSizeBits == 129) {
         return this.GET_MAT(this.KMatrix_full, var2);
      } else if (var1.stateSizeBits == 192) {
         return var1.numRounds == 4 ? this.GET_MAT(this.KMatrix_full, var2) : this.GET_MAT(this.KMatrix, var2);
      } else if (var1.stateSizeBits == 255) {
         return this.GET_MAT(this.KMatrix_full, var2);
      } else {
         return var1.stateSizeBits == 256 ? this.GET_MAT(this.KMatrix, var2) : null;
      }
   }

   protected KMatricesWithPointer KMatrixInv(PicnicEngine var1) {
      byte var2 = 0;
      if (var1.stateSizeBits == 129) {
         return this.GET_MAT(this.KMatrix_inv, var2);
      } else if (var1.stateSizeBits == 192 && var1.numRounds == 4) {
         return this.GET_MAT(this.KMatrix_inv, var2);
      } else {
         return var1.stateSizeBits == 255 ? this.GET_MAT(this.KMatrix_inv, var2) : null;
      }
   }

   protected KMatricesWithPointer RConstant(PicnicEngine var1, int var2) {
      if (var1.stateSizeBits == 128) {
         return this.GET_MAT(this.RConstants, var2);
      } else if (var1.stateSizeBits == 129) {
         return this.GET_MAT(this.RConstants_full, var2);
      } else if (var1.stateSizeBits == 192) {
         return var1.numRounds == 4 ? this.GET_MAT(this.RConstants_full, var2) : this.GET_MAT(this.RConstants, var2);
      } else if (var1.stateSizeBits == 255) {
         return this.GET_MAT(this.RConstants_full, var2);
      } else {
         return var1.stateSizeBits == 256 ? this.GET_MAT(this.RConstants, var2) : null;
      }
   }
}
