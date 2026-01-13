package org.bouncycastle.openssl.jcajce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkix.jcajce.JcaPKIXIdentity;

public class JcaPKIXIdentityBuilder {
   private JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
   private JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();

   public JcaPKIXIdentityBuilder setProvider(Provider var1) {
      this.keyConverter = this.keyConverter.setProvider(var1);
      this.certConverter = this.certConverter.setProvider(var1);
      return this;
   }

   public JcaPKIXIdentityBuilder setProvider(String var1) {
      this.keyConverter = this.keyConverter.setProvider(var1);
      this.certConverter = this.certConverter.setProvider(var1);
      return this;
   }

   public JcaPKIXIdentity build(File var1, File var2) throws IOException, CertificateException {
      this.checkFile(var1);
      this.checkFile(var2);
      FileInputStream var3 = new FileInputStream(var1);
      FileInputStream var4 = new FileInputStream(var2);
      JcaPKIXIdentity var5 = this.build(var3, var4);
      var3.close();
      var4.close();
      return var5;
   }

   public JcaPKIXIdentity build(InputStream var1, InputStream var2) throws IOException, CertificateException {
      PEMParser var3 = new PEMParser(new InputStreamReader(var1));
      Object var5 = var3.readObject();
      PrivateKey var4;
      if (var5 instanceof PEMKeyPair) {
         PEMKeyPair var6 = (PEMKeyPair)var5;
         var4 = this.keyConverter.getPrivateKey(var6.getPrivateKeyInfo());
      } else {
         if (!(var5 instanceof PrivateKeyInfo)) {
            throw new IOException("unrecognised private key file");
         }

         var4 = this.keyConverter.getPrivateKey((PrivateKeyInfo)var5);
      }

      PEMParser var9 = new PEMParser(new InputStreamReader(var2));
      ArrayList var7 = new ArrayList();

      Object var8;
      while ((var8 = var9.readObject()) != null) {
         var7.add(this.certConverter.getCertificate((X509CertificateHolder)var8));
      }

      return new JcaPKIXIdentity(var4, var7.toArray(new X509Certificate[var7.size()]));
   }

   private void checkFile(File var1) throws IOException {
      if (!var1.canRead()) {
         if (var1.exists()) {
            throw new IOException("Unable to open file " + var1.getPath() + " for reading.");
         } else {
            throw new FileNotFoundException("Unable to open " + var1.getPath() + ": it does not exist.");
         }
      }
   }
}
