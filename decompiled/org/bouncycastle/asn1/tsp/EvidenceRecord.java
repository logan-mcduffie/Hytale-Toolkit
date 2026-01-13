package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EvidenceRecord extends ASN1Object {
   private static final ASN1ObjectIdentifier OID = new ASN1ObjectIdentifier("1.3.6.1.5.5.11.0.2.1");
   private ASN1Integer version = new ASN1Integer(1L);
   private ASN1Sequence digestAlgorithms;
   private CryptoInfos cryptoInfos;
   private EncryptionInfo encryptionInfo;
   private ArchiveTimeStampSequence archiveTimeStampSequence;

   public static EvidenceRecord getInstance(Object var0) {
      if (var0 instanceof EvidenceRecord) {
         return (EvidenceRecord)var0;
      } else {
         return var0 != null ? new EvidenceRecord(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static EvidenceRecord getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   private EvidenceRecord(EvidenceRecord var1, ArchiveTimeStampSequence var2, ArchiveTimeStamp var3) {
      this.version = var1.version;
      if (var3 != null) {
         AlgorithmIdentifier var4 = var3.getDigestAlgorithmIdentifier();
         ASN1EncodableVector var5 = new ASN1EncodableVector();
         Enumeration var6 = var1.digestAlgorithms.getObjects();
         boolean var7 = false;

         while (var6.hasMoreElements()) {
            AlgorithmIdentifier var8 = AlgorithmIdentifier.getInstance(var6.nextElement());
            var5.add(var8);
            if (var8.equals(var4)) {
               var7 = true;
               break;
            }
         }

         if (!var7) {
            var5.add(var4);
            this.digestAlgorithms = new DERSequence(var5);
         } else {
            this.digestAlgorithms = var1.digestAlgorithms;
         }
      } else {
         this.digestAlgorithms = var1.digestAlgorithms;
      }

      this.cryptoInfos = var1.cryptoInfos;
      this.encryptionInfo = var1.encryptionInfo;
      this.archiveTimeStampSequence = var2;
   }

   public EvidenceRecord(CryptoInfos var1, EncryptionInfo var2, ArchiveTimeStamp var3) {
      this.digestAlgorithms = new DERSequence(var3.getDigestAlgorithmIdentifier());
      this.cryptoInfos = var1;
      this.encryptionInfo = var2;
      this.archiveTimeStampSequence = new ArchiveTimeStampSequence(new ArchiveTimeStampChain(var3));
   }

   public EvidenceRecord(AlgorithmIdentifier[] var1, CryptoInfos var2, EncryptionInfo var3, ArchiveTimeStampSequence var4) {
      this.digestAlgorithms = new DERSequence(var1);
      this.cryptoInfos = var2;
      this.encryptionInfo = var3;
      this.archiveTimeStampSequence = var4;
   }

   private EvidenceRecord(ASN1Sequence var1) {
      if (var1.size() < 3 && var1.size() > 5) {
         throw new IllegalArgumentException("wrong sequence size in constructor: " + var1.size());
      } else {
         ASN1Integer var2 = ASN1Integer.getInstance(var1.getObjectAt(0));
         if (!var2.hasValue(1)) {
            throw new IllegalArgumentException("incompatible version");
         } else {
            this.version = var2;
            this.digestAlgorithms = ASN1Sequence.getInstance(var1.getObjectAt(1));

            for (int var3 = 2; var3 != var1.size() - 1; var3++) {
               ASN1Encodable var4 = var1.getObjectAt(var3);
               if (!(var4 instanceof ASN1TaggedObject)) {
                  throw new IllegalArgumentException("unknown object in getInstance: " + var4.getClass().getName());
               }

               ASN1TaggedObject var5 = (ASN1TaggedObject)var4;
               switch (var5.getTagNo()) {
                  case 0:
                     this.cryptoInfos = CryptoInfos.getInstance(var5, false);
                     break;
                  case 1:
                     this.encryptionInfo = EncryptionInfo.getInstance(var5, false);
                     break;
                  default:
                     throw new IllegalArgumentException("unknown tag in getInstance: " + var5.getTagNo());
               }
            }

            this.archiveTimeStampSequence = ArchiveTimeStampSequence.getInstance(var1.getObjectAt(var1.size() - 1));
         }
      }
   }

   public AlgorithmIdentifier[] getDigestAlgorithms() {
      AlgorithmIdentifier[] var1 = new AlgorithmIdentifier[this.digestAlgorithms.size()];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = AlgorithmIdentifier.getInstance(this.digestAlgorithms.getObjectAt(var2));
      }

      return var1;
   }

   public ArchiveTimeStampSequence getArchiveTimeStampSequence() {
      return this.archiveTimeStampSequence;
   }

   public EvidenceRecord addArchiveTimeStamp(ArchiveTimeStamp var1, boolean var2) {
      if (var2) {
         ArchiveTimeStampChain var5 = new ArchiveTimeStampChain(var1);
         return new EvidenceRecord(this, this.archiveTimeStampSequence.append(var5), var1);
      } else {
         ArchiveTimeStampChain[] var3 = this.archiveTimeStampSequence.getArchiveTimeStampChains();
         AlgorithmIdentifier var4 = var3[var3.length - 1].getArchiveTimestamps()[0].getDigestAlgorithmIdentifier();
         if (!var4.equals(var1.getDigestAlgorithmIdentifier())) {
            throw new IllegalArgumentException("mismatch of digest algorithm in addArchiveTimeStamp");
         } else {
            var3[var3.length - 1] = var3[var3.length - 1].append(var1);
            return new EvidenceRecord(this, new ArchiveTimeStampSequence(var3), null);
         }
      }
   }

   @Override
   public String toString() {
      return "EvidenceRecord: Oid(" + OID + ")";
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.version);
      var1.add(this.digestAlgorithms);
      if (null != this.cryptoInfos) {
         var1.add(new DERTaggedObject(false, 0, this.cryptoInfos));
      }

      if (null != this.encryptionInfo) {
         var1.add(new DERTaggedObject(false, 1, this.encryptionInfo));
      }

      var1.add(this.archiveTimeStampSequence);
      return new DERSequence(var1);
   }
}
