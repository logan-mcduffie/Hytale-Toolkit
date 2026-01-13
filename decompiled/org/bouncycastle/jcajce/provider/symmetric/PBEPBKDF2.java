package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PasswordConverter;
import org.bouncycastle.internal.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.jcajce.PBKDF2Key;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.util.Integers;

public class PBEPBKDF2 {
   private static final Map prfCodes = new HashMap();

   private PBEPBKDF2() {
   }

   static {
      prfCodes.put(CryptoProObjectIdentifiers.gostR3411Hmac, Integers.valueOf(6));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA1, Integers.valueOf(1));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA256, Integers.valueOf(4));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA224, Integers.valueOf(7));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA384, Integers.valueOf(8));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512, Integers.valueOf(9));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512_224, Integers.valueOf(15));
      prfCodes.put(PKCSObjectIdentifiers.id_hmacWithSHA512_256, Integers.valueOf(16));
      prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_256, Integers.valueOf(11));
      prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_224, Integers.valueOf(10));
      prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_384, Integers.valueOf(12));
      prfCodes.put(NISTObjectIdentifiers.id_hmacWithSHA3_512, Integers.valueOf(13));
      prfCodes.put(IANAObjectIdentifiers.hmacRIPEMD160, Integers.valueOf(2));
      prfCodes.put(GMObjectIdentifiers.hmac_sm3, Integers.valueOf(14));
   }

   public static class AlgParams extends BaseAlgorithmParameters {
      PBKDF2Params params;

      @Override
      protected byte[] engineGetEncoded() {
         try {
            return this.params.getEncoded("DER");
         } catch (IOException var2) {
            throw new RuntimeException("Oooops! " + var2.toString());
         }
      }

      @Override
      protected byte[] engineGetEncoded(String var1) {
         return this.isASN1FormatString(var1) ? this.engineGetEncoded() : null;
      }

      @Override
      protected AlgorithmParameterSpec localEngineGetParameterSpec(Class var1) throws InvalidParameterSpecException {
         if (var1 != PBEParameterSpec.class && var1 != AlgorithmParameterSpec.class) {
            throw new InvalidParameterSpecException("unknown parameter spec passed to PBKDF2 PBE parameters object.");
         } else {
            return new PBEParameterSpec(this.params.getSalt(), this.params.getIterationCount().intValue());
         }
      }

      @Override
      protected void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
         if (!(var1 instanceof PBEParameterSpec)) {
            throw new InvalidParameterSpecException("PBEParameterSpec required to initialise a PBKDF2 PBE parameters algorithm parameters object");
         } else {
            PBEParameterSpec var2 = (PBEParameterSpec)var1;
            this.params = new PBKDF2Params(var2.getSalt(), var2.getIterationCount());
         }
      }

      @Override
      protected void engineInit(byte[] var1) throws IOException {
         this.params = PBKDF2Params.getInstance(ASN1Primitive.fromByteArray(var1));
      }

      @Override
      protected void engineInit(byte[] var1, String var2) throws IOException {
         if (this.isASN1FormatString(var2)) {
            this.engineInit(var1);
         } else {
            throw new IOException("Unknown parameters format in PBKDF2 parameters object");
         }
      }

      @Override
      protected String engineToString() {
         return "PBKDF2 Parameters";
      }
   }

   public static class BasePBKDF2 extends BaseSecretKeyFactory {
      private int scheme;
      private int defaultDigest;

      public BasePBKDF2(String var1, int var2) {
         this(var1, var2, 1);
      }

      public BasePBKDF2(String var1, int var2, int var3) {
         super(var1, PKCSObjectIdentifiers.id_PBKDF2);
         this.scheme = var2;
         this.defaultDigest = var3;
      }

      @Override
      protected SecretKey engineGenerateSecret(KeySpec var1) throws InvalidKeySpecException {
         if (var1 instanceof PBEKeySpec) {
            PBEKeySpec var2 = (PBEKeySpec)var1;
            if (var2.getSalt() == null) {
               return new PBKDF2Key(((PBEKeySpec)var1).getPassword(), this.scheme == 1 ? PasswordConverter.ASCII : PasswordConverter.UTF8);
            } else if (var2.getIterationCount() <= 0) {
               throw new InvalidKeySpecException("positive iteration count required: " + var2.getIterationCount());
            } else if (var2.getKeyLength() <= 0) {
               throw new InvalidKeySpecException("positive key length required: " + var2.getKeyLength());
            } else if (var2.getPassword().length == 0) {
               throw new IllegalArgumentException("password empty");
            } else if (var2 instanceof PBKDF2KeySpec) {
               PBKDF2KeySpec var8 = (PBKDF2KeySpec)var2;
               int var9 = this.getDigestCode(var8.getPrf().getAlgorithm());
               int var10 = var2.getKeyLength();
               byte var11 = -1;
               CipherParameters var7 = PBE.Util.makePBEMacParameters(var2, this.scheme, var9, var10);
               return new BCPBEKey(this.algName, this.algOid, this.scheme, var9, var10, var11, var2, var7);
            } else {
               int var3 = this.defaultDigest;
               int var4 = var2.getKeyLength();
               byte var5 = -1;
               CipherParameters var6 = PBE.Util.makePBEMacParameters(var2, this.scheme, var3, var4);
               return new BCPBEKey(this.algName, this.algOid, this.scheme, var3, var4, var5, var2, var6);
            }
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec");
         }
      }

      private int getDigestCode(ASN1ObjectIdentifier var1) throws InvalidKeySpecException {
         Integer var2 = (Integer)PBEPBKDF2.prfCodes.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            throw new InvalidKeySpecException("Invalid KeySpec: unknown PRF algorithm " + var1);
         }
      }
   }

   public static class Mappings extends AlgorithmProvider {
      private static final String PREFIX = PBEPBKDF2.class.getName();

      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.PBKDF2", PREFIX + "$AlgParams");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2", PREFIX + "$PBKDF2withUTF8");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1", "PBKDF2");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1ANDUTF8", "PBKDF2");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory." + PKCSObjectIdentifiers.id_PBKDF2, "PBKDF2");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHASCII", PREFIX + "$PBKDF2with8BIT");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITH8BIT", "PBKDF2WITHASCII");
         var1.addAlgorithm("Alg.Alias.SecretKeyFactory.PBKDF2WITHHMACSHA1AND8BIT", "PBKDF2WITHASCII");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA224", PREFIX + "$PBKDF2withSHA224");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA256", PREFIX + "$PBKDF2withSHA256");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA384", PREFIX + "$PBKDF2withSHA384");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512", PREFIX + "$PBKDF2withSHA512");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512-224", PREFIX + "$PBKDF2withSHA512_224");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA512-256", PREFIX + "$PBKDF2withSHA512_256");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-224", PREFIX + "$PBKDF2withSHA3_224");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-256", PREFIX + "$PBKDF2withSHA3_256");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-384", PREFIX + "$PBKDF2withSHA3_384");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSHA3-512", PREFIX + "$PBKDF2withSHA3_512");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACGOST3411", PREFIX + "$PBKDF2withGOST3411");
         var1.addAlgorithm("SecretKeyFactory.PBKDF2WITHHMACSM3", PREFIX + "$PBKDF2withSM3");
      }
   }

   public static class PBKDF2with8BIT extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2with8BIT() {
         super("PBKDF2", 1);
      }
   }

   public static class PBKDF2withGOST3411 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withGOST3411() {
         super("PBKDF2", 5, 6);
      }
   }

   public static class PBKDF2withSHA224 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA224() {
         super("PBKDF2", 5, 7);
      }
   }

   public static class PBKDF2withSHA256 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA256() {
         super("PBKDF2", 5, 4);
      }
   }

   public static class PBKDF2withSHA384 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA384() {
         super("PBKDF2", 5, 8);
      }
   }

   public static class PBKDF2withSHA3_224 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA3_224() {
         super("PBKDF2", 5, 10);
      }
   }

   public static class PBKDF2withSHA3_256 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA3_256() {
         super("PBKDF2", 5, 11);
      }
   }

   public static class PBKDF2withSHA3_384 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA3_384() {
         super("PBKDF2", 5, 12);
      }
   }

   public static class PBKDF2withSHA3_512 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA3_512() {
         super("PBKDF2", 5, 13);
      }
   }

   public static class PBKDF2withSHA512 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA512() {
         super("PBKDF2", 5, 9);
      }
   }

   public static class PBKDF2withSHA512_224 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA512_224() {
         super("PBKDF2", 5, 15);
      }
   }

   public static class PBKDF2withSHA512_256 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSHA512_256() {
         super("PBKDF2", 5, 16);
      }
   }

   public static class PBKDF2withSM3 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withSM3() {
         super("PBKDF2", 5, 14);
      }
   }

   public static class PBKDF2withUTF8 extends PBEPBKDF2.BasePBKDF2 {
      public PBKDF2withUTF8() {
         super("PBKDF2", 5);
      }
   }
}
