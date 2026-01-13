package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.MessageImprint;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

public class TimeStampRequestGenerator {
   private static final DefaultDigestAlgorithmIdentifierFinder DEFAULT_DIGEST_ALG_FINDER = new DefaultDigestAlgorithmIdentifierFinder();
   private final ExtensionsGenerator extGenerator = new ExtensionsGenerator();
   private final DigestAlgorithmIdentifierFinder digestAlgFinder;
   private ASN1ObjectIdentifier reqPolicy;
   private ASN1Boolean certReq;

   public TimeStampRequestGenerator() {
      this(DEFAULT_DIGEST_ALG_FINDER);
   }

   public TimeStampRequestGenerator(DigestAlgorithmIdentifierFinder var1) {
      if (var1 == null) {
         throw new NullPointerException("'digestAlgFinder' cannot be null");
      } else {
         this.digestAlgFinder = var1;
      }
   }

   public void setReqPolicy(ASN1ObjectIdentifier var1) {
      this.reqPolicy = var1;
   }

   /** @deprecated */
   public void setReqPolicy(String var1) {
      this.setReqPolicy(new ASN1ObjectIdentifier(var1));
   }

   public void setCertReq(ASN1Boolean var1) {
      this.certReq = var1;
   }

   public void setCertReq(boolean var1) {
      this.setCertReq(ASN1Boolean.getInstance(var1));
   }

   /** @deprecated */
   public void addExtension(String var1, boolean var2, ASN1Encodable var3) throws IOException {
      this.addExtension(new ASN1ObjectIdentifier(var1), var2, var3);
   }

   /** @deprecated */
   public void addExtension(String var1, boolean var2, byte[] var3) {
      this.addExtension(new ASN1ObjectIdentifier(var1), var2, var3);
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws TSPIOException {
      TSPUtil.addExtension(this.extGenerator, var1, var2, var3);
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      this.extGenerator.addExtension(var1, var2, var3);
   }

   /** @deprecated */
   public TimeStampRequest generate(String var1, byte[] var2) {
      return this.generate(var1, var2, null);
   }

   /** @deprecated */
   public TimeStampRequest generate(String var1, byte[] var2, BigInteger var3) {
      if (var1 == null) {
         throw new NullPointerException("'digestAlgorithmOID' cannot be null");
      } else {
         return this.generate(new ASN1ObjectIdentifier(var1), var2, var3);
      }
   }

   public TimeStampRequest generate(ASN1ObjectIdentifier var1, byte[] var2) {
      return this.generate(var1, var2, null);
   }

   public TimeStampRequest generate(ASN1ObjectIdentifier var1, byte[] var2, BigInteger var3) {
      return this.generate(this.digestAlgFinder.find(var1), var2, var3);
   }

   public TimeStampRequest generate(AlgorithmIdentifier var1, byte[] var2) {
      return this.generate(var1, var2, null);
   }

   public TimeStampRequest generate(AlgorithmIdentifier var1, byte[] var2, BigInteger var3) {
      if (var1 == null) {
         throw new NullPointerException("'digestAlgorithmID' cannot be null");
      } else {
         MessageImprint var4 = new MessageImprint(var1, var2);
         ASN1Integer var5 = var3 == null ? null : new ASN1Integer(var3);
         Extensions var6 = this.extGenerator.isEmpty() ? null : this.extGenerator.generate();
         return new TimeStampRequest(new TimeStampReq(var4, this.reqPolicy, var5, this.certReq, var6));
      }
   }
}
