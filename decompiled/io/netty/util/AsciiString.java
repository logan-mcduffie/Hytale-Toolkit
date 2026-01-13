package io.netty.util;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public final class AsciiString implements CharSequence, Comparable<CharSequence> {
   public static final AsciiString EMPTY_STRING = cached("");
   private static final char MAX_CHAR_VALUE = 'ÿ';
   public static final int INDEX_NOT_FOUND = -1;
   private final byte[] value;
   private final int offset;
   private final int length;
   private int hash;
   private String string;
   public static final HashingStrategy<CharSequence> CASE_INSENSITIVE_HASHER = new HashingStrategy<CharSequence>() {
      public int hashCode(CharSequence o) {
         return AsciiString.hashCode(o);
      }

      public boolean equals(CharSequence a, CharSequence b) {
         return AsciiString.contentEqualsIgnoreCase(a, b);
      }
   };
   public static final HashingStrategy<CharSequence> CASE_SENSITIVE_HASHER = new HashingStrategy<CharSequence>() {
      public int hashCode(CharSequence o) {
         return AsciiString.hashCode(o);
      }

      public boolean equals(CharSequence a, CharSequence b) {
         return AsciiString.contentEquals(a, b);
      }
   };

   public AsciiString(byte[] value) {
      this(value, true);
   }

   public AsciiString(byte[] value, boolean copy) {
      this(value, 0, value.length, copy);
   }

   public AsciiString(byte[] value, int start, int length, boolean copy) {
      if (copy) {
         byte[] rangedCopy = new byte[length];
         System.arraycopy(value, start, rangedCopy, 0, rangedCopy.length);
         this.value = rangedCopy;
         this.offset = 0;
      } else {
         if (MathUtil.isOutOfBounds(start, length, value.length)) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')');
         }

         this.value = value;
         this.offset = start;
      }

      this.length = length;
   }

   public AsciiString(ByteBuffer value) {
      this(value, true);
   }

   public AsciiString(ByteBuffer value, boolean copy) {
      this(value, value.position(), value.remaining(), copy);
   }

   public AsciiString(ByteBuffer value, int start, int length, boolean copy) {
      if (MathUtil.isOutOfBounds(start, length, value.capacity())) {
         throw new IndexOutOfBoundsException(
            "expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.capacity(" + value.capacity() + ')'
         );
      } else {
         if (value.hasArray()) {
            if (copy) {
               int bufferOffset = value.arrayOffset() + start;
               this.value = Arrays.copyOfRange(value.array(), bufferOffset, bufferOffset + length);
               this.offset = 0;
            } else {
               this.value = value.array();
               this.offset = start;
            }
         } else {
            this.value = PlatformDependent.allocateUninitializedArray(length);
            int oldPos = value.position();
            value.get(this.value, 0, length);
            ((Buffer)value).position(oldPos);
            this.offset = 0;
         }

         this.length = length;
      }
   }

   public AsciiString(char[] value) {
      this(value, 0, value.length);
   }

   public AsciiString(char[] value, int start, int length) {
      if (MathUtil.isOutOfBounds(start, length, value.length)) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length + ')');
      } else {
         this.value = PlatformDependent.allocateUninitializedArray(length);
         int i = 0;

         for (int j = start; i < length; j++) {
            this.value[i] = c2b(value[j]);
            i++;
         }

         this.offset = 0;
         this.length = length;
      }
   }

   public AsciiString(char[] value, Charset charset) {
      this(value, charset, 0, value.length);
   }

   public AsciiString(char[] value, Charset charset, int start, int length) {
      CharBuffer cbuf = CharBuffer.wrap(value, start, length);
      CharsetEncoder encoder = CharsetUtil.encoder(charset);
      ByteBuffer nativeBuffer = ByteBuffer.allocate((int)(encoder.maxBytesPerChar() * length));
      encoder.encode(cbuf, nativeBuffer, true);
      int bufferOffset = nativeBuffer.arrayOffset();
      this.value = Arrays.copyOfRange(nativeBuffer.array(), bufferOffset, bufferOffset + nativeBuffer.position());
      this.offset = 0;
      this.length = this.value.length;
   }

   public AsciiString(CharSequence value) {
      this(value, 0, value.length());
   }

   public AsciiString(CharSequence value, int start, int length) {
      if (MathUtil.isOutOfBounds(start, length, value.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= start + length(" + length + ") <= value.length(" + value.length() + ')');
      } else {
         this.value = PlatformDependent.allocateUninitializedArray(length);
         int i = 0;

         for (int j = start; i < length; j++) {
            this.value[i] = c2b(value.charAt(j));
            i++;
         }

         this.offset = 0;
         this.length = length;
      }
   }

   public AsciiString(CharSequence value, Charset charset) {
      this(value, charset, 0, value.length());
   }

   public AsciiString(CharSequence value, Charset charset, int start, int length) {
      CharBuffer cbuf = CharBuffer.wrap(value, start, start + length);
      CharsetEncoder encoder = CharsetUtil.encoder(charset);
      ByteBuffer nativeBuffer = ByteBuffer.allocate((int)(encoder.maxBytesPerChar() * length));
      encoder.encode(cbuf, nativeBuffer, true);
      int offset = nativeBuffer.arrayOffset();
      this.value = Arrays.copyOfRange(nativeBuffer.array(), offset, offset + nativeBuffer.position());
      this.offset = 0;
      this.length = this.value.length;
   }

   public int forEachByte(ByteProcessor visitor) throws Exception {
      return this.forEachByte0(0, this.length(), visitor);
   }

   public int forEachByte(int index, int length, ByteProcessor visitor) throws Exception {
      if (MathUtil.isOutOfBounds(index, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + 41);
      } else {
         return this.forEachByte0(index, length, visitor);
      }
   }

   private int forEachByte0(int index, int length, ByteProcessor visitor) throws Exception {
      int len = this.offset + index + length;

      for (int i = this.offset + index; i < len; i++) {
         if (!visitor.process(this.value[i])) {
            return i - this.offset;
         }
      }

      return -1;
   }

   public int forEachByteDesc(ByteProcessor visitor) throws Exception {
      return this.forEachByteDesc0(0, this.length(), visitor);
   }

   public int forEachByteDesc(int index, int length, ByteProcessor visitor) throws Exception {
      if (MathUtil.isOutOfBounds(index, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= index(" + index + ") <= start + length(" + length + ") <= length(" + this.length() + 41);
      } else {
         return this.forEachByteDesc0(index, length, visitor);
      }
   }

   private int forEachByteDesc0(int index, int length, ByteProcessor visitor) throws Exception {
      int end = this.offset + index;

      for (int i = this.offset + index + length - 1; i >= end; i--) {
         if (!visitor.process(this.value[i])) {
            return i - this.offset;
         }
      }

      return -1;
   }

   public byte byteAt(int index) {
      if (index < 0 || index >= this.length) {
         throw new IndexOutOfBoundsException("index: " + index + " must be in the range [0," + this.length + ")");
      } else {
         return PlatformDependent.hasUnsafe() ? PlatformDependent.getByte(this.value, index + this.offset) : this.value[index + this.offset];
      }
   }

   @Override
   public boolean isEmpty() {
      return this.length == 0;
   }

   @Override
   public int length() {
      return this.length;
   }

   public void arrayChanged() {
      this.string = null;
      this.hash = 0;
   }

   public byte[] array() {
      return this.value;
   }

   public int arrayOffset() {
      return this.offset;
   }

   public boolean isEntireArrayUsed() {
      return this.offset == 0 && this.length == this.value.length;
   }

   public byte[] toByteArray() {
      return this.toByteArray(0, this.length());
   }

   public byte[] toByteArray(int start, int end) {
      return Arrays.copyOfRange(this.value, start + this.offset, end + this.offset);
   }

   public void copy(int srcIdx, byte[] dst, int dstIdx, int length) {
      if (MathUtil.isOutOfBounds(srcIdx, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
      } else {
         System.arraycopy(this.value, srcIdx + this.offset, ObjectUtil.checkNotNull(dst, "dst"), dstIdx, length);
      }
   }

   @Override
   public char charAt(int index) {
      return b2c(this.byteAt(index));
   }

   public boolean contains(CharSequence cs) {
      return this.indexOf(cs) >= 0;
   }

   public int compareTo(CharSequence string) {
      if (this == string) {
         return 0;
      } else {
         int length1 = this.length();
         int length2 = string.length();
         int minLength = Math.min(length1, length2);
         int i = 0;

         for (int j = this.arrayOffset(); i < minLength; j++) {
            int result = b2c(this.value[j]) - string.charAt(i);
            if (result != 0) {
               return result;
            }

            i++;
         }

         return length1 - length2;
      }
   }

   public AsciiString concat(CharSequence string) {
      int thisLen = this.length();
      int thatLen = string.length();
      if (thatLen == 0) {
         return this;
      } else if (string instanceof AsciiString) {
         AsciiString that = (AsciiString)string;
         if (this.isEmpty()) {
            return that;
         } else {
            byte[] newValue = PlatformDependent.allocateUninitializedArray(thisLen + thatLen);
            System.arraycopy(this.value, this.arrayOffset(), newValue, 0, thisLen);
            System.arraycopy(that.value, that.arrayOffset(), newValue, thisLen, thatLen);
            return new AsciiString(newValue, false);
         }
      } else if (this.isEmpty()) {
         return new AsciiString(string);
      } else {
         byte[] newValue = PlatformDependent.allocateUninitializedArray(thisLen + thatLen);
         System.arraycopy(this.value, this.arrayOffset(), newValue, 0, thisLen);
         int i = thisLen;

         for (int j = 0; i < newValue.length; j++) {
            newValue[i] = c2b(string.charAt(j));
            i++;
         }

         return new AsciiString(newValue, false);
      }
   }

   public boolean endsWith(CharSequence suffix) {
      int suffixLen = suffix.length();
      return this.regionMatches(this.length() - suffixLen, suffix, 0, suffixLen);
   }

   public boolean contentEqualsIgnoreCase(CharSequence string) {
      if (this == string) {
         return true;
      } else if (string != null && string.length() == this.length()) {
         if (string instanceof AsciiString) {
            AsciiString other = (AsciiString)string;
            byte[] value = this.value;
            if (this.offset == 0 && other.offset == 0 && this.length == value.length) {
               byte[] otherValue = other.value;

               for (int i = 0; i < value.length; i++) {
                  if (!equalsIgnoreCase(value[i], otherValue[i])) {
                     return false;
                  }
               }

               return true;
            } else {
               return this.misalignedEqualsIgnoreCase(other);
            }
         } else {
            byte[] value = this.value;
            int ix = this.offset;

            for (int j = 0; j < string.length(); j++) {
               if (!equalsIgnoreCase(b2c(value[ix]), string.charAt(j))) {
                  return false;
               }

               ix++;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean misalignedEqualsIgnoreCase(AsciiString other) {
      byte[] value = this.value;
      byte[] otherValue = other.value;
      int i = this.offset;
      int j = other.offset;

      for (int end = this.offset + this.length; i < end; j++) {
         if (!equalsIgnoreCase(value[i], otherValue[j])) {
            return false;
         }

         i++;
      }

      return true;
   }

   public char[] toCharArray() {
      return this.toCharArray(0, this.length());
   }

   public char[] toCharArray(int start, int end) {
      int length = end - start;
      if (length == 0) {
         return EmptyArrays.EMPTY_CHARS;
      } else if (MathUtil.isOutOfBounds(start, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
      } else {
         char[] buffer = new char[length];
         int i = 0;

         for (int j = start + this.arrayOffset(); i < length; j++) {
            buffer[i] = b2c(this.value[j]);
            i++;
         }

         return buffer;
      }
   }

   public void copy(int srcIdx, char[] dst, int dstIdx, int length) {
      ObjectUtil.checkNotNull(dst, "dst");
      if (MathUtil.isOutOfBounds(srcIdx, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + srcIdx + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
      } else {
         int dstEnd = dstIdx + length;
         int i = dstIdx;

         for (int j = srcIdx + this.arrayOffset(); i < dstEnd; j++) {
            dst[i] = b2c(this.value[j]);
            i++;
         }
      }
   }

   public AsciiString subSequence(int start) {
      return this.subSequence(start, this.length());
   }

   public AsciiString subSequence(int start, int end) {
      return this.subSequence(start, end, true);
   }

   public AsciiString subSequence(int start, int end, boolean copy) {
      if (MathUtil.isOutOfBounds(start, end - start, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end + ") <= length(" + this.length() + ')');
      } else if (start == 0 && end == this.length()) {
         return this;
      } else {
         return end == start ? EMPTY_STRING : new AsciiString(this.value, start + this.offset, end - start, copy);
      }
   }

   public int indexOf(CharSequence string) {
      return this.indexOf(string, 0);
   }

   public int indexOf(CharSequence subString, int start) {
      int subCount = subString.length();
      if (start < 0) {
         start = 0;
      }

      if (subCount <= 0) {
         return start < this.length ? start : this.length;
      } else if (subCount > this.length - start) {
         return -1;
      } else {
         char firstChar = subString.charAt(0);
         if (firstChar > 255) {
            return -1;
         } else {
            byte firstCharAsByte = c2b0(firstChar);
            int len = this.offset + this.length - subCount;

            for (int i = start + this.offset; i <= len; i++) {
               if (this.value[i] == firstCharAsByte) {
                  int o1 = i;
                  int o2 = 0;

                  while (++o2 < subCount) {
                     if (b2c(this.value[++o1]) != subString.charAt(o2)) {
                        break;
                     }
                  }

                  if (o2 == subCount) {
                     return i - this.offset;
                  }
               }
            }

            return -1;
         }
      }
   }

   public int indexOf(char ch, int start) {
      if (ch > 255) {
         return -1;
      } else {
         if (start < 0) {
            start = 0;
         }

         byte chAsByte = c2b0(ch);
         int len = this.offset + this.length;

         for (int i = start + this.offset; i < len; i++) {
            if (this.value[i] == chAsByte) {
               return i - this.offset;
            }
         }

         return -1;
      }
   }

   public int lastIndexOf(CharSequence string) {
      return this.lastIndexOf(string, this.length);
   }

   public int lastIndexOf(CharSequence subString, int start) {
      int subCount = subString.length();
      start = Math.min(start, this.length - subCount);
      if (start < 0) {
         return -1;
      } else if (subCount == 0) {
         return start;
      } else {
         char firstChar = subString.charAt(0);
         if (firstChar > 255) {
            return -1;
         } else {
            byte firstCharAsByte = c2b0(firstChar);

            for (int i = this.offset + start; i >= this.offset; i--) {
               if (this.value[i] == firstCharAsByte) {
                  int o1 = i;
                  int o2 = 0;

                  while (++o2 < subCount) {
                     if (b2c(this.value[++o1]) != subString.charAt(o2)) {
                        break;
                     }
                  }

                  if (o2 == subCount) {
                     return i - this.offset;
                  }
               }
            }

            return -1;
         }
      }
   }

   public boolean regionMatches(int thisStart, CharSequence string, int start, int length) {
      ObjectUtil.checkNotNull(string, "string");
      if (start >= 0 && string.length() - start >= length) {
         int thisLen = this.length();
         if (thisStart >= 0 && thisLen - thisStart >= length) {
            if (length <= 0) {
               return true;
            } else if (string instanceof AsciiString) {
               AsciiString asciiString = (AsciiString)string;
               return PlatformDependent.equals(this.value, thisStart + this.offset, asciiString.value, start + asciiString.offset, length);
            } else {
               int thatEnd = start + length;
               int i = start;

               for (int j = thisStart + this.arrayOffset(); i < thatEnd; j++) {
                  if (b2c(this.value[j]) != string.charAt(i)) {
                     return false;
                  }

                  i++;
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean regionMatches(boolean ignoreCase, int thisStart, CharSequence string, int start, int length) {
      if (!ignoreCase) {
         return this.regionMatches(thisStart, string, start, length);
      } else {
         ObjectUtil.checkNotNull(string, "string");
         int thisLen = this.length();
         if (thisStart >= 0 && length <= thisLen - thisStart) {
            if (start >= 0 && length <= string.length() - start) {
               thisStart += this.arrayOffset();
               int thisEnd = thisStart + length;
               if (string instanceof AsciiString) {
                  AsciiString asciiString = (AsciiString)string;
                  byte[] value = this.value;
                  byte[] otherValue = asciiString.value;
                  start += asciiString.offset;

                  while (thisStart < thisEnd) {
                     if (!equalsIgnoreCase(value[thisStart++], otherValue[start++])) {
                        return false;
                     }
                  }

                  return true;
               } else {
                  while (thisStart < thisEnd) {
                     if (!equalsIgnoreCase(b2c(this.value[thisStart++]), string.charAt(start++))) {
                        return false;
                     }
                  }

                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public AsciiString replace(char oldChar, char newChar) {
      if (oldChar > 255) {
         return this;
      } else {
         byte oldCharAsByte = c2b0(oldChar);
         byte newCharAsByte = c2b(newChar);
         int len = this.offset + this.length;

         for (int i = this.offset; i < len; i++) {
            if (this.value[i] == oldCharAsByte) {
               byte[] buffer = PlatformDependent.allocateUninitializedArray(this.length());
               System.arraycopy(this.value, this.offset, buffer, 0, i - this.offset);
               buffer[i - this.offset] = newCharAsByte;
               i++;

               while (i < len) {
                  byte oldValue = this.value[i];
                  buffer[i - this.offset] = oldValue != oldCharAsByte ? oldValue : newCharAsByte;
                  i++;
               }

               return new AsciiString(buffer, false);
            }
         }

         return this;
      }
   }

   public boolean startsWith(CharSequence prefix) {
      return this.startsWith(prefix, 0);
   }

   public boolean startsWith(CharSequence prefix, int start) {
      return this.regionMatches(start, prefix, 0, prefix.length());
   }

   public AsciiString toLowerCase() {
      return AsciiStringUtil.toLowerCase(this);
   }

   public AsciiString toUpperCase() {
      return AsciiStringUtil.toUpperCase(this);
   }

   public static CharSequence trim(CharSequence c) {
      if (c instanceof AsciiString) {
         return ((AsciiString)c).trim();
      } else if (c instanceof String) {
         return ((String)c).trim();
      } else {
         int start = 0;
         int last = c.length() - 1;
         int end = last;

         while (start <= end && c.charAt(start) <= ' ') {
            start++;
         }

         while (end >= start && c.charAt(end) <= ' ') {
            end--;
         }

         return start == 0 && end == last ? c : c.subSequence(start, end);
      }
   }

   public AsciiString trim() {
      int start = this.arrayOffset();
      int last = this.arrayOffset() + this.length() - 1;
      int end = last;

      while (start <= end && this.value[start] <= 32) {
         start++;
      }

      while (end >= start && this.value[end] <= 32) {
         end--;
      }

      return start == 0 && end == last ? this : new AsciiString(this.value, start, end - start + 1, false);
   }

   public boolean contentEquals(CharSequence a) {
      if (this == a) {
         return true;
      } else if (a != null && a.length() == this.length()) {
         if (a instanceof AsciiString) {
            return this.equals(a);
         } else {
            int i = this.arrayOffset();

            for (int j = 0; j < a.length(); j++) {
               if (b2c(this.value[i]) != a.charAt(j)) {
                  return false;
               }

               i++;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean matches(String expr) {
      return Pattern.matches(expr, this);
   }

   public AsciiString[] split(String expr, int max) {
      return toAsciiStringArray(Pattern.compile(expr).split(this, max));
   }

   public AsciiString[] split(char delim) {
      List<AsciiString> res = InternalThreadLocalMap.get().arrayList();
      int start = 0;
      int length = this.length();

      for (int i = start; i < length; i++) {
         if (this.charAt(i) == delim) {
            if (start == i) {
               res.add(EMPTY_STRING);
            } else {
               res.add(new AsciiString(this.value, start + this.arrayOffset(), i - start, false));
            }

            start = i + 1;
         }
      }

      if (start == 0) {
         res.add(this);
      } else if (start != length) {
         res.add(new AsciiString(this.value, start + this.arrayOffset(), length - start, false));
      } else {
         for (int ix = res.size() - 1; ix >= 0 && res.get(ix).isEmpty(); ix--) {
            res.remove(ix);
         }
      }

      return res.toArray(EmptyArrays.EMPTY_ASCII_STRINGS);
   }

   @Override
   public int hashCode() {
      int h = this.hash;
      if (h == 0) {
         h = PlatformDependent.hashCodeAscii(this.value, this.offset, this.length);
         this.hash = h;
      }

      return h;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || obj.getClass() != AsciiString.class) {
         return false;
      } else if (this == obj) {
         return true;
      } else {
         AsciiString other = (AsciiString)obj;
         return this.length() == other.length()
            && this.hashCode() == other.hashCode()
            && PlatformDependent.equals(this.array(), this.arrayOffset(), other.array(), other.arrayOffset(), this.length());
      }
   }

   @Override
   public String toString() {
      String cache = this.string;
      if (cache == null) {
         cache = this.toString(0);
         this.string = cache;
      }

      return cache;
   }

   public String toString(int start) {
      return this.toString(start, this.length());
   }

   public String toString(int start, int end) {
      int length = end - start;
      if (length == 0) {
         return "";
      } else if (MathUtil.isOutOfBounds(start, length, this.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= srcIdx + length(" + length + ") <= srcLen(" + this.length() + ')');
      } else {
         return new String(this.value, 0, start + this.offset, length);
      }
   }

   public boolean parseBoolean() {
      return this.length >= 1 && this.value[this.offset] != 0;
   }

   public char parseChar() {
      return this.parseChar(0);
   }

   public char parseChar(int start) {
      if (start + 1 >= this.length()) {
         throw new IndexOutOfBoundsException("2 bytes required to convert to character. index " + start + " would go out of bounds.");
      } else {
         int startWithOffset = start + this.offset;
         return (char)(b2c(this.value[startWithOffset]) << '\b' | b2c(this.value[startWithOffset + 1]));
      }
   }

   public short parseShort() {
      return this.parseShort(0, this.length(), 10);
   }

   public short parseShort(int radix) {
      return this.parseShort(0, this.length(), radix);
   }

   public short parseShort(int start, int end) {
      return this.parseShort(start, end, 10);
   }

   public short parseShort(int start, int end, int radix) {
      int intValue = this.parseInt(start, end, radix);
      short result = (short)intValue;
      if (result != intValue) {
         throw new NumberFormatException(this.subSequence(start, end, false).toString());
      } else {
         return result;
      }
   }

   public int parseInt() {
      return this.parseInt(0, this.length(), 10);
   }

   public int parseInt(int radix) {
      return this.parseInt(0, this.length(), radix);
   }

   public int parseInt(int start, int end) {
      return this.parseInt(start, end, 10);
   }

   public int parseInt(int start, int end, int radix) {
      if (radix < 2 || radix > 36) {
         throw new NumberFormatException();
      } else if (start == end) {
         throw new NumberFormatException();
      } else {
         int i = start;
         boolean negative = this.byteAt(start) == 45;
         if (negative) {
            i = start + 1;
            if (i == end) {
               throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
         }

         return this.parseInt(i, end, radix, negative);
      }
   }

   private int parseInt(int start, int end, int radix, boolean negative) {
      int max = Integer.MIN_VALUE / radix;
      int result = 0;
      int currOffset = start;

      while (currOffset < end) {
         int digit = Character.digit((char)(this.value[currOffset++ + this.offset] & 255), radix);
         if (digit == -1) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         if (max > result) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         int next = result * radix - digit;
         if (next > result) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         result = next;
      }

      if (!negative) {
         result = -result;
         if (result < 0) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }
      }

      return result;
   }

   public long parseLong() {
      return this.parseLong(0, this.length(), 10);
   }

   public long parseLong(int radix) {
      return this.parseLong(0, this.length(), radix);
   }

   public long parseLong(int start, int end) {
      return this.parseLong(start, end, 10);
   }

   public long parseLong(int start, int end, int radix) {
      if (radix < 2 || radix > 36) {
         throw new NumberFormatException();
      } else if (start == end) {
         throw new NumberFormatException();
      } else {
         int i = start;
         boolean negative = this.byteAt(start) == 45;
         if (negative) {
            i = start + 1;
            if (i == end) {
               throw new NumberFormatException(this.subSequence(start, end, false).toString());
            }
         }

         return this.parseLong(i, end, radix, negative);
      }
   }

   private long parseLong(int start, int end, int radix, boolean negative) {
      long max = Long.MIN_VALUE / radix;
      long result = 0L;
      int currOffset = start;

      while (currOffset < end) {
         int digit = Character.digit((char)(this.value[currOffset++ + this.offset] & 255), radix);
         if (digit == -1) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         if (max > result) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         long next = result * radix - digit;
         if (next > result) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }

         result = next;
      }

      if (!negative) {
         result = -result;
         if (result < 0L) {
            throw new NumberFormatException(this.subSequence(start, end, false).toString());
         }
      }

      return result;
   }

   public float parseFloat() {
      return this.parseFloat(0, this.length());
   }

   public float parseFloat(int start, int end) {
      return Float.parseFloat(this.toString(start, end));
   }

   public double parseDouble() {
      return this.parseDouble(0, this.length());
   }

   public double parseDouble(int start, int end) {
      return Double.parseDouble(this.toString(start, end));
   }

   public static AsciiString of(CharSequence string) {
      return string instanceof AsciiString ? (AsciiString)string : new AsciiString(string);
   }

   public static AsciiString cached(String string) {
      AsciiString asciiString = new AsciiString(string);
      asciiString.string = string;
      return asciiString;
   }

   public static int hashCode(CharSequence value) {
      if (value == null) {
         return 0;
      } else {
         return value instanceof AsciiString ? value.hashCode() : PlatformDependent.hashCodeAscii(value);
      }
   }

   public static boolean contains(CharSequence a, CharSequence b) {
      return contains(a, b, AsciiString.DefaultCharEqualityComparator.INSTANCE);
   }

   public static boolean containsIgnoreCase(CharSequence a, CharSequence b) {
      return contains(a, b, AsciiString.AsciiCaseInsensitiveCharEqualityComparator.INSTANCE);
   }

   public static boolean contentEqualsIgnoreCase(CharSequence a, CharSequence b) {
      if (a != null && b != null) {
         if (a instanceof AsciiString) {
            return ((AsciiString)a).contentEqualsIgnoreCase(b);
         } else if (b instanceof AsciiString) {
            return ((AsciiString)b).contentEqualsIgnoreCase(a);
         } else if (a.length() != b.length()) {
            return false;
         } else {
            for (int i = 0; i < a.length(); i++) {
               if (!equalsIgnoreCase(a.charAt(i), b.charAt(i))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return a == b;
      }
   }

   public static boolean containsContentEqualsIgnoreCase(Collection<CharSequence> collection, CharSequence value) {
      for (CharSequence v : collection) {
         if (contentEqualsIgnoreCase(value, v)) {
            return true;
         }
      }

      return false;
   }

   public static boolean containsAllContentEqualsIgnoreCase(Collection<CharSequence> a, Collection<CharSequence> b) {
      for (CharSequence v : b) {
         if (!containsContentEqualsIgnoreCase(a, v)) {
            return false;
         }
      }

      return true;
   }

   public static boolean contentEquals(CharSequence a, CharSequence b) {
      if (a != null && b != null) {
         if (a instanceof AsciiString) {
            return ((AsciiString)a).contentEquals(b);
         } else if (b instanceof AsciiString) {
            return ((AsciiString)b).contentEquals(a);
         } else if (a.length() != b.length()) {
            return false;
         } else {
            for (int i = 0; i < a.length(); i++) {
               if (a.charAt(i) != b.charAt(i)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return a == b;
      }
   }

   private static AsciiString[] toAsciiStringArray(String[] jdkResult) {
      AsciiString[] res = new AsciiString[jdkResult.length];

      for (int i = 0; i < jdkResult.length; i++) {
         res[i] = new AsciiString(jdkResult[i]);
      }

      return res;
   }

   private static boolean contains(CharSequence a, CharSequence b, AsciiString.CharEqualityComparator cmp) {
      if (a != null && b != null && a.length() >= b.length()) {
         if (b.length() == 0) {
            return true;
         } else {
            int bStart = 0;

            for (int i = 0; i < a.length(); i++) {
               if (cmp.equals(b.charAt(bStart), a.charAt(i))) {
                  if (++bStart == b.length()) {
                     return true;
                  }
               } else {
                  if (a.length() - i < b.length()) {
                     return false;
                  }

                  bStart = 0;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean regionMatchesCharSequences(
      CharSequence cs, int csStart, CharSequence string, int start, int length, AsciiString.CharEqualityComparator charEqualityComparator
   ) {
      if (csStart >= 0 && length <= cs.length() - csStart) {
         if (start >= 0 && length <= string.length() - start) {
            int csIndex = csStart;
            int csEnd = csStart + length;
            int stringIndex = start;

            while (csIndex < csEnd) {
               char c1 = cs.charAt(csIndex++);
               char c2 = string.charAt(stringIndex++);
               if (!charEqualityComparator.equals(c1, c2)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean regionMatches(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
      if (cs == null || string == null) {
         return false;
      } else if (cs instanceof String && string instanceof String) {
         return ((String)cs).regionMatches(ignoreCase, csStart, (String)string, start, length);
      } else {
         return cs instanceof AsciiString
            ? ((AsciiString)cs).regionMatches(ignoreCase, csStart, string, start, length)
            : regionMatchesCharSequences(
               cs,
               csStart,
               string,
               start,
               length,
               (AsciiString.CharEqualityComparator)(ignoreCase
                  ? AsciiString.GeneralCaseInsensitiveCharEqualityComparator.INSTANCE
                  : AsciiString.DefaultCharEqualityComparator.INSTANCE)
            );
      }
   }

   public static boolean regionMatchesAscii(CharSequence cs, boolean ignoreCase, int csStart, CharSequence string, int start, int length) {
      if (cs == null || string == null) {
         return false;
      } else if (!ignoreCase && cs instanceof String && string instanceof String) {
         return ((String)cs).regionMatches(false, csStart, (String)string, start, length);
      } else {
         return cs instanceof AsciiString
            ? ((AsciiString)cs).regionMatches(ignoreCase, csStart, string, start, length)
            : regionMatchesCharSequences(
               cs,
               csStart,
               string,
               start,
               length,
               (AsciiString.CharEqualityComparator)(ignoreCase
                  ? AsciiString.AsciiCaseInsensitiveCharEqualityComparator.INSTANCE
                  : AsciiString.DefaultCharEqualityComparator.INSTANCE)
            );
      }
   }

   public static int indexOfIgnoreCase(CharSequence str, CharSequence searchStr, int startPos) {
      if (str != null && searchStr != null) {
         if (startPos < 0) {
            startPos = 0;
         }

         int searchStrLen = searchStr.length();
         int endLimit = str.length() - searchStrLen + 1;
         if (startPos > endLimit) {
            return -1;
         } else if (searchStrLen == 0) {
            return startPos;
         } else {
            for (int i = startPos; i < endLimit; i++) {
               if (regionMatches(str, true, i, searchStr, 0, searchStrLen)) {
                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOfIgnoreCaseAscii(CharSequence str, CharSequence searchStr, int startPos) {
      if (str != null && searchStr != null) {
         if (startPos < 0) {
            startPos = 0;
         }

         int searchStrLen = searchStr.length();
         int endLimit = str.length() - searchStrLen + 1;
         if (startPos > endLimit) {
            return -1;
         } else if (searchStrLen == 0) {
            return startPos;
         } else {
            for (int i = startPos; i < endLimit; i++) {
               if (regionMatchesAscii(str, true, i, searchStr, 0, searchStrLen)) {
                  return i;
               }
            }

            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOf(CharSequence cs, char searchChar, int start) {
      if (cs instanceof String) {
         return ((String)cs).indexOf(searchChar, start);
      } else if (cs instanceof AsciiString) {
         return ((AsciiString)cs).indexOf(searchChar, start);
      } else if (cs == null) {
         return -1;
      } else {
         int sz = cs.length();

         for (int i = start < 0 ? 0 : start; i < sz; i++) {
            if (cs.charAt(i) == searchChar) {
               return i;
            }
         }

         return -1;
      }
   }

   private static boolean equalsIgnoreCase(byte a, byte b) {
      return a == b || AsciiStringUtil.toLowerCase(a) == AsciiStringUtil.toLowerCase(b);
   }

   private static boolean equalsIgnoreCase(char a, char b) {
      return a == b || toLowerCase(a) == toLowerCase(b);
   }

   public static char toLowerCase(char c) {
      return isUpperCase(c) ? (char)(c + ' ') : c;
   }

   private static byte toUpperCase(byte b) {
      return AsciiStringUtil.toUpperCase(b);
   }

   public static boolean isUpperCase(byte value) {
      return AsciiStringUtil.isUpperCase(value);
   }

   public static boolean isUpperCase(char value) {
      return value >= 'A' && value <= 'Z';
   }

   public static byte c2b(char c) {
      return (byte)(c > 255 ? 63 : c);
   }

   private static byte c2b0(char c) {
      return (byte)c;
   }

   public static char b2c(byte b) {
      return (char)(b & 255);
   }

   private static final class AsciiCaseInsensitiveCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.AsciiCaseInsensitiveCharEqualityComparator INSTANCE = new AsciiString.AsciiCaseInsensitiveCharEqualityComparator();

      @Override
      public boolean equals(char a, char b) {
         return AsciiString.equalsIgnoreCase(a, b);
      }
   }

   private interface CharEqualityComparator {
      boolean equals(char var1, char var2);
   }

   private static final class DefaultCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.DefaultCharEqualityComparator INSTANCE = new AsciiString.DefaultCharEqualityComparator();

      @Override
      public boolean equals(char a, char b) {
         return a == b;
      }
   }

   private static final class GeneralCaseInsensitiveCharEqualityComparator implements AsciiString.CharEqualityComparator {
      static final AsciiString.GeneralCaseInsensitiveCharEqualityComparator INSTANCE = new AsciiString.GeneralCaseInsensitiveCharEqualityComparator();

      @Override
      public boolean equals(char a, char b) {
         return Character.toUpperCase(a) == Character.toUpperCase(b) || Character.toLowerCase(a) == Character.toLowerCase(b);
      }
   }
}
