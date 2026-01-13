package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.math.ec.ECCurve;

public class OpenSSHPublicKeyUtil {
   private static final String RSA = "ssh-rsa";
   private static final String ECDSA = "ecdsa";
   private static final String ED_25519 = "ssh-ed25519";
   private static final String DSS = "ssh-dss";
   private static final String FIDO2_EC_P256 = "sk-ecdsa-sha2-nistp256@openssh.com";
   private static final String FIDO_ED_25519 = "sk-ssh-ed25519@openssh.com";

   private OpenSSHPublicKeyUtil() {
   }

   public static AsymmetricKeyParameter parsePublicKey(byte[] var0) {
      SSHBuffer var1 = new SSHBuffer(var0);
      return parsePublicKey(var1);
   }

   public static byte[] encodePublicKey(AsymmetricKeyParameter var0) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("cipherParameters was null.");
      } else if (var0 instanceof RSAKeyParameters) {
         if (var0.isPrivate()) {
            throw new IllegalArgumentException("RSAKeyParamaters was for encryption");
         } else {
            RSAKeyParameters var6 = (RSAKeyParameters)var0;
            SSHBuilder var8 = new SSHBuilder();
            var8.writeString("ssh-rsa");
            var8.writeBigNum(var6.getExponent());
            var8.writeBigNum(var6.getModulus());
            return var8.getBytes();
         }
      } else if (var0 instanceof ECPublicKeyParameters) {
         SSHBuilder var5 = new SSHBuilder();
         String var7 = SSHNamedCurves.getNameForParameters(((ECPublicKeyParameters)var0).getParameters());
         if (var7 == null) {
            throw new IllegalArgumentException(
               "unable to derive ssh curve name for " + ((ECPublicKeyParameters)var0).getParameters().getCurve().getClass().getName()
            );
         } else {
            var5.writeString("ecdsa-sha2-" + var7);
            var5.writeString(var7);
            var5.writeBlock(((ECPublicKeyParameters)var0).getQ().getEncoded(false));
            return var5.getBytes();
         }
      } else if (var0 instanceof DSAPublicKeyParameters) {
         DSAPublicKeyParameters var4 = (DSAPublicKeyParameters)var0;
         DSAParameters var2 = var4.getParameters();
         SSHBuilder var3 = new SSHBuilder();
         var3.writeString("ssh-dss");
         var3.writeBigNum(var2.getP());
         var3.writeBigNum(var2.getQ());
         var3.writeBigNum(var2.getG());
         var3.writeBigNum(var4.getY());
         return var3.getBytes();
      } else if (var0 instanceof Ed25519PublicKeyParameters) {
         SSHBuilder var1 = new SSHBuilder();
         var1.writeString("ssh-ed25519");
         var1.writeBlock(((Ed25519PublicKeyParameters)var0).getEncoded());
         return var1.getBytes();
      } else {
         throw new IllegalArgumentException("unable to convert " + var0.getClass().getName() + " to public key");
      }
   }

   public static AsymmetricKeyParameter parsePublicKey(SSHBuffer var0) {
      Object var1 = null;
      String var2 = var0.readString();
      if ("ssh-rsa".equals(var2)) {
         BigInteger var3 = var0.readBigNumPositive();
         BigInteger var4 = var0.readBigNumPositive();
         var1 = new RSAKeyParameters(false, var4, var3);
      } else if ("ssh-dss".equals(var2)) {
         BigInteger var9 = var0.readBigNumPositive();
         BigInteger var14 = var0.readBigNumPositive();
         BigInteger var5 = var0.readBigNumPositive();
         BigInteger var6 = var0.readBigNumPositive();
         var1 = new DSAPublicKeyParameters(var6, new DSAParameters(var9, var14, var5));
      } else if (var2.startsWith("ecdsa")) {
         String var10 = var0.readString();
         ASN1ObjectIdentifier var15 = SSHNamedCurves.getByName(var10);
         X9ECParameters var18 = SSHNamedCurves.getParameters(var15);
         if (var18 == null) {
            throw new IllegalStateException("unable to find curve for " + var2 + " using curve name " + var10);
         }

         ECCurve var20 = var18.getCurve();
         byte[] var7 = var0.readBlock();
         var1 = new ECPublicKeyParameters(var20.decodePoint(var7), new ECNamedDomainParameters(var15, var18));
      } else if (var2.equals("sk-ecdsa-sha2-nistp256@openssh.com")) {
         String var11 = var0.readString();
         ASN1ObjectIdentifier var16 = SSHNamedCurves.getByName(var11);
         X9ECParameters var19 = SSHNamedCurves.getParameters(var16);
         if (var19 == null) {
            throw new IllegalStateException("unable to find curve for " + var2 + " using curve name " + var11);
         }

         ECCurve var21 = var19.getCurve();
         byte[] var22 = var0.readBlock();
         String var8 = var0.readString();
         var1 = new ECPublicKeyParameters(var21.decodePoint(var22), new ECNamedDomainParameters(var16, var19));
      } else if ("ssh-ed25519".equals(var2)) {
         byte[] var12 = var0.readBlock();
         if (var12.length != 32) {
            throw new IllegalStateException("public key value of wrong length");
         }

         var1 = new Ed25519PublicKeyParameters(var12, 0);
      } else if ("sk-ecdsa-sha2-nistp256@openssh.com".equals(var2)) {
         byte[] var13 = var0.readBlock();
         if (var13.length != 32) {
            throw new IllegalStateException("public key value of wrong length");
         }

         String var17 = var0.readString();
         var1 = new Ed25519PublicKeyParameters(var13, 0);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("unable to parse key");
      } else if (var0.hasRemaining()) {
         throw new IllegalArgumentException("decoded key has trailing data");
      } else {
         return (AsymmetricKeyParameter)var1;
      }
   }
}
