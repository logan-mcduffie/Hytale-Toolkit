package com.google.crypto.tink;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.GeneralSecurityException;

public interface StreamingAead {
   WritableByteChannel newEncryptingChannel(WritableByteChannel ciphertextDestination, byte[] associatedData) throws GeneralSecurityException, IOException;

   SeekableByteChannel newSeekableDecryptingChannel(SeekableByteChannel ciphertextSource, byte[] associatedData) throws GeneralSecurityException, IOException;

   ReadableByteChannel newDecryptingChannel(ReadableByteChannel ciphertextSource, byte[] associatedData) throws GeneralSecurityException, IOException;

   OutputStream newEncryptingStream(OutputStream ciphertextDestination, byte[] associatedData) throws GeneralSecurityException, IOException;

   InputStream newDecryptingStream(InputStream ciphertextSource, byte[] associatedData) throws GeneralSecurityException, IOException;
}
