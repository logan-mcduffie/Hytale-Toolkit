package org.bouncycastle.pqc.jcajce.interfaces;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;

/** @deprecated */
public interface StateAwareSignature {
   void initVerify(PublicKey var1) throws InvalidKeyException;

   void initVerify(Certificate var1) throws InvalidKeyException;

   void initSign(PrivateKey var1) throws InvalidKeyException;

   void initSign(PrivateKey var1, SecureRandom var2) throws InvalidKeyException;

   byte[] sign() throws SignatureException;

   int sign(byte[] var1, int var2, int var3) throws SignatureException;

   boolean verify(byte[] var1) throws SignatureException;

   boolean verify(byte[] var1, int var2, int var3) throws SignatureException;

   void update(byte var1) throws SignatureException;

   void update(byte[] var1) throws SignatureException;

   void update(byte[] var1, int var2, int var3) throws SignatureException;

   void update(ByteBuffer var1) throws SignatureException;

   String getAlgorithm();

   boolean isSigningCapable();

   PrivateKey getUpdatedPrivateKey();
}
