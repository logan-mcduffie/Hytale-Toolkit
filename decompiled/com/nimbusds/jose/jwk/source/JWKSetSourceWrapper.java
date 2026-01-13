package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.util.Objects;

public abstract class JWKSetSourceWrapper<C extends SecurityContext> implements JWKSetSource<C> {
   private final JWKSetSource<C> source;

   public JWKSetSourceWrapper(JWKSetSource<C> source) {
      Objects.requireNonNull(source);
      this.source = source;
   }

   public JWKSetSource<C> getSource() {
      return this.source;
   }

   @Override
   public void close() throws IOException {
      this.source.close();
   }
}
