package org.bouncycastle.pqc.crypto.lms;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class LMSPublicKeyParameters extends LMSKeyParameters implements LMSContextBasedVerifier {
   private final LMSigParameters parameterSet;
   private final LMOtsParameters lmOtsType;
   private final byte[] I;
   private final byte[] T1;

   public LMSPublicKeyParameters(LMSigParameters var1, LMOtsParameters var2, byte[] var3, byte[] var4) {
      super(false);
      this.parameterSet = var1;
      this.lmOtsType = var2;
      this.I = Arrays.clone(var4);
      this.T1 = Arrays.clone(var3);
   }

   public static LMSPublicKeyParameters getInstance(Object var0) throws IOException {
      if (var0 instanceof LMSPublicKeyParameters) {
         return (LMSPublicKeyParameters)var0;
      } else if (var0 instanceof DataInputStream) {
         int var9 = ((DataInputStream)var0).readInt();
         LMSigParameters var10 = LMSigParameters.getParametersForType(var9);
         LMOtsParameters var3 = LMOtsParameters.getParametersForType(((DataInputStream)var0).readInt());
         byte[] var4 = new byte[16];
         ((DataInputStream)var0).readFully(var4);
         byte[] var5 = new byte[var10.getM()];
         ((DataInputStream)var0).readFully(var5);
         return new LMSPublicKeyParameters(var10, var3, var5, var4);
      } else if (var0 instanceof byte[]) {
         DataInputStream var1 = null;

         LMSPublicKeyParameters var2;
         try {
            var1 = new DataInputStream(new ByteArrayInputStream((byte[])var0));
            var2 = getInstance(var1);
         } finally {
            if (var1 != null) {
               var1.close();
            }
         }

         return var2;
      } else if (var0 instanceof InputStream) {
         return getInstance(Streams.readAll((InputStream)var0));
      } else {
         throw new IllegalArgumentException("cannot parse " + var0);
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.toByteArray();
   }

   public LMSigParameters getSigParameters() {
      return this.parameterSet;
   }

   public LMOtsParameters getOtsParameters() {
      return this.lmOtsType;
   }

   public LMSParameters getLMSParameters() {
      return new LMSParameters(this.getSigParameters(), this.getOtsParameters());
   }

   public byte[] getT1() {
      return Arrays.clone(this.T1);
   }

   boolean matchesT1(byte[] var1) {
      return Arrays.constantTimeAreEqual(this.T1, var1);
   }

   public byte[] getI() {
      return Arrays.clone(this.I);
   }

   byte[] refI() {
      return this.I;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LMSPublicKeyParameters var2 = (LMSPublicKeyParameters)var1;
         if (!this.parameterSet.equals(var2.parameterSet)) {
            return false;
         } else if (!this.lmOtsType.equals(var2.lmOtsType)) {
            return false;
         } else {
            return !Arrays.areEqual(this.I, var2.I) ? false : Arrays.areEqual(this.T1, var2.T1);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.parameterSet.hashCode();
      var1 = 31 * var1 + this.lmOtsType.hashCode();
      var1 = 31 * var1 + Arrays.hashCode(this.I);
      return 31 * var1 + Arrays.hashCode(this.T1);
   }

   byte[] toByteArray() {
      return Composer.compose().u32str(this.parameterSet.getType()).u32str(this.lmOtsType.getType()).bytes(this.I).bytes(this.T1).build();
   }

   @Override
   public LMSContext generateLMSContext(byte[] var1) {
      try {
         return this.generateOtsContext(LMSSignature.getInstance(var1));
      } catch (IOException var3) {
         throw new IllegalStateException("cannot parse signature: " + var3.getMessage());
      }
   }

   LMSContext generateOtsContext(LMSSignature var1) {
      int var2 = this.getOtsParameters().getType();
      if (var1.getOtsSignature().getType().getType() != var2) {
         throw new IllegalArgumentException("ots type from lsm signature does not match ots signature type from embedded ots signature");
      } else {
         return new LMOtsPublicKey(LMOtsParameters.getParametersForType(var2), this.I, var1.getQ(), null).createOtsContext(var1);
      }
   }

   @Override
   public boolean verify(LMSContext var1) {
      return LMS.verifySignature(this, var1);
   }
}
