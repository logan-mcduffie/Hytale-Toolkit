package org.bouncycastle.jcajce.provider.asymmetric.dsa;

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
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class BCDSAPublicKey implements DSAPublicKey {
   private static final long serialVersionUID = 1752452449903495175L;
   private static BigInteger ZERO = BigInteger.valueOf(0L);
   private BigInteger y;
   private transient DSAPublicKeyParameters lwKeyParams;
   private transient DSAParams dsaSpec;

   BCDSAPublicKey(DSAPublicKeySpec var1) {
      this.y = var1.getY();
      this.dsaSpec = new DSAParameterSpec(var1.getP(), var1.getQ(), var1.getG());
      this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
   }

   BCDSAPublicKey(DSAPublicKey var1) {
      this.y = var1.getY();
      this.dsaSpec = var1.getParams();
      this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
   }

   BCDSAPublicKey(DSAPublicKeyParameters var1) {
      this.y = var1.getY();
      if (var1.getParameters() != null) {
         this.dsaSpec = new DSAParameterSpec(var1.getParameters().getP(), var1.getParameters().getQ(), var1.getParameters().getG());
      } else {
         this.dsaSpec = null;
      }

      this.lwKeyParams = var1;
   }

   public BCDSAPublicKey(SubjectPublicKeyInfo var1) {
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
      } else {
         this.dsaSpec = null;
      }

      this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
   }

   private boolean isNotNull(ASN1Encodable var1) {
      return var1 != null && !DERNull.INSTANCE.equals(var1.toASN1Primitive());
   }

   @Override
   public String getAlgorithm() {
      return "DSA";
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   DSAPublicKeyParameters engineGetKeyParameters() {
      return this.lwKeyParams;
   }

   @Override
   public byte[] getEncoded() {
      return this.dsaSpec == null
         ? KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y))
         : KeyUtil.getEncodedSubjectPublicKeyInfo(
            new AlgorithmIdentifier(
               X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()
            ),
            new ASN1Integer(this.y)
         );
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
      var1.append("DSA Public Key [").append(DSAUtil.generateKeyFingerprint(this.y, this.getParams())).append("]").append(var2);
      var1.append("            Y: ").append(this.getY().toString(16)).append(var2);
      return var1.toString();
   }

   @Override
   public int hashCode() {
      return this.dsaSpec != null
         ? this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode()
         : this.getY().hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DSAPublicKey)) {
         return false;
      } else {
         DSAPublicKey var2 = (DSAPublicKey)var1;
         return this.dsaSpec != null
            ? this.getY().equals(var2.getY())
               && var2.getParams() != null
               && this.getParams().getG().equals(var2.getParams().getG())
               && this.getParams().getP().equals(var2.getParams().getP())
               && this.getParams().getQ().equals(var2.getParams().getQ())
            : this.getY().equals(var2.getY()) && var2.getParams() == null;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      BigInteger var2 = (BigInteger)var1.readObject();
      if (var2.equals(ZERO)) {
         this.dsaSpec = null;
      } else {
         this.dsaSpec = new DSAParameterSpec(var2, (BigInteger)var1.readObject(), (BigInteger)var1.readObject());
      }

      this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.dsaSpec == null) {
         var1.writeObject(ZERO);
      } else {
         var1.writeObject(this.dsaSpec.getP());
         var1.writeObject(this.dsaSpec.getQ());
         var1.writeObject(this.dsaSpec.getG());
      }
   }
}
