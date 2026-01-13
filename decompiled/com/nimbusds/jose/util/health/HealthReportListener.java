package com.nimbusds.jose.util.health;

import com.nimbusds.jose.proc.SecurityContext;

public interface HealthReportListener<S, C extends SecurityContext> {
   void notify(HealthReport<S, C> var1);
}
