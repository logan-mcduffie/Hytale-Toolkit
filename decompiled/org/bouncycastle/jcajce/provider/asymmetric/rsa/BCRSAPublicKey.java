package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.interfaces.BCKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class BCRSAPublicKey implements RSAPublicKey, BCKey {
   static final AlgorithmIdentifier DEFAULT_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
   static final long serialVersionUID = 2675817738516720772L;
   private BigInteger modulus;
   private BigInteger publicExponent;
   private transient AlgorithmIdentifier algorithmIdentifier;
   private transient RSAKeyParameters rsaPublicKey;

   BCRSAPublicKey(RSAKeyParameters var1) {
      this(DEFAULT_ALGORITHM_IDENTIFIER, var1);
   }

   BCRSAPublicKey(AlgorithmIdentifier var1, RSAKeyParameters var2) {
      this.algorithmIdentifier = var1;
      this.modulus = var2.getModulus();
      this.publicExponent = var2.getExponent();
      this.rsaPublicKey = var2;
   }

   BCRSAPublicKey(RSAPublicKeySpec var1) {
      this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
   }

   BCRSAPublicKey(RSAPublicKey var1) {
      this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
      this.modulus = var1.getModulus();
      this.publicExponent = var1.getPublicExponent();
      this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
   }

   BCRSAPublicKey(SubjectPublicKeyInfo var1) {
      this.populateFromPublicKeyInfo(var1);
   }

   private void populateFromPublicKeyInfo(SubjectPublicKeyInfo var1) {
      try {
         org.bouncycastle.asn1.pkcs.RSAPublicKey var2 = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(var1.parsePublicKey());
         this.algorithmIdentifier = var1.getAlgorithm();
         this.modulus = var2.getModulus();
         this.publicExponent = var2.getPublicExponent();
         this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
      } catch (IOException var3) {
         throw new IllegalArgumentException("invalid info structure in RSA public key");
      }
   }

   @Override
   public BigInteger getModulus() {
      return this.modulus;
   }

   @Override
   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   @Override
   public String getAlgorithm() {
      return this.algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS) ? "RSASSA-PSS" : "RSA";
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      return KeyUtil.getEncodedSubjectPublicKeyInfo(
         this.algorithmIdentifier, new org.bouncycastle.asn1.pkcs.RSAPublicKey(this.getModulus(), this.getPublicExponent())
      );
   }

   RSAKeyParameters engineGetKeyParameters() {
      return this.rsaPublicKey;
   }

   @Override
   public int hashCode() {
      return this.getModulus().hashCode() ^ this.getPublicExponent().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof RSAPublicKey)) {
         return false;
      } else {
         RSAPublicKey var2 = (RSAPublicKey)var1;
         return this.getModulus().equals(var2.getModulus()) && this.getPublicExponent().equals(var2.getPublicExponent());
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("RSA Public Key [")
         .append(RSAUtil.generateKeyFingerprint(this.getModulus()))
         .append("]")
         .append(",[")
         .append(RSAUtil.generateExponentFingerprint(this.getPublicExponent()))
         .append("]")
         .append(var2);
      var1.append("        modulus: ").append(this.getModulus().toString(16)).append(var2);
      var1.append("public exponent: ").append(this.getPublicExponent().toString(16)).append(var2);
      return var1.toString();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      try {
         this.algorithmIdentifier = AlgorithmIdentifier.getInstance(var1.readObject());
      } catch (Exception var3) {
         this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
      }

      this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (!this.algorithmIdentifier.equals(DEFAULT_ALGORITHM_IDENTIFIER)) {
         var1.writeObject(this.algorithmIdentifier.getEncoded());
      }
   }
}
