package org.bouncycastle.jcajce.spec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.spec.AlgorithmParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public class SkeinParameterSpec implements AlgorithmParameterSpec {
   public static final int PARAM_TYPE_KEY = 0;
   public static final int PARAM_TYPE_CONFIG = 4;
   public static final int PARAM_TYPE_PERSONALISATION = 8;
   public static final int PARAM_TYPE_PUBLIC_KEY = 12;
   public static final int PARAM_TYPE_KEY_IDENTIFIER = 16;
   public static final int PARAM_TYPE_NONCE = 20;
   public static final int PARAM_TYPE_MESSAGE = 48;
   public static final int PARAM_TYPE_OUTPUT = 63;
   private Map parameters;

   public SkeinParameterSpec() {
      this(new HashMap());
   }

   private SkeinParameterSpec(Map var1) {
      this.parameters = Collections.unmodifiableMap(var1);
   }

   public Map getParameters() {
      return this.parameters;
   }

   public byte[] getKey() {
      return Arrays.clone((byte[])this.parameters.get(Integers.valueOf(0)));
   }

   public byte[] getPersonalisation() {
      return Arrays.clone((byte[])this.parameters.get(Integers.valueOf(8)));
   }

   public byte[] getPublicKey() {
      return Arrays.clone((byte[])this.parameters.get(Integers.valueOf(12)));
   }

   public byte[] getKeyIdentifier() {
      return Arrays.clone((byte[])this.parameters.get(Integers.valueOf(16)));
   }

   public byte[] getNonce() {
      return Arrays.clone((byte[])this.parameters.get(Integers.valueOf(20)));
   }

   public static class Builder {
      private Map parameters = new HashMap();

      public Builder() {
      }

      public Builder(SkeinParameterSpec var1) {
         for (Integer var3 : var1.parameters.keySet()) {
            this.parameters.put(var3, var1.parameters.get(var3));
         }
      }

      public SkeinParameterSpec.Builder set(int var1, byte[] var2) {
         if (var2 == null) {
            throw new IllegalArgumentException("Parameter value must not be null.");
         } else if (var1 == 0 || var1 > 4 && var1 < 63 && var1 != 48) {
            if (var1 == 4) {
               throw new IllegalArgumentException("Parameter type 4 is reserved for internal use.");
            } else {
               this.parameters.put(Integers.valueOf(var1), var2);
               return this;
            }
         } else {
            throw new IllegalArgumentException("Parameter types must be in the range 0,5..47,49..62.");
         }
      }

      public SkeinParameterSpec.Builder setKey(byte[] var1) {
         return this.set(0, var1);
      }

      public SkeinParameterSpec.Builder setPersonalisation(byte[] var1) {
         return this.set(8, var1);
      }

      public SkeinParameterSpec.Builder setPersonalisation(Date var1, String var2, String var3) {
         try {
            ByteArrayOutputStream var4 = new ByteArrayOutputStream();
            OutputStreamWriter var5 = new OutputStreamWriter(var4, "UTF-8");
            SimpleDateFormat var6 = new SimpleDateFormat("yyyyMMdd");
            var5.write(var6.format(var1));
            var5.write(" ");
            var5.write(var2);
            var5.write(" ");
            var5.write(var3);
            var5.close();
            return this.set(8, var4.toByteArray());
         } catch (IOException var7) {
            throw new IllegalStateException("Byte I/O failed: " + var7);
         }
      }

      public SkeinParameterSpec.Builder setPersonalisation(Date var1, Locale var2, String var3, String var4) {
         try {
            ByteArrayOutputStream var5 = new ByteArrayOutputStream();
            OutputStreamWriter var6 = new OutputStreamWriter(var5, "UTF-8");
            SimpleDateFormat var7 = new SimpleDateFormat("yyyyMMdd", var2);
            var6.write(var7.format(var1));
            var6.write(" ");
            var6.write(var3);
            var6.write(" ");
            var6.write(var4);
            var6.close();
            return this.set(8, var5.toByteArray());
         } catch (IOException var8) {
            throw new IllegalStateException("Byte I/O failed: " + var8);
         }
      }

      public SkeinParameterSpec.Builder setPublicKey(byte[] var1) {
         return this.set(12, var1);
      }

      public SkeinParameterSpec.Builder setKeyIdentifier(byte[] var1) {
         return this.set(16, var1);
      }

      public SkeinParameterSpec.Builder setNonce(byte[] var1) {
         return this.set(20, var1);
      }

      public SkeinParameterSpec build() {
         return new SkeinParameterSpec(this.parameters);
      }
   }
}
