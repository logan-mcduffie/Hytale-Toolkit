package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class ExtensionsGenerator {
   private Hashtable extensions = new Hashtable();
   private Vector extOrdering = new Vector();
   private static final Set dupsAllowed;

   public void reset() {
      this.extensions = new Hashtable();
      this.extOrdering = new Vector();
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws IOException {
      Extension var4 = (Extension)this.extensions.get(var1);
      if (var4 != null) {
         this.implAddExtensionDup(var4, var2, var3.toASN1Primitive().getEncoded("DER"));
      } else {
         this.implAddExtension(new Extension(var1, var2, new DEROctetString(var3)));
      }
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      Extension var4 = (Extension)this.extensions.get(var1);
      if (var4 != null) {
         this.implAddExtensionDup(var4, var2, var3);
      } else {
         this.implAddExtension(new Extension(var1, var2, var3));
      }
   }

   public void addExtension(Extension var1) {
      if (this.hasExtension(var1.getExtnId())) {
         throw new IllegalArgumentException("extension " + var1.getExtnId() + " already added");
      } else {
         this.implAddExtension(var1);
      }
   }

   /** @deprecated */
   public void addExtension(Extensions var1) {
      this.addExtensions(var1);
   }

   public void addExtensions(Extensions var1) {
      ASN1ObjectIdentifier[] var2 = var1.getExtensionOIDs();

      for (int var3 = 0; var3 != var2.length; var3++) {
         ASN1ObjectIdentifier var4 = var2[var3];
         Extension var5 = var1.getExtension(var4);
         this.addExtension(ASN1ObjectIdentifier.getInstance(var4), var5.isCritical(), var5.getExtnValue().getOctets());
      }
   }

   public void replaceExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) throws IOException {
      this.replaceExtension(new Extension(var1, var2, new DEROctetString(var3)));
   }

   public void replaceExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      this.replaceExtension(new Extension(var1, var2, var3));
   }

   public void replaceExtension(Extension var1) {
      if (!this.hasExtension(var1.getExtnId())) {
         throw new IllegalArgumentException("extension " + var1.getExtnId() + " not present");
      } else {
         this.extensions.put(var1.getExtnId(), var1);
      }
   }

   public void removeExtension(ASN1ObjectIdentifier var1) {
      if (!this.hasExtension(var1)) {
         throw new IllegalArgumentException("extension " + var1 + " not present");
      } else {
         this.extOrdering.removeElement(var1);
         this.extensions.remove(var1);
      }
   }

   public boolean hasExtension(ASN1ObjectIdentifier var1) {
      return this.extensions.containsKey(var1);
   }

   public Extension getExtension(ASN1ObjectIdentifier var1) {
      return (Extension)this.extensions.get(var1);
   }

   public boolean isEmpty() {
      return this.extOrdering.isEmpty();
   }

   public Extensions generate() {
      Extension[] var1 = new Extension[this.extOrdering.size()];

      for (int var2 = 0; var2 != this.extOrdering.size(); var2++) {
         var1[var2] = (Extension)this.extensions.get(this.extOrdering.elementAt(var2));
      }

      return new Extensions(var1);
   }

   private void implAddExtension(Extension var1) {
      this.extOrdering.addElement(var1.getExtnId());
      this.extensions.put(var1.getExtnId(), var1);
   }

   private void implAddExtensionDup(Extension var1, boolean var2, byte[] var3) {
      ASN1ObjectIdentifier var4 = var1.getExtnId();
      if (!dupsAllowed.contains(var4)) {
         throw new IllegalArgumentException("extension " + var4 + " already added");
      } else {
         ASN1Sequence var5 = ASN1Sequence.getInstance(DEROctetString.getInstance(var1.getExtnValue()).getOctets());
         ASN1Sequence var6 = ASN1Sequence.getInstance(var3);
         ASN1EncodableVector var7 = new ASN1EncodableVector(var5.size() + var6.size());
         Enumeration var8 = var5.getObjects();

         while (var8.hasMoreElements()) {
            var7.add((ASN1Encodable)var8.nextElement());
         }

         var8 = var6.getObjects();

         while (var8.hasMoreElements()) {
            var7.add((ASN1Encodable)var8.nextElement());
         }

         try {
            this.extensions.put(var4, new Extension(var4, var2, new DEROctetString(new DERSequence(var7))));
         } catch (IOException var9) {
            throw new ASN1ParsingException(var9.getMessage(), var9);
         }
      }
   }

   static {
      HashSet var0 = new HashSet();
      var0.add(Extension.subjectAlternativeName);
      var0.add(Extension.issuerAlternativeName);
      var0.add(Extension.subjectDirectoryAttributes);
      var0.add(Extension.certificateIssuer);
      dupsAllowed = Collections.unmodifiableSet(var0);
   }
}
