package org.bouncycastle.jcajce.spec;

import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class KEMGenerateSpec extends KEMKDFSpec implements AlgorithmParameterSpec {
   private static final byte[] EMPTY_OTHER_INFO = new byte[0];
   private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)
   );
   private final PublicKey publicKey;

   private KEMGenerateSpec(PublicKey var1, String var2, int var3, AlgorithmIdentifier var4, byte[] var5) {
      super(var4, var5, var2, var3);
      this.publicKey = var1;
   }

   public KEMGenerateSpec(PublicKey var1, String var2) {
      this(var1, var2, 256, DefKdf, EMPTY_OTHER_INFO);
   }

   public KEMGenerateSpec(PublicKey var1, String var2, int var3) {
      this(var1, var2, var3, DefKdf, EMPTY_OTHER_INFO);
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public static final class Builder {
      private final PublicKey publicKey;
      private final String algorithmName;
      private final int keySizeInBits;
      private AlgorithmIdentifier kdfAlgorithm;
      private byte[] otherInfo;

      public Builder(PublicKey var1, String var2, int var3) {
         this.publicKey = var1;
         this.algorithmName = var2;
         this.keySizeInBits = var3;
         this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
         this.otherInfo = KEMGenerateSpec.EMPTY_OTHER_INFO;
      }

      public KEMGenerateSpec.Builder withNoKdf() {
         this.kdfAlgorithm = null;
         return this;
      }

      public KEMGenerateSpec.Builder withKdfAlgorithm(AlgorithmIdentifier var1) {
         this.kdfAlgorithm = var1;
         return this;
      }

      public KEMGenerateSpec.Builder withOtherInfo(byte[] var1) {
         this.otherInfo = var1 == null ? KEMGenerateSpec.EMPTY_OTHER_INFO : Arrays.clone(var1);
         return this;
      }

      public KEMGenerateSpec build() {
         return new KEMGenerateSpec(this.publicKey, this.algorithmName, this.keySizeInBits, this.kdfAlgorithm, this.otherInfo);
      }
   }
}
