package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class MessageImprint extends ASN1Object {
   AlgorithmIdentifier hashAlgorithm;
   byte[] hashedMessage;

   public static MessageImprint getInstance(Object var0) {
      if (var0 instanceof MessageImprint) {
         return (MessageImprint)var0;
      } else {
         return var0 != null ? new MessageImprint(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private MessageImprint(ASN1Sequence var1) {
      if (var1.size() == 2) {
         this.hashAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.hashedMessage = ASN1OctetString.getInstance(var1.getObjectAt(1)).getOctets();
      } else {
         throw new IllegalArgumentException("sequence has wrong number of elements");
      }
   }

   public MessageImprint(AlgorithmIdentifier var1, byte[] var2) {
      this.hashAlgorithm = var1;
      this.hashedMessage = Arrays.clone(var2);
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public byte[] getHashedMessage() {
      return Arrays.clone(this.hashedMessage);
   }

   public int getHashedMessageLength() {
      return this.hashedMessage.length;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.hashAlgorithm, new DEROctetString(this.hashedMessage));
   }
}
