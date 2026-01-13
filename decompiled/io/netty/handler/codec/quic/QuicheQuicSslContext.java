package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextOption;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.Mapping;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.LongFunction;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicSslContext extends QuicSslContext {
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(QuicheQuicSslContext.class);
   private static final String[] DEFAULT_NAMED_GROUPS = new String[]{"x25519", "secp256r1", "secp384r1", "secp521r1"};
   private static final String[] NAMED_GROUPS;
   final ClientAuth clientAuth;
   private final boolean server;
   private final ApplicationProtocolNegotiator apn;
   private long sessionCacheSize;
   private long sessionTimeout;
   private final QuicheQuicSslContext.QuicheQuicSslSessionContext sessionCtx;
   private final QuicheQuicSslEngineMap engineMap = new QuicheQuicSslEngineMap();
   private final QuicClientSessionCache sessionCache;
   private final BoringSSLSessionTicketCallback sessionTicketCallback = new BoringSSLSessionTicketCallback();
   final QuicheQuicSslContext.NativeSslContext nativeSslContext;

   QuicheQuicSslContext(
      boolean server,
      long sessionTimeout,
      long sessionCacheSize,
      ClientAuth clientAuth,
      @Nullable TrustManagerFactory trustManagerFactory,
      @Nullable KeyManagerFactory keyManagerFactory,
      String password,
      @Nullable Mapping<? super String, ? extends QuicSslContext> mapping,
      @Nullable Boolean earlyData,
      @Nullable BoringSSLKeylog keylog,
      String[] applicationProtocols,
      Entry<SslContextOption<?>, Object>... ctxOptions
   ) {
      Quic.ensureAvailability();
      this.server = server;
      this.clientAuth = server ? ObjectUtil.checkNotNull(clientAuth, "clientAuth") : ClientAuth.NONE;
      X509TrustManager trustManager;
      if (trustManagerFactory == null) {
         try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore)null);
            trustManager = chooseTrustManager(trustManagerFactory);
         } catch (Exception var35) {
            throw new IllegalStateException(var35);
         }
      } else {
         trustManager = chooseTrustManager(trustManagerFactory);
      }

      X509ExtendedKeyManager keyManager;
      if (keyManagerFactory == null) {
         if (server) {
            throw new IllegalArgumentException("No KeyManagerFactory");
         }

         keyManager = null;
      } else {
         keyManager = this.chooseKeyManager(keyManagerFactory);
      }

      String[] groups = NAMED_GROUPS;
      String[] sigalgs = EmptyArrays.EMPTY_STRINGS;
      Map<String, String> serverKeyTypes = null;
      Set<String> clientKeyTypes = null;
      if (ctxOptions != null) {
         for (Entry<SslContextOption<?>, Object> ctxOpt : ctxOptions) {
            SslContextOption<?> option = ctxOpt.getKey();
            if (option == BoringSSLContextOption.GROUPS) {
               String[] groupsArray = (String[])ctxOpt.getValue();
               Set<String> groupsSet = new LinkedHashSet<>(groupsArray.length);

               for (String group : groupsArray) {
                  groupsSet.add(GroupsConverter.toBoringSSL(group));
               }

               groups = groupsSet.toArray(EmptyArrays.EMPTY_STRINGS);
            } else if (option != BoringSSLContextOption.SIGNATURE_ALGORITHMS) {
               if (option == BoringSSLContextOption.CLIENT_KEY_TYPES) {
                  clientKeyTypes = (Set<String>)ctxOpt.getValue();
               } else if (option == BoringSSLContextOption.SERVER_KEY_TYPES) {
                  serverKeyTypes = (Map<String, String>)ctxOpt.getValue();
               } else {
                  LOGGER.debug("Skipping unsupported " + SslContextOption.class.getSimpleName() + ": " + ctxOpt.getKey());
               }
            } else {
               String[] sigalgsArray = (String[])ctxOpt.getValue();
               Set<String> sigalgsSet = new LinkedHashSet<>(sigalgsArray.length);

               for (String sigalg : sigalgsArray) {
                  sigalgsSet.add(sigalg);
               }

               sigalgs = sigalgsSet.toArray(EmptyArrays.EMPTY_STRINGS);
            }
         }
      }

      BoringSSLPrivateKeyMethod privateKeyMethod;
      if (keyManagerFactory instanceof BoringSSLKeylessManagerFactory) {
         privateKeyMethod = new QuicheQuicSslContext.BoringSSLAsyncPrivateKeyMethodAdapter(
            this.engineMap, ((BoringSSLKeylessManagerFactory)keyManagerFactory).privateKeyMethod
         );
      } else {
         privateKeyMethod = null;
      }

      this.sessionCache = server ? null : new QuicClientSessionCache();
      int verifyMode = server ? boringSSLVerifyModeForServer(this.clientAuth) : BoringSSL.SSL_VERIFY_PEER;
      this.nativeSslContext = new QuicheQuicSslContext.NativeSslContext(
         BoringSSL.SSLContext_new(
            server,
            applicationProtocols,
            new BoringSSLHandshakeCompleteCallback(this.engineMap),
            new BoringSSLCertificateCallback(this.engineMap, keyManager, password, serverKeyTypes, clientKeyTypes),
            new BoringSSLCertificateVerifyCallback(this.engineMap, trustManager),
            mapping == null ? null : new BoringSSLTlsextServernameCallback(this.engineMap, mapping),
            keylog == null ? null : new BoringSSLKeylogCallback(this.engineMap, keylog),
            server ? null : new BoringSSLSessionCallback(this.engineMap, this.sessionCache),
            privateKeyMethod,
            this.sessionTicketCallback,
            verifyMode,
            BoringSSL.subjectNames(trustManager.getAcceptedIssuers())
         )
      );
      boolean success = false;

      try {
         if (groups.length > 0 && BoringSSL.SSLContext_set1_groups_list(this.nativeSslContext.ctx, groups) == 0) {
            String msg = "failed to set curves / groups list: " + Arrays.toString((Object[])groups);
            String lastError = BoringSSL.ERR_last_error();
            if (lastError != null) {
               msg = msg + ". " + lastError;
            }

            throw new IllegalStateException(msg);
         }

         if (sigalgs.length > 0 && BoringSSL.SSLContext_set1_sigalgs_list(this.nativeSslContext.ctx, sigalgs) == 0) {
            String msg = "failed to set signature algorithm list: " + Arrays.toString((Object[])sigalgs);
            String lastError = BoringSSL.ERR_last_error();
            if (lastError != null) {
               msg = msg + ". " + lastError;
            }

            throw new IllegalStateException(msg);
         }

         this.apn = new QuicheQuicSslContext.QuicheQuicApplicationProtocolNegotiator(applicationProtocols);
         if (this.sessionCache != null) {
            this.sessionCache.setSessionCacheSize((int)sessionCacheSize);
            this.sessionCache.setSessionTimeout((int)sessionTimeout);
         } else {
            BoringSSL.SSLContext_setSessionCacheSize(this.nativeSslContext.address(), sessionCacheSize);
            this.sessionCacheSize = sessionCacheSize;
            BoringSSL.SSLContext_setSessionCacheTimeout(this.nativeSslContext.address(), sessionTimeout);
            this.sessionTimeout = sessionTimeout;
         }

         if (earlyData != null) {
            BoringSSL.SSLContext_set_early_data_enabled(this.nativeSslContext.address(), earlyData);
         }

         this.sessionCtx = new QuicheQuicSslContext.QuicheQuicSslSessionContext(this);
         success = true;
      } finally {
         if (!success) {
            this.nativeSslContext.release();
         }
      }
   }

   private X509ExtendedKeyManager chooseKeyManager(KeyManagerFactory keyManagerFactory) {
      for (KeyManager manager : keyManagerFactory.getKeyManagers()) {
         if (manager instanceof X509ExtendedKeyManager) {
            return (X509ExtendedKeyManager)manager;
         }
      }

      throw new IllegalArgumentException("No X509ExtendedKeyManager included");
   }

   private static X509TrustManager chooseTrustManager(TrustManagerFactory trustManagerFactory) {
      for (TrustManager manager : trustManagerFactory.getTrustManagers()) {
         if (manager instanceof X509TrustManager) {
            return (X509TrustManager)manager;
         }
      }

      throw new IllegalArgumentException("No X509TrustManager included");
   }

   static X509Certificate @Nullable [] toX509Certificates0(@Nullable File file) throws CertificateException {
      return toX509Certificates(file);
   }

   static PrivateKey toPrivateKey0(@Nullable File keyFile, @Nullable String keyPassword) throws Exception {
      return toPrivateKey(keyFile, keyPassword);
   }

   static TrustManagerFactory buildTrustManagerFactory0(X509Certificate @Nullable [] certCollection) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
      return buildTrustManagerFactory(certCollection, null, null);
   }

   private static int boringSSLVerifyModeForServer(ClientAuth mode) {
      switch (mode) {
         case NONE:
            return BoringSSL.SSL_VERIFY_NONE;
         case REQUIRE:
            return BoringSSL.SSL_VERIFY_PEER | BoringSSL.SSL_VERIFY_FAIL_IF_NO_PEER_CERT;
         case OPTIONAL:
            return BoringSSL.SSL_VERIFY_PEER;
         default:
            throw new Error("Unexpected mode: " + mode);
      }
   }

   @Nullable
   QuicheQuicConnection createConnection(LongFunction<Long> connectionCreator, QuicheQuicSslEngine engine) {
      this.nativeSslContext.retain();
      long ssl = BoringSSL.SSL_new(this.nativeSslContext.address(), this.isServer(), engine.tlsHostName);
      this.engineMap.put(ssl, engine);
      long connection = connectionCreator.apply(ssl);
      if (connection == -1L) {
         this.engineMap.remove(ssl);
         this.nativeSslContext.release();
         return null;
      } else {
         return new QuicheQuicConnection(connection, ssl, engine, this.nativeSslContext);
      }
   }

   long add(QuicheQuicSslEngine engine) {
      this.nativeSslContext.retain();
      engine.connection.reattach(this.nativeSslContext);
      this.engineMap.put(engine.connection.ssl, engine);
      return this.nativeSslContext.address();
   }

   void remove(QuicheQuicSslEngine engine) {
      QuicheQuicSslEngine removed = this.engineMap.remove(engine.connection.ssl);

      assert removed == null || removed == engine;

      engine.removeSessionFromCacheIfInvalid();
   }

   @Nullable
   QuicClientSessionCache getSessionCache() {
      return this.sessionCache;
   }

   @Override
   public boolean isClient() {
      return !this.server;
   }

   @Override
   public List<String> cipherSuites() {
      return Arrays.asList("TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384");
   }

   @Override
   public long sessionCacheSize() {
      if (this.sessionCache != null) {
         return this.sessionCache.getSessionCacheSize();
      } else {
         synchronized (this) {
            return this.sessionCacheSize;
         }
      }
   }

   @Override
   public long sessionTimeout() {
      if (this.sessionCache != null) {
         return this.sessionCache.getSessionTimeout();
      } else {
         synchronized (this) {
            return this.sessionTimeout;
         }
      }
   }

   @Override
   public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
      return this.apn;
   }

   @Override
   public QuicSslEngine newEngine(ByteBufAllocator alloc) {
      return new QuicheQuicSslEngine(this, null, -1);
   }

   @Override
   public QuicSslEngine newEngine(ByteBufAllocator alloc, String peerHost, int peerPort) {
      return new QuicheQuicSslEngine(this, peerHost, peerPort);
   }

   @Override
   public QuicSslSessionContext sessionContext() {
      return this.sessionCtx;
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SslHandler newHandler(ByteBufAllocator alloc, Executor delegatedTaskExecutor) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator alloc, boolean startTls, Executor executor) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls) {
      throw new UnsupportedOperationException();
   }

   @Override
   public SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, Executor delegatedTaskExecutor) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected SslHandler newHandler(ByteBufAllocator alloc, String peerHost, int peerPort, boolean startTls, Executor delegatedTaskExecutor) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected void finalize() throws Throwable {
      try {
         this.nativeSslContext.release();
      } finally {
         super.finalize();
      }
   }

   void setSessionTimeout(int seconds) throws IllegalArgumentException {
      if (this.sessionCache != null) {
         this.sessionCache.setSessionTimeout(seconds);
      } else {
         BoringSSL.SSLContext_setSessionCacheTimeout(this.nativeSslContext.address(), seconds);
         this.sessionTimeout = seconds;
      }
   }

   void setSessionCacheSize(int size) throws IllegalArgumentException {
      if (this.sessionCache != null) {
         this.sessionCache.setSessionCacheSize(size);
      } else {
         BoringSSL.SSLContext_setSessionCacheSize(this.nativeSslContext.address(), size);
         this.sessionCacheSize = size;
      }
   }

   void setSessionTicketKeys(SslSessionTicketKey @Nullable [] ticketKeys) {
      this.sessionTicketCallback.setSessionTicketKeys(ticketKeys);
      BoringSSL.SSLContext_setSessionTicketKeys(this.nativeSslContext.address(), ticketKeys != null && ticketKeys.length != 0);
   }

   static {
      String[] namedGroups = DEFAULT_NAMED_GROUPS;
      Set<String> defaultConvertedNamedGroups = new LinkedHashSet<>(namedGroups.length);

      for (int i = 0; i < namedGroups.length; i++) {
         defaultConvertedNamedGroups.add(GroupsConverter.toBoringSSL(namedGroups[i]));
      }

      if (Quic.isAvailable()) {
         long sslCtx = BoringSSL.SSLContext_new();

         try {
            Iterator<String> defaultGroupsIter = defaultConvertedNamedGroups.iterator();

            while (defaultGroupsIter.hasNext()) {
               if (BoringSSL.SSLContext_set1_groups_list(sslCtx, defaultGroupsIter.next()) == 0) {
                  defaultGroupsIter.remove();
               }
            }

            String groups = SystemPropertyUtil.get("jdk.tls.namedGroups", null);
            if (groups != null) {
               String[] nGroups = groups.split(",");
               Set<String> supportedNamedGroups = new LinkedHashSet<>(nGroups.length);
               Set<String> supportedConvertedNamedGroups = new LinkedHashSet<>(nGroups.length);
               Set<String> unsupportedNamedGroups = new LinkedHashSet<>();

               for (String namedGroup : nGroups) {
                  String converted = GroupsConverter.toBoringSSL(namedGroup);
                  if (BoringSSL.SSLContext_set1_groups_list(sslCtx, converted) == 0) {
                     unsupportedNamedGroups.add(namedGroup);
                  } else {
                     supportedConvertedNamedGroups.add(converted);
                     supportedNamedGroups.add(namedGroup);
                  }
               }

               if (supportedNamedGroups.isEmpty()) {
                  namedGroups = defaultConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
                  LOGGER.info(
                     "All configured namedGroups are not supported: {}. Use default: {}.",
                     Arrays.toString(unsupportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS)),
                     Arrays.toString((Object[])DEFAULT_NAMED_GROUPS)
                  );
               } else {
                  String[] groupArray = supportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
                  if (unsupportedNamedGroups.isEmpty()) {
                     LOGGER.info("Using configured namedGroups -D 'jdk.tls.namedGroup': {} ", Arrays.toString((Object[])groupArray));
                  } else {
                     LOGGER.info(
                        "Using supported configured namedGroups: {}. Unsupported namedGroups: {}. ",
                        Arrays.toString((Object[])groupArray),
                        Arrays.toString(unsupportedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS))
                     );
                  }

                  namedGroups = supportedConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
               }
            } else {
               namedGroups = defaultConvertedNamedGroups.toArray(EmptyArrays.EMPTY_STRINGS);
            }
         } finally {
            BoringSSL.SSLContext_free(sslCtx);
         }
      }

      NAMED_GROUPS = namedGroups;
   }

   private static final class BoringSSLAsyncPrivateKeyMethodAdapter implements BoringSSLPrivateKeyMethod {
      private final QuicheQuicSslEngineMap engineMap;
      private final BoringSSLAsyncPrivateKeyMethod privateKeyMethod;

      BoringSSLAsyncPrivateKeyMethodAdapter(QuicheQuicSslEngineMap engineMap, BoringSSLAsyncPrivateKeyMethod privateKeyMethod) {
         this.engineMap = engineMap;
         this.privateKeyMethod = privateKeyMethod;
      }

      @Override
      public void sign(long ssl, int signatureAlgorithm, byte[] input, BiConsumer<byte[], Throwable> callback) {
         QuicheQuicSslEngine engine = this.engineMap.get(ssl);
         if (engine == null) {
            callback.accept(null, null);
         } else {
            this.privateKeyMethod.sign(engine, signatureAlgorithm, input).addListener(f -> {
               Throwable cause = f.cause();
               if (cause != null) {
                  callback.accept(null, cause);
               } else {
                  callback.accept(f.getNow(), null);
               }
            });
         }
      }

      @Override
      public void decrypt(long ssl, byte[] input, BiConsumer<byte[], Throwable> callback) {
         QuicheQuicSslEngine engine = this.engineMap.get(ssl);
         if (engine == null) {
            callback.accept(null, null);
         } else {
            this.privateKeyMethod.decrypt(engine, input).addListener(f -> {
               Throwable cause = f.cause();
               if (cause != null) {
                  callback.accept(null, cause);
               } else {
                  callback.accept(f.getNow(), null);
               }
            });
         }
      }
   }

   static final class NativeSslContext extends AbstractReferenceCounted {
      private final long ctx;

      NativeSslContext(long ctx) {
         this.ctx = ctx;
      }

      long address() {
         return this.ctx;
      }

      @Override
      protected void deallocate() {
         BoringSSL.SSLContext_free(this.ctx);
      }

      @Override
      public ReferenceCounted touch(Object hint) {
         return this;
      }

      @Override
      public String toString() {
         return "NativeSslContext{ctx=" + this.ctx + '}';
      }
   }

   private static final class QuicheQuicApplicationProtocolNegotiator implements ApplicationProtocolNegotiator {
      private final List<String> protocols;

      QuicheQuicApplicationProtocolNegotiator(String @Nullable ... protocols) {
         if (protocols == null) {
            this.protocols = Collections.emptyList();
         } else {
            this.protocols = Collections.unmodifiableList(Arrays.asList(protocols));
         }
      }

      @Override
      public List<String> protocols() {
         return this.protocols;
      }
   }

   private static final class QuicheQuicSslSessionContext implements QuicSslSessionContext {
      private final QuicheQuicSslContext context;

      QuicheQuicSslSessionContext(QuicheQuicSslContext context) {
         this.context = context;
      }

      @Nullable
      @Override
      public SSLSession getSession(byte[] sessionId) {
         return null;
      }

      @Override
      public Enumeration<byte[]> getIds() {
         return new Enumeration<byte[]>() {
            @Override
            public boolean hasMoreElements() {
               return false;
            }

            public byte[] nextElement() {
               throw new NoSuchElementException();
            }
         };
      }

      @Override
      public void setSessionTimeout(int seconds) throws IllegalArgumentException {
         this.context.setSessionTimeout(seconds);
      }

      @Override
      public int getSessionTimeout() {
         return (int)this.context.sessionTimeout();
      }

      @Override
      public void setSessionCacheSize(int size) throws IllegalArgumentException {
         this.context.setSessionCacheSize(size);
      }

      @Override
      public int getSessionCacheSize() {
         return (int)this.context.sessionCacheSize();
      }

      @Override
      public void setTicketKeys(SslSessionTicketKey @Nullable ... keys) {
         this.context.setSessionTicketKeys(keys);
      }
   }
}
