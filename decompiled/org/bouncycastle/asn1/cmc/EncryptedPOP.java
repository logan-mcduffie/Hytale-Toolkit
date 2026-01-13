package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class EncryptedPOP extends ASN1Object {
   private final TaggedRequest request;
   private final ContentInfo cms;
   private final AlgorithmIdentifier thePOPAlgID;
   private final AlgorithmIdentifier witnessAlgID;
   private final byte[] witness;

   private EncryptedPOP(ASN1Sequence var1) {
      if (var1.size() != 5) {
         throw new IllegalArgumentException("incorrect sequence size");
      } else {
         this.request = TaggedRequest.getInstance(var1.getObjectAt(0));
         this.cms = ContentInfo.getInstance(var1.getObjectAt(1));
         this.thePOPAlgID = AlgorithmIdentifier.getInstance(var1.getObjectAt(2));
         this.witnessAlgID = AlgorithmIdentifier.getInstance(var1.getObjectAt(3));
         this.witness = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(4)).getOctets());
      }
   }

   public EncryptedPOP(TaggedRequest var1, ContentInfo var2, AlgorithmIdentifier var3, AlgorithmIdentifier var4, byte[] var5) {
      this.request = var1;
      this.cms = var2;
      this.thePOPAlgID = var3;
      this.witnessAlgID = var4;
      this.witness = Arrays.clone(var5);
   }

   public static EncryptedPOP getInstance(Object var0) {
      if (var0 instanceof EncryptedPOP) {
         return (EncryptedPOP)var0;
      } else {
         return var0 != null ? new EncryptedPOP(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TaggedRequest getRequest() {
      return this.request;
   }

   public ContentInfo getCms() {
      return this.cms;
   }

   public AlgorithmIdentifier getThePOPAlgID() {
      return this.thePOPAlgID;
   }

   public AlgorithmIdentifier getWitnessAlgID() {
      return this.witnessAlgID;
   }

   public byte[] getWitness() {
      return Arrays.clone(this.witness);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.request);
      var1.add(this.cms);
      var1.add(this.thePOPAlgID);
      var1.add(this.witnessAlgID);
      var1.add(new DEROctetString(this.witness));
      return new DERSequence(var1);
   }
}
