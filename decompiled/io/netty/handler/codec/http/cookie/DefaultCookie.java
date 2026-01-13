package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;

public class DefaultCookie implements Cookie {
   private final String name;
   private String value;
   private boolean wrap;
   private String domain;
   private String path;
   private long maxAge = Long.MIN_VALUE;
   private boolean secure;
   private boolean httpOnly;
   private CookieHeaderNames.SameSite sameSite;
   private boolean partitioned;

   public DefaultCookie(String name, String value) {
      this.name = ObjectUtil.checkNonEmptyAfterTrim(name, "name");
      this.setValue(value);
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public String value() {
      return this.value;
   }

   @Override
   public void setValue(String value) {
      this.value = ObjectUtil.checkNotNull(value, "value");
   }

   @Override
   public boolean wrap() {
      return this.wrap;
   }

   @Override
   public void setWrap(boolean wrap) {
      this.wrap = wrap;
   }

   @Override
   public String domain() {
      return this.domain;
   }

   @Override
   public void setDomain(String domain) {
      this.domain = CookieUtil.validateAttributeValue("domain", domain);
   }

   @Override
   public String path() {
      return this.path;
   }

   @Override
   public void setPath(String path) {
      this.path = CookieUtil.validateAttributeValue("path", path);
   }

   @Override
   public long maxAge() {
      return this.maxAge;
   }

   @Override
   public void setMaxAge(long maxAge) {
      this.maxAge = maxAge;
   }

   @Override
   public boolean isSecure() {
      return this.secure;
   }

   @Override
   public void setSecure(boolean secure) {
      this.secure = secure;
   }

   @Override
   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   @Override
   public void setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
   }

   public CookieHeaderNames.SameSite sameSite() {
      return this.sameSite;
   }

   public void setSameSite(CookieHeaderNames.SameSite sameSite) {
      this.sameSite = sameSite;
   }

   public boolean isPartitioned() {
      return this.partitioned;
   }

   public void setPartitioned(boolean partitioned) {
      this.partitioned = partitioned;
   }

   @Override
   public int hashCode() {
      return this.name().hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Cookie)) {
         return false;
      } else {
         Cookie that = (Cookie)o;
         if (!this.name().equals(that.name())) {
            return false;
         } else {
            if (this.path() == null) {
               if (that.path() != null) {
                  return false;
               }
            } else {
               if (that.path() == null) {
                  return false;
               }

               if (!this.path().equals(that.path())) {
                  return false;
               }
            }

            return this.domain() == null ? that.domain() == null : this.domain().equalsIgnoreCase(that.domain());
         }
      }
   }

   public int compareTo(Cookie c) {
      int v = this.name().compareTo(c.name());
      if (v != 0) {
         return v;
      } else {
         if (this.path() == null) {
            if (c.path() != null) {
               return -1;
            }
         } else {
            if (c.path() == null) {
               return 1;
            }

            v = this.path().compareTo(c.path());
            if (v != 0) {
               return v;
            }
         }

         if (this.domain() == null) {
            return c.domain() != null ? -1 : 0;
         } else {
            return c.domain() == null ? 1 : this.domain().compareToIgnoreCase(c.domain());
         }
      }
   }

   @Deprecated
   protected String validateValue(String name, String value) {
      return CookieUtil.validateAttributeValue(name, value);
   }

   @Override
   public String toString() {
      StringBuilder buf = CookieUtil.stringBuilder().append(this.name()).append('=').append(this.value());
      if (this.domain() != null) {
         buf.append(", domain=").append(this.domain());
      }

      if (this.path() != null) {
         buf.append(", path=").append(this.path());
      }

      if (this.maxAge() >= 0L) {
         buf.append(", maxAge=").append(this.maxAge()).append('s');
      }

      if (this.isSecure()) {
         buf.append(", secure");
      }

      if (this.isHttpOnly()) {
         buf.append(", HTTPOnly");
      }

      if (this.sameSite() != null) {
         buf.append(", SameSite=").append(this.sameSite());
      }

      if (this.isPartitioned()) {
         buf.append(", Partitioned");
      }

      return buf.toString();
   }
}
