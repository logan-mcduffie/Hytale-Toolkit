package org.bouncycastle.x509;

import java.util.ArrayList;
import java.util.Collection;

public class X509CollectionStoreParameters implements X509StoreParameters {
   private Collection collection;

   public X509CollectionStoreParameters(Collection var1) {
      if (var1 == null) {
         throw new NullPointerException("collection cannot be null");
      } else {
         this.collection = var1;
      }
   }

   @Override
   public Object clone() {
      return new X509CollectionStoreParameters(this.collection);
   }

   public Collection getCollection() {
      return new ArrayList(this.collection);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("X509CollectionStoreParameters: [\n");
      var1.append("  collection: " + this.collection + "\n");
      var1.append("]");
      return var1.toString();
   }
}
