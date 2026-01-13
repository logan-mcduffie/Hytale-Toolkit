package org.bouncycastle.jcajce.provider.asymmetric.slhdsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;

public class SLHDSAKeyFactorySpi extends BaseKeyFactorySpi {
   private static final Set<ASN1ObjectIdentifier> pureKeyOids = new HashSet<>();
   private static final Set<ASN1ObjectIdentifier> hashKeyOids = new HashSet<>();

   public SLHDSAKeyFactorySpi(Set<ASN1ObjectIdentifier> var1) {
      super(var1);
   }

   public SLHDSAKeyFactorySpi(ASN1ObjectIdentifier var1) {
      super(var1);
   }

   @Override
   public final KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var1 instanceof BCSLHDSAPrivateKey) {
         if (PKCS8EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new PKCS8EncodedKeySpec(var1.getEncoded());
         }
      } else {
         if (!(var1 instanceof BCSLHDSAPublicKey)) {
            throw new InvalidKeySpecException("Unsupported key type: " + var1.getClass() + ".");
         }

         if (X509EncodedKeySpec.class.isAssignableFrom(var2)) {
            return new X509EncodedKeySpec(var1.getEncoded());
         }
      }

      throw new InvalidKeySpecException("Unknown key specification: " + var2 + ".");
   }

   @Override
   public final Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (!(var1 instanceof BCSLHDSAPrivateKey) && !(var1 instanceof BCSLHDSAPublicKey)) {
         throw new InvalidKeyException("Unsupported key type");
      } else {
         return var1;
      }
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      return new BCSLHDSAPrivateKey(var1);
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      return new BCSLHDSAPublicKey(var1);
   }

   static {
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_128f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_128s);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_192f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_192s);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_256f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_256s);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_128f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_128s);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_192f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_192s);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_256f);
      pureKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_256s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_128f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_128s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_192f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_192s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_256f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_sha2_256s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_128f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_128s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_192f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_192s);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_256f);
      hashKeyOids.add(NISTObjectIdentifiers.id_slh_dsa_shake_256s);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256);
      hashKeyOids.add(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256);
   }

   public static class Hash extends SLHDSAKeyFactorySpi {
      public Hash() {
         super(SLHDSAKeyFactorySpi.hashKeyOids);
      }
   }

   public static class HashSha2_128f extends SLHDSAKeyFactorySpi {
      public HashSha2_128f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128f_with_sha256);
      }
   }

   public static class HashSha2_128s extends SLHDSAKeyFactorySpi {
      public HashSha2_128s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_128s_with_sha256);
      }
   }

   public static class HashSha2_192f extends SLHDSAKeyFactorySpi {
      public HashSha2_192f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192f_with_sha512);
      }
   }

   public static class HashSha2_192s extends SLHDSAKeyFactorySpi {
      public HashSha2_192s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_192s_with_sha512);
      }
   }

   public static class HashSha2_256f extends SLHDSAKeyFactorySpi {
      public HashSha2_256f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256f_with_sha512);
      }
   }

   public static class HashSha2_256s extends SLHDSAKeyFactorySpi {
      public HashSha2_256s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_sha2_256s_with_sha512);
      }
   }

   public static class HashShake_128f extends SLHDSAKeyFactorySpi {
      public HashShake_128f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128f_with_shake128);
      }
   }

   public static class HashShake_128s extends SLHDSAKeyFactorySpi {
      public HashShake_128s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_128s_with_shake128);
      }
   }

   public static class HashShake_192f extends SLHDSAKeyFactorySpi {
      public HashShake_192f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192f_with_shake256);
      }
   }

   public static class HashShake_192s extends SLHDSAKeyFactorySpi {
      public HashShake_192s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_192s_with_shake256);
      }
   }

   public static class HashShake_256f extends SLHDSAKeyFactorySpi {
      public HashShake_256f() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256f_with_shake256);
      }
   }

   public static class HashShake_256s extends SLHDSAKeyFactorySpi {
      public HashShake_256s() {
         super(NISTObjectIdentifiers.id_hash_slh_dsa_shake_256s_with_shake256);
      }
   }

   public static class Pure extends SLHDSAKeyFactorySpi {
      public Pure() {
         super(SLHDSAKeyFactorySpi.pureKeyOids);
      }
   }

   public static class Sha2_128f extends SLHDSAKeyFactorySpi {
      public Sha2_128f() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_128f);
      }
   }

   public static class Sha2_128s extends SLHDSAKeyFactorySpi {
      public Sha2_128s() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_128s);
      }
   }

   public static class Sha2_192f extends SLHDSAKeyFactorySpi {
      public Sha2_192f() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_192f);
      }
   }

   public static class Sha2_192s extends SLHDSAKeyFactorySpi {
      public Sha2_192s() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_192s);
      }
   }

   public static class Sha2_256f extends SLHDSAKeyFactorySpi {
      public Sha2_256f() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_256f);
      }
   }

   public static class Sha2_256s extends SLHDSAKeyFactorySpi {
      public Sha2_256s() {
         super(NISTObjectIdentifiers.id_slh_dsa_sha2_256s);
      }
   }

   public static class Shake_128f extends SLHDSAKeyFactorySpi {
      public Shake_128f() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_128f);
      }
   }

   public static class Shake_128s extends SLHDSAKeyFactorySpi {
      public Shake_128s() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_128s);
      }
   }

   public static class Shake_192f extends SLHDSAKeyFactorySpi {
      public Shake_192f() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_192f);
      }
   }

   public static class Shake_192s extends SLHDSAKeyFactorySpi {
      public Shake_192s() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_192s);
      }
   }

   public static class Shake_256f extends SLHDSAKeyFactorySpi {
      public Shake_256f() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_256f);
      }
   }

   public static class Shake_256s extends SLHDSAKeyFactorySpi {
      public Shake_256s() {
         super(NISTObjectIdentifiers.id_slh_dsa_shake_256s);
      }
   }
}
