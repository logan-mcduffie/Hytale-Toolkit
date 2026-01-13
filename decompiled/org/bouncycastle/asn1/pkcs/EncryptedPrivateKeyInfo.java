package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedPrivateKeyInfo extends ASN1Object {
   private AlgorithmIdentifier algId;
   private ASN1OctetString data;

   private EncryptedPrivateKeyInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.algId = AlgorithmIdentifier.getInstance(var2.nextElement());
      this.data = ASN1OctetString.getInstance(var2.nextElement());
   }

   public EncryptedPrivateKeyInfo(AlgorithmIdentifier var1, byte[] var2) {
      this.algId = var1;
      this.data = new DEROctetString(Arrays.clone(var2));
   }

   public static EncryptedPrivateKeyInfo getInstance(Object var0) {
      if (var0 instanceof EncryptedPrivateKeyInfo) {
         return (EncryptedPrivateKeyInfo)var0;
      } else {
         return var0 != null ? new EncryptedPrivateKeyInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getEncryptionAlgorithm() {
      return this.algId;
   }

   public byte[] getEncryptedData() {
      return Arrays.clone(this.data.getOctets());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algId, this.data);
   }
}
