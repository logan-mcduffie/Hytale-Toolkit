package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class ETSIEncryptedData {
   private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Encrypted.build();
   private final EncryptedData encryptedData;

   public ETSIEncryptedData(byte[] var1) throws IOException {
      this(new ByteArrayInputStream(var1));
   }

   public ETSIEncryptedData(InputStream var1) throws IOException {
      OERInputStream var2;
      if (var1 instanceof OERInputStream) {
         var2 = (OERInputStream)var1;
      } else {
         var2 = new OERInputStream(var1);
      }

      ASN1Object var3 = var2.parse(oerDef);
      Ieee1609Dot2Content var4 = EtsiTs103097DataEncrypted.getInstance(var3).getContent();
      if (var4.getChoice() != 2) {
         throw new IllegalStateException("EtsiTs103097Data-Encrypted did not have encrypted data content");
      } else {
         this.encryptedData = EncryptedData.getInstance(var4.getIeee1609Dot2Content());
      }
   }

   ETSIEncryptedData(EncryptedData var1) {
      this.encryptedData = var1;
   }

   public byte[] getEncoded() {
      return OEREncoder.toByteArray(new EtsiTs103097DataEncrypted(Ieee1609Dot2Content.encryptedData(this.encryptedData)), oerDef);
   }

   public EncryptedData getEncryptedData() {
      return this.encryptedData;
   }

   public Store<ETSIRecipientInfo> getRecipients() {
      ArrayList var1 = new ArrayList();

      for (RecipientInfo var3 : this.encryptedData.getRecipients().getRecipientInfos()) {
         var1.add(new ETSIRecipientInfo(this.encryptedData, var3));
      }

      return new CollectionStore<>(var1);
   }
}
