package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.cms.CMSEncryptedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.operator.OutputEncryptor;

public class PKCS12PfxPduBuilder {
   private ASN1EncodableVector dataVector = new ASN1EncodableVector();

   public PKCS12PfxPduBuilder addData(PKCS12SafeBag var1) throws IOException {
      this.dataVector.add(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DLSequence(var1.toASN1Structure()).getEncoded())));
      return this;
   }

   public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor var1, PKCS12SafeBag var2) throws IOException {
      return this.addEncryptedData(var1, new DERSequence(var2.toASN1Structure()));
   }

   public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor var1, PKCS12SafeBag[] var2) throws IOException {
      ASN1EncodableVector var3 = new ASN1EncodableVector();

      for (int var4 = 0; var4 != var2.length; var4++) {
         var3.add(var2[var4].toASN1Structure());
      }

      return this.addEncryptedData(var1, new DLSequence(var3));
   }

   private PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor var1, ASN1Sequence var2) throws IOException {
      CMSEncryptedDataGenerator var3 = new CMSEncryptedDataGenerator();

      try {
         this.dataVector.add(var3.generate(new CMSProcessableByteArray(var2.getEncoded()), var1).toASN1Structure());
         return this;
      } catch (CMSException var5) {
         throw new PKCSIOException(var5.getMessage(), var5.getCause());
      }
   }

   public PKCS12PfxPdu build(PKCS12MacCalculatorBuilder var1, char[] var2) throws PKCSException {
      AuthenticatedSafe var3 = AuthenticatedSafe.getInstance(new DLSequence(this.dataVector));

      byte[] var4;
      try {
         var4 = var3.getEncoded();
      } catch (IOException var8) {
         throw new PKCSException("unable to encode AuthenticatedSafe: " + var8.getMessage(), var8);
      }

      ContentInfo var5 = new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(var4));
      MacData var6 = null;
      if (var1 != null) {
         MacDataGenerator var7 = new MacDataGenerator(var1);
         var6 = var7.build(var2, var4);
      }

      Pfx var9 = new Pfx(var5, var6);
      return new PKCS12PfxPdu(var9);
   }
}
