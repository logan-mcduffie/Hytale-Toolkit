package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST28147Parameters;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.CryptoProWrapEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST28147WrapEngine;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.util.Strings;

public final class GOST28147 {
   private static Map<ASN1ObjectIdentifier, String> oidMappings = new HashMap<>();
   private static Map<String, ASN1ObjectIdentifier> nameMappings = new HashMap<>();

   private GOST28147() {
   }

   static {
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_TestParamSet, "E-TEST");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, "E-A");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet, "E-B");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet, "E-C");
      oidMappings.put(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet, "E-D");
      oidMappings.put(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, "PARAM-Z");
      nameMappings.put("E-A", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet);
      nameMappings.put("E-B", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_B_ParamSet);
      nameMappings.put("E-C", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_C_ParamSet);
      nameMappings.put("E-D", CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_D_ParamSet);
      nameMappings.put("PARAM-Z", RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z);
   }

   public static class AlgParamGen extends BaseAlgorithmParameterGenerator {
      byte[] iv = new byte[8];
      byte[] sBox = GOST28147Engine.getSBox("E-A");

      @Override
      protected void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         if (var1 instanceof GOST28147ParameterSpec) {
            this.sBox = ((GOST28147ParameterSpec)var1).getSBox();
         } else {
            throw new InvalidAlgorithmParameterException("parameter spec not supported");
         }
      }

      @Override
      protected AlgorithmParameters engineGenerateParameters() {
         if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.random.nextBytes(this.iv);

         try {
            AlgorithmParameters var1 = this.createParametersInstance("GOST28147");
            var1.init(new GOST28147ParameterSpec(this.sBox, this.iv));
            return var1;
         } catch (Exception var3) {
            throw new RuntimeException(var3.getMessage());
         }
      }
   }

   public static class AlgParams extends GOST28147.BaseAlgParams {
      private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
      private byte[] iv;

      @Override
      protected byte[] localGetEncoded() throws IOException {
         return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 == IvParameterSpec.class) {
            return new IvParameterSpec(this.iv);
         } else if (var1 != GOST28147ParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + var1.getName());
         } else {
            return new GOST28147ParameterSpec(this.sBox, this.iv);
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (var1 instanceof IvParameterSpec) {
            this.iv = ((IvParameterSpec)var1).getIV();
         } else {
            if (!(var1 instanceof GOST28147ParameterSpec)) {
               throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }

            this.iv = ((GOST28147ParameterSpec)var1).getIV();

            try {
               this.sBox = getSBoxOID(((GOST28147ParameterSpec)var1).getSBox());
            } catch (IllegalArgumentException var3) {
               throw new InvalidParameterSpecException(var3.getMessage());
            }
         }
      }

      @Override
      protected void localInit(byte[] var1) throws IOException {
         ASN1Primitive var2 = ASN1Primitive.fromByteArray(var1);
         if (var2 instanceof ASN1OctetString) {
            this.iv = ASN1OctetString.getInstance(var2).getOctets();
         } else {
            if (!(var2 instanceof ASN1Sequence)) {
               throw new IOException("Unable to recognize parameters");
            }

            GOST28147Parameters var3 = GOST28147Parameters.getInstance(var2);
            this.sBox = var3.getEncryptionParamSet();
            this.iv = var3.getIV();
         }
      }

      @Override
      protected String engineToString() {
         return "GOST 28147 IV Parameters";
      }
   }

   public abstract static class BaseAlgParams extends BaseAlgorithmParameters {
      private ASN1ObjectIdentifier sBox = CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet;
      private byte[] iv;

      @Override
      protected final void engineInit(byte[] var1) throws IOException {
         this.engineInit(var1, "ASN.1");
      }

      @Override
      protected final byte[] engineGetEncoded() throws IOException {
         return this.engineGetEncoded("ASN.1");
      }

      @Override
      protected final byte[] engineGetEncoded(String var1) throws IOException {
         if (this.isASN1FormatString(var1)) {
            return this.localGetEncoded();
         } else {
            throw new IOException("Unknown parameter format: " + var1);
         }
      }

      @Override
      protected final void engineInit(byte[] var1, String var2) throws IOException {
         if (var1 == null) {
            throw new NullPointerException("Encoded parameters cannot be null");
         } else if (this.isASN1FormatString(var2)) {
            try {
               this.localInit(var1);
            } catch (IOException var4) {
               throw var4;
            } catch (Exception var5) {
               throw new IOException("Parameter parsing failed: " + var5.getMessage());
            }
         } else {
            throw new IOException("Unknown parameter format: " + var2);
         }
      }

      protected byte[] localGetEncoded() throws IOException {
         return new GOST28147Parameters(this.iv, this.sBox).getEncoded();
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 == IvParameterSpec.class) {
            return new IvParameterSpec(this.iv);
         } else if (var1 != GOST28147ParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + var1.getName());
         } else {
            return new GOST28147ParameterSpec(this.sBox, this.iv);
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (var1 instanceof IvParameterSpec) {
            this.iv = ((IvParameterSpec)var1).getIV();
         } else {
            if (!(var1 instanceof GOST28147ParameterSpec)) {
               throw new InvalidParameterSpecException("IvParameterSpec required to initialise a IV parameters algorithm parameters object");
            }

            this.iv = ((GOST28147ParameterSpec)var1).getIV();

            try {
               this.sBox = getSBoxOID(((GOST28147ParameterSpec)var1).getSBox());
            } catch (IllegalArgumentException var3) {
               throw new InvalidParameterSpecException(var3.getMessage());
            }
         }
      }

      protected static ASN1ObjectIdentifier getSBoxOID(String var0) {
         ASN1ObjectIdentifier var1 = null;
         if (var0 != null) {
            var1 = GOST28147.nameMappings.get(Strings.toUpperCase(var0));
         }

         if (var1 == null) {
            throw new IllegalArgumentException("Unknown SBOX name: " + var0);
         } else {
            return var1;
         }
      }

      protected static ASN1ObjectIdentifier getSBoxOID(byte[] var0) {
         return getSBoxOID(GOST28147Engine.getSBoxName(var0));
      }

      abstract void localInit(byte[] var1) throws IOException;
   }

   public static class CBC extends BaseBlockCipher {
      public CBC() {
         super(new CBCBlockCipher(new GOST28147Engine()), 64);
      }
   }

   public static class CryptoProWrap extends BaseWrapCipher {
      public CryptoProWrap() {
         super(new CryptoProWrapEngine());
      }
   }

   public static class ECB extends BaseBlockCipher {
      public ECB() {
         super(new GOST28147Engine());
      }
   }

   public static class GCFB extends BaseBlockCipher {
      public GCFB() {
         super(new BufferedBlockCipher(new GCFBBlockCipher(new GOST28147Engine())), 64);
      }
   }

   public static class GostWrap extends BaseWrapCipher {
      public GostWrap() {
         super(new GOST28147WrapEngine());
      }
   }

   public static class KeyGen extends BaseKeyGenerator {
      public KeyGen() {
         this(256);
      }

      public KeyGen(int var1) {
         super("GOST28147", var1, new CipherKeyGenerator());
      }
   }

   public static class Mac extends BaseMac {
      public Mac() {
         super(new GOST28147Mac());
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = GOST28147.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("Cipher.GOST28147", PREFIX + "$ECB");
         var1.addAlgorithm("Alg.Alias.Cipher.GOST", "GOST28147");
         var1.addAlgorithm("Alg.Alias.Cipher.GOST-28147", "GOST28147");
         var1.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.gostR28147_gcfb, PREFIX + "$GCFB");
         var1.addAlgorithm("KeyGenerator.GOST28147", PREFIX + "$KeyGen");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.GOST", "GOST28147");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.GOST-28147", "GOST28147");
         var1.addAlgorithm("Alg.Alias.KeyGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
         var1.addAlgorithm("AlgorithmParameters.GOST28147", PREFIX + "$AlgParams");
         var1.addAlgorithm("AlgorithmParameterGenerator.GOST28147", PREFIX + "$AlgParamGen");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + CryptoProObjectIdentifiers.gostR28147_gcfb, "GOST28147");
         var1.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap, PREFIX + "$CryptoProWrap");
         var1.addAlgorithm("Cipher." + CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap, PREFIX + "$GostWrap");
         var1.addAlgorithm("Mac.GOST28147MAC", PREFIX + "$Mac");
         var1.addAlgorithm("Alg.Alias.Mac.GOST28147", "GOST28147MAC");
      }
   }
}
