package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import com.nimbusds.jose.util.events.EventListener;
import java.util.Objects;

@ThreadSafe
public class RetryingJWKSetSource<C extends SecurityContext> extends JWKSetSourceWrapper<C> {
   private final EventListener<RetryingJWKSetSource<C>, C> eventListener;

   public RetryingJWKSetSource(JWKSetSource<C> source, EventListener<RetryingJWKSetSource<C>, C> eventListener) {
      super(source);
      this.eventListener = eventListener;
   }

   @Override
   public JWKSet getJWKSet(JWKSetCacheRefreshEvaluator refreshEvaluator, long currentTime, C context) throws KeySourceException {
      try {
         return this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
      } catch (JWKSetUnavailableException var6) {
         if (this.eventListener != null) {
            this.eventListener.notify(new RetryingJWKSetSource.RetrialEvent<>(this, var6, context));
         }

         return this.getSource().getJWKSet(refreshEvaluator, currentTime, context);
      }
   }

   public static class RetrialEvent<C extends SecurityContext> extends AbstractJWKSetSourceEvent<RetryingJWKSetSource<C>, C> {
      private final Exception exception;

      private RetrialEvent(RetryingJWKSetSource<C> source, Exception exception, C securityContext) {
         super(source, securityContext);
         Objects.requireNonNull(exception);
         this.exception = exception;
      }

      public Exception getException() {
         return this.exception;
      }
   }
}
