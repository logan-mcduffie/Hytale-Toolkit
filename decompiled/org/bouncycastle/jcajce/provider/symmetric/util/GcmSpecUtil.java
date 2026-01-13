package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.util.Integers;

public class GcmSpecUtil {
   static final Class gcmSpecClass = ClassUtil.loadClass(GcmSpecUtil.class, "javax.crypto.spec.GCMParameterSpec");
   private static final Constructor constructor;
   private static final Method tLen;
   private static final Method iv;

   private static Constructor extractConstructor() {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction() {
            @Override
            public Object run() throws Exception {
               return GcmSpecUtil.gcmSpecClass.getConstructor(int.class, byte[].class);
            }
         });
      } catch (PrivilegedActionException var1) {
         return null;
      }
   }

   private static Method extractMethod(final String var0) {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction() {
            @Override
            public Object run() throws Exception {
               return GcmSpecUtil.gcmSpecClass.getDeclaredMethod(var0);
            }
         });
      } catch (PrivilegedActionException var2) {
         return null;
      }
   }

   public static boolean gcmSpecExists() {
      return gcmSpecClass != null;
   }

   public static boolean gcmSpecExtractable() {
      return constructor != null;
   }

   public static boolean isGcmSpec(AlgorithmParameterSpec var0) {
      return gcmSpecClass != null && gcmSpecClass.isInstance(var0);
   }

   public static boolean isGcmSpec(Class var0) {
      return gcmSpecClass == var0;
   }

   public static AlgorithmParameterSpec extractGcmSpec(ASN1Primitive var0) throws InvalidParameterSpecException {
      try {
         GCMParameters var1 = GCMParameters.getInstance(var0);
         return (AlgorithmParameterSpec)constructor.newInstance(Integers.valueOf(var1.getIcvLen() * 8), var1.getNonce());
      } catch (Exception var2) {
         throw new InvalidParameterSpecException("Construction failed: " + var2.getMessage());
      }
   }

   static AEADParameters extractAeadParameters(final KeyParameter var0, final AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction() {
            @Override
            public Object run() throws Exception {
               return new AEADParameters(var0, (Integer)GcmSpecUtil.tLen.invoke(var1), (byte[])GcmSpecUtil.iv.invoke(var1));
            }
         });
      } catch (Exception var3) {
         throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
      }
   }

   public static GCMParameters extractGcmParameters(final AlgorithmParameterSpec var0) throws InvalidParameterSpecException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction() {
            @Override
            public Object run() throws Exception {
               return new GCMParameters((byte[])GcmSpecUtil.iv.invoke(var0), (Integer)GcmSpecUtil.tLen.invoke(var0) / 8);
            }
         });
      } catch (Exception var2) {
         throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
      }
   }

   static {
      if (gcmSpecClass != null) {
         constructor = extractConstructor();
         tLen = extractMethod("getTLen");
         iv = extractMethod("getIV");
      } else {
         constructor = null;
         tLen = null;
         iv = null;
      }
   }
}
