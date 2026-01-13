package com.nimbusds.jose.util;

import com.nimbusds.jose.shaded.jcip.Immutable;
import java.util.Objects;

@Immutable
public class Resource {
   private final String content;
   private final String contentType;

   public Resource(String content, String contentType) {
      this.content = Objects.requireNonNull(content);
      this.contentType = contentType;
   }

   public String getContent() {
      return this.content;
   }

   public String getContentType() {
      return this.contentType;
   }
}
