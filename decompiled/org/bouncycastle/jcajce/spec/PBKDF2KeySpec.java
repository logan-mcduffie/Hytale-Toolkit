package org.bouncycastle.jcajce.spec;

import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBKDF2KeySpec extends PBEKeySpec {
   private static final AlgorithmIdentifier defaultPRF = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE);
   private AlgorithmIdentifier prf;

   public PBKDF2KeySpec(char[] var1, byte[] var2, int var3, int var4, AlgorithmIdentifier var5) {
      super(var1, var2, var3, var4);
      this.prf = var5;
   }

   public boolean isDefaultPrf() {
      return defaultPRF.equals(this.prf);
   }

   public AlgorithmIdentifier getPrf() {
      return this.prf;
   }
}
