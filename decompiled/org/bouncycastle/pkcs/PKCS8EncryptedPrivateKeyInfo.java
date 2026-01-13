package org.bouncycastle.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.util.io.Streams;

public class PKCS8EncryptedPrivateKeyInfo {
   private EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;

   private static EncryptedPrivateKeyInfo parseBytes(byte[] var0) throws IOException {
      try {
         return EncryptedPrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(var0));
      } catch (ClassCastException var2) {
         throw new PKCSIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new PKCSIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo var1) {
      this.encryptedPrivateKeyInfo = var1;
   }

   public PKCS8EncryptedPrivateKeyInfo(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public AlgorithmIdentifier getEncryptionAlgorithm() {
      return this.encryptedPrivateKeyInfo.getEncryptionAlgorithm();
   }

   public byte[] getEncryptedData() {
      return this.encryptedPrivateKeyInfo.getEncryptedData();
   }

   public EncryptedPrivateKeyInfo toASN1Structure() {
      return this.encryptedPrivateKeyInfo;
   }

   public byte[] getEncoded() throws IOException {
      return this.encryptedPrivateKeyInfo.getEncoded();
   }

   public PrivateKeyInfo decryptPrivateKeyInfo(InputDecryptorProvider var1) throws PKCSException {
      try {
         InputDecryptor var2 = var1.get(this.encryptedPrivateKeyInfo.getEncryptionAlgorithm());
         ByteArrayInputStream var3 = new ByteArrayInputStream(this.encryptedPrivateKeyInfo.getEncryptedData());
         return PrivateKeyInfo.getInstance(Streams.readAll(var2.getInputStream(var3)));
      } catch (Exception var4) {
         throw new PKCSException("unable to read encrypted data: " + var4.getMessage(), var4);
      }
   }
}
