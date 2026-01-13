package org.bouncycastle.oer.its.ieee1609dot2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.util.Arrays;

public class Opaque extends ASN1Object {
   private final byte[] content;

   public Opaque(byte[] var1) {
      this.content = Arrays.clone(var1);
   }

   private Opaque(ASN1OctetString var1) {
      this(var1.getOctets());
   }

   public static Opaque getInstance(Object var0) {
      if (var0 instanceof Opaque) {
         return (Opaque)var0;
      } else {
         return var0 != null ? new Opaque(ASN1OctetString.getInstance(var0)) : null;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DEROctetString(this.content);
   }

   public byte[] getContent() {
      return this.content;
   }

   public InputStream getInputStream() {
      return new ByteArrayInputStream(this.content);
   }

   public static <T> T getValue(final Class<T> var0, final Element var1, final Opaque var2) {
      return AccessController.doPrivileged(new PrivilegedAction<T>() {
         @Override
         public T run() {
            try {
               ASN1Encodable var1x = OERInputStream.parse(var2.content, var1);
               Method var2x = var0.getMethod("getInstance", Object.class);
               return (T)var0.cast(var2x.invoke(null, var1x));
            } catch (Exception var3) {
               throw new IllegalStateException("could not invoke getInstance on type " + var3.getMessage(), var3);
            }
         }
      });
   }
}
