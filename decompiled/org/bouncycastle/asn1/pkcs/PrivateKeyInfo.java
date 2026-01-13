package org.bouncycastle.asn1.pkcs;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.BigIntegers;

public class PrivateKeyInfo extends ASN1Object {
   private ASN1Integer version;
   private AlgorithmIdentifier privateKeyAlgorithm;
   private ASN1OctetString privateKey;
   private ASN1Set attributes;
   private ASN1BitString publicKey;

   public static PrivateKeyInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static PrivateKeyInfo getInstance(Object var0) {
      if (var0 instanceof PrivateKeyInfo) {
         return (PrivateKeyInfo)var0;
      } else {
         return var0 != null ? new PrivateKeyInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private static int getVersionValue(ASN1Integer var0) {
      int var1 = var0.intValueExact();
      if (var1 >= 0 && var1 <= 1) {
         return var1;
      } else {
         throw new IllegalArgumentException("invalid version for private key info");
      }
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, byte[] var2) throws IOException {
      this(var1, var2, null, null);
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, ASN1Encodable var2) throws IOException {
      this(var1, var2, null, null);
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, ASN1Encodable var2, ASN1Set var3) throws IOException {
      this(var1, var2, var3, null);
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, byte[] var2, ASN1Set var3) throws IOException {
      this(var1, var2, var3, null);
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, ASN1Encodable var2, ASN1Set var3, byte[] var4) throws IOException {
      this(var1, var2.toASN1Primitive().getEncoded("DER"), var3, var4);
   }

   public PrivateKeyInfo(AlgorithmIdentifier var1, byte[] var2, ASN1Set var3, byte[] var4) {
      this.version = new ASN1Integer(var4 != null ? BigIntegers.ONE : BigIntegers.ZERO);
      this.privateKeyAlgorithm = var1;
      this.privateKey = new DEROctetString(var2);
      this.attributes = var3;
      this.publicKey = var4 == null ? null : new DERBitString(var4);
   }

   private PrivateKeyInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.version = ASN1Integer.getInstance(var2.nextElement());
      int var3 = getVersionValue(this.version);
      this.privateKeyAlgorithm = AlgorithmIdentifier.getInstance(var2.nextElement());
      this.privateKey = ASN1OctetString.getInstance(var2.nextElement());
      int var4 = -1;

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var5 = (ASN1TaggedObject)var2.nextElement();
         int var6 = var5.getTagNo();
         if (var6 <= var4) {
            throw new IllegalArgumentException("invalid optional field in private key info");
         }

         var4 = var6;
         switch (var6) {
            case 0:
               this.attributes = ASN1Set.getInstance(var5, false);
               break;
            case 1:
               if (var3 < 1) {
                  throw new IllegalArgumentException("'publicKey' requires version v2(1) or later");
               }

               this.publicKey = ASN1BitString.getInstance(var5, false);
               break;
            default:
               throw new IllegalArgumentException("unknown optional field in private key info");
         }
      }
   }

   public ASN1Integer getVersion() {
      return this.version;
   }

   public ASN1Set getAttributes() {
      return this.attributes;
   }

   public AlgorithmIdentifier getPrivateKeyAlgorithm() {
      return this.privateKeyAlgorithm;
   }

   public ASN1OctetString getPrivateKey() {
      return new DEROctetString(this.privateKey.getOctets());
   }

   public int getPrivateKeyLength() {
      return this.privateKey.getOctetsLength();
   }

   public ASN1Encodable parsePrivateKey() throws IOException {
      return ASN1Primitive.fromByteArray(this.privateKey.getOctets());
   }

   public boolean hasPublicKey() {
      return this.publicKey != null;
   }

   public ASN1Encodable parsePublicKey() throws IOException {
      return this.publicKey == null ? null : ASN1Primitive.fromByteArray(this.publicKey.getOctets());
   }

   public ASN1BitString getPublicKeyData() {
      return this.publicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.version);
      var1.add(this.privateKeyAlgorithm);
      var1.add(this.privateKey);
      if (this.attributes != null) {
         var1.add(new DERTaggedObject(false, 0, this.attributes));
      }

      if (this.publicKey != null) {
         var1.add(new DERTaggedObject(false, 1, this.publicKey));
      }

      return new DERSequence(var1);
   }
}
