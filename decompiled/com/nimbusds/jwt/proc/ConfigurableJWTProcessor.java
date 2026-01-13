package com.nimbusds.jwt.proc;

import com.nimbusds.jose.proc.SecurityContext;

public interface ConfigurableJWTProcessor<C extends SecurityContext> extends JWTProcessor<C>, JWTProcessorConfiguration<C> {
}
