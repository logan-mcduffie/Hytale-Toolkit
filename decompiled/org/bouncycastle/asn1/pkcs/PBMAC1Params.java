package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBMAC1Params extends ASN1Object implements PKCSObjectIdentifiers {
   private AlgorithmIdentifier func;
   private AlgorithmIdentifier scheme;

   public static PBMAC1Params getInstance(Object var0) {
      if (var0 instanceof PBMAC1Params) {
         return (PBMAC1Params)var0;
      } else {
         return var0 != null ? new PBMAC1Params(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PBMAC1Params(AlgorithmIdentifier var1, AlgorithmIdentifier var2) {
      this.func = var1;
      this.scheme = var2;
   }

   private PBMAC1Params(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      ASN1Sequence var3 = ASN1Sequence.getInstance(((ASN1Encodable)var2.nextElement()).toASN1Primitive());
      if (var3.getObjectAt(0).equals(id_PBKDF2)) {
         this.func = new AlgorithmIdentifier(id_PBKDF2, PBKDF2Params.getInstance(var3.getObjectAt(1)));
      } else {
         this.func = AlgorithmIdentifier.getInstance(var3);
      }

      this.scheme = AlgorithmIdentifier.getInstance(var2.nextElement());
   }

   public AlgorithmIdentifier getKeyDerivationFunc() {
      return this.func;
   }

   public AlgorithmIdentifier getMessageAuthScheme() {
      return this.scheme;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.func, this.scheme);
   }
}
