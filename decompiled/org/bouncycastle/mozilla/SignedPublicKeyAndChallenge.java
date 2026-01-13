package org.bouncycastle.mozilla;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;

public class SignedPublicKeyAndChallenge implements Encodable {
   protected final org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge spkacSeq;

   public SignedPublicKeyAndChallenge(byte[] var1) {
      this.spkacSeq = org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge.getInstance(var1);
   }

   protected SignedPublicKeyAndChallenge(org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge var1) {
      this.spkacSeq = var1;
   }

   public org.bouncycastle.asn1.mozilla.SignedPublicKeyAndChallenge toASN1Structure() {
      return this.spkacSeq;
   }

   /** @deprecated */
   public ASN1Primitive toASN1Primitive() {
      return this.spkacSeq.toASN1Primitive();
   }

   public PublicKeyAndChallenge getPublicKeyAndChallenge() {
      return this.spkacSeq.getPublicKeyAndChallenge();
   }

   public boolean isSignatureValid(ContentVerifierProvider var1) throws OperatorCreationException, IOException {
      ContentVerifier var2 = var1.get(this.spkacSeq.getSignatureAlgorithm());
      OutputStream var3 = var2.getOutputStream();
      this.spkacSeq.getPublicKeyAndChallenge().encodeTo(var3, "DER");
      var3.close();
      return var2.verify(this.spkacSeq.getSignature().getOctets());
   }

   /** @deprecated */
   public boolean verify() throws NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
      return this.verify((String)null);
   }

   /** @deprecated */
   public boolean verify(String var1) throws NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
      Signature var2 = null;
      if (var1 == null) {
         var2 = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId());
      } else {
         var2 = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId(), var1);
      }

      PublicKey var3 = this.getPublicKey(var1);
      var2.initVerify(var3);

      try {
         var2.update(this.spkacSeq.getPublicKeyAndChallenge().getEncoded());
         return var2.verify(this.spkacSeq.getSignature().getOctets());
      } catch (Exception var5) {
         throw new InvalidKeyException("error encoding public key");
      }
   }

   public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
      return this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
   }

   public String getChallenge() {
      return this.spkacSeq.getPublicKeyAndChallenge().getChallenge().getString();
   }

   /** @deprecated */
   public PublicKey getPublicKey(String var1) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
      SubjectPublicKeyInfo var2 = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();

      try {
         DERBitString var3 = new DERBitString(var2);
         X509EncodedKeySpec var4 = new X509EncodedKeySpec(var3.getOctets());
         AlgorithmIdentifier var5 = var2.getAlgorithm();
         KeyFactory var6 = KeyFactory.getInstance(var5.getAlgorithm().getId(), var1);
         return var6.generatePublic(var4);
      } catch (Exception var7) {
         throw new InvalidKeyException("error encoding public key");
      }
   }

   @Override
   public byte[] getEncoded() throws IOException {
      return this.toASN1Structure().getEncoded();
   }
}
