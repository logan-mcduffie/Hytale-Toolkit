package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;

/** @deprecated */
public class X509ExtensionsGenerator {
   private Hashtable extensions = new Hashtable();
   private Vector extOrdering = new Vector();

   public void reset() {
      this.extensions = new Hashtable();
      this.extOrdering = new Vector();
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, ASN1Encodable var3) {
      try {
         this.addExtension(var1, var2, var3.toASN1Primitive().getEncoded("DER"));
      } catch (IOException var5) {
         throw new IllegalArgumentException("error encoding value: " + var5);
      }
   }

   public void addExtension(ASN1ObjectIdentifier var1, boolean var2, byte[] var3) {
      if (this.extensions.containsKey(var1)) {
         throw new IllegalArgumentException("extension " + var1 + " already added");
      } else {
         this.extOrdering.addElement(var1);
         this.extensions.put(var1, new X509Extension(var2, new DEROctetString(var3)));
      }
   }

   public boolean isEmpty() {
      return this.extOrdering.isEmpty();
   }

   public X509Extensions generate() {
      return new X509Extensions(this.extOrdering, this.extensions);
   }
}
