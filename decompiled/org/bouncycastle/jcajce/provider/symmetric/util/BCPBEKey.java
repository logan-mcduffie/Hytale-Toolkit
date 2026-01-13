package org.bouncycastle.jcajce.provider.symmetric.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Destroyable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class BCPBEKey implements PBEKey, Destroyable {
   private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
   String algorithm;
   ASN1ObjectIdentifier oid;
   int type;
   int digest;
   int keySize;
   int ivSize;
   private final char[] password;
   private final byte[] salt;
   private final int iterationCount;
   private final CipherParameters param;
   boolean tryWrong = false;

   public BCPBEKey(String var1, ASN1ObjectIdentifier var2, int var3, int var4, int var5, int var6, PBEKeySpec var7, CipherParameters var8) {
      this.algorithm = var1;
      this.oid = var2;
      this.type = var3;
      this.digest = var4;
      this.keySize = var5;
      this.ivSize = var6;
      this.password = var7.getPassword();
      this.iterationCount = var7.getIterationCount();
      this.salt = var7.getSalt();
      this.param = var8;
   }

   public BCPBEKey(String var1, CipherParameters var2) {
      this.algorithm = var1;
      this.param = var2;
      this.password = null;
      this.iterationCount = -1;
      this.salt = null;
   }

   @Override
   public String getAlgorithm() {
      String var1 = this.algorithm;
      checkDestroyed(this);
      return var1;
   }

   @Override
   public String getFormat() {
      checkDestroyed(this);
      return "RAW";
   }

   @Override
   public byte[] getEncoded() {
      byte[] var1;
      if (this.param != null) {
         KeyParameter var2;
         if (this.param instanceof ParametersWithIV) {
            var2 = (KeyParameter)((ParametersWithIV)this.param).getParameters();
         } else {
            var2 = (KeyParameter)this.param;
         }

         var1 = var2.getKey();
      } else if (this.type == 2) {
         var1 = PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
      } else if (this.type == 5) {
         var1 = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);
      } else {
         var1 = PBEParametersGenerator.PKCS5PasswordToBytes(this.password);
      }

      checkDestroyed(this);
      return var1;
   }

   int getType() {
      int var1 = this.type;
      checkDestroyed(this);
      return var1;
   }

   int getDigest() {
      int var1 = this.digest;
      checkDestroyed(this);
      return var1;
   }

   int getKeySize() {
      int var1 = this.keySize;
      checkDestroyed(this);
      return var1;
   }

   public int getIvSize() {
      int var1 = this.ivSize;
      checkDestroyed(this);
      return var1;
   }

   public CipherParameters getParam() {
      CipherParameters var1 = this.param;
      checkDestroyed(this);
      return var1;
   }

   @Override
   public char[] getPassword() {
      char[] var1 = Arrays.clone(this.password);
      checkDestroyed(this);
      if (var1 == null) {
         throw new IllegalStateException("no password available");
      } else {
         return var1;
      }
   }

   @Override
   public byte[] getSalt() {
      byte[] var1 = Arrays.clone(this.salt);
      checkDestroyed(this);
      return var1;
   }

   @Override
   public int getIterationCount() {
      int var1 = this.iterationCount;
      checkDestroyed(this);
      return var1;
   }

   public ASN1ObjectIdentifier getOID() {
      ASN1ObjectIdentifier var1 = this.oid;
      checkDestroyed(this);
      return var1;
   }

   public void setTryWrongPKCS12Zero(boolean var1) {
      this.tryWrong = var1;
   }

   boolean shouldTryWrongPKCS12() {
      return this.tryWrong;
   }

   @Override
   public void destroy() {
      if (!this.hasBeenDestroyed.getAndSet(true)) {
         if (this.password != null) {
            Arrays.fill(this.password, '\u0000');
         }

         if (this.salt != null) {
            Arrays.fill(this.salt, (byte)0);
         }
      }
   }

   @Override
   public boolean isDestroyed() {
      return this.hasBeenDestroyed.get();
   }

   static void checkDestroyed(Destroyable var0) {
      if (var0.isDestroyed()) {
         throw new IllegalStateException("key has been destroyed");
      }
   }
}
