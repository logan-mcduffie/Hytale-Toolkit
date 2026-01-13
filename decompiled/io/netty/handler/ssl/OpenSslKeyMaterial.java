package io.netty.handler.ssl;

import io.netty.util.ReferenceCounted;
import java.security.cert.X509Certificate;

interface OpenSslKeyMaterial extends ReferenceCounted {
   X509Certificate[] certificateChain();

   long certificateChainAddress();

   long privateKeyAddress();

   OpenSslKeyMaterial retain();

   OpenSslKeyMaterial retain(int var1);

   OpenSslKeyMaterial touch();

   OpenSslKeyMaterial touch(Object var1);

   @Override
   boolean release();

   @Override
   boolean release(int var1);
}
