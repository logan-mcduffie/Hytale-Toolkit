package org.bouncycastle.jcajce;

import java.security.PublicKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;

public class MLDSAProxyPrivateKey implements MLDSAPrivateKey {
   private final MLDSAPublicKey publicKey;

   public MLDSAProxyPrivateKey(PublicKey var1) {
      if (!(var1 instanceof MLDSAPublicKey)) {
         throw new IllegalArgumentException("public key must be an ML-DSA public key");
      } else {
         this.publicKey = (MLDSAPublicKey)var1;
      }
   }

   @Override
   public MLDSAPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Override
   public String getAlgorithm() {
      return this.publicKey.getAlgorithm();
   }

   @Override
   public String getFormat() {
      return null;
   }

   @Override
   public byte[] getEncoded() {
      return new byte[0];
   }

   @Override
   public MLDSAParameterSpec getParameterSpec() {
      return this.publicKey.getParameterSpec();
   }

   @Override
   public byte[] getPrivateData() {
      return new byte[0];
   }

   @Override
   public byte[] getSeed() {
      return new byte[0];
   }

   @Override
   public MLDSAPrivateKey getPrivateKey(boolean var1) {
      return null;
   }
}
