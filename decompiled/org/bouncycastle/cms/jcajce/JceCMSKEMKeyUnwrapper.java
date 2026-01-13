package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyUnwrapper extends AsymmetricKeyUnwrapper {
   private final AlgorithmIdentifier symWrapAlgorithm;
   private final int kekLength;
   private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
   private Map extraMappings = new HashMap();
   private PrivateKey privateKey;

   public JceCMSKEMKeyUnwrapper(AlgorithmIdentifier var1, PrivateKey var2) {
      super(PrivateKeyInfo.getInstance(var2.getEncoded()).getPrivateKeyAlgorithm());
      KEMRecipientInfo var3 = KEMRecipientInfo.getInstance(var1.getParameters());
      this.privateKey = var2;
      this.symWrapAlgorithm = var1;
      this.kekLength = CMSUtils.getKekSize(var3.getWrap().getAlgorithm());
   }

   public JceCMSKEMKeyUnwrapper setProvider(Provider var1) {
      this.helper = new ProviderJcaJceExtHelper(var1);
      return this;
   }

   public JceCMSKEMKeyUnwrapper setProvider(String var1) {
      this.helper = new NamedJcaJceExtHelper(var1);
      return this;
   }

   public JceCMSKEMKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   public int getKekLength() {
      return this.kekLength;
   }

   @Override
   public GenericKey generateUnwrappedKey(AlgorithmIdentifier var1, byte[] var2) throws OperatorException {
      KEMRecipientInfo var3 = KEMRecipientInfo.getInstance(this.symWrapAlgorithm.getParameters());
      AlgorithmIdentifier var4 = var3.getWrap();

      try {
         byte[] var5 = new CMSORIforKEMOtherInfo(var4, this.kekLength, var3.getUkm()).getEncoded();
         if (this.privateKey instanceof RSAPrivateKey) {
            Cipher var12 = CMSUtils.createAsymmetricWrapper(this.helper, var3.getKem().getAlgorithm(), new HashMap());

            try {
               String var13 = CMSUtils.getWrapAlgorithmName(var4.getAlgorithm());
               KTSParameterSpec var14 = new KTSParameterSpec.Builder(var13, this.kekLength * 8, var5).withKdfAlgorithm(var3.getKdf()).build();
               var12.init(4, this.privateKey, var14);
               Key var15 = var12.unwrap(Arrays.concatenate(var3.getKemct().getOctets(), var3.getEncryptedKey().getOctets()), var13, 3);
               return new JceGenericKey(var1, var15);
            } catch (Exception var10) {
               throw new OperatorException("Unable to wrap contents key: " + var10.getMessage(), var10);
            }
         } else {
            Cipher var6 = CMSUtils.createAsymmetricWrapper(this.helper, var3.getKem().getAlgorithm(), new HashMap());
            String var7 = CMSUtils.getWrapAlgorithmName(var4.getAlgorithm());
            KTSParameterSpec var8 = new KTSParameterSpec.Builder(var7, this.kekLength * 8, var5).withKdfAlgorithm(var3.getKdf()).build();
            var6.init(4, this.privateKey, var8);
            Key var9 = var6.unwrap(Arrays.concatenate(var3.getKemct().getOctets(), var3.getEncryptedKey().getOctets()), var7, 3);
            return new JceGenericKey(var1, var9);
         }
      } catch (Exception var11) {
         throw new OperatorException("exception encrypting key: " + var11.getMessage(), var11);
      }
   }
}
