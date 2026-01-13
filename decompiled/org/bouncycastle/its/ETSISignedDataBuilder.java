package org.bouncycastle.its;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload;
import org.bouncycastle.oer.its.ieee1609dot2.SignerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;

public class ETSISignedDataBuilder {
   private static final Element def = IEEE1609dot2.ToBeSignedData.build();
   private final HeaderInfo headerInfo;
   private Ieee1609Dot2Data data;
   private HashedData extDataHash;

   private ETSISignedDataBuilder(Psid var1) {
      this(HeaderInfo.builder().setPsid(var1).setGenerationTime(Time64.now()).createHeaderInfo());
   }

   private ETSISignedDataBuilder(HeaderInfo var1) {
      this.headerInfo = var1;
   }

   public static ETSISignedDataBuilder builder(Psid var0) {
      return new ETSISignedDataBuilder(var0);
   }

   public static ETSISignedDataBuilder builder(HeaderInfo var0) {
      return new ETSISignedDataBuilder(var0);
   }

   public ETSISignedDataBuilder setData(Ieee1609Dot2Content var1) {
      this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(var1).createIeee1609Dot2Data();
      return this;
   }

   public ETSISignedDataBuilder setUnsecuredData(byte[] var1) {
      this.data = Ieee1609Dot2Data.builder()
         .setProtocolVersion(new UINT8(3))
         .setContent(Ieee1609Dot2Content.unsecuredData(new Opaque(var1)))
         .createEtsiTs103097Data();
      return this;
   }

   public ETSISignedDataBuilder setExtDataHash(HashedData var1) {
      this.extDataHash = var1;
      return this;
   }

   private ToBeSignedData getToBeSignedData() {
      SignedDataPayload var1 = new SignedDataPayload(this.data, this.extDataHash);
      return ToBeSignedData.builder().setPayload(var1).setHeaderInfo(this.headerInfo).createToBeSignedData();
   }

   public ETSISignedData build(ITSContentSigner var1) {
      ToBeSignedData var2 = this.getToBeSignedData();
      write(var1.getOutputStream(), OEREncoder.toByteArray(var2, def));
      Signature var3 = ECDSAEncoder.toITS(var1.getCurveID(), var1.getSignature());
      return new ETSISignedData(
         SignedData.builder()
            .setHashId(ITSAlgorithmUtils.getHashAlgorithm(var1.getDigestAlgorithm().getAlgorithm()))
            .setTbsData(var2)
            .setSigner(SignerIdentifier.self())
            .setSignature(var3)
            .createSignedData()
      );
   }

   public ETSISignedData build(ITSContentSigner var1, List<ITSCertificate> var2) {
      ToBeSignedData var3 = this.getToBeSignedData();
      write(var1.getOutputStream(), OEREncoder.toByteArray(var3, def));
      ArrayList var4 = new ArrayList();

      for (ITSCertificate var6 : var2) {
         var4.add(Certificate.getInstance(var6.toASN1Structure()));
      }

      Signature var7 = ECDSAEncoder.toITS(var1.getCurveID(), var1.getSignature());
      return new ETSISignedData(
         SignedData.builder()
            .setHashId(ITSAlgorithmUtils.getHashAlgorithm(var1.getDigestAlgorithm().getAlgorithm()))
            .setTbsData(var3)
            .setSigner(SignerIdentifier.certificate(new SequenceOfCertificate(var4)))
            .setSignature(var7)
            .createSignedData()
      );
   }

   public ETSISignedData build(ITSContentSigner var1, HashedId8 var2) {
      ToBeSignedData var3 = this.getToBeSignedData();
      write(var1.getOutputStream(), OEREncoder.toByteArray(var3, def));
      Signature var4 = ECDSAEncoder.toITS(var1.getCurveID(), var1.getSignature());
      return new ETSISignedData(
         SignedData.builder()
            .setHashId(ITSAlgorithmUtils.getHashAlgorithm(var1.getDigestAlgorithm().getAlgorithm()))
            .setTbsData(var3)
            .setSigner(SignerIdentifier.digest(var2))
            .setSignature(var4)
            .createSignedData()
      );
   }

   private static void write(OutputStream var0, byte[] var1) {
      try {
         var0.write(var1);
         var0.flush();
         var0.close();
      } catch (Exception var3) {
         throw new RuntimeException(var3.getMessage(), var3);
      }
   }
}
