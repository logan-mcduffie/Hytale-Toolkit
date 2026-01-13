package org.jline.terminal.impl;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public class DumbTerminalProvider implements TerminalProvider {
   @Override
   public String name() {
      return "dumb";
   }

   @Override
   public Terminal sysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      return new DumbTerminal(
         this,
         systemStream,
         name,
         type,
         new FileInputStream(FileDescriptor.in),
         new FileOutputStream(systemStream == SystemStream.Error ? FileDescriptor.err : FileDescriptor.out),
         encoding,
         inputEncoding,
         outputEncoding,
         signalHandler
      );
   }

   @Override
   public Terminal sysTerminal(
      String name,
      String type,
      boolean ansiPassThrough,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      boolean nativeSignals,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      SystemStream systemStream
   ) throws IOException {
      Charset outputEncoding = systemStream == SystemStream.Error ? stderrEncoding : stdoutEncoding;
      return this.sysTerminal(name, type, ansiPassThrough, encoding, stdinEncoding, outputEncoding, nativeSignals, signalHandler, paused, systemStream);
   }

   @Override
   public Terminal newTerminal(
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Charset inputEncoding,
      Charset outputEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Terminal newTerminal(
      String name,
      String type,
      InputStream masterInput,
      OutputStream masterOutput,
      Charset encoding,
      Charset stdinEncoding,
      Charset stdoutEncoding,
      Charset stderrEncoding,
      Terminal.SignalHandler signalHandler,
      boolean paused,
      Attributes attributes,
      Size size
   ) throws IOException {
      return this.newTerminal(name, type, masterInput, masterOutput, encoding, stdinEncoding, stdoutEncoding, signalHandler, paused, attributes, size);
   }

   @Override
   public boolean isSystemStream(SystemStream stream) {
      return false;
   }

   @Override
   public String systemStreamName(SystemStream stream) {
      return null;
   }

   @Override
   public int systemStreamWidth(SystemStream stream) {
      return 0;
   }

   @Override
   public String toString() {
      return "TerminalProvider[" + this.name() + "]";
   }
}
