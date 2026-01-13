package org.bouncycastle.jce.netscape;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class NetscapeCertRequest extends ASN1Object {
   AlgorithmIdentifier sigAlg;
   AlgorithmIdentifier keyAlg;
   byte[] sigBits;
   String challenge;
   DERBitString content;
   PublicKey pubkey;

   private static ASN1Sequence getReq(byte[] var0) throws IOException {
      ASN1InputStream var1 = new ASN1InputStream(new ByteArrayInputStream(var0));
      return ASN1Sequence.getInstance(var1.readObject());
   }

   public NetscapeCertRequest(byte[] var1) throws IOException {
      this(getReq(var1));
   }

   public NetscapeCertRequest(ASN1Sequence var1) {
      try {
         if (var1.size() != 3) {
            throw new IllegalArgumentException("invalid SPKAC (size):" + var1.size());
         } else {
            this.sigAlg = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
            this.sigBits = ((DERBitString)var1.getObjectAt(2)).getOctets();
            ASN1Sequence var2 = (ASN1Sequence)var1.getObjectAt(0);
            if (var2.size() != 2) {
               throw new IllegalArgumentException("invalid PKAC (len): " + var2.size());
            } else {
               this.challenge = ((ASN1IA5String)var2.getObjectAt(1)).getString();
               this.content = new DERBitString(var2);
               SubjectPublicKeyInfo var3 = SubjectPublicKeyInfo.getInstance(var2.getObjectAt(0));
               X509EncodedKeySpec var4 = new X509EncodedKeySpec(var3.getEncoded("DER"));
               this.keyAlg = var3.getAlgorithm();
               this.pubkey = KeyFactory.getInstance(this.keyAlg.getAlgorithm().getId(), "BC").generatePublic(var4);
            }
         }
      } catch (Exception var5) {
         throw new IllegalArgumentException(var5.toString());
      }
   }

   public NetscapeCertRequest(String var1, AlgorithmIdentifier var2, PublicKey var3) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
      this.challenge = var1;
      this.sigAlg = var2;
      this.pubkey = var3;
      ASN1EncodableVector var4 = new ASN1EncodableVector();
      var4.add(this.getKeySpec());
      var4.add(new DERIA5String(var1));

      try {
         this.content = new DERBitString(new DERSequence(var4));
      } catch (IOException var6) {
         throw new InvalidKeySpecException("exception encoding key: " + var6.toString());
      }
   }

   public String getChallenge() {
      return this.challenge;
   }

   public void setChallenge(String var1) {
      this.challenge = var1;
   }

   public AlgorithmIdentifier getSigningAlgorithm() {
      return this.sigAlg;
   }

   public void setSigningAlgorithm(AlgorithmIdentifier var1) {
      this.sigAlg = var1;
   }

   public AlgorithmIdentifier getKeyAlgorithm() {
      return this.keyAlg;
   }

   public void setKeyAlgorithm(AlgorithmIdentifier var1) {
      this.keyAlg = var1;
   }

   public PublicKey getPublicKey() {
      return this.pubkey;
   }

   public void setPublicKey(PublicKey var1) {
      this.pubkey = var1;
   }

   public boolean verify(String var1) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
      if (!var1.equals(this.challenge)) {
         return false;
      } else {
         Signature var2 = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
         var2.initVerify(this.pubkey);
         var2.update(this.content.getOctets());
         return var2.verify(this.sigBits);
      }
   }

   public void sign(PrivateKey var1) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
      this.sign(var1, null);
   }

   public void sign(PrivateKey var1, SecureRandom var2) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
      Signature var3 = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
      if (var2 != null) {
         var3.initSign(var1, var2);
      } else {
         var3.initSign(var1);
      }

      ASN1EncodableVector var4 = new ASN1EncodableVector();
      var4.add(this.getKeySpec());
      var4.add(new DERIA5String(this.challenge));

      try {
         var3.update(new DERSequence(var4).getEncoded("DER"));
      } catch (IOException var6) {
         throw new SignatureException(var6.getMessage());
      }

      this.sigBits = var3.sign();
   }

   private ASN1Primitive getKeySpec() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();
      Object var2 = null;

      try {
         var1.write(this.pubkey.getEncoded());
         var1.close();
         ASN1InputStream var3 = new ASN1InputStream(new ByteArrayInputStream(var1.toByteArray()));
         return var3.readObject();
      } catch (IOException var4) {
         throw new InvalidKeySpecException(var4.getMessage());
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      ASN1EncodableVector var2 = new ASN1EncodableVector();

      try {
         var2.add(this.getKeySpec());
      } catch (Exception var4) {
      }

      var2.add(new DERIA5String(this.challenge));
      var1.add(new DERSequence(var2));
      var1.add(this.sigAlg);
      var1.add(new DERBitString(this.sigBits));
      return new DERSequence(var1);
   }
}
