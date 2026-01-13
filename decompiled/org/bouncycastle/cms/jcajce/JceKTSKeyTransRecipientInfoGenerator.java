package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyWrapper;
import org.bouncycastle.util.encoders.Hex;

public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
   private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");

   private JceKTSKeyTransRecipientInfoGenerator(X509Certificate var1, IssuerAndSerialNumber var2, String var3, int var4) throws CertificateEncodingException {
      super(var2, new JceKTSKeyWrapper(var1, var3, var4, ANONYMOUS_SENDER, getEncodedRecipID(var2)));
   }

   public JceKTSKeyTransRecipientInfoGenerator(X509Certificate var1, String var2, int var3) throws CertificateEncodingException {
      this(var1, new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), var2, var3);
   }

   public JceKTSKeyTransRecipientInfoGenerator(byte[] var1, PublicKey var2, String var3, int var4) {
      super(var1, new JceKTSKeyWrapper(var2, var3, var4, ANONYMOUS_SENDER, getEncodedSubKeyId(var1)));
   }

   private static byte[] getEncodedRecipID(IssuerAndSerialNumber var0) throws CertificateEncodingException {
      try {
         return var0.getEncoded("DER");
      } catch (final IOException var2) {
         throw new CertificateEncodingException("Cannot process extracted IssuerAndSerialNumber: " + var2.getMessage()) {
            @Override
            public Throwable getCause() {
               return var2;
            }
         };
      }
   }

   private static byte[] getEncodedSubKeyId(byte[] var0) {
      try {
         return new DEROctetString(var0).getEncoded();
      } catch (final IOException var2) {
         throw new IllegalArgumentException("Cannot process subject key identifier: " + var2.getMessage()) {
            @Override
            public Throwable getCause() {
               return var2;
            }
         };
      }
   }

   public JceKTSKeyTransRecipientInfoGenerator(X509Certificate var1, AlgorithmIdentifier var2) throws CertificateEncodingException {
      super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(var1).toASN1Structure()), new JceAsymmetricKeyWrapper(var2, var1.getPublicKey()));
   }

   public JceKTSKeyTransRecipientInfoGenerator(byte[] var1, AlgorithmIdentifier var2, PublicKey var3) {
      super(var1, new JceAsymmetricKeyWrapper(var2, var3));
   }

   public JceKTSKeyTransRecipientInfoGenerator setProvider(String var1) {
      ((JceKTSKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }

   public JceKTSKeyTransRecipientInfoGenerator setProvider(Provider var1) {
      ((JceKTSKeyWrapper)this.wrapper).setProvider(var1);
      return this;
   }
}
