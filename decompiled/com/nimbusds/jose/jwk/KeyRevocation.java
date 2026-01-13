package com.nimbusds.jose.jwk;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Immutable
public final class KeyRevocation implements Serializable {
   private final Date revokedAt;
   private final KeyRevocation.Reason reason;

   public KeyRevocation(Date revokedAt, KeyRevocation.Reason reason) {
      this.revokedAt = Objects.requireNonNull(revokedAt);
      this.reason = reason;
   }

   public Date getRevocationTime() {
      return this.revokedAt;
   }

   public KeyRevocation.Reason getReason() {
      return this.reason;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof KeyRevocation)) {
         return false;
      } else {
         KeyRevocation that = (KeyRevocation)o;
         return Objects.equals(this.revokedAt, that.revokedAt) && Objects.equals(this.getReason(), that.getReason());
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.revokedAt, this.getReason());
   }

   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();
      o.put("revoked_at", DateUtils.toSecondsSinceEpoch(this.getRevocationTime()));
      if (this.getReason() != null) {
         o.put("reason", this.getReason().getValue());
      }

      return o;
   }

   public static KeyRevocation parse(Map<String, Object> jsonObject) throws ParseException {
      Date revokedAt = DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(jsonObject, "revoked_at"));
      KeyRevocation.Reason reason = null;
      if (jsonObject.get("reason") != null) {
         reason = KeyRevocation.Reason.parse(JSONObjectUtils.getString(jsonObject, "reason"));
      }

      return new KeyRevocation(revokedAt, reason);
   }

   public static class Reason {
      public static final KeyRevocation.Reason UNSPECIFIED = new KeyRevocation.Reason("unspecified");
      public static final KeyRevocation.Reason COMPROMISED = new KeyRevocation.Reason("compromised");
      public static final KeyRevocation.Reason SUPERSEDED = new KeyRevocation.Reason("superseded");
      private final String value;

      public Reason(String value) {
         this.value = Objects.requireNonNull(value);
      }

      public String getValue() {
         return this.value;
      }

      @Override
      public String toString() {
         return this.getValue();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof KeyRevocation.Reason)) {
            return false;
         } else {
            KeyRevocation.Reason reason = (KeyRevocation.Reason)o;
            return Objects.equals(this.getValue(), reason.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.getValue());
      }

      public static KeyRevocation.Reason parse(String s) {
         if (UNSPECIFIED.getValue().equals(s)) {
            return UNSPECIFIED;
         } else if (COMPROMISED.getValue().equals(s)) {
            return COMPROMISED;
         } else {
            return SUPERSEDED.getValue().equals(s) ? SUPERSEDED : new KeyRevocation.Reason(s);
         }
      }
   }
}
