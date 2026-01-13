package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPrivateKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

public class BCXDHPrivateKey implements XDHPrivateKey {
   static final long serialVersionUID = 1L;
   transient AsymmetricKeyParameter xdhPrivateKey;
   transient AsymmetricKeyParameter xdhPublicKey;
   transient int hashCode;
   private final boolean hasPublicKey;
   private final byte[] attributes;

   BCXDHPrivateKey(AsymmetricKeyParameter var1) {
      this.hasPublicKey = true;
      this.attributes = null;
      this.xdhPrivateKey = var1;
      if (this.xdhPrivateKey instanceof X448PrivateKeyParameters) {
         this.xdhPublicKey = ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
      } else {
         this.xdhPublicKey = ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
      }

      this.hashCode = this.calculateHashCode();
   }

   BCXDHPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.hasPublicKey = var1.hasPublicKey();
      this.attributes = var1.getAttributes() != null ? var1.getAttributes().getEncoded() : null;
      this.populateFromPrivateKeyInfo(var1);
   }

   private void populateFromPrivateKeyInfo(PrivateKeyInfo var1) throws IOException {
      int var2 = var1.getPrivateKeyLength();
      byte[] var3;
      if (var2 != 32 && var2 != 56) {
         var3 = ASN1OctetString.getInstance(var1.parsePrivateKey()).getOctets();
      } else {
         var3 = var1.getPrivateKey().getOctets();
      }

      if (EdECObjectIdentifiers.id_X448.equals(var1.getPrivateKeyAlgorithm().getAlgorithm())) {
         this.xdhPrivateKey = new X448PrivateKeyParameters(var3);
         this.xdhPublicKey = ((X448PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
      } else {
         this.xdhPrivateKey = new X25519PrivateKeyParameters(var3);
         this.xdhPublicKey = ((X25519PrivateKeyParameters)this.xdhPrivateKey).generatePublicKey();
      }

      this.hashCode = this.calculateHashCode();
   }

   @Override
   public String getAlgorithm() {
      if (Properties.isOverrideSet("org.bouncycastle.emulate.oracle")) {
         return "XDH";
      } else {
         return this.xdhPrivateKey instanceof X448PrivateKeyParameters ? "X448" : "X25519";
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
         PrivateKeyInfo var2 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.xdhPrivateKey, var1);
         return this.hasPublicKey && !Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")
            ? var2
            : new PrivateKeyInfo(var2.getPrivateKeyAlgorithm(), var2.parsePrivateKey(), var1);
      } catch (IOException var3) {
         return null;
      }
   }

   @Override
   public XDHPublicKey getPublicKey() {
      return new BCXDHPublicKey(this.xdhPublicKey);
   }

   AsymmetricKeyParameter engineGetKeyParameters() {
      return this.xdhPrivateKey;
   }

   @Override
   public String toString() {
      return Utils.keyToString("Private Key", this.getAlgorithm(), this.xdhPublicKey);
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
         PrivateKeyInfo var4 = var2 instanceof BCXDHPrivateKey ? ((BCXDHPrivateKey)var2).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(var2.getEncoded());
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
      if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
         var1 = ((X448PublicKeyParameters)this.xdhPublicKey).getEncoded();
      } else {
         var1 = ((X25519PublicKeyParameters)this.xdhPublicKey).getEncoded();
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
