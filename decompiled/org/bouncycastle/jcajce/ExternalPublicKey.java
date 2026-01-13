package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Arrays;

public class ExternalPublicKey implements PublicKey {
   private final GeneralName location;
   private final AlgorithmIdentifier digestAlg;
   private final byte[] digest;

   public ExternalPublicKey(GeneralName var1, AlgorithmIdentifier var2, byte[] var3) {
      this.location = var1;
      this.digestAlg = var2;
      this.digest = Arrays.clone(var3);
   }

   public ExternalPublicKey(PublicKey var1, GeneralName var2, MessageDigest var3) {
      this(var2, MessageDigestUtils.getDigestAlgID(var3.getAlgorithm()), var3.digest(var1.getEncoded()));
   }

   public ExternalPublicKey(ExternalValue var1) {
      this(var1.getLocation(), var1.getHashAlg(), var1.getHashValue());
   }

   @Override
   public String getAlgorithm() {
      return "ExternalKey";
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      try {
         return new SubjectPublicKeyInfo(
               new AlgorithmIdentifier(BCObjectIdentifiers.external_value), new ExternalValue(this.location, this.digestAlg, this.digest)
            )
            .getEncoded("DER");
      } catch (IOException var2) {
         throw new IllegalStateException("unable to encode composite key: " + var2.getMessage());
      }
   }
}
