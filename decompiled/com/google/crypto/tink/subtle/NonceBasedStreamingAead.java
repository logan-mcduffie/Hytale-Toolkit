package com.google.crypto.tink.subtle;

import com.google.crypto.tink.StreamingAead;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

abstract class NonceBasedStreamingAead implements StreamingAead {
   public abstract StreamSegmentEncrypter newStreamSegmentEncrypter(byte[] associatedData) throws GeneralSecurityException;

   public abstract StreamSegmentDecrypter newStreamSegmentDecrypter() throws GeneralSecurityException;

   public abstract int getPlaintextSegmentSize();

   public abstract int getCiphertextSegmentSize();

   public abstract int getCiphertextOffset();

   public abstract int getCiphertextOverhead();

   public abstract int getHeaderLength();

   @Override
   public WritableByteChannel newEncryptingChannel(WritableByteChannel ciphertextChannel, byte[] associatedData) throws GeneralSecurityException, IOException {
      return new StreamingAeadEncryptingChannel(this, ciphertextChannel, associatedData);
   }

   @Override
   public ReadableByteChannel newDecryptingChannel(ReadableByteChannel ciphertextChannel, byte[] associatedData) throws GeneralSecurityException, IOException {
      return new StreamingAeadDecryptingChannel(this, ciphertextChannel, associatedData);
   }

   @Override
   public SeekableByteChannel newSeekableDecryptingChannel(SeekableByteChannel ciphertextSource, byte[] associatedData) throws GeneralSecurityException, IOException {
      return new StreamingAeadSeekableDecryptingChannel(this, ciphertextSource, associatedData);
   }

   @Override
   public OutputStream newEncryptingStream(OutputStream ciphertext, byte[] associatedData) throws GeneralSecurityException, IOException {
      return new StreamingAeadEncryptingStream(this, ciphertext, associatedData);
   }

   @Override
   public InputStream newDecryptingStream(InputStream ciphertextStream, byte[] associatedData) throws GeneralSecurityException, IOException {
      return new StreamingAeadDecryptingStream(this, ciphertextStream, associatedData);
   }
}
