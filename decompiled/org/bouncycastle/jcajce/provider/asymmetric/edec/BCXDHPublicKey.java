package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCXDHPublicKey implements XDHPublicKey {
   static final long serialVersionUID = 1L;
   transient AsymmetricKeyParameter xdhPublicKey;

   BCXDHPublicKey(AsymmetricKeyParameter var1) {
      this.xdhPublicKey = var1;
   }

   BCXDHPublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   BCXDHPublicKey(byte[] var1, byte[] var2) throws InvalidKeySpecException {
      int var3 = var1.length;
      if (Utils.isValidPrefix(var1, var2)) {
         if (var2.length - var3 == 56) {
            this.xdhPublicKey = new X448PublicKeyParameters(var2, var3);
         } else {
            if (var2.length - var3 != 32) {
               throw new InvalidKeySpecException("raw key data not recognised");
            }

            this.xdhPublicKey = new X25519PublicKeyParameters(var2, var3);
         }
      } else {
         throw new InvalidKeySpecException("raw key data not recognised");
      }
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      byte[] var2 = var1.getPublicKeyData().getOctets();
      if (EdECObjectIdentifiers.id_X448.equals(var1.getAlgorithm().getAlgorithm())) {
         this.xdhPublicKey = new X448PublicKeyParameters(var2);
      } else {
         this.xdhPublicKey = new X25519PublicKeyParameters(var2);
      }
   }

   @Override
   public String getAlgorithm() {
      if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
         return "XDH";
      } else {
         return this.xdhPublicKey instanceof X448PublicKeyParameters ? "X448" : "X25519";
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
         byte[] var2 = new byte[KeyFactorySpi.x448Prefix.length + 56];
         System.arraycopy(KeyFactorySpi.x448Prefix, 0, var2, 0, KeyFactorySpi.x448Prefix.length);
         ((X448PublicKeyParameters)this.xdhPublicKey).encode(var2, KeyFactorySpi.x448Prefix.length);
         return var2;
      } else {
         byte[] var1 = new byte[KeyFactorySpi.x25519Prefix.length + 32];
         System.arraycopy(KeyFactorySpi.x25519Prefix, 0, var1, 0, KeyFactorySpi.x25519Prefix.length);
         ((X25519PublicKeyParameters)this.xdhPublicKey).encode(var1, KeyFactorySpi.x25519Prefix.length);
         return var1;
      }
   }

   AsymmetricKeyParameter engineGetKeyParameters() {
      return this.xdhPublicKey;
   }

   @Override
   public BigInteger getU() {
      byte[] var1 = this.getUEncoding();
      Arrays.reverseInPlace(var1);
      return new BigInteger(1, var1);
   }

   @Override
   public byte[] getUEncoding() {
      return this.xdhPublicKey instanceof X448PublicKeyParameters
         ? ((X448PublicKeyParameters)this.xdhPublicKey).getEncoded()
         : ((X25519PublicKeyParameters)this.xdhPublicKey).getEncoded();
   }

   @Override
   public String toString() {
      return Utils.keyToString("Public Key", this.getAlgorithm(), this.xdhPublicKey);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PublicKey)) {
         return false;
      } else {
         PublicKey var2 = (PublicKey)var1;
         return Arrays.areEqual(var2.getEncoded(), this.getEncoded());
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.getEncoded());
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
