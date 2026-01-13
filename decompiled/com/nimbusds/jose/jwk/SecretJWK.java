package com.nimbusds.jose.jwk;

import javax.crypto.SecretKey;

public interface SecretJWK {
   SecretKey toSecretKey();
}
