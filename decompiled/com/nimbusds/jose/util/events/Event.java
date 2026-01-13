package com.nimbusds.jose.util.events;

import com.nimbusds.jose.proc.SecurityContext;

public interface Event<S, C extends SecurityContext> {
   S getSource();

   C getContext();
}
