package org.bouncycastle.cms;

import java.io.IOException;
import java.util.List;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Arrays;

public class KeyAgreeRecipientInformation extends RecipientInformation {
   private KeyAgreeRecipientInfo info;
   private ASN1OctetString encryptedKey;

   static void readRecipientInfo(List var0, KeyAgreeRecipientInfo var1, AlgorithmIdentifier var2, CMSSecureReadable var3) {
      ASN1Sequence var4 = var1.getRecipientEncryptedKeys();

      for (int var5 = 0; var5 < var4.size(); var5++) {
         RecipientEncryptedKey var6 = RecipientEncryptedKey.getInstance(var4.getObjectAt(var5));
         KeyAgreeRecipientIdentifier var8 = var6.getIdentifier();
         IssuerAndSerialNumber var9 = var8.getIssuerAndSerialNumber();
         KeyAgreeRecipientId var7;
         if (var9 != null) {
            var7 = new KeyAgreeRecipientId(var9.getName(), var9.getSerialNumber().getValue());
         } else {
            RecipientKeyIdentifier var10 = var8.getRKeyID();
            var7 = new KeyAgreeRecipientId(var10.getSubjectKeyIdentifier().getOctets());
         }

         var0.add(new KeyAgreeRecipientInformation(var1, var7, var6.getEncryptedKey(), var2, var3));
      }
   }

   KeyAgreeRecipientInformation(KeyAgreeRecipientInfo var1, RecipientId var2, ASN1OctetString var3, AlgorithmIdentifier var4, CMSSecureReadable var5) {
      super(var1.getKeyEncryptionAlgorithm(), var4, var5);
      this.info = var1;
      this.rid = var2;
      this.encryptedKey = var3;
   }

   public OriginatorIdentifierOrKey getOriginator() {
      return this.info.getOriginator();
   }

   public byte[] getUserKeyingMaterial() {
      ASN1OctetString var1 = this.info.getUserKeyingMaterial();
      return var1 != null ? Arrays.clone(var1.getOctets()) : null;
   }

   private SubjectPublicKeyInfo getSenderPublicKeyInfo(AlgorithmIdentifier var1, OriginatorIdentifierOrKey var2) throws CMSException, IOException {
      OriginatorPublicKey var3 = var2.getOriginatorKey();
      if (var3 != null) {
         return this.getPublicKeyInfoFromOriginatorPublicKey(var1, var3);
      } else {
         IssuerAndSerialNumber var5 = var2.getIssuerAndSerialNumber();
         OriginatorId var4;
         if (var5 != null) {
            var4 = new OriginatorId(var5.getName(), var5.getSerialNumber().getValue());
         } else {
            SubjectKeyIdentifier var6 = var2.getSubjectKeyIdentifier();
            var4 = new OriginatorId(var6.getKeyIdentifier());
         }

         return this.getPublicKeyInfoFromOriginatorId(var4);
      }
   }

   private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(AlgorithmIdentifier var1, OriginatorPublicKey var2) {
      return new SubjectPublicKeyInfo(var1, var2.getPublicKeyData());
   }

   private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(OriginatorId var1) throws CMSException {
      throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
   }

   @Override
   protected RecipientOperator getRecipientOperator(Recipient var1) throws CMSException, IOException {
      KeyAgreeRecipient var2 = (KeyAgreeRecipient)var1;
      AlgorithmIdentifier var3 = var2.getPrivateKeyAlgorithmIdentifier();
      return ((KeyAgreeRecipient)var1)
         .getRecipientOperator(
            this.keyEncAlg,
            this.messageAlgorithm,
            this.getSenderPublicKeyInfo(var3, this.info.getOriginator()),
            this.info.getUserKeyingMaterial(),
            this.encryptedKey.getOctets()
         );
   }
}
