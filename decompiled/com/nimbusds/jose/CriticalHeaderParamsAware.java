package com.nimbusds.jose;

import java.util.Set;

public interface CriticalHeaderParamsAware {
   Set<String> getProcessedCriticalHeaderParams();

   Set<String> getDeferredCriticalHeaderParams();
}
