package io.netty.handler.codec.http;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Deprecated
public final class CookieDecoder {
   private final InternalLogger logger = InternalLoggerFactory.getInstance(this.getClass());
   private static final CookieDecoder STRICT = new CookieDecoder(true);
   private static final CookieDecoder LAX = new CookieDecoder(false);
   private static final String COMMENT = "Comment";
   private static final String COMMENTURL = "CommentURL";
   private static final String DISCARD = "Discard";
   private static final String PORT = "Port";
   private static final String VERSION = "Version";
   private final boolean strict;

   public static Set<Cookie> decode(String header) {
      return decode(header, true);
   }

   public static Set<Cookie> decode(String header, boolean strict) {
      return (strict ? STRICT : LAX).doDecode(header);
   }

   private Set<Cookie> doDecode(String header) {
      List<String> names = new ArrayList<>(8);
      List<String> values = new ArrayList<>(8);
      extractKeyValuePairs(header, names, values);
      if (names.isEmpty()) {
         return Collections.emptySet();
      } else {
         int version = 0;
         int i;
         if (names.get(0).equalsIgnoreCase("Version")) {
            try {
               version = Integer.parseInt(values.get(0));
            } catch (NumberFormatException var28) {
            }

            i = 1;
         } else {
            i = 0;
         }

         if (names.size() <= i) {
            return Collections.emptySet();
         } else {
            Set<Cookie> cookies;
            label114:
            for (cookies = new TreeSet<>(); i < names.size(); i++) {
               String name = names.get(i);
               String value = values.get(i);
               if (value == null) {
                  value = "";
               }

               Cookie c = this.initCookie(name, value);
               if (c == null) {
                  break;
               }

               boolean discard = false;
               boolean secure = false;
               boolean httpOnly = false;
               String comment = null;
               String commentURL = null;
               String domain = null;
               String path = null;
               long maxAge = Long.MIN_VALUE;
               List<Integer> ports = new ArrayList<>(2);

               for (int j = i + 1; j < names.size(); i++) {
                  name = names.get(j);
                  value = values.get(j);
                  if ("Discard".equalsIgnoreCase(name)) {
                     discard = true;
                  } else if ("Secure".equalsIgnoreCase(name)) {
                     secure = true;
                  } else if ("HTTPOnly".equalsIgnoreCase(name)) {
                     httpOnly = true;
                  } else if ("Comment".equalsIgnoreCase(name)) {
                     comment = value;
                  } else if ("CommentURL".equalsIgnoreCase(name)) {
                     commentURL = value;
                  } else if ("Domain".equalsIgnoreCase(name)) {
                     domain = value;
                  } else if ("Path".equalsIgnoreCase(name)) {
                     path = value;
                  } else if ("Expires".equalsIgnoreCase(name)) {
                     Date date = DateFormatter.parseHttpDate(value);
                     if (date != null) {
                        long maxAgeMillis = date.getTime() - System.currentTimeMillis();
                        maxAge = maxAgeMillis / 1000L + (maxAgeMillis % 1000L != 0L ? 1 : 0);
                     }
                  } else if ("Max-Age".equalsIgnoreCase(name)) {
                     maxAge = Integer.parseInt(value);
                  } else if ("Version".equalsIgnoreCase(name)) {
                     version = Integer.parseInt(value);
                  } else {
                     if (!"Port".equalsIgnoreCase(name)) {
                        c.setVersion(version);
                        c.setMaxAge(maxAge);
                        c.setPath(path);
                        c.setDomain(domain);
                        c.setSecure(secure);
                        c.setHttpOnly(httpOnly);
                        if (version > 0) {
                           c.setComment(comment);
                        }

                        if (version > 1) {
                           c.setCommentUrl(commentURL);
                           c.setPorts(ports);
                           c.setDiscard(discard);
                        }

                        cookies.add(c);
                        continue label114;
                     }

                     String[] portList = value.split(",");

                     for (String s1 : portList) {
                        try {
                           ports.add(Integer.valueOf(s1));
                        } catch (NumberFormatException var27) {
                        }
                     }
                  }

                  j++;
               }
               break;
            }

            return cookies;
         }
      }
   }

   private static void extractKeyValuePairs(String header, List<String> names, List<String> values) {
      int headerLen = header.length();
      int i = 0;

      label77:
      while (i != headerLen) {
         switch (header.charAt(i)) {
            case '\t':
            case '\n':
            case '\u000b':
            case '\f':
            case '\r':
            case ' ':
            case ',':
            case ';':
               i++;
               continue;
         }

         while (i != headerLen) {
            if (header.charAt(i) != '$') {
               String name;
               String value;
               if (i == headerLen) {
                  name = null;
                  value = null;
               } else {
                  int newNameStart = i;

                  label68:
                  while (true) {
                     switch (header.charAt(i)) {
                        case ';':
                           name = header.substring(newNameStart, i);
                           value = null;
                           break label68;
                        case '=':
                           name = header.substring(newNameStart, i);
                           if (++i == headerLen) {
                              value = "";
                           } else {
                              char c = header.charAt(i);
                              if (c == '"' || c == '\'') {
                                 StringBuilder newValueBuf = new StringBuilder(header.length() - i);
                                 char q = c;
                                 boolean hadBackslash = false;
                                 i++;

                                 while (i != headerLen) {
                                    if (hadBackslash) {
                                       hadBackslash = false;
                                       c = header.charAt(i++);
                                       switch (c) {
                                          case '"':
                                          case '\'':
                                          case '\\':
                                             newValueBuf.setCharAt(newValueBuf.length() - 1, c);
                                             break;
                                          default:
                                             newValueBuf.append(c);
                                       }
                                    } else {
                                       c = header.charAt(i++);
                                       if (c == q) {
                                          value = newValueBuf.toString();
                                          break label68;
                                       }

                                       newValueBuf.append(c);
                                       if (c == '\\') {
                                          hadBackslash = true;
                                       }
                                    }
                                 }

                                 value = newValueBuf.toString();
                                 break label68;
                              }

                              int semiPos = header.indexOf(59, i);
                              if (semiPos > 0) {
                                 value = header.substring(i, semiPos);
                                 i = semiPos;
                              } else {
                                 value = header.substring(i);
                                 i = headerLen;
                              }
                           }
                           break label68;
                        default:
                           if (++i == headerLen) {
                              name = header.substring(newNameStart);
                              value = null;
                              break label68;
                           }
                     }
                  }
               }

               names.add(name);
               values.add(value);
               continue label77;
            }

            i++;
         }
         break;
      }
   }

   private CookieDecoder(boolean strict) {
      this.strict = strict;
   }

   private DefaultCookie initCookie(String name, String value) {
      if (name == null || name.length() == 0) {
         this.logger.debug("Skipping cookie with null name");
         return null;
      } else if (value == null) {
         this.logger.debug("Skipping cookie with null value");
         return null;
      } else {
         CharSequence unwrappedValue = CookieUtil.unwrapValue(value);
         if (unwrappedValue == null) {
            this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '{}'", unwrappedValue);
            return null;
         } else {
            int invalidOctetPos;
            if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name)) >= 0) {
               if (this.logger.isDebugEnabled()) {
                  this.logger.debug("Skipping cookie because name '{}' contains invalid char '{}'", name, name.charAt(invalidOctetPos));
               }

               return null;
            } else {
               boolean wrap = unwrappedValue.length() != value.length();
               if (this.strict && (invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue)) >= 0) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Skipping cookie because value '{}' contains invalid char '{}'", unwrappedValue, unwrappedValue.charAt(invalidOctetPos));
                  }

                  return null;
               } else {
                  DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
                  cookie.setWrap(wrap);
                  return cookie;
               }
            }
         }
      }
   }
}
