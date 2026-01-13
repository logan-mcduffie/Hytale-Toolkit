package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCEdDSAPublicKey implements EdDSAPublicKey, BCKey {
   static final long serialVersionUID = 1L;
   transient AsymmetricKeyParameter eddsaPublicKey;

   BCEdDSAPublicKey(AsymmetricKeyParameter var1) {
      this.eddsaPublicKey = var1;
   }

   BCEdDSAPublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPubKeyInfo(var1);
   }

   BCEdDSAPublicKey(byte[] var1, byte[] var2) throws InvalidKeySpecException {
      int var3 = var1.length;
      if (Utils.isValidPrefix(var1, var2)) {
         if (var2.length - var3 == 57) {
            this.eddsaPublicKey = new Ed448PublicKeyParameters(var2, var3);
         } else {
            if (var2.length - var3 != 32) {
               throw new InvalidKeySpecException("raw key data not recognised");
            }

            this.eddsaPublicKey = new Ed25519PublicKeyParameters(var2, var3);
         }
      } else {
         throw new InvalidKeySpecException("raw key data not recognised");
      }
   }

   @Override
   public byte[] getPointEncoding() {
      return this.eddsaPublicKey instanceof Ed448PublicKeyParameters
         ? ((Ed448PublicKeyParameters)this.eddsaPublicKey).getEncoded()
         : ((Ed25519PublicKeyParameters)this.eddsaPublicKey).getEncoded();
   }

   private void populateFromPubKeyInfo(SubjectPublicKeyInfo var1) {
      byte[] var2 = var1.getPublicKeyData().getOctets();
      if (EdECObjectIdentifiers.id_Ed448.equals(var1.getAlgorithm().getAlgorithm())) {
         this.eddsaPublicKey = new Ed448PublicKeyParameters(var2);
      } else {
         this.eddsaPublicKey = new Ed25519PublicKeyParameters(var2);
      }
   }

   @Override
   public String getAlgorithm() {
      if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
         return "EdDSA";
      } else {
         return this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? "Ed448" : "Ed25519";
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      if (this.eddsaPublicKey instanceof Ed448PublicKeyParameters) {
         byte[] var2 = new byte[KeyFactorySpi.Ed448Prefix.length + 57];
         System.arraycopy(KeyFactorySpi.Ed448Prefix, 0, var2, 0, KeyFactorySpi.Ed448Prefix.length);
         ((Ed448PublicKeyParameters)this.eddsaPublicKey).encode(var2, KeyFactorySpi.Ed448Prefix.length);
         return var2;
      } else {
         byte[] var1 = new byte[KeyFactorySpi.Ed25519Prefix.length + 32];
         System.arraycopy(KeyFactorySpi.Ed25519Prefix, 0, var1, 0, KeyFactorySpi.Ed25519Prefix.length);
         ((Ed25519PublicKeyParameters)this.eddsaPublicKey).encode(var1, KeyFactorySpi.Ed25519Prefix.length);
         return var1;
      }
   }

   AsymmetricKeyParameter engineGetKeyParameters() {
      return this.eddsaPublicKey;
   }

   @Override
   public String toString() {
      return Utils.keyToString("Public Key", this.getAlgorithm(), this.eddsaPublicKey);
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
