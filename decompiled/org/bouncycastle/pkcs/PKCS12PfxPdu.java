package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PKCS12PfxPdu {
   private Pfx pfx;

   private static Pfx parseBytes(byte[] var0) throws IOException {
      try {
         return Pfx.getInstance(ASN1Primitive.fromByteArray(var0));
      } catch (ClassCastException var2) {
         throw new PKCSIOException("malformed data: " + var2.getMessage(), var2);
      } catch (IllegalArgumentException var3) {
         throw new PKCSIOException("malformed data: " + var3.getMessage(), var3);
      }
   }

   public PKCS12PfxPdu(Pfx var1) {
      this.pfx = var1;
   }

   public PKCS12PfxPdu(byte[] var1) throws IOException {
      this(parseBytes(var1));
   }

   public ContentInfo[] getContentInfos() {
      ASN1Sequence var1 = ASN1Sequence.getInstance(ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
      ContentInfo[] var2 = new ContentInfo[var1.size()];

      for (int var3 = 0; var3 != var1.size(); var3++) {
         var2[var3] = ContentInfo.getInstance(var1.getObjectAt(var3));
      }

      return var2;
   }

   public boolean hasMac() {
      return this.pfx.getMacData() != null;
   }

   public AlgorithmIdentifier getMacAlgorithmID() {
      MacData var1 = this.pfx.getMacData();
      return var1 != null ? var1.getMac().getAlgorithmId() : null;
   }

   public boolean isMacValid(PKCS12MacCalculatorBuilderProvider var1, char[] var2) throws PKCSException {
      if (this.hasMac()) {
         MacData var3 = this.pfx.getMacData();
         MacDataGenerator var4;
         if (PKCSObjectIdentifiers.id_PBMAC1.equals(var3.getMac().getAlgorithmId().getAlgorithm())) {
            PBMAC1Params var5 = PBMAC1Params.getInstance(var3.getMac().getAlgorithmId().getParameters());
            if (var5 == null) {
               throw new PKCSException("If the DigestAlgorithmIdentifier is id-PBMAC1, then the parameters field must contain valid PBMAC1-params parameters.");
            }

            var4 = new MacDataGenerator(var1.get(new AlgorithmIdentifier(var3.getMac().getAlgorithmId().getAlgorithm(), var5)));
         } else {
            var4 = new MacDataGenerator(
               var1.get(
                  new AlgorithmIdentifier(
                     var3.getMac().getAlgorithmId().getAlgorithm(), new PKCS12PBEParams(var3.getSalt(), var3.getIterationCount().intValue())
                  )
               )
            );
         }

         try {
            MacData var7 = var4.build(var2, ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
            return Arrays.constantTimeAreEqual(var7.getEncoded(), this.pfx.getMacData().getEncoded());
         } catch (IOException var6) {
            throw new PKCSException("unable to process AuthSafe: " + var6.getMessage());
         }
      } else {
         throw new IllegalStateException("no MAC present on PFX");
      }
   }

   public Pfx toASN1Structure() {
      return this.pfx;
   }

   public byte[] getEncoded() throws IOException {
      return this.toASN1Structure().getEncoded();
   }

   public byte[] getEncoded(String var1) throws IOException {
      return this.toASN1Structure().getEncoded(var1);
   }
}
