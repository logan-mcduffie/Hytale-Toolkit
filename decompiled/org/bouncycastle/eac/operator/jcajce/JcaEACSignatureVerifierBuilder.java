package org.bouncycastle.eac.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.eac.operator.EACSignatureVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

public class JcaEACSignatureVerifierBuilder {
   private EACHelper helper = new DefaultEACHelper();

   public JcaEACSignatureVerifierBuilder setProvider(String var1) {
      this.helper = new NamedEACHelper(var1);
      return this;
   }

   public JcaEACSignatureVerifierBuilder setProvider(Provider var1) {
      this.helper = new ProviderEACHelper(var1);
      return this;
   }

   public EACSignatureVerifier build(final ASN1ObjectIdentifier var1, PublicKey var2) throws OperatorCreationException {
      Signature var3;
      try {
         var3 = this.helper.getSignature(var1);
         var3.initVerify(var2);
      } catch (NoSuchAlgorithmException var5) {
         throw new OperatorCreationException("unable to find algorithm: " + var5.getMessage(), var5);
      } catch (NoSuchProviderException var6) {
         throw new OperatorCreationException("unable to find provider: " + var6.getMessage(), var6);
      } catch (InvalidKeyException var7) {
         throw new OperatorCreationException("invalid key: " + var7.getMessage(), var7);
      }

      final JcaEACSignatureVerifierBuilder.SignatureOutputStream var4 = new JcaEACSignatureVerifierBuilder.SignatureOutputStream(var3);
      return new EACSignatureVerifier() {
         @Override
         public ASN1ObjectIdentifier getUsageIdentifier() {
            return var1;
         }

         @Override
         public OutputStream getOutputStream() {
            return var4;
         }

         @Override
         public boolean verify(byte[] var1x) {
            try {
               if (var1.on(EACObjectIdentifiers.id_TA_ECDSA)) {
                  try {
                     byte[] var2x = JcaEACSignatureVerifierBuilder.derEncode(var1x);
                     return var4.verify(var2x);
                  } catch (Exception var3x) {
                     return false;
                  }
               } else {
                  return var4.verify(var1x);
               }
            } catch (SignatureException var4x) {
               throw new RuntimeOperatorException("exception obtaining signature: " + var4x.getMessage(), var4x);
            }
         }
      };
   }

   private static byte[] derEncode(byte[] var0) throws IOException {
      int var1 = var0.length / 2;
      byte[] var2 = new byte[var1];
      byte[] var3 = new byte[var1];
      System.arraycopy(var0, 0, var2, 0, var1);
      System.arraycopy(var0, var1, var3, 0, var1);
      ASN1EncodableVector var4 = new ASN1EncodableVector();
      var4.add(new ASN1Integer(new BigInteger(1, var2)));
      var4.add(new ASN1Integer(new BigInteger(1, var3)));
      DERSequence var5 = new DERSequence(var4);
      return var5.getEncoded();
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

      boolean verify(byte[] var1) throws SignatureException {
         return this.sig.verify(var1);
      }
   }
}
