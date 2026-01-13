package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import java.util.Collections;
import java.util.Set;

public class CriticalHeaderParamsDeferral {
   private Set<String> deferredParams = Collections.emptySet();

   public Set<String> getProcessedCriticalHeaderParams() {
      return Collections.singleton("b64");
   }

   public Set<String> getDeferredCriticalHeaderParams() {
      return Collections.unmodifiableSet(this.deferredParams);
   }

   public void setDeferredCriticalHeaderParams(Set<String> defCritHeaders) {
      if (defCritHeaders == null) {
         this.deferredParams = Collections.emptySet();
      } else {
         this.deferredParams = defCritHeaders;
      }
   }

   public boolean headerPasses(Header header) {
      if (header.getCriticalParams() == null) {
         return true;
      } else {
         for (String critParam : header.getCriticalParams()) {
            if (!this.getProcessedCriticalHeaderParams().contains(critParam) && !this.getDeferredCriticalHeaderParams().contains(critParam)) {
               return false;
            }
         }

         return true;
      }
   }

   public void ensureHeaderPasses(JWEHeader header) throws JOSEException {
      if (!this.headerPasses(header)) {
         throw new JOSEException("Unsupported critical header parameter(s)");
      }
   }
}
