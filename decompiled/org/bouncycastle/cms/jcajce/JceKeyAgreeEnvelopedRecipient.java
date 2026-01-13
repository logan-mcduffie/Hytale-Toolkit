package org.bouncycastle.cms.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.operator.InputDecryptor;

public class JceKeyAgreeEnvelopedRecipient extends JceKeyAgreeRecipient {
   public JceKeyAgreeEnvelopedRecipient(PrivateKey var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(
      AlgorithmIdentifier var1, final AlgorithmIdentifier var2, SubjectPublicKeyInfo var3, ASN1OctetString var4, byte[] var5
   ) throws CMSException {
      Key var6 = this.extractSecretKey(var1, var2, var3, var4, var5);
      final Cipher var7 = this.contentHelper.createContentCipher(var6, var2);
      return new RecipientOperator(new InputDecryptor() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var2;
         }

         @Override
         public InputStream getInputStream(InputStream var1) {
            return new CipherInputStream(var1, var7);
         }
      });
   }
}
