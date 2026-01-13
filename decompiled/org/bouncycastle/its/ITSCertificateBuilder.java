package org.bouncycastle.its;

import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ITSCertificateBuilder {
   protected final ToBeSignedCertificate.Builder tbsCertificateBuilder;
   protected final ITSCertificate issuer;
   protected UINT8 version = new UINT8(3);
   protected HashedId3 cracaId = new HashedId3(new byte[3]);
   protected CrlSeries crlSeries = new CrlSeries(0);

   public ITSCertificateBuilder(ToBeSignedCertificate.Builder var1) {
      this(null, var1);
   }

   public ITSCertificateBuilder(ITSCertificate var1, ToBeSignedCertificate.Builder var2) {
      this.issuer = var1;
      this.tbsCertificateBuilder = var2;
      this.tbsCertificateBuilder.setCracaId(this.cracaId);
      this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
   }

   public ITSCertificate getIssuer() {
      return this.issuer;
   }

   public ITSCertificateBuilder setVersion(int var1) {
      this.version = new UINT8(var1);
      return this;
   }

   public ITSCertificateBuilder setCracaId(byte[] var1) {
      this.cracaId = new HashedId3(var1);
      this.tbsCertificateBuilder.setCracaId(this.cracaId);
      return this;
   }

   public ITSCertificateBuilder setCrlSeries(int var1) {
      this.crlSeries = new CrlSeries(var1);
      this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
      return this;
   }

   public ITSCertificateBuilder setValidityPeriod(ITSValidityPeriod var1) {
      this.tbsCertificateBuilder.setValidityPeriod(var1.toASN1Structure());
      return this;
   }

   public ITSCertificateBuilder setCertIssuePermissions(PsidGroupPermissions... var1) {
      this.tbsCertificateBuilder
         .setCertIssuePermissions(SequenceOfPsidGroupPermissions.builder().addGroupPermission(var1).createSequenceOfPsidGroupPermissions());
      return this;
   }

   public ITSCertificateBuilder setAppPermissions(PsidSsp... var1) {
      SequenceOfPsidSsp.Builder var2 = SequenceOfPsidSsp.builder();

      for (int var3 = 0; var3 != var1.length; var3++) {
         var2.setItem(var1[var3]);
      }

      this.tbsCertificateBuilder.setAppPermissions(var2.createSequenceOfPsidSsp());
      return this;
   }
}
