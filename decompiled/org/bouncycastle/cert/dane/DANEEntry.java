package org.bouncycastle.cert.dane;

import java.io.IOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;

public class DANEEntry {
   public static final int CERT_USAGE_CA = 0;
   public static final int CERT_USAGE_PKIX_VALIDATE = 1;
   public static final int CERT_USAGE_TRUST_ANCHOR = 2;
   public static final int CERT_USAGE_ACCEPT = 3;
   static final int CERT_USAGE = 0;
   static final int SELECTOR = 1;
   static final int MATCHING_TYPE = 2;
   private final String domainName;
   private final byte[] flags;
   private final X509CertificateHolder certHolder;

   DANEEntry(String var1, byte[] var2, X509CertificateHolder var3) {
      this.flags = var2;
      this.domainName = var1;
      this.certHolder = var3;
   }

   public DANEEntry(String var1, byte[] var2) throws IOException {
      this(var1, Arrays.copyOfRange(var2, 0, 3), new X509CertificateHolder(Arrays.copyOfRange(var2, 3, var2.length)));
   }

   public byte[] getFlags() {
      return Arrays.clone(this.flags);
   }

   public X509CertificateHolder getCertificate() {
      return this.certHolder;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public byte[] getRDATA() throws IOException {
      byte[] var1 = this.certHolder.getEncoded();
      byte[] var2 = new byte[this.flags.length + var1.length];
      System.arraycopy(this.flags, 0, var2, 0, this.flags.length);
      System.arraycopy(var1, 0, var2, this.flags.length, var1.length);
      return var2;
   }

   public static boolean isValidCertificate(byte[] var0) {
      return (var0[0] >= 0 || var0[0] <= 3) && var0[1] == 0 && var0[2] == 0;
   }
}
