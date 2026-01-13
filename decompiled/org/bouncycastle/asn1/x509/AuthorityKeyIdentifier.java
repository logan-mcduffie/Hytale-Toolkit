package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

public class AuthorityKeyIdentifier extends ASN1Object {
   ASN1OctetString keyIdentifier = null;
   GeneralNames certissuer = null;
   ASN1Integer certserno = null;

   public static AuthorityKeyIdentifier getInstance(ASN1TaggedObject var0, boolean var1) {
      return new AuthorityKeyIdentifier(ASN1Sequence.getInstance(var0, var1));
   }

   public static AuthorityKeyIdentifier getInstance(Object var0) {
      if (var0 instanceof AuthorityKeyIdentifier) {
         return (AuthorityKeyIdentifier)var0;
      } else {
         return var0 != null ? new AuthorityKeyIdentifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static AuthorityKeyIdentifier fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.authorityKeyIdentifier));
   }

   protected AuthorityKeyIdentifier(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = ASN1TaggedObject.getInstance(var2.nextElement());
         switch (var3.getTagNo()) {
            case 0:
               this.keyIdentifier = ASN1OctetString.getInstance(var3, false);
               break;
            case 1:
               this.certissuer = GeneralNames.getInstance(var3, false);
               break;
            case 2:
               this.certserno = ASN1Integer.getInstance(var3, false);
               break;
            default:
               throw new IllegalArgumentException("illegal tag");
         }
      }
   }

   /** @deprecated */
   public AuthorityKeyIdentifier(SubjectPublicKeyInfo var1) {
      this(var1, null, null);
   }

   /** @deprecated */
   public AuthorityKeyIdentifier(SubjectPublicKeyInfo var1, GeneralNames var2, BigInteger var3) {
      SHA1Digest var4 = new SHA1Digest();
      byte[] var5 = new byte[var4.getDigestSize()];
      byte[] var6 = var1.getPublicKeyData().getBytes();
      var4.update(var6, 0, var6.length);
      var4.doFinal(var5, 0);
      this.keyIdentifier = new DEROctetString(var5);
      this.certissuer = var2;
      this.certserno = var3 != null ? new ASN1Integer(var3) : null;
   }

   public AuthorityKeyIdentifier(GeneralNames var1, BigInteger var2) {
      this((byte[])null, var1, var2);
   }

   public AuthorityKeyIdentifier(byte[] var1) {
      this(var1, null, null);
   }

   public AuthorityKeyIdentifier(byte[] var1, GeneralNames var2, BigInteger var3) {
      this.keyIdentifier = var1 != null ? new DEROctetString(Arrays.clone(var1)) : null;
      this.certissuer = var2;
      this.certserno = var3 != null ? new ASN1Integer(var3) : null;
   }

   /** @deprecated */
   public byte[] getKeyIdentifier() {
      return this.getKeyIdentifierOctets();
   }

   public byte[] getKeyIdentifierOctets() {
      return this.keyIdentifier != null ? this.keyIdentifier.getOctets() : null;
   }

   public ASN1OctetString getKeyIdentifierObject() {
      return this.keyIdentifier;
   }

   public GeneralNames getAuthorityCertIssuer() {
      return this.certissuer;
   }

   public BigInteger getAuthorityCertSerialNumber() {
      return this.certserno != null ? this.certserno.getValue() : null;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.keyIdentifier != null) {
         var1.add(new DERTaggedObject(false, 0, this.keyIdentifier));
      }

      if (this.certissuer != null) {
         var1.add(new DERTaggedObject(false, 1, this.certissuer));
      }

      if (this.certserno != null) {
         var1.add(new DERTaggedObject(false, 2, this.certserno));
      }

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      String var1 = this.keyIdentifier != null ? Hex.toHexString(this.keyIdentifier.getOctets()) : "null";
      return "AuthorityKeyIdentifier: KeyID(" + var1 + ")";
   }
}
