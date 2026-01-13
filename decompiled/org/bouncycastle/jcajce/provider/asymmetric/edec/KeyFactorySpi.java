package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.jcajce.spec.RawEncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class KeyFactorySpi extends BaseKeyFactorySpi implements AsymmetricKeyInfoConverter {
   static final byte[] x448Prefix = Hex.decode("3042300506032b656f033900");
   static final byte[] x25519Prefix = Hex.decode("302a300506032b656e032100");
   static final byte[] Ed448Prefix = Hex.decode("3043300506032b6571033a00");
   static final byte[] Ed25519Prefix = Hex.decode("302a300506032b6570032100");
   private static final byte x448_type = 111;
   private static final byte x25519_type = 110;
   private static final byte Ed448_type = 113;
   private static final byte Ed25519_type = 112;
   String algorithm;
   private final boolean isXdh;
   private final int specificBase;

   public KeyFactorySpi(String var1, boolean var2, int var3) {
      this.algorithm = var1;
      this.isXdh = var2;
      this.specificBase = var3;
   }

   @Override
   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      throw new InvalidKeyException("key type unknown");
   }

   @Override
   protected KeySpec engineGetKeySpec(Key var1, Class var2) throws InvalidKeySpecException {
      if (var2.isAssignableFrom(OpenSSHPrivateKeySpec.class) && var1 instanceof BCEdDSAPrivateKey) {
         try {
            ASN1Sequence var8 = ASN1Sequence.getInstance(var1.getEncoded());
            ASN1OctetString var9 = ASN1OctetString.getInstance(var8.getObjectAt(2));
            byte[] var5 = ASN1OctetString.getInstance(ASN1Primitive.fromByteArray(var9.getOctets())).getOctets();
            return new OpenSSHPrivateKeySpec(OpenSSHPrivateKeyUtil.encodePrivateKey(new Ed25519PrivateKeyParameters(var5)));
         } catch (IOException var6) {
            throw new InvalidKeySpecException(var6.getMessage(), var6.getCause());
         }
      } else if (var2.isAssignableFrom(OpenSSHPublicKeySpec.class) && var1 instanceof BCEdDSAPublicKey) {
         try {
            byte[] var3 = var1.getEncoded();
            if (!Arrays.areEqual(Ed25519Prefix, 0, Ed25519Prefix.length, var3, 0, var3.length - 32)) {
               throw new InvalidKeySpecException("Invalid Ed25519 public key encoding");
            } else {
               Ed25519PublicKeyParameters var4 = new Ed25519PublicKeyParameters(var3, Ed25519Prefix.length);
               return new OpenSSHPublicKeySpec(OpenSSHPublicKeyUtil.encodePublicKey(var4));
            }
         } catch (IOException var7) {
            throw new InvalidKeySpecException(var7.getMessage(), var7.getCause());
         }
      } else {
         if (var2.isAssignableFrom(RawEncodedKeySpec.class)) {
            if (var1 instanceof XDHPublicKey) {
               return new RawEncodedKeySpec(((XDHPublicKey)var1).getUEncoding());
            }

            if (var1 instanceof EdDSAPublicKey) {
               return new RawEncodedKeySpec(((EdDSAPublicKey)var1).getPointEncoding());
            }
         }

         return super.engineGetKeySpec(var1, var2);
      }
   }

   @Override
   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof OpenSSHPrivateKeySpec) {
         AsymmetricKeyParameter var2 = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec)var1).getEncoded());
         if (var2 instanceof Ed25519PrivateKeyParameters) {
            return new BCEdDSAPrivateKey((Ed25519PrivateKeyParameters)var2);
         } else {
            throw new IllegalStateException("openssh private key not Ed25519 private key");
         }
      } else {
         return super.engineGeneratePrivate(var1);
      }
   }

   @Override
   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      if (var1 instanceof X509EncodedKeySpec) {
         byte[] var7 = ((X509EncodedKeySpec)var1).getEncoded();
         if (this.specificBase == 0 || this.specificBase == var7[8]) {
            if (var7[9] == 5 && var7[10] == 0) {
               SubjectPublicKeyInfo var3 = SubjectPublicKeyInfo.getInstance(var7);
               var3 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(var3.getAlgorithm().getAlgorithm()), var3.getPublicKeyData().getBytes());

               try {
                  var7 = var3.getEncoded("DER");
               } catch (IOException var5) {
                  throw new InvalidKeySpecException("attempt to reconstruct key failed: " + var5.getMessage());
               }
            }

            switch (var7[8]) {
               case 110:
                  return new BCXDHPublicKey(x25519Prefix, var7);
               case 111:
                  return new BCXDHPublicKey(x448Prefix, var7);
               case 112:
                  return new BCEdDSAPublicKey(Ed25519Prefix, var7);
               case 113:
                  return new BCEdDSAPublicKey(Ed448Prefix, var7);
               default:
                  return super.engineGeneratePublic(var1);
            }
         }
      } else {
         if (var1 instanceof RawEncodedKeySpec) {
            byte[] var6 = ((RawEncodedKeySpec)var1).getEncoded();
            switch (this.specificBase) {
               case 110:
                  return new BCXDHPublicKey(new X25519PublicKeyParameters(var6));
               case 111:
                  return new BCXDHPublicKey(new X448PublicKeyParameters(var6));
               case 112:
                  return new BCEdDSAPublicKey(new Ed25519PublicKeyParameters(var6));
               case 113:
                  return new BCEdDSAPublicKey(new Ed448PublicKeyParameters(var6));
               default:
                  throw new InvalidKeySpecException("factory not a specific type, cannot recognise raw encoding");
            }
         }

         if (var1 instanceof OpenSSHPublicKeySpec) {
            AsymmetricKeyParameter var2 = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)var1).getEncoded());
            if (var2 instanceof Ed25519PublicKeyParameters) {
               return new BCEdDSAPublicKey(new byte[0], ((Ed25519PublicKeyParameters)var2).getEncoded());
            }

            throw new IllegalStateException("openssh public key not Ed25519 public key");
         }
      }

      return super.engineGeneratePublic(var1);
   }

   @Override
   public PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getPrivateKeyAlgorithm().getAlgorithm();
      if (this.isXdh) {
         if ((this.specificBase == 0 || this.specificBase == 111) && var2.equals(EdECObjectIdentifiers.id_X448)) {
            return new BCXDHPrivateKey(var1);
         }

         if ((this.specificBase == 0 || this.specificBase == 110) && var2.equals(EdECObjectIdentifiers.id_X25519)) {
            return new BCXDHPrivateKey(var1);
         }
      } else if (var2.equals(EdECObjectIdentifiers.id_Ed448) || var2.equals(EdECObjectIdentifiers.id_Ed25519)) {
         if ((this.specificBase == 0 || this.specificBase == 113) && var2.equals(EdECObjectIdentifiers.id_Ed448)) {
            return new BCEdDSAPrivateKey(var1);
         }

         if ((this.specificBase == 0 || this.specificBase == 112) && var2.equals(EdECObjectIdentifiers.id_Ed25519)) {
            return new BCEdDSAPrivateKey(var1);
         }
      }

      throw new IOException("algorithm identifier " + var2 + " in key not recognized");
   }

   @Override
   public PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException {
      ASN1ObjectIdentifier var2 = var1.getAlgorithm().getAlgorithm();
      if (this.isXdh) {
         if ((this.specificBase == 0 || this.specificBase == 111) && var2.equals(EdECObjectIdentifiers.id_X448)) {
            return new BCXDHPublicKey(var1);
         }

         if ((this.specificBase == 0 || this.specificBase == 110) && var2.equals(EdECObjectIdentifiers.id_X25519)) {
            return new BCXDHPublicKey(var1);
         }
      } else if (var2.equals(EdECObjectIdentifiers.id_Ed448) || var2.equals(EdECObjectIdentifiers.id_Ed25519)) {
         if ((this.specificBase == 0 || this.specificBase == 113) && var2.equals(EdECObjectIdentifiers.id_Ed448)) {
            return new BCEdDSAPublicKey(var1);
         }

         if ((this.specificBase == 0 || this.specificBase == 112) && var2.equals(EdECObjectIdentifiers.id_Ed25519)) {
            return new BCEdDSAPublicKey(var1);
         }
      }

      throw new IOException("algorithm identifier " + var2 + " in key not recognized");
   }

   public static class Ed25519 extends KeyFactorySpi {
      public Ed25519() {
         super("Ed25519", false, 112);
      }
   }

   public static class Ed448 extends KeyFactorySpi {
      public Ed448() {
         super("Ed448", false, 113);
      }
   }

   public static class EdDSA extends KeyFactorySpi {
      public EdDSA() {
         super("EdDSA", false, 0);
      }
   }

   public static class X25519 extends KeyFactorySpi {
      public X25519() {
         super("X25519", true, 110);
      }
   }

   public static class X448 extends KeyFactorySpi {
      public X448() {
         super("X448", true, 111);
      }
   }

   public static class XDH extends KeyFactorySpi {
      public XDH() {
         super("XDH", true, 0);
      }
   }
}
