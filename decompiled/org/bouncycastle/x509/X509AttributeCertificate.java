package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Extension;
import java.util.Date;

/** @deprecated */
public interface X509AttributeCertificate extends X509Extension {
   int getVersion();

   BigInteger getSerialNumber();

   Date getNotBefore();

   Date getNotAfter();

   AttributeCertificateHolder getHolder();

   AttributeCertificateIssuer getIssuer();

   X509Attribute[] getAttributes();

   X509Attribute[] getAttributes(String var1);

   boolean[] getIssuerUniqueID();

   void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;

   void checkValidity(Date var1) throws CertificateExpiredException, CertificateNotYetValidException;

   byte[] getSignature();

   void verify(PublicKey var1, String var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;

   byte[] getEncoded() throws IOException;
}
