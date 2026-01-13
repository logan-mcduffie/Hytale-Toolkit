package com.nimbusds.jose.util.events;

import com.nimbusds.jose.proc.SecurityContext;

public interface EventListener<S, C extends SecurityContext> {
   void notify(Event<S, C> var1);
}
