package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

class LazyConstructionEnumeration implements Enumeration {
   private ASN1InputStream aIn;
   private Object nextObj;

   public LazyConstructionEnumeration(byte[] var1) {
      this.aIn = new ASN1InputStream(var1, true);
      this.nextObj = this.readObject();
   }

   @Override
   public boolean hasMoreElements() {
      return this.nextObj != null;
   }

   @Override
   public Object nextElement() {
      if (this.nextObj != null) {
         Object var1 = this.nextObj;
         this.nextObj = this.readObject();
         return var1;
      } else {
         throw new NoSuchElementException();
      }
   }

   private Object readObject() {
      try {
         return this.aIn.readObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("malformed ASN.1: " + var2, var2);
      }
   }
}
