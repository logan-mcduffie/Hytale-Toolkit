package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import java.security.PrivateKey;
import javax.crypto.Mac;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JceKeyAgreeAuthenticatedRecipient extends JceKeyAgreeRecipient {
   public JceKeyAgreeAuthenticatedRecipient(PrivateKey var1) {
      super(var1);
   }

   @Override
   public RecipientOperator getRecipientOperator(
      AlgorithmIdentifier var1, final AlgorithmIdentifier var2, SubjectPublicKeyInfo var3, ASN1OctetString var4, byte[] var5
   ) throws CMSException {
      final Key var6 = this.extractSecretKey(var1, var2, var3, var4, var5);
      final Mac var7 = this.contentHelper.createContentMac(var6, var2);
      return new RecipientOperator(new MacCalculator() {
         @Override
         public AlgorithmIdentifier getAlgorithmIdentifier() {
            return var2;
         }

         @Override
         public GenericKey getKey() {
            return new JceGenericKey(var2, var6);
         }

         @Override
         public OutputStream getOutputStream() {
            return new MacOutputStream(var7);
         }

         @Override
         public byte[] getMac() {
            return var7.doFinal();
         }
      });
   }
}
