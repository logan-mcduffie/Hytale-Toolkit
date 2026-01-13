package com.nimbusds.jose.mint;

import com.nimbusds.jose.proc.SecurityContext;

public interface ConfigurableJWSMinter<C extends SecurityContext> extends JWSMinter<C>, JWSMinterConfiguration<C> {
}
