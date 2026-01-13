package org.bouncycastle.cms;

import java.io.IOException;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSPatchKit {
   public static SignerInformation createNonDERSignerInfo(SignerInformation var0) {
      return new CMSPatchKit.DLSignerInformation(var0);
   }

   public static SignerInformation createWithSignatureAlgorithm(SignerInformation var0, AlgorithmIdentifier var1) {
      return new CMSPatchKit.ModEncAlgSignerInformation(var0, var1);
   }

   private static class DLSignerInformation extends SignerInformation {
      protected DLSignerInformation(SignerInformation var1) {
         super(var1);
      }

      @Override
      public byte[] getEncodedSignedAttributes() throws IOException {
         return this.signedAttributeSet.getEncoded("DL");
      }
   }

   private static class ModEncAlgSignerInformation extends SignerInformation {
      protected ModEncAlgSignerInformation(SignerInformation var1, AlgorithmIdentifier var2) {
         super(var1, editEncAlg(var1.info, var2));
      }

      private static SignerInfo editEncAlg(SignerInfo var0, AlgorithmIdentifier var1) {
         return new SignerInfo(
            var0.getSID(), var0.getDigestAlgorithm(), var0.getAuthenticatedAttributes(), var1, var0.getEncryptedDigest(), var0.getUnauthenticatedAttributes()
         );
      }
   }
}
