package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ArchiveTimeStamp extends ASN1Object {
   private final AlgorithmIdentifier digestAlgorithm;
   private final Attributes attributes;
   private final ASN1Sequence reducedHashTree;
   private final ContentInfo timeStamp;

   public static ArchiveTimeStamp getInstance(Object var0) {
      if (var0 instanceof ArchiveTimeStamp) {
         return (ArchiveTimeStamp)var0;
      } else {
         return var0 != null ? new ArchiveTimeStamp(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ArchiveTimeStamp(AlgorithmIdentifier var1, PartialHashtree[] var2, ContentInfo var3) {
      this(var1, null, var2, var3);
   }

   public ArchiveTimeStamp(ContentInfo var1) {
      this(null, null, null, var1);
   }

   public ArchiveTimeStamp(AlgorithmIdentifier var1, Attributes var2, PartialHashtree[] var3, ContentInfo var4) {
      this.digestAlgorithm = var1;
      this.attributes = var2;
      if (var3 != null) {
         this.reducedHashTree = new DERSequence(var3);
      } else {
         this.reducedHashTree = null;
      }

      this.timeStamp = var4;
   }

   private ArchiveTimeStamp(ASN1Sequence var1) {
      if (var1.size() >= 1 && var1.size() <= 4) {
         AlgorithmIdentifier var2 = null;
         Attributes var3 = null;
         ASN1Sequence var4 = null;

         for (int var5 = 0; var5 < var1.size() - 1; var5++) {
            ASN1Encodable var6 = var1.getObjectAt(var5);
            if (var6 instanceof ASN1TaggedObject) {
               ASN1TaggedObject var7 = ASN1TaggedObject.getInstance(var6);
               switch (var7.getTagNo()) {
                  case 0:
                     var2 = AlgorithmIdentifier.getInstance(var7, false);
                     break;
                  case 1:
                     var3 = Attributes.getInstance(var7, false);
                     break;
                  case 2:
                     var4 = ASN1Sequence.getInstance(var7, false);
                     break;
                  default:
                     throw new IllegalArgumentException("invalid tag no in constructor: " + var7.getTagNo());
               }
            }
         }

         this.digestAlgorithm = var2;
         this.attributes = var3;
         this.reducedHashTree = var4;
         this.timeStamp = ContentInfo.getInstance(var1.getObjectAt(var1.size() - 1));
      } else {
         throw new IllegalArgumentException("wrong sequence size in constructor: " + var1.size());
      }
   }

   public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
      return this.digestAlgorithm != null ? this.digestAlgorithm : this.getTimeStampInfo().getMessageImprint().getHashAlgorithm();
   }

   public byte[] getTimeStampDigestValue() {
      return this.getTimeStampInfo().getMessageImprint().getHashedMessage();
   }

   private TSTInfo getTimeStampInfo() {
      if (this.timeStamp.getContentType().equals(CMSObjectIdentifiers.signedData)) {
         SignedData var1 = SignedData.getInstance(this.timeStamp.getContent());
         if (var1.getEncapContentInfo().getContentType().equals(PKCSObjectIdentifiers.id_ct_TSTInfo)) {
            return TSTInfo.getInstance(ASN1OctetString.getInstance(var1.getEncapContentInfo().getContent()).getOctets());
         } else {
            throw new IllegalStateException("cannot parse time stamp");
         }
      } else {
         throw new IllegalStateException("cannot identify algorithm identifier for digest");
      }
   }

   public AlgorithmIdentifier getDigestAlgorithm() {
      return this.digestAlgorithm;
   }

   public PartialHashtree getHashTreeLeaf() {
      return this.reducedHashTree == null ? null : PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(0));
   }

   public PartialHashtree[] getReducedHashTree() {
      if (this.reducedHashTree == null) {
         return null;
      } else {
         PartialHashtree[] var1 = new PartialHashtree[this.reducedHashTree.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(var2));
         }

         return var1;
      }
   }

   public ContentInfo getTimeStamp() {
      return this.timeStamp;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      if (this.digestAlgorithm != null) {
         var1.add(new DERTaggedObject(false, 0, this.digestAlgorithm));
      }

      if (this.attributes != null) {
         var1.add(new DERTaggedObject(false, 1, this.attributes));
      }

      if (this.reducedHashTree != null) {
         var1.add(new DERTaggedObject(false, 2, this.reducedHashTree));
      }

      var1.add(this.timeStamp);
      return new DERSequence(var1);
   }
}
