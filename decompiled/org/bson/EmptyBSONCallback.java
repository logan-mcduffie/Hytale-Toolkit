package org.bson;

import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class EmptyBSONCallback implements BSONCallback {
   @Override
   public void objectStart() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void objectStart(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public Object objectDone() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void reset() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public Object get() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public BSONCallback createBSONCallback() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void arrayStart() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void arrayStart(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public Object arrayDone() {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotNull(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotUndefined(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotMinKey(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotMaxKey(String name) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotBoolean(String name, boolean value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotDouble(String name, double value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotInt(String name, int value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotLong(String name, long value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotDecimal128(String name, Decimal128 value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotDate(String name, long millis) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotString(String name, String value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotSymbol(String name, String value) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotRegex(String name, String pattern, String flags) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotTimestamp(String name, int time, int increment) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotObjectId(String name, ObjectId id) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotDBRef(String name, String namespace, ObjectId id) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotBinary(String name, byte type, byte[] data) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotUUID(String name, long part1, long part2) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotCode(String name, String code) {
      throw new UnsupportedOperationException("Operation is not supported");
   }

   @Override
   public void gotCodeWScope(String name, String code, Object scope) {
      throw new UnsupportedOperationException("Operation is not supported");
   }
}
