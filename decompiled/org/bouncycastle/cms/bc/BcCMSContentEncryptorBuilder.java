package org.bouncycastle.cms.bc;

import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.CipherFactory;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCaptureStream;
import org.bouncycastle.operator.OutputAEADEncryptor;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.SecretKeySizeProvider;

public class BcCMSContentEncryptorBuilder {
   private static final SecretKeySizeProvider KEY_SIZE_PROVIDER = DefaultSecretKeySizeProvider.INSTANCE;
   private final ASN1ObjectIdentifier encryptionOID;
   private final int keySize;
   private EnvelopedDataHelper helper = new EnvelopedDataHelper();
   private SecureRandom random;

   public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier var1) {
      this(var1, KEY_SIZE_PROVIDER.getKeySize(var1));
   }

   public BcCMSContentEncryptorBuilder(ASN1ObjectIdentifier var1, int var2) {
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

   public BcCMSContentEncryptorBuilder setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public OutputEncryptor build() throws CMSException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      CipherKeyGenerator var1 = this.helper.createKeyGenerator(this.encryptionOID, this.keySize, this.random);
      return this.build(var1.generateKey());
   }

   public OutputEncryptor build(byte[] var1) throws CMSException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      if (this.keySize > 0 && (this.keySize + 7) / 8 != var1.length && this.keySize != 56 && var1.length != 8 && this.keySize != 168 && var1.length != 24) {
         throw new IllegalArgumentException("attempt to create encryptor with the wrong sized key");
      } else {
         return (OutputEncryptor)(this.helper.isAuthEnveloped(this.encryptionOID)
            ? new BcCMSContentEncryptorBuilder.CMSAuthOutputEncryptor(this.encryptionOID, new KeyParameter(var1), this.random)
            : new BcCMSContentEncryptorBuilder.CMSOutputEncryptor(this.encryptionOID, new KeyParameter(var1), this.random));
      }
   }

   private static class AADStream extends OutputStream {
      private AEADBlockCipher cipher;

      public AADStream(AEADBlockCipher var1) {
         this.cipher = var1;
      }

      @Override
      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.cipher.processAADBytes(var1, var2, var3);
      }

      @Override
      public void write(int var1) throws IOException {
         this.cipher.processAADByte((byte)var1);
      }
   }

   private class CMSAuthOutputEncryptor extends BcCMSContentEncryptorBuilder.CMSOutputEncryptor implements OutputAEADEncryptor {
      private AEADBlockCipher aeadCipher = this.getCipher();
      private MacCaptureStream macOut;

      CMSAuthOutputEncryptor(ASN1ObjectIdentifier nullx, KeyParameter nullxx, SecureRandom nullxxx) throws CMSException {
         super(nullx, nullxx, nullxxx);
      }

      private AEADBlockCipher getCipher() {
         if (!(this.cipher instanceof AEADBlockCipher)) {
            throw new IllegalArgumentException("Unable to create Authenticated Output Encryptor without Authenticaed Data cipher!");
         } else {
            return (AEADBlockCipher)this.cipher;
         }
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         this.macOut = new MacCaptureStream(var1, this.aeadCipher.getMac().length);
         return CipherFactory.createOutputStream(this.macOut, this.cipher);
      }

      @Override
      public OutputStream getAADStream() {
         return new BcCMSContentEncryptorBuilder.AADStream(this.aeadCipher);
      }

      @Override
      public byte[] getMAC() {
         return this.macOut.getMac();
      }
   }

   private class CMSOutputEncryptor implements OutputEncryptor {
      private KeyParameter encKey;
      private AlgorithmIdentifier algorithmIdentifier;
      protected Object cipher;

      CMSOutputEncryptor(ASN1ObjectIdentifier nullx, KeyParameter nullxx, SecureRandom nullxxx) throws CMSException {
         this.algorithmIdentifier = BcCMSContentEncryptorBuilder.this.helper.generateEncryptionAlgID(nullx, nullxx, nullxxx);
         this.encKey = nullxx;
         this.cipher = EnvelopedDataHelper.createContentCipher(true, nullxx, this.algorithmIdentifier);
      }

      @Override
      public AlgorithmIdentifier getAlgorithmIdentifier() {
         return this.algorithmIdentifier;
      }

      @Override
      public OutputStream getOutputStream(OutputStream var1) {
         return CipherFactory.createOutputStream(var1, this.cipher);
      }

      @Override
      public GenericKey getKey() {
         return new GenericKey(this.algorithmIdentifier, this.encKey.getKey());
      }
   }
}
