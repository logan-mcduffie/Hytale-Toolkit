package it.unimi.dsi.fastutil.io;

import java.io.IOException;

public interface MeasurableStream {
   long length() throws IOException;

   long position() throws IOException;
}
