package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.internal.asn1.oiw.ElGamalParameter;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

public class BCElGamalPublicKey implements ElGamalPublicKey, DHPublicKey {
   static final long serialVersionUID = 8712728417091216948L;
   private BigInteger y;
   private transient ElGamalParameterSpec elSpec;

   BCElGamalPublicKey(ElGamalPublicKeySpec var1) {
      this.y = var1.getY();
      this.elSpec = new ElGamalParameterSpec(var1.getParams().getP(), var1.getParams().getG());
   }

   BCElGamalPublicKey(DHPublicKeySpec var1) {
      this.y = var1.getY();
      this.elSpec = new ElGamalParameterSpec(var1.getP(), var1.getG());
   }

   BCElGamalPublicKey(ElGamalPublicKey var1) {
      this.y = var1.getY();
      this.elSpec = var1.getParameters();
   }

   BCElGamalPublicKey(DHPublicKey var1) {
      this.y = var1.getY();
      this.elSpec = new ElGamalParameterSpec(var1.getParams().getP(), var1.getParams().getG());
   }

   BCElGamalPublicKey(ElGamalPublicKeyParameters var1) {
      this.y = var1.getY();
      this.elSpec = new ElGamalParameterSpec(var1.getParameters().getP(), var1.getParameters().getG());
   }

   BCElGamalPublicKey(BigInteger var1, ElGamalParameterSpec var2) {
      this.y = var1;
      this.elSpec = var2;
   }

   BCElGamalPublicKey(SubjectPublicKeyInfo var1) {
      ElGamalParameter var2 = ElGamalParameter.getInstance(var1.getAlgorithm().getParameters());
      ASN1Integer var3 = null;

      try {
         var3 = (ASN1Integer)var1.parsePublicKey();
      } catch (IOException var5) {
         throw new IllegalArgumentException("invalid info structure in DSA public key");
      }

      this.y = var3.getValue();
      this.elSpec = new ElGamalParameterSpec(var2.getP(), var2.getG());
   }

   @Override
   public String getAlgorithm() {
      return "ElGamal";
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public byte[] getEncoded() {
      try {
         SubjectPublicKeyInfo var1 = new SubjectPublicKeyInfo(
            new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())),
            new ASN1Integer(this.y)
         );
         return var1.getEncoded("DER");
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public ElGamalParameterSpec getParameters() {
      return this.elSpec;
   }

   @Override
   public DHParameterSpec getParams() {
      return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
   }

   @Override
   public BigInteger getY() {
      return this.y;
   }

   @Override
   public int hashCode() {
      return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DHPublicKey)) {
         return false;
      } else {
         DHPublicKey var2 = (DHPublicKey)var1;
         return this.getY().equals(var2.getY())
            && this.getParams().getG().equals(var2.getParams().getG())
            && this.getParams().getP().equals(var2.getParams().getP())
            && this.getParams().getL() == var2.getParams().getL();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.elSpec = new ElGamalParameterSpec((BigInteger)var1.readObject(), (BigInteger)var1.readObject());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.elSpec.getP());
      var1.writeObject(this.elSpec.getG());
   }
}
