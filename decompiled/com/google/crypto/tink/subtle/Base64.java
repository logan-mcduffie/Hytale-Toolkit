package com.google.crypto.tink.subtle;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public final class Base64 {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   public static final int DEFAULT = 0;
   public static final int NO_PADDING = 1;
   public static final int NO_WRAP = 2;
   public static final int CRLF = 4;
   public static final int URL_SAFE = 8;
   public static final int NO_CLOSE = 16;

   public static byte[] decode(String input) {
      return decode(input, 2);
   }

   public static byte[] decode(String str, int flags) {
      return decode(str.getBytes(UTF_8), flags);
   }

   public static byte[] decode(byte[] input, int flags) {
      return decode(input, 0, input.length, flags);
   }

   public static byte[] decode(byte[] input, int offset, int len, int flags) {
      Base64.Decoder decoder = new Base64.Decoder(flags, new byte[len * 3 / 4]);
      if (!decoder.process(input, offset, len, true)) {
         throw new IllegalArgumentException("bad base-64");
      } else if (decoder.op == decoder.output.length) {
         return decoder.output;
      } else {
         byte[] temp = new byte[decoder.op];
         System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
         return temp;
      }
   }

   public static byte[] urlSafeDecode(String input) {
      return decode(input, 11);
   }

   public static String encode(final byte[] input) {
      return encodeToString(input, 2);
   }

   public static byte[] encode(byte[] input, int flags) {
      return encode(input, 0, input.length, flags);
   }

   public static byte[] encode(byte[] input, int offset, int len, int flags) {
      Base64.Encoder encoder = new Base64.Encoder(flags, null);
      int outputLen = len / 3 * 4;
      if (encoder.doPadding) {
         if (len % 3 > 0) {
            outputLen += 4;
         }
      } else {
         switch (len % 3) {
            case 0:
            default:
               break;
            case 1:
               outputLen += 2;
               break;
            case 2:
               outputLen += 3;
         }
      }

      if (encoder.doNewline && len > 0) {
         outputLen += ((len - 1) / 57 + 1) * (encoder.doCr ? 2 : 1);
      }

      encoder.output = new byte[outputLen];
      encoder.process(input, offset, len, true);

      assert encoder.op == outputLen;

      return encoder.output;
   }

   public static String urlSafeEncode(final byte[] input) {
      return encodeToString(input, 11);
   }

   public static String encodeToString(byte[] input, int flags) {
      try {
         return new String(encode(input, flags), "US-ASCII");
      } catch (UnsupportedEncodingException var3) {
         throw new AssertionError(var3);
      }
   }

   public static String encodeToString(byte[] input, int offset, int len, int flags) {
      try {
         return new String(encode(input, offset, len, flags), "US-ASCII");
      } catch (UnsupportedEncodingException var5) {
         throw new AssertionError(var5);
      }
   }

   private Base64() {
   }

   abstract static class Coder {
      public byte[] output;
      public int op;

      public abstract boolean process(byte[] input, int offset, int len, boolean finish);

      public abstract int maxOutputSize(int len);
   }

   static class Decoder extends Base64.Coder {
      private static final int[] decode = new int[]{
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         62,
         -1,
         -1,
         -1,
         63,
         52,
         53,
         54,
         55,
         56,
         57,
         58,
         59,
         60,
         61,
         -1,
         -1,
         -1,
         -2,
         -1,
         -1,
         -1,
         0,
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20,
         21,
         22,
         23,
         24,
         25,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         26,
         27,
         28,
         29,
         30,
         31,
         32,
         33,
         34,
         35,
         36,
         37,
         38,
         39,
         40,
         41,
         42,
         43,
         44,
         45,
         46,
         47,
         48,
         49,
         50,
         51,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1
      };
      private static final int[] decodeWebsafe = new int[]{
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         62,
         -1,
         -1,
         52,
         53,
         54,
         55,
         56,
         57,
         58,
         59,
         60,
         61,
         -1,
         -1,
         -1,
         -2,
         -1,
         -1,
         -1,
         0,
         1,
         2,
         3,
         4,
         5,
         6,
         7,
         8,
         9,
         10,
         11,
         12,
         13,
         14,
         15,
         16,
         17,
         18,
         19,
         20,
         21,
         22,
         23,
         24,
         25,
         -1,
         -1,
         -1,
         -1,
         63,
         -1,
         26,
         27,
         28,
         29,
         30,
         31,
         32,
         33,
         34,
         35,
         36,
         37,
         38,
         39,
         40,
         41,
         42,
         43,
         44,
         45,
         46,
         47,
         48,
         49,
         50,
         51,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1,
         -1
      };
      private static final int SKIP = -1;
      private static final int EQUALS = -2;
      private int state;
      private int value;
      private final int[] alphabet;

      public Decoder(int flags, byte[] output) {
         this.output = output;
         this.alphabet = (flags & 8) == 0 ? decode : decodeWebsafe;
         this.state = 0;
         this.value = 0;
      }

      @Override
      public int maxOutputSize(int len) {
         return len * 3 / 4 + 10;
      }

      @Override
      public boolean process(byte[] input, int offset, int len, boolean finish) {
         if (this.state == 6) {
            return false;
         } else {
            int p = offset;
            len += offset;
            int state = this.state;
            int value = this.value;
            int op = 0;
            byte[] output = this.output;
            int[] alphabet = this.alphabet;

            while (p < len) {
               if (state == 0) {
                  while (
                     p + 4 <= len
                        && (
                              value = alphabet[input[p] & 255] << 18
                                 | alphabet[input[p + 1] & 255] << 12
                                 | alphabet[input[p + 2] & 255] << 6
                                 | alphabet[input[p + 3] & 255]
                           )
                           >= 0
                  ) {
                     output[op + 2] = (byte)value;
                     output[op + 1] = (byte)(value >> 8);
                     output[op] = (byte)(value >> 16);
                     op += 3;
                     p += 4;
                  }

                  if (p >= len) {
                     break;
                  }
               }

               int d = alphabet[input[p++] & 255];
               switch (state) {
                  case 0:
                     if (d >= 0) {
                        value = d;
                        state++;
                     } else if (d != -1) {
                        this.state = 6;
                        return false;
                     }
                     break;
                  case 1:
                     if (d >= 0) {
                        value = value << 6 | d;
                        state++;
                     } else if (d != -1) {
                        this.state = 6;
                        return false;
                     }
                     break;
                  case 2:
                     if (d >= 0) {
                        value = value << 6 | d;
                        state++;
                     } else if (d == -2) {
                        output[op++] = (byte)(value >> 4);
                        state = 4;
                     } else if (d != -1) {
                        this.state = 6;
                        return false;
                     }
                     break;
                  case 3:
                     if (d >= 0) {
                        value = value << 6 | d;
                        output[op + 2] = (byte)value;
                        output[op + 1] = (byte)(value >> 8);
                        output[op] = (byte)(value >> 16);
                        op += 3;
                        state = 0;
                     } else if (d == -2) {
                        output[op + 1] = (byte)(value >> 2);
                        output[op] = (byte)(value >> 10);
                        op += 2;
                        state = 5;
                     } else if (d != -1) {
                        this.state = 6;
                        return false;
                     }
                     break;
                  case 4:
                     if (d == -2) {
                        state++;
                     } else if (d != -1) {
                        this.state = 6;
                        return false;
                     }
                     break;
                  case 5:
                     if (d != -1) {
                        this.state = 6;
                        return false;
                     }
               }
            }

            if (!finish) {
               this.state = state;
               this.value = value;
               this.op = op;
               return true;
            } else {
               switch (state) {
                  case 0:
                  case 5:
                  default:
                     break;
                  case 1:
                     this.state = 6;
                     return false;
                  case 2:
                     output[op++] = (byte)(value >> 4);
                     break;
                  case 3:
                     output[op++] = (byte)(value >> 10);
                     output[op++] = (byte)(value >> 2);
                     break;
                  case 4:
                     this.state = 6;
                     return false;
               }

               this.state = state;
               this.op = op;
               return true;
            }
         }
      }
   }

   static class Encoder extends Base64.Coder {
      public static final int LINE_GROUPS = 19;
      private static final byte[] encode = new byte[]{
         65,
         66,
         67,
         68,
         69,
         70,
         71,
         72,
         73,
         74,
         75,
         76,
         77,
         78,
         79,
         80,
         81,
         82,
         83,
         84,
         85,
         86,
         87,
         88,
         89,
         90,
         97,
         98,
         99,
         100,
         101,
         102,
         103,
         104,
         105,
         106,
         107,
         108,
         109,
         110,
         111,
         112,
         113,
         114,
         115,
         116,
         117,
         118,
         119,
         120,
         121,
         122,
         48,
         49,
         50,
         51,
         52,
         53,
         54,
         55,
         56,
         57,
         43,
         47
      };
      private static final byte[] encodeWebsafe = new byte[]{
         65,
         66,
         67,
         68,
         69,
         70,
         71,
         72,
         73,
         74,
         75,
         76,
         77,
         78,
         79,
         80,
         81,
         82,
         83,
         84,
         85,
         86,
         87,
         88,
         89,
         90,
         97,
         98,
         99,
         100,
         101,
         102,
         103,
         104,
         105,
         106,
         107,
         108,
         109,
         110,
         111,
         112,
         113,
         114,
         115,
         116,
         117,
         118,
         119,
         120,
         121,
         122,
         48,
         49,
         50,
         51,
         52,
         53,
         54,
         55,
         56,
         57,
         45,
         95
      };
      private final byte[] tail;
      int tailLen;
      private int count;
      public final boolean doPadding;
      public final boolean doNewline;
      public final boolean doCr;
      private final byte[] alphabet;

      public Encoder(int flags, byte[] output) {
         this.output = output;
         this.doPadding = (flags & 1) == 0;
         this.doNewline = (flags & 2) == 0;
         this.doCr = (flags & 4) != 0;
         this.alphabet = (flags & 8) == 0 ? encode : encodeWebsafe;
         this.tail = new byte[2];
         this.tailLen = 0;
         this.count = this.doNewline ? 19 : -1;
      }

      @Override
      public int maxOutputSize(int len) {
         return len * 8 / 5 + 10;
      }

      @CanIgnoreReturnValue
      @Override
      public boolean process(byte[] input, int offset, int len, boolean finish) {
         byte[] alphabet = this.alphabet;
         byte[] output = this.output;
         int op = 0;
         int count = this.count;
         int p = offset;
         len += offset;
         int v = -1;
         switch (this.tailLen) {
            case 0:
            default:
               break;
            case 1:
               if (offset + 2 <= len) {
                  int var25 = (this.tail[0] & 255) << 16;
                  p = offset + 1;
                  v = var25 | (input[offset] & 255) << 8 | input[p++] & 255;
                  this.tailLen = 0;
               }
               break;
            case 2:
               if (offset + 1 <= len) {
                  int var10000 = (this.tail[0] & 255) << 16 | (this.tail[1] & 255) << 8;
                  p = offset + 1;
                  v = var10000 | input[offset] & 255;
                  this.tailLen = 0;
               }
         }

         if (v != -1) {
            output[op++] = alphabet[v >> 18 & 63];
            output[op++] = alphabet[v >> 12 & 63];
            output[op++] = alphabet[v >> 6 & 63];
            output[op++] = alphabet[v & 63];
            if (--count == 0) {
               if (this.doCr) {
                  output[op++] = 13;
               }

               output[op++] = 10;
               count = 19;
            }
         }

         while (p + 3 <= len) {
            v = (input[p] & 255) << 16 | (input[p + 1] & 255) << 8 | input[p + 2] & 255;
            output[op] = alphabet[v >> 18 & 63];
            output[op + 1] = alphabet[v >> 12 & 63];
            output[op + 2] = alphabet[v >> 6 & 63];
            output[op + 3] = alphabet[v & 63];
            p += 3;
            op += 4;
            if (--count == 0) {
               if (this.doCr) {
                  output[op++] = 13;
               }

               output[op++] = 10;
               count = 19;
            }
         }

         if (finish) {
            if (p - this.tailLen == len - 1) {
               int t = 0;
               v = ((this.tailLen > 0 ? this.tail[t++] : input[p++]) & 255) << 4;
               this.tailLen -= t;
               output[op++] = alphabet[v >> 6 & 63];
               output[op++] = alphabet[v & 63];
               if (this.doPadding) {
                  output[op++] = 61;
                  output[op++] = 61;
               }

               if (this.doNewline) {
                  if (this.doCr) {
                     output[op++] = 13;
                  }

                  output[op++] = 10;
               }
            } else if (p - this.tailLen == len - 2) {
               int tx = 0;
               v = ((this.tailLen > 1 ? this.tail[tx++] : input[p++]) & 255) << 10 | ((this.tailLen > 0 ? this.tail[tx++] : input[p++]) & 255) << 2;
               this.tailLen -= tx;
               output[op++] = alphabet[v >> 12 & 63];
               output[op++] = alphabet[v >> 6 & 63];
               output[op++] = alphabet[v & 63];
               if (this.doPadding) {
                  output[op++] = 61;
               }

               if (this.doNewline) {
                  if (this.doCr) {
                     output[op++] = 13;
                  }

                  output[op++] = 10;
               }
            } else if (this.doNewline && op > 0 && count != 19) {
               if (this.doCr) {
                  output[op++] = 13;
               }

               output[op++] = 10;
            }

            assert this.tailLen == 0;

            assert p == len;
         } else if (p == len - 1) {
            this.tail[this.tailLen++] = input[p];
         } else if (p == len - 2) {
            this.tail[this.tailLen++] = input[p];
            this.tail[this.tailLen++] = input[p + 1];
         }

         this.op = op;
         this.count = count;
         return true;
      }
   }
}
