package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.security.cert.X509Certificate;

abstract class ExtendedOpenSslSession extends ExtendedSSLSession implements OpenSslInternalSession {
   private static final String[] LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS = new String[]{
      "SHA512withRSA",
      "SHA512withECDSA",
      "SHA384withRSA",
      "SHA384withECDSA",
      "SHA256withRSA",
      "SHA256withECDSA",
      "SHA224withRSA",
      "SHA224withECDSA",
      "SHA1withRSA",
      "SHA1withECDSA",
      "RSASSA-PSS"
   };
   private final OpenSslInternalSession wrapped;

   ExtendedOpenSslSession(OpenSslInternalSession wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public abstract List<SNIServerName> getRequestedServerNames();

   @Override
   public List<byte[]> getStatusResponses() {
      return Collections.emptyList();
   }

   @Override
   public void prepareHandshake() {
      this.wrapped.prepareHandshake();
   }

   @Override
   public Map<String, Object> keyValueStorage() {
      return this.wrapped.keyValueStorage();
   }

   @Override
   public OpenSslSessionId sessionId() {
      return this.wrapped.sessionId();
   }

   @Override
   public void setSessionDetails(long creationTime, long lastAccessedTime, OpenSslSessionId id, Map<String, Object> keyValueStorage) {
      this.wrapped.setSessionDetails(creationTime, lastAccessedTime, id, keyValueStorage);
   }

   @Override
   public final void setLocalCertificate(Certificate[] localCertificate) {
      this.wrapped.setLocalCertificate(localCertificate);
   }

   @Override
   public String[] getPeerSupportedSignatureAlgorithms() {
      return EmptyArrays.EMPTY_STRINGS;
   }

   @Override
   public final void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
      this.wrapped.tryExpandApplicationBufferSize(packetLengthDataOnly);
   }

   @Override
   public final String[] getLocalSupportedSignatureAlgorithms() {
      return (String[])LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS.clone();
   }

   @Override
   public final byte[] getId() {
      return this.wrapped.getId();
   }

   @Override
   public final OpenSslSessionContext getSessionContext() {
      return this.wrapped.getSessionContext();
   }

   @Override
   public final long getCreationTime() {
      return this.wrapped.getCreationTime();
   }

   @Override
   public final long getLastAccessedTime() {
      return this.wrapped.getLastAccessedTime();
   }

   @Override
   public void setLastAccessedTime(long time) {
      this.wrapped.setLastAccessedTime(time);
   }

   @Override
   public final void invalidate() {
      this.wrapped.invalidate();
   }

   @Override
   public final boolean isValid() {
      return this.wrapped.isValid();
   }

   @Override
   public final void putValue(String name, Object value) {
      if (value instanceof SSLSessionBindingListener) {
         value = new ExtendedOpenSslSession.SSLSessionBindingListenerDecorator((SSLSessionBindingListener)value);
      }

      this.wrapped.putValue(name, value);
   }

   @Override
   public final Object getValue(String s) {
      Object value = this.wrapped.getValue(s);
      return value instanceof ExtendedOpenSslSession.SSLSessionBindingListenerDecorator
         ? ((ExtendedOpenSslSession.SSLSessionBindingListenerDecorator)value).delegate
         : value;
   }

   @Override
   public final void removeValue(String s) {
      this.wrapped.removeValue(s);
   }

   @Override
   public final String[] getValueNames() {
      return this.wrapped.getValueNames();
   }

   @Override
   public final Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      return this.wrapped.getPeerCertificates();
   }

   @Override
   public boolean hasPeerCertificates() {
      return this.wrapped.hasPeerCertificates();
   }

   @Override
   public final Certificate[] getLocalCertificates() {
      return this.wrapped.getLocalCertificates();
   }

   @Override
   public final X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      return this.wrapped.getPeerCertificateChain();
   }

   @Override
   public final Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      return this.wrapped.getPeerPrincipal();
   }

   @Override
   public final Principal getLocalPrincipal() {
      return this.wrapped.getLocalPrincipal();
   }

   @Override
   public final String getCipherSuite() {
      return this.wrapped.getCipherSuite();
   }

   @Override
   public String getProtocol() {
      return this.wrapped.getProtocol();
   }

   @Override
   public final String getPeerHost() {
      return this.wrapped.getPeerHost();
   }

   @Override
   public final int getPeerPort() {
      return this.wrapped.getPeerPort();
   }

   @Override
   public final int getPacketBufferSize() {
      return this.wrapped.getPacketBufferSize();
   }

   @Override
   public final int getApplicationBufferSize() {
      return this.wrapped.getApplicationBufferSize();
   }

   @Override
   public void handshakeFinished(
      byte[] id, String cipher, String protocol, byte[] peerCertificate, byte[][] peerCertificateChain, long creationTime, long timeout
   ) throws SSLException {
      this.wrapped.handshakeFinished(id, cipher, protocol, peerCertificate, peerCertificateChain, creationTime, timeout);
   }

   @Override
   public boolean equals(Object o) {
      return this.wrapped.equals(o);
   }

   @Override
   public int hashCode() {
      return this.wrapped.hashCode();
   }

   @Override
   public String toString() {
      return "ExtendedOpenSslSession{wrapped=" + this.wrapped + '}';
   }

   private final class SSLSessionBindingListenerDecorator implements SSLSessionBindingListener {
      final SSLSessionBindingListener delegate;

      SSLSessionBindingListenerDecorator(SSLSessionBindingListener delegate) {
         this.delegate = delegate;
      }

      @Override
      public void valueBound(SSLSessionBindingEvent event) {
         this.delegate.valueBound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
      }

      @Override
      public void valueUnbound(SSLSessionBindingEvent event) {
         this.delegate.valueUnbound(new SSLSessionBindingEvent(ExtendedOpenSslSession.this, event.getName()));
      }
   }
}
