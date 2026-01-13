package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Immutable
public final class UnprotectedHeader {
   private final Map<String, Object> params;

   private UnprotectedHeader(Map<String, Object> params) {
      Objects.requireNonNull(params);
      this.params = params;
   }

   public String getKeyID() {
      return (String)this.params.get("kid");
   }

   public Object getParam(String name) {
      return this.params.get(name);
   }

   public Set<String> getIncludedParams() {
      return this.params.keySet();
   }

   public Map<String, Object> toJSONObject() {
      Map<String, Object> o = JSONObjectUtils.newJSONObject();
      o.putAll(this.params);
      return o;
   }

   public static UnprotectedHeader parse(Map<String, Object> jsonObject) throws ParseException {
      if (jsonObject == null) {
         return null;
      } else {
         UnprotectedHeader.Builder header = new UnprotectedHeader.Builder();

         for (String name : jsonObject.keySet()) {
            header = header.param(name, jsonObject.get(name));
         }

         return header.build();
      }
   }

   public static class Builder {
      private final Map<String, Object> params = JSONObjectUtils.newJSONObject();

      public UnprotectedHeader.Builder keyID(String kid) {
         this.params.put("kid", kid);
         return this;
      }

      public UnprotectedHeader.Builder param(String name, Object value) {
         this.params.put(name, value);
         return this;
      }

      public UnprotectedHeader build() {
         return new UnprotectedHeader(this.params);
      }
   }
}
