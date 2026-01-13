package org.bouncycastle.pkcs.bc;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;

class PKCS12PBEUtils {
   private static Map keySizes = new HashMap();
   private static Set noIvAlgs = new HashSet();
   private static Set desAlgs = new HashSet();

   static int getKeySize(ASN1ObjectIdentifier var0) {
      return (Integer)keySizes.get(var0);
   }

   static boolean hasNoIv(ASN1ObjectIdentifier var0) {
      return noIvAlgs.contains(var0);
   }

   static boolean isDesAlg(ASN1ObjectIdentifier var0) {
      return desAlgs.contains(var0);
   }

   static PaddedBufferedBlockCipher getEngine(ASN1ObjectIdentifier var0) {
      Object var1;
      if (!var0.equals(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC) && !var0.equals(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC)) {
         if (!var0.equals(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC) && !var0.equals(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC)) {
            throw new IllegalStateException("unknown algorithm");
         }

         var1 = new RC2Engine();
      } else {
         var1 = new DESedeEngine();
      }

      return new PaddedBufferedBlockCipher(new CBCBlockCipher((BlockCipher)var1), new PKCS7Padding());
   }

   static MacCalculator createMacCalculator(final ASN1ObjectIdentifier var0, ExtendedDigest var1, final PKCS12PBEParams var2, final char[] var3) {
      PKCS12ParametersGenerator var4 = new PKCS12ParametersGenerator(var1);
      var4.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(var3), var2.getIV(), var2.getIterations().intValue());
      KeyParameter var5 = (KeyParameter)var4.generateDerivedMacParameters(var1.getDigestSize() * 8);
      final HMac var6 = new HMac(var1);
      var6.init(var5);
      return new MacCalculator() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(var0, var2);
         }

         @Override
         public OutputStream getOutputStream() {
            return new MacOutputStream(var6);
         }

         @Override
         public byte[] getMac() {
            byte[] var1x = new byte[var6.getMacSize()];
            var6.doFinal(var1x, 0);
            return var1x;
         }

         @Override
         public GenericKey getKey() {
            return new GenericKey(this.getAlgorithmIdentifier(), PKCS12ParametersGenerator.PKCS12PasswordToBytes(var3));
         }
      };
   }

   static MacCalculator createPBMac1Calculator(final PBMAC1Params var0, PBKDF2Params var1, final char[] var2) {
      final HMac var3 = new HMac(getPrf(var0.getMessageAuthScheme().getAlgorithm()));
      PKCS5S2ParametersGenerator var4 = new PKCS5S2ParametersGenerator(getPrf(var1.getPrf().getAlgorithm()));
      var4.init(Strings.toUTF8ByteArray(var2), var1.getSalt(), BigIntegers.intValueExact(var1.getIterationCount()));
      CipherParameters var5 = var4.generateDerivedParameters(BigIntegers.intValueExact(var1.getKeyLength()) * 8);
      var3.init(var5);
      return new MacCalculator() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, var0);
         }

         @Override
         public OutputStream getOutputStream() {
            return new MacOutputStream(var3);
         }

         @Override
         public byte[] getMac() {
            byte[] var1x = new byte[var3.getMacSize()];
            var3.doFinal(var1x, 0);
            return var1x;
         }

         @Override
         public GenericKey getKey() {
            return new GenericKey(this.getAlgorithmIdentifier(), Strings.toUTF8ByteArray(var2));
         }
      };
   }

   private static Digest getPrf(ASN1ObjectIdentifier var0) {
      if (PKCSObjectIdentifiers.id_hmacWithSHA256.equals(var0)) {
         return new SHA256Digest();
      } else if (PKCSObjectIdentifiers.id_hmacWithSHA512.equals(var0)) {
         return new SHA512Digest();
      } else {
         throw new IllegalArgumentException("unknown prf id " + var0);
      }
   }

   static CipherParameters createCipherParameters(ASN1ObjectIdentifier var0, ExtendedDigest var1, int var2, PKCS12PBEParams var3, char[] var4) {
      PKCS12ParametersGenerator var5 = new PKCS12ParametersGenerator(var1);
      var5.init(PKCS12ParametersGenerator.PKCS12PasswordToBytes(var4), var3.getIV(), var3.getIterations().intValue());
      CipherParameters var6;
      if (hasNoIv(var0)) {
         var6 = var5.generateDerivedParameters(getKeySize(var0));
      } else {
         var6 = var5.generateDerivedParameters(getKeySize(var0), var2 * 8);
         if (isDesAlg(var0)) {
            DESedeParameters.setOddParity(((KeyParameter)((ParametersWithIV)var6).getParameters()).getKey());
         }
      }

      return var6;
   }

   static {
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, Integers.valueOf(128));
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, Integers.valueOf(40));
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, Integers.valueOf(192));
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, Integers.valueOf(128));
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, Integers.valueOf(128));
      keySizes.put(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, Integers.valueOf(40));
      noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4);
      noIvAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4);
      desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC);
      desAlgs.add(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC);
   }
}
