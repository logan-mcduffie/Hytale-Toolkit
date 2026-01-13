package org.bouncycastle.operator.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.operator.OperatorCreationException;

public class BcDefaultDigestProvider implements BcDigestProvider {
   private static final Map lookup = createTable();
   public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

   private static Map createTable() {
      HashMap var0 = new HashMap();
      var0.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA1Digest();
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA224Digest();
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA256Digest();
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA384Digest();
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA512Digest();
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA3Digest(224);
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA3Digest(256);
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA3Digest(384);
         }
      });
      var0.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHA3Digest(512);
         }
      });
      var0.put(NISTObjectIdentifiers.id_shake128, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHAKEDigest(128);
         }
      });
      var0.put(NISTObjectIdentifiers.id_shake256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SHAKEDigest(256);
         }
      });
      var0.put(NISTObjectIdentifiers.id_shake128_len, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new BcDefaultDigestProvider.AdjustedXof(new SHAKEDigest(128), ASN1Integer.getInstance(var1.getParameters()).intValueExact());
         }
      });
      var0.put(NISTObjectIdentifiers.id_shake256_len, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new BcDefaultDigestProvider.AdjustedXof(new SHAKEDigest(256), ASN1Integer.getInstance(var1.getParameters()).intValueExact());
         }
      });
      var0.put(PKCSObjectIdentifiers.md5, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new MD5Digest();
         }
      });
      var0.put(PKCSObjectIdentifiers.md4, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new MD4Digest();
         }
      });
      var0.put(PKCSObjectIdentifiers.md2, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new MD2Digest();
         }
      });
      var0.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new GOST3411Digest();
         }
      });
      var0.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new GOST3411_2012_256Digest();
         }
      });
      var0.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new GOST3411_2012_512Digest();
         }
      });
      var0.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new RIPEMD128Digest();
         }
      });
      var0.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new RIPEMD160Digest();
         }
      });
      var0.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new RIPEMD256Digest();
         }
      });
      var0.put(GMObjectIdentifiers.sm3, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new SM3Digest();
         }
      });
      var0.put(MiscObjectIdentifiers.blake3_256, new BcDigestProvider() {
         @Override
         public ExtendedDigest get(AlgorithmIdentifier var1) {
            return new Blake3Digest(256);
         }
      });
      return Collections.unmodifiableMap(var0);
   }

   private BcDefaultDigestProvider() {
   }

   @Override
   public ExtendedDigest get(AlgorithmIdentifier var1) throws OperatorCreationException {
      BcDigestProvider var2 = (BcDigestProvider)lookup.get(var1.getAlgorithm());
      if (var2 == null) {
         throw new OperatorCreationException("cannot recognise digest");
      } else {
         return var2.get(var1);
      }
   }

   private static class AdjustedXof implements Xof {
      private final Xof xof;
      private final int length;

      AdjustedXof(Xof var1, int var2) {
         this.xof = var1;
         this.length = var2;
      }

      @Override
      public String getAlgorithmName() {
         return this.xof.getAlgorithmName() + "-" + this.length;
      }

      @Override
      public int getDigestSize() {
         return (this.length + 7) / 8;
      }

      @Override
      public void update(byte var1) {
         this.xof.update(var1);
      }

      @Override
      public void update(byte[] var1, int var2, int var3) {
         this.xof.update(var1, var2, var3);
      }

      @Override
      public int doFinal(byte[] var1, int var2) {
         return this.doFinal(var1, var2, this.getDigestSize());
      }

      @Override
      public void reset() {
         this.xof.reset();
      }

      @Override
      public int getByteLength() {
         return this.xof.getByteLength();
      }

      @Override
      public int doFinal(byte[] var1, int var2, int var3) {
         return this.xof.doFinal(var1, var2, var3);
      }

      @Override
      public int doOutput(byte[] var1, int var2, int var3) {
         return this.xof.doOutput(var1, var2, var3);
      }
   }
}
