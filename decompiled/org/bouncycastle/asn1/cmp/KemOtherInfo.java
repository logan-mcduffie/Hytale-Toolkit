package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KemOtherInfo extends ASN1Object {
   private static final PKIFreeText DEFAULT_staticString = new PKIFreeText("CMP-KEM");
   private final PKIFreeText staticString;
   private final ASN1OctetString transactionID;
   private final ASN1OctetString senderNonce;
   private final ASN1OctetString recipNonce;
   private final ASN1Integer len;
   private final AlgorithmIdentifier mac;
   private final ASN1OctetString ct;

   public KemOtherInfo(ASN1OctetString var1, ASN1OctetString var2, ASN1OctetString var3, ASN1Integer var4, AlgorithmIdentifier var5, ASN1OctetString var6) {
      this.staticString = DEFAULT_staticString;
      this.transactionID = var1;
      this.senderNonce = var2;
      this.recipNonce = var3;
      this.len = var4;
      this.mac = var5;
      this.ct = var6;
   }

   public KemOtherInfo(ASN1OctetString var1, ASN1OctetString var2, ASN1OctetString var3, long var4, AlgorithmIdentifier var6, ASN1OctetString var7) {
      this(var1, var2, var3, new ASN1Integer(var4), var6, var7);
   }

   private KemOtherInfo(ASN1Sequence var1) {
      if (var1.size() >= 4 && var1.size() <= 7) {
         int var2 = 0;
         this.staticString = PKIFreeText.getInstance(var1.getObjectAt(var2));
         if (!DEFAULT_staticString.equals(this.staticString)) {
            throw new IllegalArgumentException("staticString field should be " + DEFAULT_staticString);
         } else {
            ASN1OctetString var3 = null;
            ASN1OctetString var4 = null;
            ASN1OctetString var5 = null;
            ASN1TaggedObject var6 = tryGetTagged(var1, ++var2);
            if (var6 != null) {
               ASN1Primitive var7 = ASN1Util.tryGetContextBaseUniversal(var6, 0, true, 4);
               if (var7 != null) {
                  var3 = (ASN1OctetString)var7;
                  var6 = tryGetTagged(var1, ++var2);
               }
            }

            if (var6 != null) {
               ASN1Primitive var12 = ASN1Util.tryGetContextBaseUniversal(var6, 1, true, 4);
               if (var12 != null) {
                  var4 = (ASN1OctetString)var12;
                  var6 = tryGetTagged(var1, ++var2);
               }
            }

            if (var6 != null) {
               ASN1Primitive var13 = ASN1Util.tryGetContextBaseUniversal(var6, 2, true, 4);
               if (var13 != null) {
                  var5 = (ASN1OctetString)var13;
                  var6 = tryGetTagged(var1, ++var2);
               }
            }

            if (var6 != null) {
               throw new IllegalArgumentException("unknown tag: " + ASN1Util.getTagText(var6));
            } else {
               this.transactionID = var3;
               this.senderNonce = var4;
               this.recipNonce = var5;
               this.len = ASN1Integer.getInstance(var1.getObjectAt(var2));
               this.mac = AlgorithmIdentifier.getInstance(var1.getObjectAt(++var2));
               this.ct = ASN1OctetString.getInstance(var1.getObjectAt(++var2));
               if (++var2 != var1.size()) {
                  throw new IllegalArgumentException("unexpected data at end of sequence");
               }
            }
         }
      } else {
         throw new IllegalArgumentException("sequence size should be between 4 and 7 inclusive");
      }
   }

   public static KemOtherInfo getInstance(Object var0) {
      if (var0 instanceof KemOtherInfo) {
         return (KemOtherInfo)var0;
      } else {
         return var0 != null ? new KemOtherInfo(ASN1Sequence.getInstance(var0)) : null;
      }
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

   public ASN1Integer getLen() {
      return this.len;
   }

   public AlgorithmIdentifier getMac() {
      return this.mac;
   }

   public ASN1OctetString getCt() {
      return this.ct;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(7);
      var1.add(this.staticString);
      addOptional(var1, 0, this.transactionID);
      addOptional(var1, 1, this.senderNonce);
      addOptional(var1, 2, this.recipNonce);
      var1.add(this.len);
      var1.add(this.mac);
      var1.add(this.ct);
      return new DERSequence(var1);
   }

   private static void addOptional(ASN1EncodableVector var0, int var1, ASN1Encodable var2) {
      if (var2 != null) {
         var0.add(new DERTaggedObject(true, var1, var2));
      }
   }

   private static ASN1TaggedObject tryGetTagged(ASN1Sequence var0, int var1) {
      ASN1Encodable var2 = var0.getObjectAt(var1);
      return var2 instanceof ASN1TaggedObject ? (ASN1TaggedObject)var2 : null;
   }
}
