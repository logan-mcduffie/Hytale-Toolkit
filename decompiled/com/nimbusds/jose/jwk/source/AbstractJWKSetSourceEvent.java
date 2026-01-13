package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.events.Event;
import java.util.Objects;

class AbstractJWKSetSourceEvent<S extends JWKSetSource<C>, C extends SecurityContext> implements Event<S, C> {
   private final S source;
   private final C context;

   AbstractJWKSetSourceEvent(S source, C context) {
      Objects.requireNonNull(source);
      this.source = source;
      this.context = context;
   }

   public S getSource() {
      return this.source;
   }

   @Override
   public C getContext() {
      return this.context;
   }
}
