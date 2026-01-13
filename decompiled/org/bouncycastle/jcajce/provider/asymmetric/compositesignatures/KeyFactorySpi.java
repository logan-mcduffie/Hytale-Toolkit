package org.bouncycastle.jcajce.provider.asymmetric.compositesignatures;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.CompositePrivateKey;
import org.bouncycastle.jcajce.CompositePublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter {
   private static final AlgorithmIdentifier mlDsa44 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_44);
   private static final AlgorithmIdentifier mlDsa65 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_65);
   private static final AlgorithmIdentifier mlDsa87 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_ml_dsa_87);
   private static final AlgorithmIdentifier falcon512Identifier = new AlgorithmIdentifier(BCObjectIdentifiers.falcon_512);
   private static final AlgorithmIdentifier ed25519 = new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519);
   private static final AlgorithmIdentifier ecDsaP256 = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp256r1)
   );
   private static final AlgorithmIdentifier ecDsaBrainpoolP256r1 = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(TeleTrusTObjectIdentifiers.brainpoolP256r1)
   );
   private static final AlgorithmIdentifier rsa = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption);
   private static final AlgorithmIdentifier ed448 = new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448);
   private static final AlgorithmIdentifier ecDsaP384 = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp384r1)
   );
   private static final AlgorithmIdentifier ecDsaP521 = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(SECObjectIdentifiers.secp521r1)
   );
   private static final AlgorithmIdentifier ecDsaBrainpoolP384r1 = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(TeleTrusTObjectIdentifiers.brainpoolP384r1)
   );
   private static Map<ASN1ObjectIdentifier, AlgorithmIdentifier[]> pairings = new HashMap<>();
   private static Map<ASN1ObjectIdentifier, int[]> componentKeySizes = new HashMap<>();
   private JcaJceHelper helper;

   public KeyFactorySpi() {
      this(null);
   }

   public KeyFactorySpi(JcaJceHelper var1) {
      this.helper = var1;
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (this.helper == null) {
         this.helper = new BCJcaJceHelper();
      }

      try {
         if (var1 instanceof PrivateKey) {
            return this.generatePrivate(PrivateKeyInfo.getInstance(var1.getEncoded()));
         }

         if (var1 instanceof PublicKey) {
            return this.generatePublic(SubjectPublicKeyInfo.getInstance(var1.getEncoded()));
         }
      } catch (IOException var3) {
         throw new InvalidKeyException("Key could not be parsed: " + var3.getMessage());
      }

      throw new InvalidKeyException("Key not recognized");
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      if (this.helper == null) {
         this.helper = new BCJcaJceHelper();
      }

      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (!MiscObjectIdentifiers.id_alg_composite.equals(var2) && !MiscObjectIdentifiers.id_composite_key.equals(var2)) {
         try {
            List var17 = this.getKeyFactoriesFromIdentifier(var2);
            ASN1EncodableVector var19 = new ASN1EncodableVector();

            byte[] var18;
            try {
               var18 = DEROctetString.getInstance(var1.parsePrivateKey()).getOctets();
            } catch (Exception var13) {
               var18 = var1.getPrivateKey().getOctets();
            }

            var19.add(new DEROctetString(Arrays.copyOfRange(var18, 0, 32)));
            String var21 = ((KeyFactory)var17.get(1)).getAlgorithm();
            if (var21.equals("Ed25519")) {
               var19.add(new DEROctetString(Arrays.concatenate(new byte[]{4, 32}, Arrays.copyOfRange(var18, 32, var18.length))));
            } else if (var21.equals("Ed448")) {
               var19.add(new DEROctetString(Arrays.concatenate(new byte[]{4, 57}, Arrays.copyOfRange(var18, 32, var18.length))));
            } else {
               var19.add(new DEROctetString(Arrays.copyOfRange(var18, 32, var18.length)));
            }

            DERSequence var16 = new DERSequence(var19);
            PrivateKey[] var8 = new PrivateKey[var16.size()];
            AlgorithmIdentifier[] var9 = pairings.get(var2);

            for (int var10 = 0; var10 < var16.size(); var10++) {
               if (var16.getObjectAt(var10) instanceof ASN1OctetString) {
                  var19 = new ASN1EncodableVector(3);
                  var19.add(var1.getVersion());
                  var19.add(var9[var10]);
                  var19.add(var16.getObjectAt(var10));
                  PKCS8EncodedKeySpec var11 = new PKCS8EncodedKeySpec(PrivateKeyInfo.getInstance(new DERSequence(var19)).getEncoded());
                  var8[var10] = ((KeyFactory)var17.get(var10)).generatePrivate(var11);
               } else {
                  ASN1Sequence var22 = ASN1Sequence.getInstance(var16.getObjectAt(var10));
                  PKCS8EncodedKeySpec var12 = new PKCS8EncodedKeySpec(PrivateKeyInfo.getInstance(var22).getEncoded());
                  var8[var10] = ((KeyFactory)var17.get(var10)).generatePrivate(var12);
               }
            }

            return new CompositePrivateKey(var2, var8);
         } catch (GeneralSecurityException var14) {
            throw Exceptions.ioException(var14.getMessage(), var14);
         }
      } else {
         ASN1Sequence var3 = DERSequence.getInstance(var1.parsePrivateKey());
         PrivateKey[] var4 = new PrivateKey[var3.size()];

         for (int var5 = 0; var5 != var3.size(); var5++) {
            ASN1Sequence var6 = ASN1Sequence.getInstance(var3.getObjectAt(var5));
            PrivateKeyInfo var7 = PrivateKeyInfo.getInstance(var6);

            try {
               var4[var5] = this.helper
                  .createKeyFactory(var7.getPrivateKeyAlgorithm().getAlgorithm().getId())
                  .generatePrivate(new PKCS8EncodedKeySpec(var7.getEncoded()));
            } catch (Exception var15) {
               throw new IOException("cannot decode generic composite: " + var15.getMessage(), var15);
            }
         }

         return new CompositePrivateKey(var4);
      }
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      if (this.helper == null) {
         this.helper = new BCJcaJceHelper();
      }

      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      ASN1Sequence var3 = null;
      byte[][] var4 = new byte[2][];

      try {
         var3 = DERSequence.getInstance(var1.getPublicKeyData().getBytes());
      } catch (Exception var12) {
         var4 = this.split(var2, var1.getPublicKeyData());
      }

      if (!MiscObjectIdentifiers.id_alg_composite.equals(var2) && !MiscObjectIdentifiers.id_composite_key.equals(var2)) {
         try {
            int var14 = var3 == null ? var4.length : var3.size();
            List var15 = this.getKeyFactoriesFromIdentifier(var2);
            ASN1BitString[] var16 = new ASN1BitString[var14];

            for (int var17 = 0; var17 < var14; var17++) {
               if (var3 != null) {
                  if (var3.getObjectAt(var17) instanceof DEROctetString) {
                     var16[var17] = new DERBitString(((DEROctetString)var3.getObjectAt(var17)).getOctets());
                  } else {
                     var16[var17] = (DERBitString)var3.getObjectAt(var17);
                  }
               } else {
                  var16[var17] = new DERBitString(var4[var17]);
               }
            }

            X509EncodedKeySpec[] var18 = this.getKeysSpecs(var2, var16);
            PublicKey[] var9 = new PublicKey[var14];

            for (int var10 = 0; var10 < var14; var10++) {
               var9[var10] = ((KeyFactory)var15.get(var10)).generatePublic(var18[var10]);
            }

            return new CompositePublicKey(var2, var9);
         } catch (GeneralSecurityException var13) {
            throw Exceptions.ioException(var13.getMessage(), var13);
         }
      } else {
         ASN1Sequence var5 = ASN1Sequence.getInstance(var1.getPublicKeyData().getBytes());
         PublicKey[] var6 = new PublicKey[var5.size()];

         for (int var7 = 0; var7 != var5.size(); var7++) {
            SubjectPublicKeyInfo var8 = SubjectPublicKeyInfo.getInstance(var5.getObjectAt(var7));

            try {
               var6[var7] = this.helper.createKeyFactory(var8.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(var8.getEncoded()));
            } catch (Exception var11) {
               throw new IOException("cannot decode generic composite: " + var11.getMessage(), var11);
            }
         }

         return new CompositePublicKey(var6);
      }
   }

   byte[][] split(ASN1ObjectIdentifier var1, ASN1BitString var2) {
      int[] var3 = componentKeySizes.get(var1);
      byte[] var4 = var2.getOctets();
      byte[][] var5 = new byte[][]{new byte[var3[0]], new byte[var4.length - var3[0]]};
      System.arraycopy(var4, 0, var5[0], 0, var3[0]);
      System.arraycopy(var4, var3[0], var5[1], 0, var5[1].length);
      return var5;
   }

   private List<KeyFactory> getKeyFactoriesFromIdentifier(ASN1ObjectIdentifier var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      ArrayList var2 = new ArrayList();
      new ArrayList();
      String[] var4 = CompositeIndex.getPairing(var1);
      if (var4 == null) {
         throw new NoSuchAlgorithmException("Cannot create KeyFactories. Unsupported algorithm identifier.");
      } else {
         var2.add(this.helper.createKeyFactory(CompositeIndex.getBaseName(var4[0])));
         var2.add(this.helper.createKeyFactory(CompositeIndex.getBaseName(var4[1])));
         return Collections.unmodifiableList(var2);
      }
   }

   private X509EncodedKeySpec[] getKeysSpecs(ASN1ObjectIdentifier var1, ASN1BitString[] var2) throws IOException {
      X509EncodedKeySpec[] var3 = new X509EncodedKeySpec[var2.length];
      SubjectPublicKeyInfo[] var4 = new SubjectPublicKeyInfo[var2.length];
      AlgorithmIdentifier[] var5 = pairings.get(var1);
      if (var5 == null) {
         throw new IOException("Cannot create key specs. Unsupported algorithm identifier.");
      } else {
         var4[0] = new SubjectPublicKeyInfo(var5[0], var2[0]);
         var4[1] = new SubjectPublicKeyInfo(var5[1], var2[1]);
         var3[0] = new X509EncodedKeySpec(var4[0].getEncoded());
         var3[1] = new X509EncodedKeySpec(var4[1].getEncoded());
         return var3;
      }
   }

   static {
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new AlgorithmIdentifier[]{mlDsa44, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new AlgorithmIdentifier[]{mlDsa44, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new AlgorithmIdentifier[]{mlDsa44, ed25519});
      pairings.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new AlgorithmIdentifier[]{mlDsa44, ecDsaP256});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new AlgorithmIdentifier[]{mlDsa65, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaP256});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaP384});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new AlgorithmIdentifier[]{mlDsa65, ecDsaBrainpoolP256r1});
      pairings.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new AlgorithmIdentifier[]{mlDsa65, ed25519});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaP384});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaBrainpoolP384r1});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new AlgorithmIdentifier[]{mlDsa87, ed448});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa87, rsa});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new AlgorithmIdentifier[]{mlDsa87, ecDsaP521});
      pairings.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new AlgorithmIdentifier[]{mlDsa87, rsa});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PSS_SHA256, new int[]{1312, 268});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_RSA2048_PKCS15_SHA256, new int[]{1312, 284});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_Ed25519_SHA512, new int[]{1312, 32});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA44_ECDSA_P256_SHA256, new int[]{1312, 76});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PSS_SHA512, new int[]{1952, 256});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA3072_PKCS15_SHA512, new int[]{1952, 256});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PSS_SHA512, new int[]{1952, 542});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_RSA4096_PKCS15_SHA512, new int[]{1952, 542});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P256_SHA512, new int[]{1952, 76});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_P384_SHA512, new int[]{1952, 87});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_ECDSA_brainpoolP256r1_SHA512, new int[]{1952, 76});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA65_Ed25519_SHA512, new int[]{1952, 32});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P384_SHA512, new int[]{2592, 87});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_brainpoolP384r1_SHA512, new int[]{2592, 87});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_Ed448_SHAKE256, new int[]{2592, 57});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_RSA4096_PSS_SHA512, new int[]{2592, 542});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_RSA3072_PSS_SHA512, new int[]{2592, 256});
      componentKeySizes.put(IANAObjectIdentifiers.id_MLDSA87_ECDSA_P521_SHA512, new int[]{2592, 93});
   }
}
