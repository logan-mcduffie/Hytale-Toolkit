package com.nimbusds.jose.proc;

public interface ConfigurableJOSEProcessor<C extends SecurityContext> extends JOSEProcessor<C>, JOSEProcessorConfiguration<C> {
}
