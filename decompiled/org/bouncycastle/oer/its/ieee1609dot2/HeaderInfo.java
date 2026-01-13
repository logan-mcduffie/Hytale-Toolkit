package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ThreeDLocation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;

public class HeaderInfo extends ASN1Object {
   private final Psid psid;
   private final Time64 generationTime;
   private final Time64 expiryTime;
   private final ThreeDLocation generationLocation;
   private final HashedId3 p2pcdLearningRequest;
   private final MissingCrlIdentifier missingCrlIdentifier;
   private final EncryptionKey encryptionKey;
   private final SequenceOfHashedId3 inlineP2pcdRequest;
   private final Certificate requestedCertificate;
   private final PduFunctionalType pduFunctionalType;
   private final ContributedExtensionBlocks contributedExtensions;

   private HeaderInfo(ASN1Sequence var1) {
      if (var1.size() != 11 && var1.size() != 7) {
         throw new IllegalArgumentException("expected sequence size of 11 or 7");
      } else {
         Iterator var2 = var1.iterator();
         this.psid = Psid.getInstance(var2.next());
         this.generationTime = OEROptional.getValue(Time64.class, var2.next());
         this.expiryTime = OEROptional.getValue(Time64.class, var2.next());
         this.generationLocation = OEROptional.getValue(ThreeDLocation.class, var2.next());
         this.p2pcdLearningRequest = OEROptional.getValue(HashedId3.class, var2.next());
         this.missingCrlIdentifier = OEROptional.getValue(MissingCrlIdentifier.class, var2.next());
         this.encryptionKey = OEROptional.getValue(EncryptionKey.class, var2.next());
         if (var1.size() > 7) {
            this.inlineP2pcdRequest = OEROptional.getValue(SequenceOfHashedId3.class, var2.next());
            this.requestedCertificate = OEROptional.getValue(Certificate.class, var2.next());
            this.pduFunctionalType = OEROptional.getValue(PduFunctionalType.class, var2.next());
            this.contributedExtensions = OEROptional.getValue(ContributedExtensionBlocks.class, var2.next());
         } else {
            this.inlineP2pcdRequest = null;
            this.requestedCertificate = null;
            this.pduFunctionalType = null;
            this.contributedExtensions = null;
         }
      }
   }

   public HeaderInfo(
      Psid var1,
      Time64 var2,
      Time64 var3,
      ThreeDLocation var4,
      HashedId3 var5,
      MissingCrlIdentifier var6,
      EncryptionKey var7,
      SequenceOfHashedId3 var8,
      Certificate var9,
      PduFunctionalType var10,
      ContributedExtensionBlocks var11
   ) {
      this.psid = var1;
      this.generationTime = var2;
      this.expiryTime = var3;
      this.generationLocation = var4;
      this.p2pcdLearningRequest = var5;
      this.missingCrlIdentifier = var6;
      this.encryptionKey = var7;
      this.inlineP2pcdRequest = var8;
      this.requestedCertificate = var9;
      this.pduFunctionalType = var10;
      this.contributedExtensions = var11;
   }

   public static HeaderInfo getInstance(Object var0) {
      if (var0 instanceof HeaderInfo) {
         return (HeaderInfo)var0;
      } else {
         return var0 != null ? new HeaderInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public Psid getPsid() {
      return this.psid;
   }

   public Time64 getGenerationTime() {
      return this.generationTime;
   }

   public Time64 getExpiryTime() {
      return this.expiryTime;
   }

   public ThreeDLocation getGenerationLocation() {
      return this.generationLocation;
   }

   public HashedId3 getP2pcdLearningRequest() {
      return this.p2pcdLearningRequest;
   }

   public MissingCrlIdentifier getMissingCrlIdentifier() {
      return this.missingCrlIdentifier;
   }

   public EncryptionKey getEncryptionKey() {
      return this.encryptionKey;
   }

   public SequenceOfHashedId3 getInlineP2pcdRequest() {
      return this.inlineP2pcdRequest;
   }

   public Certificate getRequestedCertificate() {
      return this.requestedCertificate;
   }

   public PduFunctionalType getPduFunctionalType() {
      return this.pduFunctionalType;
   }

   public ContributedExtensionBlocks getContributedExtensions() {
      return this.contributedExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(
         new ASN1Encodable[]{
            this.psid,
            OEROptional.getInstance(this.generationTime),
            OEROptional.getInstance(this.expiryTime),
            OEROptional.getInstance(this.generationLocation),
            OEROptional.getInstance(this.p2pcdLearningRequest),
            OEROptional.getInstance(this.missingCrlIdentifier),
            OEROptional.getInstance(this.encryptionKey),
            OEROptional.getInstance(this.inlineP2pcdRequest),
            OEROptional.getInstance(this.requestedCertificate),
            OEROptional.getInstance(this.pduFunctionalType),
            OEROptional.getInstance(this.contributedExtensions)
         }
      );
   }

   public static HeaderInfo.Builder builder() {
      return new HeaderInfo.Builder();
   }

   public static class Builder {
      private Psid psid;
      private Time64 generationTime;
      private Time64 expiryTime;
      private ThreeDLocation generationLocation;
      private HashedId3 p2pcdLearningRequest;
      private MissingCrlIdentifier missingCrlIdentifier;
      private EncryptionKey encryptionKey;
      private SequenceOfHashedId3 inlineP2pcdRequest;
      private Certificate requestedCertificate;
      private PduFunctionalType pduFunctionalType;
      private ContributedExtensionBlocks contributedExtensions;

      public HeaderInfo.Builder setPsid(Psid var1) {
         this.psid = var1;
         return this;
      }

      public HeaderInfo.Builder setGenerationTime(Time64 var1) {
         this.generationTime = var1;
         return this;
      }

      public HeaderInfo.Builder setExpiryTime(Time64 var1) {
         this.expiryTime = var1;
         return this;
      }

      public HeaderInfo.Builder setGenerationLocation(ThreeDLocation var1) {
         this.generationLocation = var1;
         return this;
      }

      public HeaderInfo.Builder setP2pcdLearningRequest(HashedId3 var1) {
         this.p2pcdLearningRequest = var1;
         return this;
      }

      public HeaderInfo.Builder setEncryptionKey(EncryptionKey var1) {
         this.encryptionKey = var1;
         return this;
      }

      public HeaderInfo.Builder setMissingCrlIdentifier(MissingCrlIdentifier var1) {
         this.missingCrlIdentifier = var1;
         return this;
      }

      public HeaderInfo.Builder setInlineP2pcdRequest(SequenceOfHashedId3 var1) {
         this.inlineP2pcdRequest = var1;
         return this;
      }

      public HeaderInfo.Builder setRequestedCertificate(Certificate var1) {
         this.requestedCertificate = var1;
         return this;
      }

      public HeaderInfo.Builder setPduFunctionalType(PduFunctionalType var1) {
         this.pduFunctionalType = var1;
         return this;
      }

      public HeaderInfo.Builder setContributedExtensions(ContributedExtensionBlocks var1) {
         this.contributedExtensions = var1;
         return this;
      }

      public HeaderInfo createHeaderInfo() {
         return new HeaderInfo(
            this.psid,
            this.generationTime,
            this.expiryTime,
            this.generationLocation,
            this.p2pcdLearningRequest,
            this.missingCrlIdentifier,
            this.encryptionKey,
            this.inlineP2pcdRequest,
            this.requestedCertificate,
            this.pduFunctionalType,
            this.contributedExtensions
         );
      }
   }
}
