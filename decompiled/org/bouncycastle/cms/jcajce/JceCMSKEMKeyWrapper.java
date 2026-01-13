package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.KEMKeyWrapper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.DefaultKemEncapsulationLengthProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.KemEncapsulationLengthProvider;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyWrapper extends KEMKeyWrapper {
   private final KemEncapsulationLengthProvider kemEncLenProvider = new DefaultKemEncapsulationLengthProvider();
   private final AlgorithmIdentifier symWrapAlgorithm;
   private final int kekLength;
   private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
   private Map extraMappings = new HashMap();
   private PublicKey publicKey;
   private SecureRandom random;
   private AlgorithmIdentifier kdfAlgorithm = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE)
   );
   private byte[] encapsulation;

   public JceCMSKEMKeyWrapper(PublicKey var1, ASN1ObjectIdentifier var2) {
      super(
         var1 instanceof RSAPublicKey
            ? new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa)
            : SubjectPublicKeyInfo.getInstance(var1.getEncoded()).getAlgorithm()
      );
      this.publicKey = var1;
      this.symWrapAlgorithm = new AlgorithmIdentifier(var2);
      this.kekLength = CMSUtils.getKekSize(var2);
   }

   public JceCMSKEMKeyWrapper setProvider(Provider var1) {
      this.helper = new ProviderJcaJceExtHelper(var1);
      return this;
   }

   public JceCMSKEMKeyWrapper setProvider(String var1) {
      this.helper = new NamedJcaJceExtHelper(var1);
      return this;
   }

   public JceCMSKEMKeyWrapper setKDF(AlgorithmIdentifier var1) {
      this.kdfAlgorithm = var1;
      return this;
   }

   public JceCMSKEMKeyWrapper setSecureRandom(SecureRandom var1) {
      this.random = var1;
      return this;
   }

   public JceCMSKEMKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   @Override
   public byte[] getEncapsulation() {
      return this.encapsulation;
   }

   @Override
   public AlgorithmIdentifier getKdfAlgorithmIdentifier() {
      return this.kdfAlgorithm;
   }

   @Override
   public int getKekLength() {
      return this.kekLength;
   }

   @Override
   public AlgorithmIdentifier getWrapAlgorithmIdentifier() {
      return this.symWrapAlgorithm;
   }

   @Override
   public byte[] generateWrappedKey(GenericKey var1) throws OperatorException {
      try {
         byte[] var2 = new CMSORIforKEMOtherInfo(this.symWrapAlgorithm, this.kekLength).getEncoded();
         if (this.publicKey instanceof RSAPublicKey) {
            Cipher var10 = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());

            try {
               KTSParameterSpec var11 = new KTSParameterSpec.Builder(
                     CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, var2
                  )
                  .withKdfAlgorithm(this.kdfAlgorithm)
                  .build();
               var10.init(3, this.publicKey, var11, this.random);
               byte[] var12 = var10.wrap(CMSUtils.getJceKey(var1));
               int var13 = (((RSAPublicKey)this.publicKey).getModulus().bitLength() + 7) / 8;
               this.encapsulation = Arrays.copyOfRange(var12, 0, var13);
               return Arrays.copyOfRange(var12, var13, var12.length);
            } catch (Exception var7) {
               throw new OperatorException("Unable to wrap contents key: " + var7.getMessage(), var7);
            }
         } else {
            Cipher var3 = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());

            try {
               KTSParameterSpec var4 = new KTSParameterSpec.Builder(
                     CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, var2
                  )
                  .withKdfAlgorithm(this.kdfAlgorithm)
                  .build();
               var3.init(3, this.publicKey, var4, this.random);
               byte[] var5 = var3.wrap(CMSUtils.getJceKey(var1));
               int var6 = this.getKemEncLength(this.publicKey);
               this.encapsulation = Arrays.copyOfRange(var5, 0, var6);
               return Arrays.copyOfRange(var5, var6, var5.length);
            } catch (Exception var8) {
               throw new OperatorException("Unable to wrap contents key: " + var8.getMessage(), var8);
            }
         }
      } catch (Exception var9) {
         throw new OperatorException("unable to wrap contents key: " + var9.getMessage(), var9);
      }
   }

   private int getKemEncLength(PublicKey var1) {
      return this.kemEncLenProvider.getEncapsulationLength(SubjectPublicKeyInfo.getInstance(var1.getEncoded()).getAlgorithm());
   }
}
