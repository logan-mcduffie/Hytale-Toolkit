package org.bouncycastle.cert.crmf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

public class EncryptedValueParser {
   private EncryptedValue value;
   private EncryptedValuePadder padder;

   public EncryptedValueParser(EncryptedValue var1) {
      this.value = var1;
   }

   public EncryptedValueParser(EncryptedValue var1, EncryptedValuePadder var2) {
      this.value = var1;
      this.padder = var2;
   }

   public AlgorithmIdentifier getIntendedAlg() {
      return this.value.getIntendedAlg();
   }

   private byte[] decryptValue(ValueDecryptorGenerator var1) throws CRMFException {
      if (this.value.getValueHint() != null) {
         throw new UnsupportedOperationException();
      } else {
         InputDecryptor var2 = var1.getValueDecryptor(this.value.getKeyAlg(), this.value.getSymmAlg(), this.value.getEncSymmKey().getBytes());
         InputStream var3 = var2.getInputStream(new ByteArrayInputStream(this.value.getEncValue().getBytes()));

         try {
            return this.unpadData(Streams.readAll(var3));
         } catch (IOException var5) {
            throw new CRMFException("Cannot parse decrypted data: " + var5.getMessage(), var5);
         }
      }
   }

   public X509CertificateHolder readCertificateHolder(ValueDecryptorGenerator var1) throws CRMFException {
      return new X509CertificateHolder(Certificate.getInstance(this.decryptValue(var1)));
   }

   public PrivateKeyInfo readPrivateKeyInfo(ValueDecryptorGenerator var1) throws CRMFException {
      return PrivateKeyInfo.getInstance(this.decryptValue(var1));
   }

   public char[] readPassphrase(ValueDecryptorGenerator var1) throws CRMFException {
      return Strings.fromUTF8ByteArray(this.decryptValue(var1)).toCharArray();
   }

   private byte[] unpadData(byte[] var1) {
      return this.padder != null ? this.padder.getUnpaddedData(var1) : var1;
   }
}
