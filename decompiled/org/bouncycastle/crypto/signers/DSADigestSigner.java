package org.bouncycastle.crypto.signers;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.DSAExt;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;

public class DSADigestSigner implements Signer {
   private final DSA dsa;
   private final Digest digest;
   private final DSAEncoding encoding;
   private boolean forSigning;

   public DSADigestSigner(DSA var1, Digest var2) {
      this.dsa = var1;
      this.digest = var2;
      this.encoding = StandardDSAEncoding.INSTANCE;
   }

   public DSADigestSigner(DSAExt var1, Digest var2, DSAEncoding var3) {
      this.dsa = var1;
      this.digest = var2;
      this.encoding = var3;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forSigning = var1;
      AsymmetricKeyParameter var3;
      if (var2 instanceof ParametersWithRandom) {
         var3 = (AsymmetricKeyParameter)((ParametersWithRandom)var2).getParameters();
      } else {
         var3 = (AsymmetricKeyParameter)var2;
      }

      if (var1 && !var3.isPrivate()) {
         throw new IllegalArgumentException("Signing Requires Private Key.");
      } else if (!var1 && var3.isPrivate()) {
         throw new IllegalArgumentException("Verification Requires Public Key.");
      } else {
         this.reset();
         this.dsa.init(var1, var2);
      }
   }

   @Override
   public void update(byte var1) {
      this.digest.update(var1);
   }

   @Override
   public void update(byte[] var1, int var2, int var3) {
      this.digest.update(var1, var2, var3);
   }

   @Override
   public byte[] generateSignature() {
      if (!this.forSigning) {
         throw new IllegalStateException("DSADigestSigner not initialised for signature generation.");
      } else {
         byte[] var1 = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(var1, 0);
         BigInteger[] var2 = this.dsa.generateSignature(var1);

         try {
            return this.encoding.encode(this.getOrder(), var2[0], var2[1]);
         } catch (Exception var4) {
            throw new IllegalStateException("unable to encode signature");
         }
      }
   }

   @Override
   public boolean verifySignature(byte[] var1) {
      if (this.forSigning) {
         throw new IllegalStateException("DSADigestSigner not initialised for verification");
      } else {
         byte[] var2 = new byte[this.digest.getDigestSize()];
         this.digest.doFinal(var2, 0);

         try {
            BigInteger[] var3 = this.encoding.decode(this.getOrder(), var1);
            return this.dsa.verifySignature(var2, var3[0], var3[1]);
         } catch (Exception var4) {
            return false;
         }
      }
   }

   @Override
   public void reset() {
      this.digest.reset();
   }

   protected BigInteger getOrder() {
      return this.dsa instanceof DSAExt ? ((DSAExt)this.dsa).getOrder() : null;
   }
}
