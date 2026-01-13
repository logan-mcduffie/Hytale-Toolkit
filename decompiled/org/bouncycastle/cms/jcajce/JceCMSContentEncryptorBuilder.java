package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.GCMParameters;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Strings;

public class JceCMSContentEncryptorBuilder {
   private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
   private static final byte[] hkdfSalt = Strings.toByteArray("The Cryptographic Message Syntax");
   private final ASN1ObjectIdentifier encryptionOID;
   private final int keySize;
   private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   private SecureRandom random;
   private AlgorithmIdentifier algorithmIdentifier;
   private AlgorithmParameters algorithmParameters;
   private ASN1ObjectIdentifier kdfAlgorithm;

   public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier var1) {
      this(var1, KEY_SIZE_PROVIDER.getKeySize(var1));
   }

   public JceCMSContentEncryptorBuilder(ASN1ObjectIdentifier var1, int var2) {
      this.encryptionOID = var1;
      int var3 = KEY_SIZE_PROVIDER.getKeySize(var1);
      if (var1.equals(PKCSObjectIdentifiers.des_EDE3_CBC)) {
         if (var2 != 168 && var2 != var3) {
            throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
         }

         this.keySize = 168;
      } else if (var1.equals(OIWObjectIdentifiers.desCBC)) {
         if (var2 != 56 && var2 != var3) {
            throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
         }

         this.keySize = 56;
      } else {
         if (var3 > 0 && var3 != var2) {
            throw new IllegalArgumentException("incorrect keySize for encryptionOID passed to builder.");
         }

         this.keySize = var2;
      }
   }

   public JceCMSContentEncryptorBuilder(AlgorithmIdentifier var1) {
      this(var1.getAlgorithm(), KEY_SIZE_PROVIDER.getKeySize(var1.getAlgorithm()));
      this.algorithmIdentifier = var1;
   }

   public JceCMSContentEncryptorBuilder setEnableSha256HKdf(boolean var1) {
      if (var1) {
         this.kdfAlgorithm = CMSObjectIdentifiers.id_alg_cek_hkdf_sha256;
      } else if (this.kdfAlgorithm != null) {
         if (!this.kdfAlgorithm.equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
            throw new IllegalStateException("SHA256 HKDF not enabled");
         }

         this.kdfAlgorithm = null;
      }

      return this;
   }

   public JceCMSContentEncryptorBuilder setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceCMSContentEncryptorBuilder setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public JceCMSContentEncryptorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JceCMSContentEncryptorBuilder setAlgorithmParameters(AlgorithmParameters var1) {
      this.algorithmParameters = var1;
      return this;
   }

   public OutputEncryptor build() throws CMSException {
      KeyGenerator var1 = this.helper.createKeyGenerator(this.encryptionOID);
      this.random = CryptoServicesRegistrar.getSecureRandom(this.random);
      if (this.keySize < 0) {
         var1.init(this.random);
      } else {
         var1.init(this.keySize, this.random);
      }

      return this.build(var1.generateKey());
   }

   public OutputEncryptor build(byte[] var1) throws CMSException {
      SecretKeySpec var2 = new SecretKeySpec(var1, this.helper.getBaseCipherName(this.encryptionOID));
      return this.build(var2);
   }

   public OutputEncryptor build(SecretKey var1) throws CMSException {
      if (this.algorithmParameters != null) {
         return (OutputEncryptor)(this.helper.isAuthEnveloped(this.encryptionOID)
            ? new JceCMSContentEncryptorBuilder.CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, var1, this.algorithmParameters, this.random)
            : new JceCMSContentEncryptorBuilder.CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, var1, this.algorithmParameters, this.random));
      } else {
         if (this.algorithmIdentifier != null) {
            ASN1Encodable var2 = this.algorithmIdentifier.getParameters();
            if (var2 != null && !var2.equals(DERNull.INSTANCE)) {
               try {
                  this.algorithmParameters = this.helper.createAlgorithmParameters(this.algorithmIdentifier.getAlgorithm());
                  this.algorithmParameters.init(var2.toASN1Primitive().getEncoded());
               } catch (Exception var4) {
                  throw new CMSException("unable to process provided algorithmIdentifier: " + var4.toString(), var4);
               }
            }
         }

         return (OutputEncryptor)(this.helper.isAuthEnveloped(this.encryptionOID)
            ? new JceCMSContentEncryptorBuilder.CMSAuthOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, var1, this.algorithmParameters, this.random)
            : new JceCMSContentEncryptorBuilder.CMSOutputEncryptor(this.kdfAlgorithm, this.encryptionOID, var1, this.algorithmParameters, this.random));
      }
   }

   private static boolean checkForAEAD() {
      return AccessController.<Boolean>doPrivileged(new PrivilegedAction() {
         @Override
         public Object run() {
            try {
               return Cipher.class.getMethod("updateAAD", byte[].class) != null;
            } catch (Exception var2) {
               return Boolean.FALSE;
            }
         }
      });
   }

   private class CMSAuthOutputEncryptor extends JceCMSContentEncryptorBuilder.CMSOutEncryptor implements OutputAEADEncryptor {
      private MacCaptureStream macOut;

      CMSAuthOutputEncryptor(ASN1ObjectIdentifier nullx, ASN1ObjectIdentifier nullxx, SecretKey nullxxx, AlgorithmParameters nullxxxx, SecureRandom nullxxxxx) throws CMSException {
         this.init(nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         AlgorithmIdentifier var2;
         if (JceCMSContentEncryptorBuilder.this.kdfAlgorithm != null) {
            var2 = AlgorithmIdentifier.getInstance(this.algorithmIdentifier.getParameters());
         } else {
            var2 = this.algorithmIdentifier;
         }

         if (CMSAlgorithm.ChaCha20Poly1305.equals(this.algorithmIdentifier.getAlgorithm())) {
            this.macOut = new MacCaptureStream(var1, 16);
         } else {
            GCMParameters var3 = GCMParameters.getInstance(var2.getParameters());
            this.macOut = new MacCaptureStream(var1, var3.getIcvLen());
         }

         return new CipherOutputStream(this.macOut, this.cipher);
      }

      @Override
      public GenericKey getKey() {
         return new JceGenericKey(this.algorithmIdentifier, this.encKey);
      }

      @Override
      public OutputStream getAADStream() {
         return JceCMSContentEncryptorBuilder.checkForAEAD() ? new JceAADStream(this.cipher) : null;
      }

      @Override
      public byte[] getMAC() {
         return this.macOut.getMac();
      }
   }

   private class CMSOutEncryptor {
      protected SecretKey encKey;
      protected AlgorithmIdentifier algorithmIdentifier;
      protected Cipher cipher;

      private CMSOutEncryptor() {
      }

      private void applyKdf(ASN1ObjectIdentifier var1, AlgorithmParameters var2, SecureRandom var3) throws CMSException {
         HKDFBytesGenerator var4 = new HKDFBytesGenerator(new SHA256Digest());
         byte[] var5 = this.encKey.getEncoded();

         try {
            var4.init(new HKDFParameters(var5, JceCMSContentEncryptorBuilder.hkdfSalt, this.algorithmIdentifier.getEncoded("DER")));
         } catch (IOException var9) {
            throw new CMSException("unable to encode enc algorithm parameters", var9);
         }

         var4.generateBytes(var5, 0, var5.length);
         SecretKeySpec var6 = new SecretKeySpec(var5, this.encKey.getAlgorithm());

         try {
            this.cipher.init(1, var6, var2, var3);
         } catch (GeneralSecurityException var8) {
            throw new CMSException("unable to initialize cipher: " + var8.getMessage(), var8);
         }

         this.algorithmIdentifier = new AlgorithmIdentifier(var1, this.algorithmIdentifier);
      }

      protected void init(ASN1ObjectIdentifier var1, ASN1ObjectIdentifier var2, SecretKey var3, AlgorithmParameters var4, SecureRandom var5) throws CMSException {
         this.encKey = var3;
         var5 = CryptoServicesRegistrar.getSecureRandom(var5);
         this.cipher = JceCMSContentEncryptorBuilder.this.helper.createCipher(var2);
         if (var4 == null) {
            var4 = JceCMSContentEncryptorBuilder.this.helper.generateParameters(var2, var3, var5);
         }

         if (var4 != null) {
            this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(var2, var4);
            if (var1 != null) {
               this.applyKdf(var1, var4, var5);
            } else {
               try {
                  this.cipher.init(1, var3, var4, var5);
               } catch (GeneralSecurityException var8) {
                  throw new CMSException("unable to initialize cipher: " + var8.getMessage(), var8);
               }
            }
         } else {
            try {
               this.cipher.init(1, var3, var4, var5);
            } catch (GeneralSecurityException var7) {
               throw new CMSException("unable to initialize cipher: " + var7.getMessage(), var7);
            }

            var4 = this.cipher.getParameters();
            this.algorithmIdentifier = JceCMSContentEncryptorBuilder.this.helper.getAlgorithmIdentifier(var2, var4);
            if (var1 != null) {
               this.applyKdf(var1, var4, var5);
            }
         }
      }
   }

   private class CMSOutputEncryptor extends JceCMSContentEncryptorBuilder.CMSOutEncryptor implements OutputEncryptor {
      CMSOutputEncryptor(ASN1ObjectIdentifier nullx, ASN1ObjectIdentifier nullxx, SecretKey nullxxx, AlgorithmParameters nullxxxx, SecureRandom nullxxxxx) throws CMSException {
         this.init(nullx, nullxx, nullxxx, nullxxxx, nullxxxxx);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         return new CipherOutputStream(var1, this.cipher);
      }

      @Override
      public GenericKey getKey() {
         return new JceGenericKey(this.algorithmIdentifier, this.encKey);
      }
   }
}
