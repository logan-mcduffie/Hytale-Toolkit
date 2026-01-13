package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class StringUtils {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   public static final String PROPER_NIL_UUID = "00000000-0000-0000-0000-000000000000";
   private static final String CORRUPTED_NIL_UUID = "0000-0000";
   @NotNull
   private static final Pattern PATTERN_WORD_SNAKE_CASE = Pattern.compile("[\\W_]+");

   private StringUtils() {
   }

   @Nullable
   public static String getStringAfterDot(@Nullable String str) {
      if (str != null) {
         int lastDotIndex = str.lastIndexOf(".");
         return lastDotIndex >= 0 && str.length() > lastDotIndex + 1 ? str.substring(lastDotIndex + 1) : str;
      } else {
         return null;
      }
   }

   @Nullable
   public static String capitalize(@Nullable String str) {
      return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT) : str;
   }

   @Nullable
   public static String camelCase(@Nullable String str) {
      if (str != null && !str.isEmpty()) {
         String[] words = PATTERN_WORD_SNAKE_CASE.split(str, -1);
         StringBuilder builder = new StringBuilder();

         for (String w : words) {
            builder.append(capitalize(w));
         }

         return builder.toString();
      } else {
         return str;
      }
   }

   @Nullable
   public static String removeSurrounding(@Nullable String str, @Nullable String delimiter) {
      return str != null && delimiter != null && str.startsWith(delimiter) && str.endsWith(delimiter)
         ? str.substring(delimiter.length(), str.length() - delimiter.length())
         : str;
   }

   @NotNull
   public static String byteCountToString(long bytes) {
      if (-1000L < bytes && bytes < 1000L) {
         return bytes + " B";
      } else {
         CharacterIterator ci = new StringCharacterIterator("kMGTPE");

         while (bytes <= -999950L || bytes >= 999950L) {
            bytes /= 1000L;
            ci.next();
         }

         return String.format(Locale.ROOT, "%.1f %cB", bytes / 1000.0, ci.current());
      }
   }

   @Nullable
   public static String calculateStringHash(@Nullable String str, @NotNull ILogger logger) {
      if (str != null && !str.isEmpty()) {
         try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(str.getBytes(UTF_8));
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder stringBuilder = new StringBuilder(no.toString(16));
            return stringBuilder.toString();
         } catch (NoSuchAlgorithmException var6) {
            logger.log(SentryLevel.INFO, "SHA-1 isn't available to calculate the hash.", var6);
         } catch (Throwable var7) {
            logger.log(SentryLevel.INFO, "string: %s could not calculate its hash", var7, str);
         }

         return null;
      } else {
         return null;
      }
   }

   public static int countOf(@NotNull String str, char character) {
      int count = 0;

      for (int i = 0; i < str.length(); i++) {
         if (str.charAt(i) == character) {
            count++;
         }
      }

      return count;
   }

   public static String normalizeUUID(@NotNull String uuidString) {
      return uuidString.equals("0000-0000") ? "00000000-0000-0000-0000-000000000000" : uuidString;
   }

   public static String join(@NotNull CharSequence delimiter, @NotNull Iterable<? extends CharSequence> elements) {
      StringBuilder stringBuilder = new StringBuilder();
      Iterator<? extends CharSequence> iterator = elements.iterator();
      if (iterator.hasNext()) {
         stringBuilder.append(iterator.next());

         while (iterator.hasNext()) {
            stringBuilder.append(delimiter);
            stringBuilder.append(iterator.next());
         }
      }

      return stringBuilder.toString();
   }

   @Nullable
   public static String toString(@Nullable Object object) {
      return object == null ? null : object.toString();
   }

   @NotNull
   public static String removePrefix(@Nullable String string, @NotNull String prefix) {
      if (string == null) {
         return "";
      } else {
         int index = string.indexOf(prefix);
         return index == 0 ? string.substring(prefix.length()) : string;
      }
   }

   @NotNull
   public static String substringBefore(@Nullable String string, @NotNull String separator) {
      if (string == null) {
         return "";
      } else {
         int index = string.indexOf(separator);
         return index >= 0 ? string.substring(0, index) : string;
      }
   }
}
