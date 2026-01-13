package org.bson;

import org.bson.io.OutputBuffer;

public interface BSONEncoder {
   byte[] encode(BSONObject var1);

   int putObject(BSONObject var1);

   void done();

   void set(OutputBuffer var1);
}
