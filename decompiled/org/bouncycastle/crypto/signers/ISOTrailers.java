package org.bouncycastle.crypto.signers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Integers;

public class ISOTrailers {
   private static final Map<String, Integer> trailerMap;
   public static final int TRAILER_IMPLICIT = 188;
   public static final int TRAILER_RIPEMD160 = 12748;
   public static final int TRAILER_RIPEMD128 = 13004;
   public static final int TRAILER_SHA1 = 13260;
   public static final int TRAILER_SHA256 = 13516;
   public static final int TRAILER_SHA512 = 13772;
   public static final int TRAILER_SHA384 = 14028;
   public static final int TRAILER_WHIRLPOOL = 14284;
   public static final int TRAILER_SHA224 = 14540;
   public static final int TRAILER_SHA512_224 = 14796;
   public static final int TRAILER_SHA512_256 = 15052;

   public static Integer getTrailer(Digest var0) {
      return trailerMap.get(var0.getAlgorithmName());
   }

   public static boolean noTrailerAvailable(Digest var0) {
      return !trailerMap.containsKey(var0.getAlgorithmName());
   }

   static {
      HashMap var0 = new HashMap();
      var0.put("RIPEMD128", Integers.valueOf(13004));
      var0.put("RIPEMD160", Integers.valueOf(12748));
      var0.put("SHA-1", Integers.valueOf(13260));
      var0.put("SHA-224", Integers.valueOf(14540));
      var0.put("SHA-256", Integers.valueOf(13516));
      var0.put("SHA-384", Integers.valueOf(14028));
      var0.put("SHA-512", Integers.valueOf(13772));
      var0.put("SHA-512/224", Integers.valueOf(14796));
      var0.put("SHA-512/256", Integers.valueOf(15052));
      var0.put("Whirlpool", Integers.valueOf(14284));
      trailerMap = Collections.unmodifiableMap(var0);
   }
}
