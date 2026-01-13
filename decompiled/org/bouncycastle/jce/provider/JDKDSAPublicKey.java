package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class JDKDSAPublicKey implements DSAPublicKey {
   private static final long serialVersionUID = 1752452449903495175L;
   private BigInteger y;
   private DSAParams dsaSpec;

   JDKDSAPublicKey(DSAPublicKeySpec var1) {
      this.y = var1.getY();
      this.dsaSpec = new DSAParameterSpec(var1.getP(), var1.getQ(), var1.getG());
   }

   JDKDSAPublicKey(DSAPublicKey var1) {
      this.y = var1.getY();
      this.dsaSpec = var1.getParams();
   }

   JDKDSAPublicKey(DSAPublicKeyParameters var1) {
      this.y = var1.getY();
      this.dsaSpec = new DSAParameterSpec(var1.getParameters().getP(), var1.getParameters().getQ(), var1.getParameters().getG());
   }

   JDKDSAPublicKey(BigInteger var1, DSAParameterSpec var2) {
      this.y = var1;
      this.dsaSpec = var2;
   }

   JDKDSAPublicKey(SubjectPublicKeyInfo var1) {
      ASN1Integer var2;
      try {
         var2 = (ASN1Integer)var1.parsePublicKey();
      } catch (IOException var4) {
         throw new IllegalArgumentException("invalid info structure in DSA public key");
      }

      this.y = var2.getValue();
      if (this.isNotNull(var1.getAlgorithm().getParameters())) {
         DSAParameter var3 = DSAParameter.getInstance(var1.getAlgorithm().getParameters());
         this.dsaSpec = new DSAParameterSpec(var3.getP(), var3.getQ(), var3.getG());
      }
   }

   private boolean isNotNull(ASN1Encodable var1) {
      return var1 != null && !DERNull.INSTANCE.equals(var1);
   }

   @Override
   public String getAlgorithm() {
      return "DSA";
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      try {
         return this.dsaSpec == null
            ? new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y)).getEncoded("DER")
            : new SubjectPublicKeyInfo(
                  new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())),
                  new ASN1Integer(this.y)
               )
               .getEncoded("DER");
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public DSAParams getParams() {
      return this.dsaSpec;
   }

   @Override
   public BigInteger getY() {
      return this.y;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = Strings.lineSeparator();
      var1.append("DSA Public Key").append(var2);
      var1.append("            y: ").append(this.getY().toString(16)).append(var2);
      return var1.toString();
   }

   @Override
   public int hashCode() {
      return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DSAPublicKey)) {
         return false;
      } else {
         DSAPublicKey var2 = (DSAPublicKey)var1;
         return this.getY().equals(var2.getY())
            && this.getParams().getG().equals(var2.getParams().getG())
            && this.getParams().getP().equals(var2.getParams().getP())
            && this.getParams().getQ().equals(var2.getParams().getQ());
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.y = (BigInteger)var1.readObject();
      this.dsaSpec = new DSAParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject(), (BigInteger)var1.readObject());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeObject(this.y);
      var1.writeObject(this.dsaSpec.getP());
      var1.writeObject(this.dsaSpec.getQ());
      var1.writeObject(this.dsaSpec.getG());
   }
}
