package org.bouncycastle.jcajce.spec;

import java.security.PrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class KEMExtractSpec extends KEMKDFSpec implements AlgorithmParameterSpec {
   private static final byte[] EMPTY_OTHER_INFO = new byte[0];
   private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(
      X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)
   );
   private final PrivateKey privateKey;
   private final byte[] encapsulation;

   private KEMExtractSpec(PrivateKey var1, byte[] var2, String var3, int var4, AlgorithmIdentifier var5, byte[] var6) {
      super(var5, var6, var3, var4);
      this.privateKey = var1;
      this.encapsulation = Arrays.clone(var2);
   }

   public KEMExtractSpec(PrivateKey var1, byte[] var2, String var3) {
      this(var1, var2, var3, 256);
   }

   public KEMExtractSpec(PrivateKey var1, byte[] var2, String var3, int var4) {
      this(var1, var2, var3, var4, DefKdf, EMPTY_OTHER_INFO);
   }

   public byte[] getEncapsulation() {
      return Arrays.clone(this.encapsulation);
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   public static final class Builder {
      private final PrivateKey privateKey;
      private final byte[] encapsulation;
      private final String algorithmName;
      private final int keySizeInBits;
      private AlgorithmIdentifier kdfAlgorithm;
      private byte[] otherInfo;

      public Builder(PrivateKey var1, byte[] var2, String var3, int var4) {
         this.privateKey = var1;
         this.encapsulation = Arrays.clone(var2);
         this.algorithmName = var3;
         this.keySizeInBits = var4;
         this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
         this.otherInfo = KEMExtractSpec.EMPTY_OTHER_INFO;
      }

      public KEMExtractSpec.Builder withNoKdf() {
         this.kdfAlgorithm = null;
         return this;
      }

      public KEMExtractSpec.Builder withKdfAlgorithm(AlgorithmIdentifier var1) {
         this.kdfAlgorithm = var1;
         return this;
      }

      public KEMExtractSpec.Builder withOtherInfo(byte[] var1) {
         this.otherInfo = var1 == null ? KEMExtractSpec.EMPTY_OTHER_INFO : Arrays.clone(var1);
         return this;
      }

      public KEMExtractSpec build() {
         return new KEMExtractSpec(this.privateKey, this.encapsulation, this.algorithmName, this.keySizeInBits, this.kdfAlgorithm, this.otherInfo);
      }
   }
}
