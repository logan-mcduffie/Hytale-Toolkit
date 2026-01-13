package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;

public class JceKeyAgreeRecipientInfoGenerator extends KeyAgreeRecipientInfoGenerator {
   private SecretKeySizeProvider keySizeProvider = new DefaultSecretKeySizeProvider();
   private List recipientIDs = new ArrayList();
   private List recipientKeys = new ArrayList();
   private PublicKey senderPublicKey;
   private PrivateKey senderPrivateKey;
   private EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   private EnvelopedDataHelper wrappingHelper = null;
   private SecureRandom random;
   private KeyPair ephemeralKP;
   private byte[] userKeyingMaterial;
   private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();

   public JceKeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier var1, PrivateKey var2, PublicKey var3, ASN1ObjectIdentifier var4) {
      super(var1, SubjectPublicKeyInfo.getInstance(var3.getEncoded()), var4);
      this.senderPublicKey = var3;
      this.senderPrivateKey = CMSUtils.cleanPrivateKey(var2);
   }

   public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(byte[] var1) {
      this.userKeyingMaterial = Arrays.clone(var1);
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator setKeyWrappingProvider(Provider var1) {
      this.wrappingHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator setKeyWrappingProvider(String var1) {
      this.wrappingHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator addRecipient(X509Certificate var1) throws CertificateEncodingException {
      this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(var1)));
      this.recipientKeys.add(var1.getPublicKey());
      return this;
   }

   public JceKeyAgreeRecipientInfoGenerator addRecipient(byte[] var1, PublicKey var2) throws CertificateEncodingException {
      this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(var1)));
      this.recipientKeys.add(var2);
      return this;
   }

   @Override
   public ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier var1, AlgorithmIdentifier var2, GenericKey var3) throws CMSException {
      if (this.recipientIDs.isEmpty()) {
         throw new CMSException("No recipients associated with generator - use addRecipient()");
      } else {
         this.init(var1.getAlgorithm());
         PrivateKey var4 = this.senderPrivateKey;
         ASN1ObjectIdentifier var5 = var1.getAlgorithm();
         ASN1EncodableVector var6 = new ASN1EncodableVector();

         for (int var7 = 0; var7 != this.recipientIDs.size(); var7++) {
            PublicKey var8 = (PublicKey)this.recipientKeys.get(var7);
            KeyAgreeRecipientIdentifier var9 = (KeyAgreeRecipientIdentifier)this.recipientIDs.get(var7);

            try {
               ASN1ObjectIdentifier var11 = var2.getAlgorithm();
               Object var10;
               if (CMSUtils.isMQV(var5)) {
                  var10 = new MQVParameterSpec(this.ephemeralKP, var8, this.userKeyingMaterial);
               } else if (CMSUtils.isEC(var5)) {
                  byte[] var12 = ecc_cms_Generator.generateKDFMaterial(var2, this.keySizeProvider.getKeySize(var11), this.userKeyingMaterial);
                  var10 = new UserKeyingMaterialSpec(var12);
               } else if (CMSUtils.isRFC2631(var5)) {
                  if (this.userKeyingMaterial != null) {
                     var10 = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                  } else {
                     if (var5.equals(PKCSObjectIdentifiers.id_alg_SSDH)) {
                        throw new CMSException("User keying material must be set for static keys.");
                     }

                     var10 = null;
                  }
               } else {
                  if (!CMSUtils.isGOST(var5)) {
                     throw new CMSException("Unknown key agreement algorithm: " + var5);
                  }

                  if (this.userKeyingMaterial == null) {
                     throw new CMSException("User keying material must be set for static keys.");
                  }

                  var10 = new UserKeyingMaterialSpec(this.userKeyingMaterial);
               }

               KeyAgreement var21 = this.helper.createKeyAgreement(var5);
               var21.init(var4, (AlgorithmParameterSpec)var10, this.random);
               var21.doPhase(var8, true);
               SecretKey var13 = var21.generateSecret(var11.getId());
               EnvelopedDataHelper var14 = this.wrappingHelper != null ? this.wrappingHelper : this.helper;
               Cipher var15 = var14.createCipher(var11);
               DEROctetString var16;
               if (!var11.equals(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap)
                  && !var11.equals(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                  var15.init(3, var13, this.random);
                  byte[] var22 = var15.wrap(var14.getJceKey(var3));
                  var16 = new DEROctetString(var22);
               } else {
                  var15.init(3, var13, new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
                  byte[] var17 = var15.wrap(var14.getJceKey(var3));
                  Gost2814789EncryptedKey var18 = new Gost2814789EncryptedKey(
                     Arrays.copyOfRange(var17, 0, var17.length - 4), Arrays.copyOfRange(var17, var17.length - 4, var17.length)
                  );
                  var16 = new DEROctetString(var18.getEncoded("DER"));
               }

               var6.add(new RecipientEncryptedKey(var9, var16));
            } catch (GeneralSecurityException var19) {
               throw new CMSException("cannot perform agreement step: " + var19.getMessage(), var19);
            } catch (IOException var20) {
               throw new CMSException("unable to encode wrapped key: " + var20.getMessage(), var20);
            }
         }

         return new DERSequence(var6);
      }
   }

   @Override
   protected byte[] getUserKeyingMaterial(AlgorithmIdentifier var1) throws CMSException {
      this.init(var1.getAlgorithm());
      if (this.ephemeralKP != null) {
         OriginatorPublicKey var2 = this.createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance(this.ephemeralKP.getPublic().getEncoded()));

         try {
            return this.userKeyingMaterial != null
               ? new MQVuserKeyingMaterial(var2, new DEROctetString(this.userKeyingMaterial)).getEncoded()
               : new MQVuserKeyingMaterial(var2, null).getEncoded();
         } catch (IOException var4) {
            throw new CMSException("unable to encode user keying material: " + var4.getMessage(), var4);
         }
      } else {
         return this.userKeyingMaterial;
      }
   }

   private void init(ASN1ObjectIdentifier var1) throws CMSException {
      if (this.random == null) {
         this.random = new SecureRandom();
      }

      if (CMSUtils.isMQV(var1) && this.ephemeralKP == null) {
         try {
            SubjectPublicKeyInfo var2 = SubjectPublicKeyInfo.getInstance(this.senderPublicKey.getEncoded());
            AlgorithmParameters var3 = this.helper.createAlgorithmParameters(var1);
            var3.init(var2.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
            KeyPairGenerator var4 = this.helper.createKeyPairGenerator(var1);
            var4.initialize(var3.getParameterSpec(AlgorithmParameterSpec.class), this.random);
            this.ephemeralKP = var4.generateKeyPair();
         } catch (Exception var5) {
            throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + var5, var5);
         }
      }
   }
}
