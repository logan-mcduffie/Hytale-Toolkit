package org.bouncycastle.jcajce.provider.keystore.bc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.DigestInputStream;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.io.MacInputStream;
import org.bouncycastle.crypto.io.MacOutputStream;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.io.CipherOutputStream;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.BCKeyStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeOutputStream;

public class BcKeyStoreSpi extends KeyStoreSpi implements BCKeyStore {
   private static final int STORE_VERSION = 2;
   private static final int STORE_SALT_SIZE = 20;
   private static final String STORE_CIPHER = "PBEWithSHAAndTwofish-CBC";
   private static final int KEY_SALT_SIZE = 20;
   private static final int MIN_ITERATIONS = 1024;
   private static final String KEY_CIPHER = "PBEWithSHAAnd3-KeyTripleDES-CBC";
   static final int NULL = 0;
   static final int CERTIFICATE = 1;
   static final int KEY = 2;
   static final int SECRET = 3;
   static final int SEALED = 4;
   static final int KEY_PRIVATE = 0;
   static final int KEY_PUBLIC = 1;
   static final int KEY_SECRET = 2;
   protected Hashtable table = new Hashtable();
   protected SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   protected int version;
   private final JcaJceHelper helper = new BCJcaJceHelper();

   public BcKeyStoreSpi(int var1) {
      this.version = var1;
   }

   private void encodeCertificate(Certificate var1, DataOutputStream var2) throws IOException {
      try {
         byte[] var3 = var1.getEncoded();
         var2.writeUTF(var1.getType());
         var2.writeInt(var3.length);
         var2.write(var3);
      } catch (CertificateEncodingException var4) {
         throw new IOException(var4.toString());
      }
   }

   private Certificate decodeCertificate(DataInputStream var1) throws IOException {
      String var2 = var1.readUTF();
      byte[] var3 = new byte[var1.readInt()];
      var1.readFully(var3);

      try {
         CertificateFactory var4 = this.helper.createCertificateFactory(var2);
         ByteArrayInputStream var5 = new ByteArrayInputStream(var3);
         return var4.generateCertificate(var5);
      } catch (NoSuchProviderException var6) {
         throw new IOException(var6.toString());
      } catch (CertificateException var7) {
         throw new IOException(var7.toString());
      }
   }

   private void encodeKey(Key var1, DataOutputStream var2) throws IOException {
      byte[] var3 = var1.getEncoded();
      if (var3 == null) {
         throw new IOException("unable to store encoding of protected key");
      } else {
         if (var1 instanceof PrivateKey) {
            var2.write(0);
         } else if (var1 instanceof PublicKey) {
            var2.write(1);
         } else {
            var2.write(2);
         }

         var2.writeUTF(var1.getFormat());
         var2.writeUTF(var1.getAlgorithm());
         var2.writeInt(var3.length);
         var2.write(var3);
      }
   }

   private Key decodeKey(DataInputStream var1) throws IOException {
      int var2 = var1.read();
      String var3 = var1.readUTF();
      String var4 = var1.readUTF();
      byte[] var5 = new byte[var1.readInt()];
      var1.readFully(var5);
      Object var6;
      if (!var3.equals("PKCS#8") && !var3.equals("PKCS8")) {
         if (!var3.equals("X.509") && !var3.equals("X509")) {
            if (var3.equals("RAW")) {
               return new SecretKeySpec(var5, var4);
            }

            throw new IOException("Key format " + var3 + " not recognised!");
         }

         var6 = new X509EncodedKeySpec(var5);
      } else {
         var6 = new PKCS8EncodedKeySpec(var5);
      }

      try {
         switch (var2) {
            case 0:
               return BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(var5));
            case 1:
               return BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(var5));
            case 2:
               return this.helper.createSecretKeyFactory(var4).generateSecret((KeySpec)var6);
            default:
               throw new IOException("Key type " + var2 + " not recognised!");
         }
      } catch (Exception var8) {
         throw new IOException("Exception creating key: " + var8.toString());
      }
   }

   protected Cipher makePBECipher(String var1, int var2, char[] var3, byte[] var4, int var5) throws IOException {
      try {
         PBEKeySpec var6 = new PBEKeySpec(var3);
         SecretKeyFactory var7 = this.helper.createSecretKeyFactory(var1);
         PBEParameterSpec var8 = new PBEParameterSpec(var4, var5);
         Cipher var9 = this.helper.createCipher(var1);
         var9.init(var2, var7.generateSecret(var6), var8);
         return var9;
      } catch (Exception var10) {
         throw new IOException("Error initialising store of key store: " + var10);
      }
   }

   @Override
   public void setRandom(SecureRandom var1) {
      this.random = var1;
   }

   @Override
   public Enumeration engineAliases() {
      return this.table.keys();
   }

   @Override
   public boolean engineContainsAlias(String var1) {
      return this.table.get(var1) != null;
   }

   @Override
   public void engineDeleteEntry(String var1) throws KeyStoreException {
      Object var2 = this.table.get(var1);
      if (var2 != null) {
         this.table.remove(var1);
      }
   }

   @Override
   public Certificate engineGetCertificate(String var1) {
      BcKeyStoreSpi.StoreEntry var2 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      if (var2 != null) {
         if (var2.getType() == 1) {
            return (Certificate)var2.getObject();
         }

         Certificate[] var3 = var2.getCertificateChain();
         if (var3 != null) {
            return var3[0];
         }
      }

      return null;
   }

   @Override
   public String engineGetCertificateAlias(Certificate var1) {
      Enumeration var2 = this.table.elements();

      while (var2.hasMoreElements()) {
         BcKeyStoreSpi.StoreEntry var3 = (BcKeyStoreSpi.StoreEntry)var2.nextElement();
         if (var3.getObject() instanceof Certificate) {
            Certificate var4 = (Certificate)var3.getObject();
            if (var4.equals(var1)) {
               return var3.getAlias();
            }
         } else {
            Certificate[] var5 = var3.getCertificateChain();
            if (var5 != null && var5[0].equals(var1)) {
               return var3.getAlias();
            }
         }
      }

      return null;
   }

   @Override
   public Certificate[] engineGetCertificateChain(String var1) {
      BcKeyStoreSpi.StoreEntry var2 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      return var2 != null ? var2.getCertificateChain() : null;
   }

   @Override
   public Date engineGetCreationDate(String var1) {
      BcKeyStoreSpi.StoreEntry var2 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      return var2 != null ? var2.getDate() : null;
   }

   @Override
   public Key engineGetKey(String var1, char[] var2) throws NoSuchAlgorithmException, UnrecoverableKeyException {
      BcKeyStoreSpi.StoreEntry var3 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      return var3 != null && var3.getType() != 1 ? (Key)var3.getObject(var2) : null;
   }

   @Override
   public boolean engineIsCertificateEntry(String var1) {
      BcKeyStoreSpi.StoreEntry var2 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      return var2 != null && var2.getType() == 1;
   }

   @Override
   public boolean engineIsKeyEntry(String var1) {
      BcKeyStoreSpi.StoreEntry var2 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      return var2 != null && var2.getType() != 1;
   }

   @Override
   public void engineSetCertificateEntry(String var1, Certificate var2) throws KeyStoreException {
      BcKeyStoreSpi.StoreEntry var3 = (BcKeyStoreSpi.StoreEntry)this.table.get(var1);
      if (var3 != null && var3.getType() != 1) {
         throw new KeyStoreException("key store already has a key entry with alias " + var1);
      } else {
         this.table.put(var1, new BcKeyStoreSpi.StoreEntry(var1, var2));
      }
   }

   @Override
   public void engineSetKeyEntry(String var1, byte[] var2, Certificate[] var3) throws KeyStoreException {
      this.table.put(var1, new BcKeyStoreSpi.StoreEntry(var1, var2, var3));
   }

   @Override
   public void engineSetKeyEntry(String var1, Key var2, char[] var3, Certificate[] var4) throws KeyStoreException {
      if (var2 instanceof PrivateKey) {
         if (var4 == null) {
            throw new KeyStoreException("no certificate chain for private key");
         }

         if (var2.getEncoded() == null) {
            this.table.put(var1, new BcKeyStoreSpi.StoreEntry(var1, new Date(), 2, var2, var4));
            return;
         }
      }

      try {
         this.table.put(var1, new BcKeyStoreSpi.StoreEntry(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw new BcKeyStoreSpi.BCKeyStoreException(var6.toString(), var6);
      }
   }

   @Override
   public int engineSize() {
      return this.table.size();
   }

   protected void loadStore(InputStream var1) throws IOException {
      DataInputStream var2 = new DataInputStream(var1);

      for (int var3 = var2.read(); var3 > 0; var3 = var2.read()) {
         String var4 = var2.readUTF();
         Date var5 = new Date(var2.readLong());
         int var6 = var2.readInt();
         Certificate[] var7 = null;
         if (var6 != 0) {
            var7 = new Certificate[var6];

            for (int var8 = 0; var8 != var6; var8++) {
               var7[var8] = this.decodeCertificate(var2);
            }
         }

         switch (var3) {
            case 1:
               Certificate var11 = this.decodeCertificate(var2);
               this.table.put(var4, new BcKeyStoreSpi.StoreEntry(var4, var5, 1, var11));
               break;
            case 2:
               Key var9 = this.decodeKey(var2);
               this.table.put(var4, new BcKeyStoreSpi.StoreEntry(var4, var5, 2, var9, var7));
               break;
            case 3:
            case 4:
               byte[] var10 = new byte[var2.readInt()];
               var2.readFully(var10);
               this.table.put(var4, new BcKeyStoreSpi.StoreEntry(var4, var5, var3, var10, var7));
               break;
            default:
               throw new IOException("Unknown object type in store.");
         }
      }
   }

   protected void saveStore(OutputStream var1) throws IOException {
      Enumeration var2 = this.table.elements();
      DataOutputStream var3 = new DataOutputStream(var1);

      while (var2.hasMoreElements()) {
         BcKeyStoreSpi.StoreEntry var4 = (BcKeyStoreSpi.StoreEntry)var2.nextElement();
         var3.write(var4.getType());
         var3.writeUTF(var4.getAlias());
         var3.writeLong(var4.getDate().getTime());
         Certificate[] var5 = var4.getCertificateChain();
         if (var5 == null) {
            var3.writeInt(0);
         } else {
            var3.writeInt(var5.length);

            for (int var6 = 0; var6 != var5.length; var6++) {
               this.encodeCertificate(var5[var6], var3);
            }
         }

         switch (var4.getType()) {
            case 1:
               this.encodeCertificate((Certificate)var4.getObject(), var3);
               break;
            case 2:
               this.encodeKey((Key)var4.getObject(), var3);
               break;
            case 3:
            case 4:
               byte[] var7 = (byte[])var4.getObject();
               var3.writeInt(var7.length);
               var3.write(var7);
               break;
            default:
               throw new IOException("Unknown object type in store.");
         }
      }

      var3.write(0);
   }

   @Override
   public void engineLoad(InputStream var1, char[] var2) throws IOException {
      this.table.clear();
      if (var1 != null) {
         DataInputStream var3 = new DataInputStream(var1);
         int var4 = var3.readInt();
         if (var4 != 2 && var4 != 0 && var4 != 1) {
            throw new IOException("Wrong version of key store.");
         } else {
            int var5 = var3.readInt();
            if (var5 <= 0) {
               throw new IOException("Invalid salt detected");
            } else {
               byte[] var6 = new byte[var5];
               var3.readFully(var6);
               int var7 = var3.readInt();
               HMac var8 = new HMac(new SHA1Digest());
               if (var2 != null && var2.length != 0) {
                  byte[] var15 = PBEParametersGenerator.PKCS12PasswordToBytes(var2);
                  PKCS12ParametersGenerator var10 = new PKCS12ParametersGenerator(new SHA1Digest());
                  var10.init(var15, var6, var7);
                  CipherParameters var11;
                  if (var4 != 2) {
                     var11 = var10.generateDerivedMacParameters(var8.getMacSize());
                  } else {
                     var11 = var10.generateDerivedMacParameters(var8.getMacSize() * 8);
                  }

                  Arrays.fill(var15, (byte)0);
                  var8.init(var11);
                  MacInputStream var12 = new MacInputStream(var3, var8);
                  this.loadStore(var12);
                  byte[] var13 = new byte[var8.getMacSize()];
                  var8.doFinal(var13, 0);
                  byte[] var14 = new byte[var8.getMacSize()];
                  var3.readFully(var14);
                  if (!Arrays.constantTimeAreEqual(var13, var14)) {
                     this.table.clear();
                     throw new IOException("KeyStore integrity check failed.");
                  }
               } else {
                  this.loadStore(var3);
                  byte[] var9 = new byte[var8.getMacSize()];
                  var3.readFully(var9);
               }
            }
         }
      }
   }

   @Override
   public void engineStore(OutputStream var1, char[] var2) throws IOException {
      DataOutputStream var3 = new DataOutputStream(var1);
      byte[] var4 = new byte[20];
      int var5 = 1024 + (this.random.nextInt() & 1023);
      this.random.nextBytes(var4);
      var3.writeInt(this.version);
      var3.writeInt(var4.length);
      var3.write(var4);
      var3.writeInt(var5);
      HMac var6 = new HMac(new SHA1Digest());
      MacOutputStream var7 = new MacOutputStream(var6);
      PKCS12ParametersGenerator var8 = new PKCS12ParametersGenerator(new SHA1Digest());
      byte[] var9 = PBEParametersGenerator.PKCS12PasswordToBytes(var2);
      var8.init(var9, var4, var5);
      if (this.version < 2) {
         var6.init(var8.generateDerivedMacParameters(var6.getMacSize()));
      } else {
         var6.init(var8.generateDerivedMacParameters(var6.getMacSize() * 8));
      }

      for (int var10 = 0; var10 != var9.length; var10++) {
         var9[var10] = 0;
      }

      this.saveStore(new TeeOutputStream(var3, var7));
      byte[] var11 = new byte[var6.getMacSize()];
      var6.doFinal(var11, 0);
      var3.write(var11);
      var3.close();
   }

   private static class BCKeyStoreException extends KeyStoreException {
      private final Exception cause;

      public BCKeyStoreException(String var1, Exception var2) {
         super(var1);
         this.cause = var2;
      }

      @Override
      public Throwable getCause() {
         return this.cause;
      }
   }

   public static class BouncyCastleStore extends BcKeyStoreSpi {
      public BouncyCastleStore() {
         super(1);
      }

      @Override
      public void engineLoad(InputStream var1, char[] var2) throws IOException {
         this.table.clear();
         if (var1 != null) {
            DataInputStream var3 = new DataInputStream(var1);
            int var4 = var3.readInt();
            if (var4 != 2 && var4 != 0 && var4 != 1) {
               throw new IOException("Wrong version of key store.");
            } else {
               byte[] var5 = new byte[var3.readInt()];
               if (var5.length != 20) {
                  throw new IOException("Key store corrupted.");
               } else {
                  var3.readFully(var5);
                  int var6 = var3.readInt();
                  if (var6 >= 0 && var6 <= 65536) {
                     String var7;
                     if (var4 == 0) {
                        var7 = "OldPBEWithSHAAndTwofish-CBC";
                     } else {
                        var7 = "PBEWithSHAAndTwofish-CBC";
                     }

                     Cipher var8 = this.makePBECipher(var7, 2, var2, var5, var6);
                     CipherInputStream var9 = new CipherInputStream(var3, var8);
                     SHA1Digest var10 = new SHA1Digest();
                     DigestInputStream var11 = new DigestInputStream(var9, var10);
                     this.loadStore(var11);
                     byte[] var12 = new byte[var10.getDigestSize()];
                     var10.doFinal(var12, 0);
                     byte[] var13 = new byte[var10.getDigestSize()];
                     Streams.readFully(var9, var13);
                     if (!Arrays.constantTimeAreEqual(var12, var13)) {
                        this.table.clear();
                        throw new IOException("KeyStore integrity check failed.");
                     }
                  } else {
                     throw new IOException("Key store corrupted.");
                  }
               }
            }
         }
      }

      @Override
      public void engineStore(OutputStream var1, char[] var2) throws IOException {
         DataOutputStream var4 = new DataOutputStream(var1);
         byte[] var5 = new byte[20];
         int var6 = 1024 + (this.random.nextInt() & 1023);
         this.random.nextBytes(var5);
         var4.writeInt(this.version);
         var4.writeInt(var5.length);
         var4.write(var5);
         var4.writeInt(var6);
         Cipher var3 = this.makePBECipher("PBEWithSHAAndTwofish-CBC", 1, var2, var5, var6);
         CipherOutputStream var7 = new CipherOutputStream(var4, var3);
         DigestOutputStream var8 = new DigestOutputStream(new SHA1Digest());
         this.saveStore(new TeeOutputStream(var7, var8));
         byte[] var9 = var8.getDigest();
         var7.write(var9);
         var7.close();
      }
   }

   public static class Std extends BcKeyStoreSpi {
      public Std() {
         super(2);
      }
   }

   private class StoreEntry {
      int type;
      String alias;
      Object obj;
      Certificate[] certChain;
      Date date = new Date();

      StoreEntry(String nullx, Certificate nullxx) {
         this.type = 1;
         this.alias = nullx;
         this.obj = nullxx;
         this.certChain = null;
      }

      StoreEntry(String nullx, byte[] nullxx, Certificate[] nullxxx) {
         this.type = 3;
         this.alias = nullx;
         this.obj = nullxx;
         this.certChain = nullxxx;
      }

      StoreEntry(String nullx, Key nullxx, char[] nullxxx, Certificate[] nullxxxx) throws Exception {
         this.type = 4;
         this.alias = nullx;
         this.certChain = nullxxxx;
         byte[] var6 = new byte[20];
         BcKeyStoreSpi.this.random.nextBytes(var6);
         int var7 = 1024 + (BcKeyStoreSpi.this.random.nextInt() & 1023);
         ByteArrayOutputStream var8 = new ByteArrayOutputStream();
         DataOutputStream var9 = new DataOutputStream(var8);
         var9.writeInt(var6.length);
         var9.write(var6);
         var9.writeInt(var7);
         Cipher var10 = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, nullxxx, var6, var7);
         CipherOutputStream var11 = new CipherOutputStream(var9, var10);
         var9 = new DataOutputStream(var11);
         BcKeyStoreSpi.this.encodeKey(nullxx, var9);
         var9.close();
         this.obj = var8.toByteArray();
      }

      StoreEntry(String nullx, Date nullxx, int nullxxx, Object nullxxxx) {
         this.alias = nullx;
         this.date = nullxx;
         this.type = nullxxx;
         this.obj = nullxxxx;
      }

      StoreEntry(String nullx, Date nullxx, int nullxxx, Object nullxxxx, Certificate[] nullxxxxx) {
         this.alias = nullx;
         this.date = nullxx;
         this.type = nullxxx;
         this.obj = nullxxxx;
         this.certChain = nullxxxxx;
      }

      int getType() {
         return this.type;
      }

      String getAlias() {
         return this.alias;
      }

      Object getObject() {
         return this.obj;
      }

      Object getObject(char[] var1) throws NoSuchAlgorithmException, UnrecoverableKeyException {
         if ((var1 == null || var1.length == 0) && this.obj instanceof Key) {
            return this.obj;
         } else if (this.type == 4) {
            ByteArrayInputStream var2 = new ByteArrayInputStream((byte[])this.obj);
            DataInputStream var3 = new DataInputStream(var2);

            try {
               byte[] var4 = new byte[var3.readInt()];
               var3.readFully(var4);
               int var5 = var3.readInt();
               Cipher var6 = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 2, var1, var4, var5);
               CipherInputStream var7 = new CipherInputStream(var3, var6);

               try {
                  return BcKeyStoreSpi.this.decodeKey(new DataInputStream(var7));
               } catch (Exception var15) {
                  var2 = new ByteArrayInputStream((byte[])this.obj);
                  var3 = new DataInputStream(var2);
                  var4 = new byte[var3.readInt()];
                  var3.readFully(var4);
                  var5 = var3.readInt();
                  var6 = BcKeyStoreSpi.this.makePBECipher("BrokenPBEWithSHAAnd3-KeyTripleDES-CBC", 2, var1, var4, var5);
                  var7 = new CipherInputStream(var3, var6);
                  Object var9 = null;

                  try {
                     var9 = BcKeyStoreSpi.this.decodeKey(new DataInputStream(var7));
                  } catch (Exception var14) {
                     var2 = new ByteArrayInputStream((byte[])this.obj);
                     var3 = new DataInputStream(var2);
                     var4 = new byte[var3.readInt()];
                     var3.readFully(var4);
                     var5 = var3.readInt();
                     var6 = BcKeyStoreSpi.this.makePBECipher("OldPBEWithSHAAnd3-KeyTripleDES-CBC", 2, var1, var4, var5);
                     var7 = new CipherInputStream(var3, var6);
                     var9 = BcKeyStoreSpi.this.decodeKey(new DataInputStream(var7));
                  }

                  if (var9 != null) {
                     ByteArrayOutputStream var10 = new ByteArrayOutputStream();
                     DataOutputStream var11 = new DataOutputStream(var10);
                     var11.writeInt(var4.length);
                     var11.write(var4);
                     var11.writeInt(var5);
                     Cipher var12 = BcKeyStoreSpi.this.makePBECipher("PBEWithSHAAnd3-KeyTripleDES-CBC", 1, var1, var4, var5);
                     CipherOutputStream var13 = new CipherOutputStream(var11, var12);
                     var11 = new DataOutputStream(var13);
                     BcKeyStoreSpi.this.encodeKey((Key)var9, var11);
                     var11.close();
                     this.obj = var10.toByteArray();
                     return var9;
                  } else {
                     throw new UnrecoverableKeyException("no match");
                  }
               }
            } catch (Exception var16) {
               throw new UnrecoverableKeyException("no match");
            }
         } else {
            throw new RuntimeException("forget something!");
         }
      }

      Certificate[] getCertificateChain() {
         return this.certChain;
      }

      Date getDate() {
         return this.date;
      }
   }

   public static class Version1 extends BcKeyStoreSpi {
      public Version1() {
         super(1);
         if (!Properties.isOverrideSet("org.bouncycastle.bks.enable_v1")) {
            throw new IllegalStateException("BKS-V1 not enabled");
         }
      }
   }
}
