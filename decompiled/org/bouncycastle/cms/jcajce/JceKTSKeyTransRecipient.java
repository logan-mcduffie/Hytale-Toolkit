package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.util.encoders.Hex;

public abstract class JceKTSKeyTransRecipient implements KeyTransRecipient {
   private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
   private final byte[] partyVInfo;
   private PrivateKey recipientKey;
   protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
   protected EnvelopedDataHelper contentHelper = this.helper;
   protected Map extraMappings = new HashMap();
   protected boolean validateKeySize = false;
   protected boolean unwrappedKeyMustBeEncodable;

   public JceKTSKeyTransRecipient(PrivateKey var1, byte[] var2) {
      this.recipientKey = CMSUtils.cleanPrivateKey(var1);
      this.partyVInfo = var2;
   }

   public JceKTSKeyTransRecipient setProvider(Provider var1) {
      this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKTSKeyTransRecipient setProvider(String var1) {
      this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(var1));
      this.contentHelper = this.helper;
      return this;
   }

   public JceKTSKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier var1, String var2) {
      this.extraMappings.put(var1, var2);
      return this;
   }

   public JceKTSKeyTransRecipient setContentProvider(Provider var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKTSKeyTransRecipient setContentProvider(String var1) {
      this.contentHelper = CMSUtils.createContentHelper(var1);
      return this;
   }

   public JceKTSKeyTransRecipient setKeySizeValidation(boolean var1) {
      this.validateKeySize = var1;
      return this;
   }

   protected Key extractSecretKey(AlgorithmIdentifier var1, AlgorithmIdentifier var2, byte[] var3) throws CMSException {
      JceKTSKeyUnwrapper var4 = this.helper.createAsymmetricUnwrapper(var1, this.recipientKey, ANONYMOUS_SENDER, this.partyVInfo);

      try {
         Key var5 = this.helper.getJceKey(var2, var4.generateUnwrappedKey(var2, var3));
         if (this.validateKeySize) {
            this.helper.keySizeCheck(var2, var5);
         }

         return var5;
      } catch (OperatorException var6) {
         throw new CMSException("exception unwrapping key: " + var6.getMessage(), var6);
      }
   }

   protected static byte[] getPartyVInfoFromRID(KeyTransRecipientId var0) throws IOException {
      return var0.getSerialNumber() != null
         ? new IssuerAndSerialNumber(var0.getIssuer(), var0.getSerialNumber()).getEncoded("DER")
         : new DEROctetString(var0.getSubjectKeyIdentifier()).getEncoded();
   }
}
