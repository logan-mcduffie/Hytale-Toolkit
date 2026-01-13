package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class KTSParameterSpec extends KEMKDFSpec implements AlgorithmParameterSpec {
   private final AlgorithmParameterSpec parameterSpec;

   protected KTSParameterSpec(String var1, int var2, AlgorithmParameterSpec var3, AlgorithmIdentifier var4, byte[] var5) {
      super(var4, var5, var1, var2);
      this.parameterSpec = var3;
   }

   public AlgorithmParameterSpec getParameterSpec() {
      return this.parameterSpec;
   }

   public static final class Builder {
      private final String algorithmName;
      private final int keySizeInBits;
      private AlgorithmParameterSpec parameterSpec;
      private AlgorithmIdentifier kdfAlgorithm;
      private byte[] otherInfo;

      public Builder(String var1, int var2) {
         this(var1, var2, null);
      }

      public Builder(String var1, int var2, byte[] var3) {
         this.algorithmName = var1;
         this.keySizeInBits = var2;
         this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
         this.otherInfo = var3 == null ? new byte[0] : Arrays.clone(var3);
      }

      public KTSParameterSpec.Builder withParameterSpec(AlgorithmParameterSpec var1) {
         this.parameterSpec = var1;
         return this;
      }

      public KTSParameterSpec.Builder withNoKdf() {
         this.kdfAlgorithm = null;
         return this;
      }

      public KTSParameterSpec.Builder withKdfAlgorithm(AlgorithmIdentifier var1) {
         if (var1 == null) {
            throw new NullPointerException("kdfAlgorithm cannot be null");
         } else {
            this.kdfAlgorithm = var1;
            return this;
         }
      }

      public KTSParameterSpec build() {
         return new KTSParameterSpec(this.algorithmName, this.keySizeInBits, this.parameterSpec, this.kdfAlgorithm, this.otherInfo);
      }
   }
}
