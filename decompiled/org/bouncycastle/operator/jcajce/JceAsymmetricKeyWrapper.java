package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PSource.PSpecified;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

public class JceAsymmetricKeyWrapper extends AsymmetricKeyWrapper {
   private static final Set gostAlgs = new HashSet();
   private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());
   private Map extraMappings = new HashMap();
   private PublicKey publicKey;
   private SecureRandom random;
   private static final Map digests = new HashMap();

   static boolean isGOST(ASN1ObjectIdentifier var0) {
      return gostAlgs.contains(var0);
   }

   public JceAsymmetricKeyWrapper(PublicKey var1) {
      super(SubjectPublicKeyInfo.getInstance(var1.getEncoded()).getAlgorithm());
      this.publicKey = var1;
   }

   public JceAsymmetricKeyWrapper(X509Certificate var1) {
      this(var1.getPublicKey());
   }

   public JceAsymmetricKeyWrapper(AlgorithmIdentifier var1, PublicKey var2) {
      super(var1);
      this.publicKey = var2;
   }

   public JceAsymmetricKeyWrapper(AlgorithmParameters var1, PublicKey var2) throws InvalidParameterSpecException {
      super(extractAlgID(var2, var1.getParameterSpec(AlgorithmParameterSpec.class)));
      this.publicKey = var2;
   }

   public JceAsymmetricKeyWrapper(AlgorithmParameterSpec var1, PublicKey var2) {
      super(extractAlgID(var2, var1));
      this.publicKey = var2;
   }

   public JceAsymmetricKeyWrapper setProvider(Provider var1) {
      this.helper = new OperatorHelper(new ProviderJcaJceHelper(var1));
      return this;
   }

   public JceAsymmetricKeyWrapper setProvider(String var1) {
      this.helper = new OperatorHelper(new NamedJcaJceHelper(var1));
      return this;
   }

   public JceAsymmetricKeyWrapper setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JceAsymmetricKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   @Override
   public byte[] generateWrappedKey(GenericKey var1) throws OperatorException {
      byte[] var2 = null;
      if (isGOST(this.getAlgorithmIdentifier().getAlgorithm())) {
         try {
            this.random = CryptoServicesRegistrar.getSecureRandom(this.random);
            KeyPairGenerator var22 = this.helper.createKeyPairGenerator(this.getAlgorithmIdentifier().getAlgorithm());
            var22.initialize(((ECPublicKey)this.publicKey).getParams(), this.random);
            KeyPair var23 = var22.generateKeyPair();
            byte[] var5 = new byte[8];
            this.random.nextBytes(var5);
            SubjectPublicKeyInfo var6 = SubjectPublicKeyInfo.getInstance(var23.getPublic().getEncoded());
            GostR3410TransportParameters var7;
            if (var6.getAlgorithm().getAlgorithm().on(RosstandartObjectIdentifiers.id_tc26)) {
               var7 = new GostR3410TransportParameters(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, var6, var5);
            } else {
               var7 = new GostR3410TransportParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, var6, var5);
            }

            KeyAgreement var8 = this.helper.createKeyAgreement(this.getAlgorithmIdentifier().getAlgorithm());
            var8.init(var23.getPrivate(), new UserKeyingMaterialSpec(var7.getUkm()));
            var8.doPhase(this.publicKey, true);
            SecretKey var9 = var8.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
            byte[] var10 = OperatorUtils.getJceKey(var1).getEncoded();
            Cipher var11 = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
            var11.init(3, var9, new GOST28147WrapParameterSpec(var7.getEncryptionParamSet(), var7.getUkm()));
            byte[] var12 = var11.wrap(new SecretKeySpec(var10, "GOST"));
            GostR3410KeyTransport var13 = new GostR3410KeyTransport(
               new Gost2814789EncryptedKey(Arrays.copyOfRange(var12, 0, 32), Arrays.copyOfRange(var12, 32, 36)), var7
            );
            return var13.getEncoded();
         } catch (Exception var14) {
            throw new OperatorException("exception wrapping key: " + var14.getMessage(), var14);
         }
      } else {
         Cipher var3 = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier(), this.extraMappings);
         AlgorithmParameters var4 = null;

         try {
            if (!this.getAlgorithmIdentifier().getAlgorithm().equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
               var4 = this.helper.createAlgorithmParameters(this.getAlgorithmIdentifier());
            }

            if (var4 != null) {
               var3.init(3, this.publicKey, var4, this.random);
            } else {
               var3.init(3, this.publicKey, this.random);
            }

            var2 = var3.wrap(OperatorUtils.getJceKey(var1));
         } catch (InvalidKeyException var17) {
         } catch (GeneralSecurityException var18) {
         } catch (IllegalStateException var19) {
         } catch (UnsupportedOperationException var20) {
         } catch (ProviderException var21) {
         }

         if (var2 == null) {
            try {
               if (var4 != null) {
                  var3.init(1, this.publicKey, var4, this.random);
               } else {
                  var3.init(1, this.publicKey, this.random);
               }

               var2 = var3.doFinal(OperatorUtils.getJceKey(var1).getEncoded());
            } catch (InvalidKeyException var15) {
               throw new OperatorException("unable to encrypt contents key", var15);
            } catch (GeneralSecurityException var16) {
               throw new OperatorException("unable to encrypt contents key", var16);
            }
         }

         return var2;
      }
   }

   private static AlgorithmIdentifier extractAlgID(PublicKey var0, AlgorithmParameterSpec var1) {
      if (var1 instanceof OAEPParameterSpec) {
         OAEPParameterSpec var2 = (OAEPParameterSpec)var1;
         if (var2.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
            if (var2.getPSource() instanceof PSpecified) {
               return new AlgorithmIdentifier(
                  PKCSObjectIdentifiers.id_RSAES_OAEP,
                  new RSAESOAEPparams(
                     getDigest(var2.getDigestAlgorithm()),
                     new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, getDigest(((MGF1ParameterSpec)var2.getMGFParameters()).getDigestAlgorithm())),
                     new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(((PSpecified)var2.getPSource()).getValue()))
                  )
               );
            } else {
               throw new IllegalArgumentException("unknown PSource: " + var2.getPSource().getAlgorithm());
            }
         } else {
            throw new IllegalArgumentException("unknown MGF: " + var2.getMGFAlgorithm());
         }
      } else {
         throw new IllegalArgumentException("unknown spec: " + var1.getClass().getName());
      }
   }

   private static AlgorithmIdentifier getDigest(String var0) {
      AlgorithmIdentifier var1 = (AlgorithmIdentifier)digests.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalArgumentException("unknown digest name: " + var0);
      }
   }

   static {
      gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
      gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
      gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
      digests.put("SHA1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE));
      digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE));
      digests.put("SHA224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE));
      digests.put("SHA-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE));
      digests.put("SHA256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
      digests.put("SHA-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
      digests.put("SHA384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE));
      digests.put("SHA-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE));
      digests.put("SHA512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE));
      digests.put("SHA-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE));
      digests.put("SHA512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
      digests.put("SHA-512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
      digests.put("SHA-512(224)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
      digests.put("SHA512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
      digests.put("SHA-512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
      digests.put("SHA-512(256)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
   }
}
