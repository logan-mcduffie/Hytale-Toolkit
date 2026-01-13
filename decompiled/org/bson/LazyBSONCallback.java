package org.bson;

import java.util.List;
import org.bson.types.ObjectId;

public class LazyBSONCallback extends EmptyBSONCallback {
   private Object root;

   @Override
   public void reset() {
      this.root = null;
   }

   @Override
   public Object get() {
      return this.getRoot();
   }

   @Override
   public void gotBinary(String name, byte type, byte[] data) {
      this.setRoot(this.createObject(data, 0));
   }

   public Object createObject(byte[] bytes, int offset) {
      return new LazyBSONObject(bytes, offset, this);
   }

   public List createArray(byte[] bytes, int offset) {
      return new LazyBSONList(bytes, offset, this);
   }

   public Object createDBRef(String ns, ObjectId id) {
      return new BasicBSONObject("$ns", ns).append("$id", id);
   }

   private Object getRoot() {
      return this.root;
   }

   private void setRoot(Object root) {
      this.root = root;
   }
}
