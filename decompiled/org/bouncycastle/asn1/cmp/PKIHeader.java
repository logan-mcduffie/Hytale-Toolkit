package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class PKIHeader extends ASN1Object {
   public static final GeneralName NULL_NAME = new GeneralName(X500Name.getInstance(new DERSequence()));
   public static final int CMP_1999 = 1;
   public static final int CMP_2000 = 2;
   public static final int CMP_2021 = 3;
   private final ASN1Integer pvno;
   private final GeneralName sender;
   private final GeneralName recipient;
   private ASN1GeneralizedTime messageTime;
   private AlgorithmIdentifier protectionAlg;
   private ASN1OctetString senderKID;
   private ASN1OctetString recipKID;
   private ASN1OctetString transactionID;
   private ASN1OctetString senderNonce;
   private ASN1OctetString recipNonce;
   private PKIFreeText freeText;
   private ASN1Sequence generalInfo;

   private PKIHeader(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.pvno = ASN1Integer.getInstance(var2.nextElement());
      this.sender = GeneralName.getInstance(var2.nextElement());
      this.recipient = GeneralName.getInstance(var2.nextElement());

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var2.nextElement();
         if (!var3.hasContextTag()) {
            throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(var3));
         }

         switch (var3.getTagNo()) {
            case 0:
               this.messageTime = ASN1GeneralizedTime.getInstance(var3, true);
               break;
            case 1:
               this.protectionAlg = AlgorithmIdentifier.getInstance(var3, true);
               break;
            case 2:
               this.senderKID = ASN1OctetString.getInstance(var3, true);
               break;
            case 3:
               this.recipKID = ASN1OctetString.getInstance(var3, true);
               break;
            case 4:
               this.transactionID = ASN1OctetString.getInstance(var3, true);
               break;
            case 5:
               this.senderNonce = ASN1OctetString.getInstance(var3, true);
               break;
            case 6:
               this.recipNonce = ASN1OctetString.getInstance(var3, true);
               break;
            case 7:
               this.freeText = PKIFreeText.getInstance(var3, true);
               break;
            case 8:
               this.generalInfo = ASN1Sequence.getInstance(var3, true);
               break;
            default:
               throw new IllegalArgumentException("unknown tag number: " + var3.getTagNo());
         }
      }
   }

   public PKIHeader(int var1, GeneralName var2, GeneralName var3) {
      this(new ASN1Integer(var1), var2, var3);
   }

   private PKIHeader(ASN1Integer var1, GeneralName var2, GeneralName var3) {
      this.pvno = var1;
      this.sender = var2;
      this.recipient = var3;
   }

   public static PKIHeader getInstance(Object var0) {
      if (var0 instanceof PKIHeader) {
         return (PKIHeader)var0;
      } else {
         return var0 != null ? new PKIHeader(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getPvno() {
      return this.pvno;
   }

   public GeneralName getSender() {
      return this.sender;
   }

   public GeneralName getRecipient() {
      return this.recipient;
   }

   public ASN1GeneralizedTime getMessageTime() {
      return this.messageTime;
   }

   public AlgorithmIdentifier getProtectionAlg() {
      return this.protectionAlg;
   }

   public ASN1OctetString getSenderKID() {
      return this.senderKID;
   }

   public ASN1OctetString getRecipKID() {
      return this.recipKID;
   }

   public ASN1OctetString getTransactionID() {
      return this.transactionID;
   }

   public ASN1OctetString getSenderNonce() {
      return this.senderNonce;
   }

   public ASN1OctetString getRecipNonce() {
      return this.recipNonce;
   }

   public PKIFreeText getFreeText() {
      return this.freeText;
   }

   public InfoTypeAndValue[] getGeneralInfo() {
      if (this.generalInfo == null) {
         return null;
      } else {
         InfoTypeAndValue[] var1 = new InfoTypeAndValue[this.generalInfo.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = InfoTypeAndValue.getInstance(this.generalInfo.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(12);
      var1.add(this.pvno);
      var1.add(this.sender);
      var1.add(this.recipient);
      this.addOptional(var1, 0, this.messageTime);
      this.addOptional(var1, 1, this.protectionAlg);
      this.addOptional(var1, 2, this.senderKID);
      this.addOptional(var1, 3, this.recipKID);
      this.addOptional(var1, 4, this.transactionID);
      this.addOptional(var1, 5, this.senderNonce);
      this.addOptional(var1, 6, this.recipNonce);
      this.addOptional(var1, 7, this.freeText);
      this.addOptional(var1, 8, this.generalInfo);
      return new DERSequence(var1);
   }

   private void addOptional(ASN1EncodableVector var1, int var2, ASN1Encodable var3) {
      if (var3 != null) {
         var1.add(new DERTaggedObject(true, var2, var3));
      }
   }
}
