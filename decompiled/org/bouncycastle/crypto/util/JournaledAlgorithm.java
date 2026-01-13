package org.bouncycastle.crypto.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

public class JournaledAlgorithm implements Encodable, Serializable {
   private transient JournalingSecureRandom journaling;
   private transient AlgorithmIdentifier algID;

   public JournaledAlgorithm(AlgorithmIdentifier var1, JournalingSecureRandom var2) {
      if (var1 == null) {
         throw new NullPointerException("AlgorithmIdentifier passed to JournaledAlgorithm is null");
      } else if (var2 == null) {
         throw new NullPointerException("JournalingSecureRandom passed to JournaledAlgorithm is null");
      } else {
         this.journaling = var2;
         this.algID = var1;
      }
   }

   public JournaledAlgorithm(byte[] var1) {
      this(var1, CryptoServicesRegistrar.getSecureRandom());
   }

   public JournaledAlgorithm(byte[] var1, SecureRandom var2) {
      if (var1 == null) {
         throw new NullPointerException("encoding passed to JournaledAlgorithm is null");
      } else if (var2 == null) {
         throw new NullPointerException("random passed to JournaledAlgorithm is null");
      } else {
         this.initFromEncoding(var1, var2);
      }
   }

   private void initFromEncoding(byte[] var1, SecureRandom var2) {
      ASN1Sequence var3 = ASN1Sequence.getInstance(var1);
      this.algID = AlgorithmIdentifier.getInstance(var3.getObjectAt(0));
      this.journaling = new JournalingSecureRandom(ASN1OctetString.getInstance(var3.getObjectAt(1)).getOctets(), var2);
   }

   public JournalingSecureRandom getJournalingSecureRandom() {
      return this.journaling;
   }

   public AlgorithmIdentifier getAlgorithmIdentifier() {
      return this.algID;
   }

   public void storeState(File var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("file for storage is null in JournaledAlgorithm");
      } else {
         FileOutputStream var2 = new FileOutputStream(var1);

         try {
            this.storeState(var2);
         } finally {
            var2.close();
         }
      }
   }

   public void storeState(OutputStream var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("output stream for storage is null in JournaledAlgorithm");
      } else {
         var1.write(this.getEncoded());
      }
   }

   public static JournaledAlgorithm getState(InputStream var0, SecureRandom var1) throws IOException, ClassNotFoundException {
      if (var0 == null) {
         throw new NullPointerException("stream for loading is null in JournaledAlgorithm");
      } else {
         BufferedInputStream var2 = new BufferedInputStream(var0);

         JournaledAlgorithm var3;
         try {
            var3 = new JournaledAlgorithm(Streams.readAll(var2), var1);
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   public static JournaledAlgorithm getState(File var0, SecureRandom var1) throws IOException, ClassNotFoundException {
      if (var0 == null) {
         throw new NullPointerException("File for loading is null in JournaledAlgorithm");
      } else {
         BufferedInputStream var2 = new BufferedInputStream(new FileInputStream(var0));

         JournaledAlgorithm var3;
         try {
            var3 = new JournaledAlgorithm(Streams.readAll(var2), var1);
         } finally {
            var2.close();
         }

         return var3;
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(this.algID);
      var1.add(new DEROctetString(this.journaling.getFullTranscript()));
      return new DERSequence(var1).getEncoded();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.initFromEncoding((byte[])var1.readObject(), CryptoServicesRegistrar.getSecureRandom());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
