package org.bouncycastle.its.jcajce;

import java.security.Provider;
import java.security.interfaces.ECPublicKey;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedDataEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EciesP256EncryptedKey;
import org.bouncycastle.util.Arrays;

public class JceETSIKeyWrapper implements ETSIKeyWrapper {
   private final ECPublicKey recipientKey;
   private final byte[] recipientHash;
   private final JcaJceHelper helper;

   private JceETSIKeyWrapper(ECPublicKey var1, byte[] var2, JcaJceHelper var3) {
      this.recipientKey = var1;
      this.recipientHash = var2;
      this.helper = var3;
   }

   @Override
   public EncryptedDataEncryptionKey wrap(byte[] var1) {
      try {
         Cipher var2 = this.helper.createCipher("ETSIKEMwithSHA256");
         var2.init(3, this.recipientKey, new IESKEMParameterSpec(this.recipientHash, true));
         byte[] var3 = var2.wrap(new SecretKeySpec(var1, "AES"));
         int var4 = (this.recipientKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
         if (var3[0] == 4) {
            var4 = 2 * var4 + 1;
         } else {
            var4++;
         }

         SubjectPublicKeyInfo var5 = SubjectPublicKeyInfo.getInstance(this.recipientKey.getEncoded());
         ASN1ObjectIdentifier var6 = ASN1ObjectIdentifier.getInstance(var5.getAlgorithm().getParameters());
         EciesP256EncryptedKey var7 = EciesP256EncryptedKey.builder()
            .setV(EccP256CurvePoint.createEncodedPoint(Arrays.copyOfRange(var3, 0, var4)))
            .setC(Arrays.copyOfRange(var3, var4, var4 + var1.length))
            .setT(Arrays.copyOfRange(var3, var4 + var1.length, var3.length))
            .createEciesP256EncryptedKey();
         if (var6.equals(SECObjectIdentifiers.secp256r1)) {
            return EncryptedDataEncryptionKey.eciesNistP256(var7);
         } else if (var6.equals(TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return EncryptedDataEncryptionKey.eciesBrainpoolP256r1(var7);
         } else {
            throw new IllegalStateException("recipient key curve is not P-256 or Brainpool P256r1");
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.getMessage(), var8);
      }
   }

   public static class Builder {
      private final ECPublicKey recipientKey;
      private final byte[] recipientHash;
      private JcaJceHelper helper = new DefaultJcaJceHelper();

      public Builder(ECPublicKey var1, byte[] var2) {
         this.recipientKey = var1;
         this.recipientHash = var2;
      }

      public JceETSIKeyWrapper.Builder setProvider(Provider var1) {
         this.helper = new ProviderJcaJceHelper(var1);
         return this;
      }

      public JceETSIKeyWrapper.Builder setProvider(String var1) {
         this.helper = new NamedJcaJceHelper(var1);
         return this;
      }

      public JceETSIKeyWrapper build() {
         return new JceETSIKeyWrapper(this.recipientKey, this.recipientHash, this.helper);
      }
   }
}
