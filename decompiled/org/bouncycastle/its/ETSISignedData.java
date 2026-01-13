package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;

public class ETSISignedData {
   private final SignedData signedData;
   private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Signed.build();

   public ETSISignedData(Opaque var1) throws IOException {
      this(var1.getInputStream());
   }

   public ETSISignedData(byte[] var1) throws IOException {
      this(new ByteArrayInputStream(var1));
   }

   public ETSISignedData(InputStream var1) throws IOException {
      OERInputStream var2;
      if (var1 instanceof OERInputStream) {
         var2 = (OERInputStream)var1;
      } else {
         var2 = new OERInputStream(var1);
      }

      ASN1Object var3 = var2.parse(oerDef);
      Ieee1609Dot2Content var4 = EtsiTs103097DataSigned.getInstance(var3).getContent();
      if (var4.getChoice() != 1) {
         throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
      } else {
         this.signedData = SignedData.getInstance(var4.getIeee1609Dot2Content());
      }
   }

   public ETSISignedData(EtsiTs103097DataSigned var1) {
      Ieee1609Dot2Content var2 = var1.getContent();
      if (var2.getChoice() != 1) {
         throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
      } else {
         this.signedData = SignedData.getInstance(var1.getContent());
      }
   }

   public ETSISignedData(SignedData var1) {
      this.signedData = var1;
   }

   public boolean signatureValid(ITSContentVerifierProvider var1) throws Exception {
      Signature var2 = this.signedData.getSignature();
      ContentVerifier var3 = var1.get(var2.getChoice());
      OutputStream var4 = var3.getOutputStream();
      var4.write(OEREncoder.toByteArray(this.signedData.getTbsData(), IEEE1609dot2.ToBeSignedData.build()));
      var4.close();
      return var3.verify(ECDSAEncoder.toX962(this.signedData.getSignature()));
   }

   public byte[] getEncoded() {
      return OEREncoder.toByteArray(
         new EtsiTs103097DataSigned(Ieee1609Dot2Content.signedData(this.signedData)), EtsiTs103097Module.EtsiTs103097Data_Signed.build()
      );
   }

   public SignedData getSignedData() {
      return this.signedData;
   }
}
