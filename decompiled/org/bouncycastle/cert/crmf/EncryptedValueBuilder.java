package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfoBuilder;
import org.bouncycastle.util.Strings;

public class EncryptedValueBuilder {
   private KeyWrapper wrapper;
   private OutputEncryptor encryptor;
   private EncryptedValuePadder padder;

   public EncryptedValueBuilder(KeyWrapper var1, OutputEncryptor var2) {
      this(var1, var2, null);
   }

   public EncryptedValueBuilder(KeyWrapper var1, OutputEncryptor var2, EncryptedValuePadder var3) {
      this.wrapper = var1;
      this.encryptor = var2;
      this.padder = var3;
   }

   public EncryptedValue build(char[] var1) throws CRMFException {
      return this.encryptData(this.padData(Strings.toUTF8ByteArray(var1)));
   }

   public EncryptedValue build(X509CertificateHolder var1) throws CRMFException {
      try {
         return this.encryptData(this.padData(var1.getEncoded()));
      } catch (IOException var3) {
         throw new CRMFException("cannot encode certificate: " + var3.getMessage(), var3);
      }
   }

   public EncryptedValue build(PrivateKeyInfo var1) throws CRMFException {
      PKCS8EncryptedPrivateKeyInfoBuilder var2 = new PKCS8EncryptedPrivateKeyInfoBuilder(var1);
      AlgorithmIdentifier var3 = var1.getPrivateKeyAlgorithm();
      AlgorithmIdentifier var4 = this.encryptor.getAlgorithmIdentifier();

      try {
         PKCS8EncryptedPrivateKeyInfo var6 = var2.build(this.encryptor);
         DERBitString var5 = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
         AlgorithmIdentifier var7 = this.wrapper.getAlgorithmIdentifier();
         Object var8 = null;
         return new EncryptedValue(var3, var4, var5, var7, (ASN1OctetString)var8, new DERBitString(var6.getEncryptedData()));
      } catch (IllegalStateException var9) {
         throw new CRMFException("cannot encode key: " + var9.getMessage(), var9);
      } catch (OperatorException var10) {
         throw new CRMFException("cannot wrap key: " + var10.getMessage(), var10);
      }
   }

   private EncryptedValue encryptData(byte[] var1) throws CRMFException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      OutputStream var3 = this.encryptor.getOutputStream(var2);

      try {
         var3.write(var1);
         var3.close();
      } catch (IOException var11) {
         throw new CRMFException("cannot process data: " + var11.getMessage(), var11);
      }

      Object var4 = null;
      AlgorithmIdentifier var5 = this.encryptor.getAlgorithmIdentifier();

      DERBitString var6;
      try {
         this.wrapper.generateWrappedKey(this.encryptor.getKey());
         var6 = new DERBitString(this.wrapper.generateWrappedKey(this.encryptor.getKey()));
      } catch (OperatorException var10) {
         throw new CRMFException("cannot wrap key: " + var10.getMessage(), var10);
      }

      AlgorithmIdentifier var7 = this.wrapper.getAlgorithmIdentifier();
      Object var8 = null;
      DERBitString var9 = new DERBitString(var2.toByteArray());
      return new EncryptedValue((AlgorithmIdentifier)var4, var5, var6, var7, (ASN1OctetString)var8, var9);
   }

   private byte[] padData(byte[] var1) {
      return this.padder != null ? this.padder.getPaddedData(var1) : var1;
   }
}
