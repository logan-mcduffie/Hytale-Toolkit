package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.LoadStoreParameter;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map.Entry;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

public class JKSKeyStoreSpi extends KeyStoreSpi {
   private static final String NOT_IMPLEMENTED_MESSAGE = "BC JKS store is read-only and only supports certificate entries";
   private final Hashtable<String, JKSKeyStoreSpi.BCJKSTrustedCertEntry> certificateEntries = new Hashtable<>();
   private final JcaJceHelper helper;

   public JKSKeyStoreSpi(JcaJceHelper var1) {
      this.helper = var1;
   }

   @Override
   public boolean engineProbe(InputStream var1) throws IOException {
      DataInputStream var2;
      if (var1 instanceof DataInputStream) {
         var2 = (DataInputStream)var1;
      } else {
         var2 = new DataInputStream(var1);
      }

      int var3 = var2.readInt();
      int var4 = var2.readInt();
      return var3 == -17957139 && (var4 == 1 || var4 == 2);
   }

   @Override
   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      return null;
   }

   @Override
   public Certificate[] engineGetCertificateChain(String var1) {
      return null;
   }

   @Override
   public Certificate engineGetCertificate(String var1) {
      synchronized (this.certificateEntries) {
         JKSKeyStoreSpi.BCJKSTrustedCertEntry var3 = this.certificateEntries.get(var1);
         return var3 != null ? var3.cert : null;
      }
   }

   @Override
   public Date engineGetCreationDate(String var1) {
      synchronized (this.certificateEntries) {
         JKSKeyStoreSpi.BCJKSTrustedCertEntry var3 = this.certificateEntries.get(var1);
         return var3 != null ? var3.date : null;
      }
   }

   @Override
   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      throw new KeyStoreException("BC JKS store is read-only and only supports certificate entries");
   }

   @Override
   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      throw new KeyStoreException("BC JKS store is read-only and only supports certificate entries");
   }

   @Override
   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      throw new KeyStoreException("BC JKS store is read-only and only supports certificate entries");
   }

   @Override
   public void engineDeleteEntry(String var1) throws KeyStoreException {
      throw new KeyStoreException("BC JKS store is read-only and only supports certificate entries");
   }

   @Override
   public Enumeration<String> engineAliases() {
      synchronized (this.certificateEntries) {
         return this.certificateEntries.keys();
      }
   }

   @Override
   public boolean engineContainsAlias(String var1) {
      if (var1 == null) {
         throw new NullPointerException("alias value is null");
      } else {
         synchronized (this.certificateEntries) {
            return this.certificateEntries.containsKey(var1);
         }
      }
   }

   @Override
   public int engineSize() {
      return this.certificateEntries.size();
   }

   @Override
   public boolean engineIsKeyEntry(String var1) {
      return false;
   }

   @Override
   public boolean engineIsCertificateEntry(String var1) {
      synchronized (this.certificateEntries) {
         return this.certificateEntries.containsKey(var1);
      }
   }

   @Override
   public String engineGetCertificateAlias(Certificate var1) {
      synchronized (this.certificateEntries) {
         for (Entry var4 : this.certificateEntries.entrySet()) {
            if (((JKSKeyStoreSpi.BCJKSTrustedCertEntry)var4.getValue()).cert.equals(var1)) {
               return (String)var4.getKey();
            }
         }

         return null;
      }
   }

   @Override
   public void engineStore(OutputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      throw new IOException("BC JKS store is read-only and only supports certificate entries");
   }

   @Override
   public void engineLoad(LoadStoreParameter var1) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var1 == null) {
         this.engineLoad(null, null);
      } else {
         if (!(var1 instanceof BCLoadStoreParameter)) {
            throw new IllegalArgumentException("no support for 'param' of type " + var1.getClass().getName());
         }

         BCLoadStoreParameter var2 = (BCLoadStoreParameter)var1;
         this.engineLoad(var2.getInputStream(), ParameterUtil.extractPassword(var1));
      }
   }

   @Override
   public void engineLoad(InputStream var1, char[] var2) throws IOException, NoSuchAlgorithmException, CertificateException {
      if (var1 != null) {
         JKSKeyStoreSpi.ErasableByteStream var3 = this.validateStream(var1, var2);
         synchronized (this.certificateEntries) {
            try {
               DataInputStream var5 = new DataInputStream(var3);
               int var6 = var5.readInt();
               int var7 = var5.readInt();
               if (var6 == -17957139) {
                  CertificateFactory var8 = null;
                  Hashtable var9 = null;
                  switch (var7) {
                     case 1:
                        var8 = this.createCertFactory("X.509");
                        break;
                     case 2:
                        var9 = new Hashtable();
                        break;
                     default:
                        throw new IllegalStateException("unable to discern store version");
                  }

                  int var10 = var5.readInt();

                  for (int var11 = 0; var11 < var10; var11++) {
                     int var12 = var5.readInt();
                     switch (var12) {
                        case 1:
                           throw new IOException("BC JKS store is read-only and only supports certificate entries");
                        case 2:
                           String var13 = var5.readUTF();
                           Date var14 = new Date(var5.readLong());
                           if (var7 == 2) {
                              String var15 = var5.readUTF();
                              if (var9.containsKey(var15)) {
                                 var8 = (CertificateFactory)var9.get(var15);
                              } else {
                                 var8 = this.createCertFactory(var15);
                                 var9.put(var15, var8);
                              }
                           }

                           int var31 = var5.readInt();
                           byte[] var16 = new byte[var31];
                           var5.readFully(var16);
                           JKSKeyStoreSpi.ErasableByteStream var17 = new JKSKeyStoreSpi.ErasableByteStream(var16, 0, var16.length);

                           Certificate var18;
                           try {
                              var18 = var8.generateCertificate(var17);
                              if (var17.available() != 0) {
                                 throw new IOException("password incorrect or store tampered with");
                              }
                           } finally {
                              var17.erase();
                           }

                           this.certificateEntries.put(var13, new JKSKeyStoreSpi.BCJKSTrustedCertEntry(var14, var18));
                           break;
                        default:
                           throw new IllegalStateException("unable to discern entry type");
                     }
                  }
               }

               if (var3.available() != 0) {
                  throw new IOException("password incorrect or store tampered with");
               }
            } finally {
               var3.erase();
            }
         }
      }
   }

   private CertificateFactory createCertFactory(String var1) throws CertificateException {
      if (this.helper != null) {
         try {
            return this.helper.createCertificateFactory(var1);
         } catch (NoSuchProviderException var3) {
            throw new CertificateException(var3.toString());
         }
      } else {
         return CertificateFactory.getInstance(var1);
      }
   }

   private void addPassword(Digest var1, char[] var2) throws IOException {
      for (int var3 = 0; var3 < var2.length; var3++) {
         var1.update((byte)(var2[var3] >> '\b'));
         var1.update((byte)var2[var3]);
      }

      var1.update(Strings.toByteArray("Mighty Aphrodite"), 0, 16);
   }

   private JKSKeyStoreSpi.ErasableByteStream validateStream(InputStream var1, char[] var2) throws IOException {
      Digest var3 = DigestFactory.getDigest("SHA-1");
      byte[] var4 = Streams.readAll(var1);
      if (var2 != null) {
         this.addPassword(var3, var2);
         var3.update(var4, 0, var4.length - var3.getDigestSize());
         byte[] var5 = new byte[var3.getDigestSize()];
         var3.doFinal(var5, 0);
         byte[] var6 = new byte[var5.length];
         System.arraycopy(var4, var4.length - var5.length, var6, 0, var5.length);
         if (!Arrays.constantTimeAreEqual(var5, var6)) {
            Arrays.fill(var4, (byte)0);
            throw new IOException("password incorrect or store tampered with");
         } else {
            return new JKSKeyStoreSpi.ErasableByteStream(var4, 0, var4.length - var5.length);
         }
      } else {
         return new JKSKeyStoreSpi.ErasableByteStream(var4, 0, var4.length - var3.getDigestSize());
      }
   }

   private static final class BCJKSTrustedCertEntry {
      final Date date;
      final Certificate cert;

      public BCJKSTrustedCertEntry(Date var1, Certificate var2) {
         this.date = var1;
         this.cert = var2;
      }
   }

   private static final class ErasableByteStream extends ByteArrayInputStream {
      public ErasableByteStream(byte[] var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public void erase() {
         Arrays.fill(this.buf, (byte)0);
      }
   }
}
