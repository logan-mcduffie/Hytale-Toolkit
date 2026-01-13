package com.nimbusds.jose.jca;

public interface JCAAware<T extends JCAContext> {
   T getJCAContext();
}
