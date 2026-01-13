package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.util.Arrays;

public abstract class JceKeyTransRecipient implements KeyTransRecipient {
   private PrivateKey recipientKey;
   protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   protected EnvelopedDataHelper contentHelper = this.helper;
   protected Map extraMappings = new HashMap();
   protected boolean validateKeySize = false;
   protected boolean unwrappedKeyMustBeEncodable;

   public JceKeyTransRecipient(PrivateKey var1) {
      this.recipientKey = CMSUtils.cleanPrivateKey(var1);
   }

   public JceKeyTransRecipient setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKeyTransRecipient setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   public JceKeyTransRecipient setContentProvider(Provider var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(boolean var1) {
      this.unwrappedKeyMustBeEncodable = var1;
      return this;
   }

   public JceKeyTransRecipient setContentProvider(String var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKeyTransRecipient setKeySizeValidation(boolean var1) {
      this.validateKeySize = var1;
      return this;
   }

   protected Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      if (CMSUtils.isGOST(var1.getAlgorithm())) {
         try {
            GostR3410KeyTransport var16 = GostR3410KeyTransport.getInstance(var3);
            GostR3410TransportParameters var19 = var16.getTransportParameters();
            KeyFactory var22 = this.helper.createKeyFactory(var1.getAlgorithm());
            PublicKey var23 = var22.generatePublic(new X509EncodedKeySpec(var19.getEphemeralPublicKey().getEncoded()));
            KeyAgreement var8 = this.helper.createKeyAgreement(var1.getAlgorithm());
            var8.init(this.recipientKey, new UserKeyingMaterialSpec(var19.getUkm()));
            var8.doPhase(var23, true);
            SecretKey var9 = var8.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
            Cipher var10 = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
            var10.init(4, var9, new GOST28147WrapParameterSpec(var19.getEncryptionParamSet(), var19.getUkm()));
            Gost2814789EncryptedKey var11 = var16.getSessionEncryptedKey();
            return var10.unwrap(Arrays.concatenate(var11.getEncryptedKey(), var11.getMacKey()), this.helper.getBaseCipherName(var2.getAlgorithm()), 3);
         } catch (Exception var12) {
            throw new CMSException("exception unwrapping key: " + var12.getMessage(), var12);
         }
      } else if (CMSObjectIdentifiers.id_ori_kem.equals(var1.getAlgorithm())) {
         KEMRecipientInfo var15 = KEMRecipientInfo.getInstance(var1.getParameters());
         JceAsymmetricKeyUnwrapper var18 = this.helper
            .createAsymmetricUnwrapper(var15.getKem(), this.recipientKey)
            .setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
         if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier var7 : this.extraMappings.keySet()) {
               var18.setAlgorithmMapping(var7, (String)this.extraMappings.get(var7));
            }
         }

         try {
            Key var21 = this.helper.getJceKey(var2, var18.generateUnwrappedKey(var2, var3));
            if (this.validateKeySize) {
               this.helper.keySizeCheck(var2, var21);
            }

            return var21;
         } catch (OperatorException var13) {
            throw new CMSException("exception unwrapping key: " + var13.getMessage(), var13);
         }
      } else {
         JceAsymmetricKeyUnwrapper var4 = this.helper
            .createAsymmetricUnwrapper(var1, this.recipientKey)
            .setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
         if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier var6 : this.extraMappings.keySet()) {
               var4.setAlgorithmMapping(var6, (String)this.extraMappings.get(var6));
            }
         }

         try {
            Key var17 = this.helper.getJceKey(var2, var4.generateUnwrappedKey(var2, var3));
            if (this.validateKeySize) {
               if (var3.equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
                  this.helper.keySizeCheck(AlgorithmIdentifier.getInstance(var2.getParameters()), var17);
               } else {
                  this.helper.keySizeCheck(var2, var17);
               }
            }

            return var17;
         } catch (OperatorException var14) {
            throw new CMSException("exception unwrapping key: " + var14.getMessage(), var14);
         }
      }
   }
}
