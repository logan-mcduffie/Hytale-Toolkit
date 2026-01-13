package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaEACSignerBuilder {
   private static final Hashtable sigNames = new Hashtable();
   private EACHelper helper = new DefaultEACHelper();

   public JcaEACSignerBuilder setProvider(String var1) {
      this.helper = new NamedEACHelper(var1);
      return this;
   }

   public JcaEACSignerBuilder setProvider(Provider var1) {
      this.helper = new ProviderEACHelper(var1);
      return this;
   }

   public EACSigner build(String var1, PrivateKey var2) throws OperatorCreationException {
      return this.build((ASN1ObjectIdentifier)sigNames.get(var1), var2);
   }

   public EACSigner build(final ASN1ObjectIdentifier var1, PrivateKey var2) throws OperatorCreationException {
      Signature var3;
      try {
         var3 = this.helper.getSignature(var1);
         var3.initSign(var2);
      } catch (NoSuchAlgorithmException var5) {
         throw new OperatorCreationException("unable to find algorithm: " + var5.getMessage(), var5);
      } catch (NoSuchProviderException var6) {
         throw new OperatorCreationException("unable to find provider: " + var6.getMessage(), var6);
      } catch (InvalidKeyException var7) {
         throw new OperatorCreationException("invalid key: " + var7.getMessage(), var7);
      }

      final JcaEACSignerBuilder.SignatureOutputStream var4 = new JcaEACSignerBuilder.SignatureOutputStream(var3);
      return new EACSigner() {
         @Override
         public ASN1ObjectIdentifier getUsageIdentifier() {
            return var1;
         }

         @Override
         public OutputStream getOutputStream() {
            return var4;
         }

         @Override
         public byte[] getSignature() {
            try {
               byte[] var1x = var4.getSignature();
               return var1.on(EACObjectIdentifiers.id_TA_ECDSA) ? JcaEACSignerBuilder.reencode(var1x) : var1x;
            } catch (SignatureException var2x) {
               throw new RuntimeOperatorException("exception obtaining signature: " + var2x.getMessage(), var2x);
            }
         }
      };
   }

   public static int max(int var0, int var1) {
      return var0 > var1 ? var0 : var1;
   }

   private static byte[] reencode(byte[] var0) {
      ASN1Sequence var1 = ASN1Sequence.getInstance(var0);
      BigInteger var2 = ASN1Integer.getInstance(var1.getObjectAt(0)).getValue();
      BigInteger var3 = ASN1Integer.getInstance(var1.getObjectAt(1)).getValue();
      byte[] var4 = var2.toByteArray();
      byte[] var5 = var3.toByteArray();
      int var6 = unsignedIntLength(var4);
      int var7 = unsignedIntLength(var5);
      int var9 = max(var6, var7);
      if ((var9 & 1) != 0) {
         var9++;
      }

      byte[] var8 = new byte[var9 * 2];
      Arrays.fill(var8, (byte)0);
      copyUnsignedInt(var4, var8, var9 - var6);
      copyUnsignedInt(var5, var8, 2 * var9 - var7);
      return var8;
   }

   private static int unsignedIntLength(byte[] var0) {
      int var1 = var0.length;
      if (var0[0] == 0) {
         var1--;
      }

      return var1;
   }

   private static void copyUnsignedInt(byte[] var0, byte[] var1, int var2) {
      int var3 = var0.length;
      byte var4 = 0;
      if (var0[0] == 0) {
         var3--;
         var4 = 1;
      }

      System.arraycopy(var0, var4, var1, var2, var3);
   }

   static {
      sigNames.put("SHA1withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1);
      sigNames.put("SHA256withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256);
      sigNames.put("SHA1withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1);
      sigNames.put("SHA256withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256);
      sigNames.put("SHA512withRSA", EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_512);
      sigNames.put("SHA512withRSAandMGF1", EACObjectIdentifiers.id_TA_RSA_PSS_SHA_512);
      sigNames.put("SHA1withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_1);
      sigNames.put("SHA224withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_224);
      sigNames.put("SHA256withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_256);
      sigNames.put("SHA384withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_384);
      sigNames.put("SHA512withECDSA", EACObjectIdentifiers.id_TA_ECDSA_SHA_512);
   }

   private static class SignatureOutputStream extends OutputStream {
      private Signature sig;

      SignatureOutputStream(Signature var1) {
         this.sig = var1;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         try {
            this.sig.update(var1, var2, var3);
         } catch (SignatureException var5) {
            throw new OperatorStreamException("exception in content signer: " + var5.getMessage(), var5);
         }
      }

      @Override
      public void write(byte[] var1) throws IOException {
         try {
            this.sig.update(var1);
         } catch (SignatureException var3) {
            throw new OperatorStreamException("exception in content signer: " + var3.getMessage(), var3);
         }
      }

      @Override
      public void write(int var1) throws IOException {
         try {
            this.sig.update((byte)var1);
         } catch (SignatureException var3) {
            throw new OperatorStreamException("exception in content signer: " + var3.getMessage(), var3);
         }
      }

      byte[] getSignature() throws SignatureException {
         return this.sig.sign();
      }
   }
}
