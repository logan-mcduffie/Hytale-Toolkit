package com.hypixel.hytale.server.core.util;

import com.hypixel.hytale.protocol.BoolParamValue;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.DoubleParamValue;
import com.hypixel.hytale.protocol.FormattedMessage;
import com.hypixel.hytale.protocol.IntParamValue;
import com.hypixel.hytale.protocol.LongParamValue;
import com.hypixel.hytale.protocol.ParamValue;
import com.hypixel.hytale.protocol.StringParamValue;
import com.hypixel.hytale.protocol.packets.asseteditor.FailureReply;
import com.hypixel.hytale.protocol.packets.asseteditor.SuccessReply;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.Colors;

public class MessageUtil {
   public static AttributedString toAnsiString(@Nonnull Message message) {
      AttributedStyle style = AttributedStyle.DEFAULT;
      String color = message.getColor();
      if (color != null) {
         style = hexToStyle(color);
      }

      AttributedStringBuilder sb = new AttributedStringBuilder();
      sb.style(style).append(message.getAnsiMessage());

      for (Message child : message.getChildren()) {
         sb.append(toAnsiString(child));
      }

      return sb.toAttributedString();
   }

   public static AttributedStyle hexToStyle(@Nonnull String str) {
      Color color = ColorParseUtil.parseColor(str);
      if (color == null) {
         return AttributedStyle.DEFAULT;
      } else {
         int colorId = Colors.roundRgbColor(color.red & 255, color.green & 255, color.blue & 255, 256);
         return AttributedStyle.DEFAULT.foreground(colorId);
      }
   }

   @Deprecated
   public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token) {
      sendSuccessReply(playerRef, token, null);
   }

   @Deprecated
   public static void sendSuccessReply(@Nonnull PlayerRef playerRef, int token, @Nullable Message message) {
      FormattedMessage msg = message != null ? message.getFormattedMessage() : null;
      playerRef.getPacketHandler().writeNoCache(new SuccessReply(token, msg));
   }

   @Deprecated
   public static void sendFailureReply(@Nonnull PlayerRef playerRef, int token, @Nonnull Message message) {
      FormattedMessage msg = message != null ? message.getFormattedMessage() : null;
      playerRef.getPacketHandler().writeNoCache(new FailureReply(token, msg));
   }

   @Nonnull
   public static String formatText(String text, @Nullable Map<String, ParamValue> params, @Nullable Map<String, FormattedMessage> messageParams) {
      if (text == null) {
         throw new IllegalArgumentException("text cannot be null");
      } else if (params == null && messageParams == null) {
         return text;
      } else {
         int len = text.length();
         StringBuilder sb = new StringBuilder(text.length());
         int lastWritePos = 0;

         for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);
            if (ch == '{') {
               if (i + 1 < len && text.charAt(i + 1) == '{') {
                  if (i > lastWritePos) {
                     sb.append(text, lastWritePos, i);
                  }

                  sb.append('{');
                  lastWritePos = ++i + 1;
               } else {
                  int end = findMatchingBrace(text, i);
                  if (end >= 0) {
                     if (i > lastWritePos) {
                        sb.append(text, lastWritePos, i);
                     }

                     int contentStart = i + 1;
                     int c1 = text.indexOf(44, contentStart, end);
                     int c2 = c1 >= 0 ? text.indexOf(44, c1 + 1, end) : -1;
                     int nameEndExclusive = c1 >= 0 && c1 < end ? c1 : end;
                     int ns = trimStart(text, contentStart, nameEndExclusive - 1);
                     int nl = trimEnd(text, ns, nameEndExclusive - 1);
                     String key = nl > 0 ? text.substring(ns, ns + nl) : "";
                     String format = null;
                     if (c1 >= 0 && c1 < end) {
                        int formatStart = c1 + 1;
                        int formatEndExclusive = c2 >= 0 ? c2 : end;
                        int fs = trimStart(text, formatStart, formatEndExclusive - 1);
                        int fl = trimEnd(text, fs, formatEndExclusive - 1);
                        if (fl > 0) {
                           format = text.substring(fs, fs + fl);
                        }
                     }

                     String options = null;
                     if (c2 >= 0 && c2 < end) {
                        int optionsStart = c2 + 1;
                        int os = trimStart(text, optionsStart, end - 1);
                        int ol = trimEnd(text, os, end - 1);
                        if (ol > 0) {
                           options = text.substring(os, os + ol);
                        }
                     }

                     ParamValue replacement = params != null ? params.get(key) : null;
                     FormattedMessage replacementMessage = messageParams != null ? messageParams.get(key) : null;
                     if (replacementMessage != null) {
                        if (replacementMessage.rawText != null) {
                           sb.append(replacementMessage.rawText);
                        } else if (replacementMessage.messageId != null) {
                           String message = I18nModule.get().getMessage("en-US", replacementMessage.messageId);
                           if (message != null) {
                              sb.append(formatText(message, replacementMessage.params, replacementMessage.messageParams));
                           } else {
                              sb.append(replacementMessage.messageId);
                           }
                        }
                     } else if (replacement == null) {
                        sb.append(text, i, end);
                     } else {
                        String formattedReplacement;
                        formattedReplacement = "";
                        label185:
                        switch (format) {
                           case "upper":
                              if (replacement instanceof StringParamValue s) {
                                 formattedReplacement = s.value.toUpperCase();
                              }
                              break;
                           case "lower":
                              if (replacement instanceof StringParamValue s) {
                                 formattedReplacement = s.value.toLowerCase();
                              }
                              break;
                           case "number":
                              switch (options) {
                                 case "integer":
                                    formattedReplacement = switch (replacement) {
                                       case StringParamValue s -> s.value;
                                       case BoolParamValue b -> b.value ? "1" : "0";
                                       case DoubleParamValue d -> Integer.toString((int)d.value);
                                       case IntParamValue iv -> Integer.toString(iv.value);
                                       case LongParamValue l -> Long.toString(l.value);
                                       default -> "";
                                    };
                                    break label185;
                                 case "decimal":
                                 case null:
                                 default:
                                    formattedReplacement = switch (replacement) {
                                       case StringParamValue s -> s.value;
                                       case BoolParamValue b -> b.value ? "1" : "0";
                                       case DoubleParamValue d -> Double.toString((int)d.value);
                                       case IntParamValue iv -> Integer.toString(iv.value);
                                       case LongParamValue l -> Long.toString(l.value);
                                       default -> "";
                                    };
                                    break label185;
                              }
                           case "plural":
                              if (options != null) {
                                 String oneText = null;
                                 String otherText = null;
                                 int oneIdx = options.indexOf("one {");
                                 int otherIdx = options.indexOf("other {");
                                 if (oneIdx >= 0) {
                                    int oneStart = oneIdx + "one {".length();
                                    int oneEnd = findMatchingBrace(options, oneStart - 1);
                                    if (oneEnd > oneStart) {
                                       oneText = options.substring(oneStart, oneEnd);
                                    }
                                 }

                                 if (otherIdx >= 0) {
                                    int otherStart = otherIdx + "other {".length();
                                    int otherEnd = findMatchingBrace(options, otherStart - 1);
                                    if (otherEnd > otherStart) {
                                       otherText = options.substring(otherStart, otherEnd);
                                    }
                                 }

                                 int value = Integer.parseInt(replacement.toString());
                                 String selected;
                                 if (value == 1 && oneText != null) {
                                    selected = oneText;
                                 } else if (otherText != null) {
                                    selected = otherText;
                                 } else if (oneText != null) {
                                    selected = oneText;
                                 } else {
                                    selected = "";
                                 }

                                 formattedReplacement = formatText(selected, params, messageParams);
                              }
                           case null:
                        }

                        if (format == null) {
                           formattedReplacement = switch (replacement) {
                              case StringParamValue s -> s.value;
                              case BoolParamValue b -> Boolean.toString(b.value);
                              case DoubleParamValue d -> Double.toString(d.value);
                              case IntParamValue iv -> Integer.toString(iv.value);
                              case LongParamValue l -> Long.toString(l.value);
                              default -> "";
                           };
                        }

                        sb.append(formattedReplacement);
                     }

                     i = end;
                     lastWritePos = end + 1;
                  }
               }
            } else if (ch == '}' && i + 1 < len && text.charAt(i + 1) == '}') {
               if (i > lastWritePos) {
                  sb.append(text, lastWritePos, i);
               }

               sb.append('}');
               lastWritePos = ++i + 1;
            }
         }

         if (lastWritePos < len) {
            sb.append(text, lastWritePos, len);
         }

         return sb.toString();
      }
   }

   private static int findMatchingBrace(@Nonnull String text, int start) {
      int depth = 0;
      int len = text.length();

      for (int i = start; i < len; i++) {
         if (text.charAt(i) == '{') {
            depth++;
         } else if (text.charAt(i) == '}') {
            if (--depth == 0) {
               return i;
            }
         }
      }

      return -1;
   }

   private static int trimStart(@Nonnull String text, int start, int end) {
      int i = start;

      while (i <= end && Character.isWhitespace(text.charAt(i))) {
         i++;
      }

      return i;
   }

   private static int trimEnd(@Nonnull String text, int start, int end) {
      int i = start;

      while (end >= i && Character.isWhitespace(text.charAt(i))) {
         end--;
      }

      return end >= i ? end - i + 1 : 0;
   }
}
