package io.netty.handler.codec.http3;

import io.netty.handler.codec.quic.QuicChannel;
import io.netty.handler.codec.quic.QuicStreamChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Objects;

final class QpackAttributes {
   private final QuicChannel channel;
   private final boolean dynamicTableDisabled;
   private final Promise<QuicStreamChannel> encoderStreamPromise;
   private final Promise<QuicStreamChannel> decoderStreamPromise;
   private QuicStreamChannel encoderStream;
   private QuicStreamChannel decoderStream;

   QpackAttributes(QuicChannel channel, boolean disableDynamicTable) {
      this.channel = channel;
      this.dynamicTableDisabled = disableDynamicTable;
      this.encoderStreamPromise = this.dynamicTableDisabled ? null : channel.eventLoop().newPromise();
      this.decoderStreamPromise = this.dynamicTableDisabled ? null : channel.eventLoop().newPromise();
   }

   boolean dynamicTableDisabled() {
      return this.dynamicTableDisabled;
   }

   boolean decoderStreamAvailable() {
      return !this.dynamicTableDisabled && this.decoderStream != null;
   }

   boolean encoderStreamAvailable() {
      return !this.dynamicTableDisabled && this.encoderStream != null;
   }

   void whenEncoderStreamAvailable(GenericFutureListener<Future<? super QuicStreamChannel>> listener) {
      assert !this.dynamicTableDisabled;

      assert this.encoderStreamPromise != null;

      this.encoderStreamPromise.addListener(listener);
   }

   void whenDecoderStreamAvailable(GenericFutureListener<Future<? super QuicStreamChannel>> listener) {
      assert !this.dynamicTableDisabled;

      assert this.decoderStreamPromise != null;

      this.decoderStreamPromise.addListener(listener);
   }

   QuicStreamChannel decoderStream() {
      assert this.decoderStreamAvailable();

      return this.decoderStream;
   }

   QuicStreamChannel encoderStream() {
      assert this.encoderStreamAvailable();

      return this.encoderStream;
   }

   void decoderStream(QuicStreamChannel decoderStream) {
      assert this.channel.eventLoop().inEventLoop();

      assert !this.dynamicTableDisabled;

      assert this.decoderStreamPromise != null;

      assert this.decoderStream == null;

      this.decoderStream = Objects.requireNonNull(decoderStream);
      this.decoderStreamPromise.setSuccess(decoderStream);
   }

   void encoderStream(QuicStreamChannel encoderStream) {
      assert this.channel.eventLoop().inEventLoop();

      assert !this.dynamicTableDisabled;

      assert this.encoderStreamPromise != null;

      assert this.encoderStream == null;

      this.encoderStream = Objects.requireNonNull(encoderStream);
      this.encoderStreamPromise.setSuccess(encoderStream);
   }

   void encoderStreamInactive(Throwable cause) {
      assert this.channel.eventLoop().inEventLoop();

      assert !this.dynamicTableDisabled;

      assert this.encoderStreamPromise != null;

      this.encoderStreamPromise.tryFailure(cause);
   }

   void decoderStreamInactive(Throwable cause) {
      assert this.channel.eventLoop().inEventLoop();

      assert !this.dynamicTableDisabled;

      assert this.decoderStreamPromise != null;

      this.decoderStreamPromise.tryFailure(cause);
   }
}
