package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class TBSCertList extends ASN1Object {
   ASN1Integer version;
   AlgorithmIdentifier signature;
   X500Name issuer;
   Time thisUpdate;
   Time nextUpdate;
   ASN1Sequence revokedCertificates;
   Extensions crlExtensions;

   public static TBSCertList getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static TBSCertList getInstance(Object var0) {
      if (var0 instanceof TBSCertList) {
         return (TBSCertList)var0;
      } else {
         return var0 != null ? new TBSCertList(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public TBSCertList(ASN1Sequence var1) {
      if (var1.size() >= 3 && var1.size() <= 7) {
         int var2 = 0;
         if (var1.getObjectAt(var2) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(var1.getObjectAt(var2++));
         } else {
            this.version = null;
         }

         this.signature = AlgorithmIdentifier.getInstance(var1.getObjectAt(var2++));
         this.issuer = X500Name.getInstance(var1.getObjectAt(var2++));
         this.thisUpdate = Time.getInstance(var1.getObjectAt(var2++));
         if (var2 < var1.size()
            && (
               var1.getObjectAt(var2) instanceof ASN1UTCTime || var1.getObjectAt(var2) instanceof ASN1GeneralizedTime || var1.getObjectAt(var2) instanceof Time
            )) {
            this.nextUpdate = Time.getInstance(var1.getObjectAt(var2++));
         }

         if (var2 < var1.size() && !(var1.getObjectAt(var2) instanceof ASN1TaggedObject)) {
            this.revokedCertificates = ASN1Sequence.getInstance(var1.getObjectAt(var2++));
         }

         if (var2 < var1.size() && var1.getObjectAt(var2) instanceof ASN1TaggedObject) {
            this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)var1.getObjectAt(var2), true));
         }
      } else {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      }
   }

   public int getVersionNumber() {
      return this.version == null ? 1 : this.version.intValueExact() + 1;
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public AlgorithmIdentifier getSignature() {
      return this.signature;
   }

   public X500Name getIssuer() {
      return this.issuer;
   }

   public Time getThisUpdate() {
      return this.thisUpdate;
   }

   public Time getNextUpdate() {
      return this.nextUpdate;
   }

   public TBSCertList.CRLEntry[] getRevokedCertificates() {
      if (this.revokedCertificates == null) {
         return new TBSCertList.CRLEntry[0];
      } else {
         TBSCertList.CRLEntry[] var1 = new TBSCertList.CRLEntry[this.revokedCertificates.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = TBSCertList.CRLEntry.getInstance(this.revokedCertificates.getObjectAt(var2));
         }

         return var1;
      }
   }

   public Enumeration getRevokedCertificateEnumeration() {
      return (Enumeration)(this.revokedCertificates == null
         ? new TBSCertList.EmptyEnumeration()
         : new TBSCertList.RevokedCertificatesEnumeration(this.revokedCertificates.getObjects()));
   }

   public Extensions getExtensions() {
      return this.crlExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(7);
      if (this.version != null) {
         var1.add(this.version);
      }

      var1.add(this.signature);
      var1.add(this.issuer);
      var1.add(this.thisUpdate);
      if (this.nextUpdate != null) {
         var1.add(this.nextUpdate);
      }

      if (this.revokedCertificates != null) {
         var1.add(this.revokedCertificates);
      }

      if (this.crlExtensions != null) {
         var1.add(new DERTaggedObject(0, this.crlExtensions));
      }

      return new DERSequence(var1);
   }

   public static class CRLEntry extends ASN1Object {
      ASN1Sequence seq;
      Extensions crlEntryExtensions;

      private CRLEntry(ASN1Sequence var1) {
         if (var1.size() >= 2 && var1.size() <= 3) {
            this.seq = var1;
         } else {
            throw new IllegalArgumentException("Bad sequence size: " + var1.size());
         }
      }

      public static TBSCertList.CRLEntry getInstance(Object var0) {
         if (var0 instanceof TBSCertList.CRLEntry) {
            return (TBSCertList.CRLEntry)var0;
         } else {
            return var0 != null ? new TBSCertList.CRLEntry(ASN1Sequence.getInstance(var0)) : null;
         }
      }

      public ASN1Integer getUserCertificate() {
         return ASN1Integer.getInstance(this.seq.getObjectAt(0));
      }

      public Time getRevocationDate() {
         return Time.getInstance(this.seq.getObjectAt(1));
      }

      public Extensions getExtensions() {
         if (this.crlEntryExtensions == null && this.seq.size() == 3) {
            this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2));
         }

         return this.crlEntryExtensions;
      }

      @Override
      public ASN1Primitive toASN1Primitive() {
         return this.seq;
      }

      public boolean hasExtensions() {
         return this.seq.size() == 3;
      }
   }

   private static class EmptyEnumeration implements Enumeration {
      private EmptyEnumeration() {
      }

      @Override
      public boolean hasMoreElements() {
         return false;
      }

      @Override
      public Object nextElement() {
         throw new NoSuchElementException("Empty Enumeration");
      }
   }

   private static class RevokedCertificatesEnumeration implements Enumeration {
      private final Enumeration en;

      RevokedCertificatesEnumeration(Enumeration var1) {
         this.en = var1;
      }

      @Override
      public boolean hasMoreElements() {
         return this.en.hasMoreElements();
      }

      @Override
      public Object nextElement() {
         return TBSCertList.CRLEntry.getInstance(this.en.nextElement());
      }
   }
}
