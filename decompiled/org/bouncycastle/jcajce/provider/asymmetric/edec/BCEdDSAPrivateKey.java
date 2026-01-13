package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPrivateKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCEdDSAPrivateKey implements EdDSAPrivateKey, BCKey {
   static final long serialVersionUID = 1L;
   transient AsymmetricKeyParameter eddsaPrivateKey;
   transient AsymmetricKeyParameter eddsaPublicKey;
   transient int hashCode;
   private final boolean hasPublicKey;
   private final byte[] attributes;

   BCEdDSAPrivateKey(AsymmetricKeyParameter var1) {
      this.hasPublicKey = true;
      this.attributes = null;
      this.eddsaPrivateKey = var1;
      if (this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters) {
         this.eddsaPublicKey = ((Ed448PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
      } else {
         this.eddsaPublicKey = ((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
      }

      this.hashCode = this.calculateHashCode();
   }

   BCEdDSAPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.hasPublicKey = var1.hasPublicKey();
      this.attributes = var1.getAttributes() != null ? var1.getAttributes().getEncoded() : null;
      this.populateFromPrivateKeyInfo(var1);
   }

   private void populateFromPrivateKeyInfo(PrivateKeyInfo var1) throws IOException {
      byte[] var2 = ASN1OctetString.getInstance(var1.parsePrivateKey()).getOctets();
      if (EdECObjectIdentifiers.id_Ed448.equals(var1.getPrivateKeyAlgorithm().getAlgorithm())) {
         this.eddsaPrivateKey = new Ed448PrivateKeyParameters(var2);
         this.eddsaPublicKey = ((Ed448PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
      } else {
         this.eddsaPrivateKey = new Ed25519PrivateKeyParameters(var2);
         this.eddsaPublicKey = ((Ed25519PrivateKeyParameters)this.eddsaPrivateKey).generatePublicKey();
      }

      this.hashCode = this.calculateHashCode();
   }

   @Override
   public String getAlgorithm() {
      if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
         return "EdDSA";
      } else {
         return this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters ? "Ed448" : "Ed25519";
      }
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = this.getPrivateKeyInfo();
         return var1 == null ? null : var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   private PrivateKeyInfo getPrivateKeyInfo() {
      try {
         ASN1Set var1 = ASN1Set.getInstance(this.attributes);
         PrivateKeyInfo var2 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.eddsaPrivateKey, var1);
         return this.hasPublicKey && !Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")
            ? var2
            : new PrivateKeyInfo(var2.getPrivateKeyAlgorithm(), var2.parsePrivateKey(), var1);
      } catch (IOException var3) {
         return null;
      }
   }

   @Override
   public EdDSAPublicKey getPublicKey() {
      return new BCEdDSAPublicKey(this.eddsaPublicKey);
   }

   AsymmetricKeyParameter engineGetKeyParameters() {
      return this.eddsaPrivateKey;
   }

   @Override
   public String toString() {
      return Utils.keyToString("Private Key", this.getAlgorithm(), this.eddsaPublicKey);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PrivateKey)) {
         return false;
      } else {
         PrivateKey var2 = (PrivateKey)var1;
         PrivateKeyInfo var3 = this.getPrivateKeyInfo();
         PrivateKeyInfo var4 = var2 instanceof BCEdDSAPrivateKey
            ? ((BCEdDSAPrivateKey)var2).getPrivateKeyInfo()
            : PrivateKeyInfo.getInstance(var2.getEncoded());
         if (var3 != null && var4 != null) {
            try {
               boolean var5 = Arrays.constantTimeAreEqual(var3.getPrivateKeyAlgorithm().getEncoded(), var4.getPrivateKeyAlgorithm().getEncoded());
               boolean var6 = Arrays.constantTimeAreEqual(var3.getPrivateKey().getEncoded(), var4.getPrivateKey().getEncoded());
               return var5 & var6;
            } catch (IOException var7) {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   private int calculateHashCode() {
      byte[] var1;
      if (this.eddsaPublicKey instanceof Ed448PublicKeyParameters) {
         var1 = ((Ed448PublicKeyParameters)this.eddsaPublicKey).getEncoded();
      } else {
         var1 = ((Ed25519PublicKeyParameters)this.eddsaPublicKey).getEncoded();
      }

      int var2 = this.getAlgorithm().hashCode();
      return 31 * var2 + Arrays.hashCode(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.populateFromPrivateKeyInfo(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
