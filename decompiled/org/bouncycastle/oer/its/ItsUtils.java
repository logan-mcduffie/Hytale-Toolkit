package org.bouncycastle.oer.its;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class ItsUtils {
   public static byte[] octetStringFixed(byte[] var0, int var1) {
      if (var0.length != var1) {
         throw new IllegalArgumentException("octet string out of range");
      } else {
         return var0;
      }
   }

   public static byte[] octetStringFixed(byte[] var0) {
      if (var0.length >= 1 && var0.length <= 32) {
         return Arrays.clone(var0);
      } else {
         throw new IllegalArgumentException("octet string out of range");
      }
   }

   public static ASN1Sequence toSequence(List var0) {
      return new DERSequence(var0.toArray(new ASN1Encodable[0]));
   }

   public static ASN1Sequence toSequence(ASN1Encodable... var0) {
      return new DERSequence(var0);
   }

   @Deprecated
   public static <T> List<T> fillList(final Class<T> var0, final ASN1Sequence var1) {
      return AccessController.doPrivileged(new PrivilegedAction<List<T>>() {
         public List<T> run() {
            try {
               ArrayList var1x = new ArrayList();
               Iterator var2 = var1.iterator();

               while (var2.hasNext()) {
                  Method var3 = var0.getMethod("getInstance", Object.class);
                  var1x.add(var0.cast(var3.invoke(null, var2.next())));
               }

               return var1x;
            } catch (Exception var4) {
               throw new IllegalStateException("could not invoke getInstance on type " + var4.getMessage(), var4);
            }
         }
      });
   }
}
