package org.bouncycastle.pkcs.bc;

import java.io.IOException;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCSException;

public class BcPKCS10CertificationRequest extends PKCS10CertificationRequest {
   public BcPKCS10CertificationRequest(CertificationRequest var1) {
      super(var1);
   }

   public BcPKCS10CertificationRequest(byte[] var1) throws IOException {
      super(var1);
   }

   public BcPKCS10CertificationRequest(PKCS10CertificationRequest var1) {
      super(var1.toASN1Structure());
   }

   public AsymmetricKeyParameter getPublicKey() throws PKCSException {
      try {
         return PublicKeyFactory.createKey(this.getSubjectPublicKeyInfo());
      } catch (IOException var2) {
         throw new PKCSException("error extracting key encoding: " + var2.getMessage(), var2);
      }
   }
}
