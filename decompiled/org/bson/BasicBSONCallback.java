package org.bson;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bson.types.BSONTimestamp;
import org.bson.types.BasicBSONList;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;

public class BasicBSONCallback implements BSONCallback {
   private Object root;
   private final LinkedList<BSONObject> stack = new LinkedList<>();
   private final LinkedList<String> nameStack = new LinkedList<>();

   public BasicBSONCallback() {
      this.reset();
   }

   @Override
   public Object get() {
      return this.root;
   }

   public BSONObject create() {
      return new BasicBSONObject();
   }

   protected BSONObject createList() {
      return new BasicBSONList();
   }

   @Override
   public BSONCallback createBSONCallback() {
      return new BasicBSONCallback();
   }

   public BSONObject create(boolean array, List<String> path) {
      return array ? this.createList() : this.create();
   }

   @Override
   public void objectStart() {
      if (this.stack.size() > 0) {
         throw new IllegalStateException("Illegal object beginning in current context.");
      } else {
         this.root = this.create(false, null);
         this.stack.add((BSONObject)this.root);
      }
   }

   @Override
   public void objectStart(String name) {
      this.nameStack.addLast(name);
      BSONObject o = this.create(false, this.nameStack);
      this.stack.getLast().put(name, o);
      this.stack.addLast(o);
   }

   @Override
   public Object objectDone() {
      BSONObject o = this.stack.removeLast();
      if (this.nameStack.size() > 0) {
         this.nameStack.removeLast();
      } else if (this.stack.size() > 0) {
         throw new IllegalStateException("Illegal object end in current context.");
      }

      return o;
   }

   @Override
   public void arrayStart() {
      this.root = this.create(true, null);
      this.stack.add((BSONObject)this.root);
   }

   @Override
   public void arrayStart(String name) {
      this.nameStack.addLast(name);
      BSONObject o = this.create(true, this.nameStack);
      this.stack.getLast().put(name, o);
      this.stack.addLast(o);
   }

   @Override
   public Object arrayDone() {
      return this.objectDone();
   }

   @Override
   public void gotNull(String name) {
      this.cur().put(name, null);
   }

   @Override
   public void gotUndefined(String name) {
   }

   @Override
   public void gotMinKey(String name) {
      this.cur().put(name, new MinKey());
   }

   @Override
   public void gotMaxKey(String name) {
      this.cur().put(name, new MaxKey());
   }

   @Override
   public void gotBoolean(String name, boolean value) {
      this._put(name, value);
   }

   @Override
   public void gotDouble(String name, double value) {
      this._put(name, value);
   }

   @Override
   public void gotInt(String name, int value) {
      this._put(name, value);
   }

   @Override
   public void gotLong(String name, long value) {
      this._put(name, value);
   }

   @Override
   public void gotDecimal128(String name, Decimal128 value) {
      this._put(name, value);
   }

   @Override
   public void gotDate(String name, long millis) {
      this._put(name, new Date(millis));
   }

   @Override
   public void gotRegex(String name, String pattern, String flags) {
      this._put(name, Pattern.compile(pattern, BSON.regexFlags(flags)));
   }

   @Override
   public void gotString(String name, String value) {
      this._put(name, value);
   }

   @Override
   public void gotSymbol(String name, String value) {
      this._put(name, value);
   }

   @Override
   public void gotTimestamp(String name, int time, int increment) {
      this._put(name, new BSONTimestamp(time, increment));
   }

   @Override
   public void gotObjectId(String name, ObjectId id) {
      this._put(name, id);
   }

   @Override
   public void gotDBRef(String name, String namespace, ObjectId id) {
      this._put(name, new BasicBSONObject("$ns", namespace).append("$id", id));
   }

   @Override
   public void gotBinary(String name, byte type, byte[] data) {
      if (type != 0 && type != 2) {
         this._put(name, new Binary(type, data));
      } else {
         this._put(name, data);
      }
   }

   @Override
   public void gotUUID(String name, long part1, long part2) {
      this._put(name, new UUID(part1, part2));
   }

   @Override
   public void gotCode(String name, String code) {
      this._put(name, new Code(code));
   }

   @Override
   public void gotCodeWScope(String name, String code, Object scope) {
      this._put(name, new CodeWScope(code, (BSONObject)scope));
   }

   protected void _put(String name, Object value) {
      this.cur().put(name, value);
   }

   protected BSONObject cur() {
      return this.stack.getLast();
   }

   protected String curName() {
      return this.nameStack.peekLast();
   }

   protected void setRoot(Object root) {
      this.root = root;
   }

   protected boolean isStackEmpty() {
      return this.stack.size() < 1;
   }

   @Override
   public void reset() {
      this.root = null;
      this.stack.clear();
      this.nameStack.clear();
   }
}
