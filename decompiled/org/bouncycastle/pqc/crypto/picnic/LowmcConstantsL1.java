package org.bouncycastle.pqc.crypto.picnic;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.bouncycastle.util.Exceptions;

public class LowmcConstantsL1 extends LowmcConstants {
   LowmcConstantsL1() {
      try {
         DataInputStream var1 = new DataInputStream(new GZIPInputStream(LowmcConstants.class.getResourceAsStream("lowmcL1.bin.properties")));
         this.linearMatrices = readArray(var1);
         this.roundConstants = readArray(var1);
         this.keyMatrices = readArray(var1);
         this.linearMatrices_full = readArray(var1);
         this.keyMatrices_full = readArray(var1);
         this.keyMatrices_inv = readArray(var1);
         this.linearMatrices_inv = readArray(var1);
         this.roundConstants_full = readArray(var1);
      } catch (IOException var2) {
         throw Exceptions.illegalStateException("unable to load Picnic properties: " + var2.getMessage(), var2);
      }

      this.LMatrix = new KMatrices(20, 128, 4, this.linearMatrices);
      this.KMatrix = new KMatrices(21, 128, 4, this.keyMatrices);
      this.RConstants = new KMatrices(0, 1, 4, this.roundConstants);
      this.LMatrix_full = new KMatrices(4, 129, 5, this.linearMatrices_full);
      this.LMatrix_inv = new KMatrices(4, 129, 5, this.linearMatrices_inv);
      this.KMatrix_full = new KMatrices(5, 129, 5, this.keyMatrices_full);
      this.KMatrix_inv = new KMatrices(1, 129, 5, this.keyMatrices_inv);
      this.RConstants_full = new KMatrices(4, 1, 5, this.roundConstants_full);
   }
}
