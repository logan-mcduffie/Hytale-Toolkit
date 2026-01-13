package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Int2FloatAVLTreeMap extends AbstractInt2FloatSortedMap implements Serializable, Cloneable {
   protected transient Int2FloatAVLTreeMap.Entry tree;
   protected int count;
   protected transient Int2FloatAVLTreeMap.Entry firstEntry;
   protected transient Int2FloatAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Int2FloatMap.Entry> entries;
   protected transient IntSortedSet keys;
   protected transient FloatCollection values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Int2FloatAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2FloatAVLTreeMap(Comparator<? super Integer> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Int2FloatAVLTreeMap(Map<? extends Integer, ? extends Float> m) {
      this();
      this.putAll(m);
   }

   public Int2FloatAVLTreeMap(SortedMap<Integer, Float> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2FloatAVLTreeMap(Int2FloatMap m) {
      this();
      this.putAll(m);
   }

   public Int2FloatAVLTreeMap(Int2FloatSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2FloatAVLTreeMap(int[] k, float[] v, Comparator<? super Integer> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2FloatAVLTreeMap(int[] k, float[] v) {
      this(k, v, null);
   }

   final int compare(int k1, int k2) {
      return this.actualComparator == null ? Integer.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Int2FloatAVLTreeMap.Entry findKey(int k) {
      Int2FloatAVLTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Int2FloatAVLTreeMap.Entry locateKey(int k) {
      Int2FloatAVLTreeMap.Entry e = this.tree;
      Int2FloatAVLTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public float addTo(int k, float incr) {
      Int2FloatAVLTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public float put(int k, float v) {
      Int2FloatAVLTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Int2FloatAVLTreeMap.Entry add(int k) {
      this.modified = false;
      Int2FloatAVLTreeMap.Entry e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Int2FloatAVLTreeMap.Entry(k, this.defRetValue);
         this.modified = true;
      } else {
         Int2FloatAVLTreeMap.Entry p = this.tree;
         Int2FloatAVLTreeMap.Entry q = null;
         Int2FloatAVLTreeMap.Entry y = this.tree;
         Int2FloatAVLTreeMap.Entry z = null;
         Int2FloatAVLTreeMap.Entry w = null;
         int i = 0;

         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               return p;
            }

            if (p.balance() != 0) {
               i = 0;
               z = q;
               y = p;
            }

            if (this.dirPath[i++] = cmp > 0) {
               if (p.succ()) {
                  this.count++;
                  e = new Int2FloatAVLTreeMap.Entry(k, this.defRetValue);
                  this.modified = true;
                  if (p.right == null) {
                     this.lastEntry = e;
                  }

                  e.left = p;
                  e.right = p.right;
                  p.right(e);
                  break;
               }

               q = p;
               p = p.right;
            } else {
               if (p.pred()) {
                  this.count++;
                  e = new Int2FloatAVLTreeMap.Entry(k, this.defRetValue);
                  this.modified = true;
                  if (p.left == null) {
                     this.firstEntry = e;
                  }

                  e.right = p;
                  e.left = p.left;
                  p.left(e);
                  break;
               }

               q = p;
               p = p.left;
            }
         }

         p = y;

         for (int var14 = 0; p != e; p = this.dirPath[var14++] ? p.right : p.left) {
            if (this.dirPath[var14]) {
               p.incBalance();
            } else {
               p.decBalance();
            }
         }

         if (y.balance() == -2) {
            Int2FloatAVLTreeMap.Entry x = y.left;
            if (x.balance() == -1) {
               w = x;
               if (x.succ()) {
                  x.succ(false);
                  y.pred(x);
               } else {
                  y.left = x.right;
               }

               x.right = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == 1;

               w = x.right;
               x.right = w.left;
               w.left = x;
               y.left = w.right;
               w.right = y;
               if (w.balance() == -1) {
                  x.balance(0);
                  y.balance(1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(-1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  x.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  y.pred(w);
                  w.succ(false);
               }
            }
         } else {
            if (y.balance() != 2) {
               return e;
            }

            Int2FloatAVLTreeMap.Entry x = y.right;
            if (x.balance() == 1) {
               w = x;
               if (x.pred()) {
                  x.pred(false);
                  y.succ(x);
               } else {
                  y.right = x.left;
               }

               x.left = y;
               x.balance(0);
               y.balance(0);
            } else {
               assert x.balance() == -1;

               w = x.left;
               x.left = w.right;
               w.right = x;
               y.right = w.left;
               w.left = y;
               if (w.balance() == 1) {
                  x.balance(0);
                  y.balance(-1);
               } else if (w.balance() == 0) {
                  x.balance(0);
                  y.balance(0);
               } else {
                  x.balance(1);
                  y.balance(0);
               }

               w.balance(0);
               if (w.pred()) {
                  y.succ(w);
                  w.pred(false);
               }

               if (w.succ()) {
                  x.pred(w);
                  w.succ(false);
               }
            }
         }

         if (z == null) {
            this.tree = w;
         } else if (z.left == y) {
            z.left = w;
         } else {
            z.right = w;
         }
      }

      return e;
   }

   private Int2FloatAVLTreeMap.Entry parent(Int2FloatAVLTreeMap.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         Int2FloatAVLTreeMap.Entry y = e;

         Int2FloatAVLTreeMap.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Int2FloatAVLTreeMap.Entry p = x.left;
               if (p == null || p.right != e) {
                  while (!y.succ()) {
                     y = y.right;
                  }

                  p = y.right;
               }

               return p;
            }

            x = x.left;
         }

         Int2FloatAVLTreeMap.Entry p = y.right;
         if (p == null || p.left != e) {
            while (!x.pred()) {
               x = x.left;
            }

            p = x.left;
         }

         return p;
      }
   }

   @Override
   public float remove(int k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2FloatAVLTreeMap.Entry p = this.tree;
         Int2FloatAVLTreeMap.Entry q = null;
         boolean dir = false;
         int kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            if (dir = cmp > 0) {
               q = p;
               if ((p = p.right()) == null) {
                  return this.defRetValue;
               }
            } else {
               q = p;
               if ((p = p.left()) == null) {
                  return this.defRetValue;
               }
            }
         }

         if (p.left == null) {
            this.firstEntry = p.next();
         }

         if (p.right == null) {
            this.lastEntry = p.prev();
         }

         if (p.succ()) {
            if (p.pred()) {
               if (q != null) {
                  if (dir) {
                     q.succ(p.right);
                  } else {
                     q.pred(p.left);
                  }
               } else {
                  this.tree = dir ? p.right : p.left;
               }
            } else {
               p.prev().right = p.right;
               if (q != null) {
                  if (dir) {
                     q.right = p.left;
                  } else {
                     q.left = p.left;
                  }
               } else {
                  this.tree = p.left;
               }
            }
         } else {
            Int2FloatAVLTreeMap.Entry r = p.right;
            if (r.pred()) {
               r.left = p.left;
               r.pred(p.pred());
               if (!r.pred()) {
                  r.prev().right = r;
               }

               if (q != null) {
                  if (dir) {
                     q.right = r;
                  } else {
                     q.left = r;
                  }
               } else {
                  this.tree = r;
               }

               r.balance(p.balance());
               q = r;
               dir = true;
            } else {
               while (true) {
                  Int2FloatAVLTreeMap.Entry s = r.left;
                  if (s.pred()) {
                     if (s.succ()) {
                        r.pred(s);
                     } else {
                        r.left = s.right;
                     }

                     s.left = p.left;
                     if (!p.pred()) {
                        p.prev().right = s;
                        s.pred(false);
                     }

                     s.right = p.right;
                     s.succ(false);
                     if (q != null) {
                        if (dir) {
                           q.right = s;
                        } else {
                           q.left = s;
                        }
                     } else {
                        this.tree = s;
                     }

                     s.balance(p.balance());
                     q = r;
                     dir = false;
                     break;
                  }

                  r = s;
               }
            }
         }

         while (q != null) {
            Int2FloatAVLTreeMap.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Int2FloatAVLTreeMap.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Int2FloatAVLTreeMap.Entry w = x.left;
                     x.left = w.right;
                     w.right = x;
                     y.right = w.left;
                     w.left = y;
                     if (w.balance() == 1) {
                        x.balance(0);
                        y.balance(-1);
                     } else if (w.balance() == 0) {
                        x.balance(0);
                        y.balance(0);
                     } else {
                        assert w.balance() == -1;

                        x.balance(1);
                        y.balance(0);
                     }

                     w.balance(0);
                     if (w.pred()) {
                        y.succ(w);
                        w.pred(false);
                     }

                     if (w.succ()) {
                        x.pred(w);
                        w.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = w;
                        } else {
                           q.left = w;
                        }
                     } else {
                        this.tree = w;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = x;
                        } else {
                           q.left = x;
                        }
                     } else {
                        this.tree = x;
                     }

                     if (x.balance() == 0) {
                        y.right = x.left;
                        x.left = y;
                        x.balance(-1);
                        y.balance(1);
                        break;
                     }

                     assert x.balance() == 1;

                     if (x.pred()) {
                        y.succ(true);
                        x.pred(false);
                     } else {
                        y.right = x.left;
                     }

                     x.left = y;
                     y.balance(0);
                     x.balance(0);
                  }
               }
            } else {
               dir = q != null && q.left != y;
               y.decBalance();
               if (y.balance() == -1) {
                  break;
               }

               if (y.balance() == -2) {
                  Int2FloatAVLTreeMap.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Int2FloatAVLTreeMap.Entry wx = xx.right;
                     xx.right = wx.left;
                     wx.left = xx;
                     y.left = wx.right;
                     wx.right = y;
                     if (wx.balance() == -1) {
                        xx.balance(0);
                        y.balance(1);
                     } else if (wx.balance() == 0) {
                        xx.balance(0);
                        y.balance(0);
                     } else {
                        assert wx.balance() == 1;

                        xx.balance(-1);
                        y.balance(0);
                     }

                     wx.balance(0);
                     if (wx.pred()) {
                        xx.succ(wx);
                        wx.pred(false);
                     }

                     if (wx.succ()) {
                        y.pred(wx);
                        wx.succ(false);
                     }

                     if (q != null) {
                        if (dir) {
                           q.right = wx;
                        } else {
                           q.left = wx;
                        }
                     } else {
                        this.tree = wx;
                     }
                  } else {
                     if (q != null) {
                        if (dir) {
                           q.right = xx;
                        } else {
                           q.left = xx;
                        }
                     } else {
                        this.tree = xx;
                     }

                     if (xx.balance() == 0) {
                        y.left = xx.right;
                        xx.right = y;
                        xx.balance(1);
                        y.balance(-1);
                        break;
                     }

                     assert xx.balance() == -1;

                     if (xx.succ()) {
                        y.pred(true);
                        xx.succ(false);
                     } else {
                        y.left = xx.right;
                     }

                     xx.right = y;
                     y.balance(0);
                     xx.balance(0);
                  }
               }
            }
         }

         this.modified = true;
         this.count--;
         return p.value;
      }
   }

   @Override
   public boolean containsValue(float v) {
      Int2FloatAVLTreeMap.ValueIterator i = new Int2FloatAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         float ev = i.nextFloat();
         if (Float.floatToIntBits(ev) == Float.floatToIntBits(v)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void clear() {
      this.count = 0;
      this.tree = null;
      this.entries = null;
      this.values = null;
      this.keys = null;
      this.firstEntry = this.lastEntry = null;
   }

   @Override
   public boolean containsKey(int k) {
      return this.findKey(k) != null;
   }

   @Override
   public int size() {
      return this.count;
   }

   @Override
   public boolean isEmpty() {
      return this.count == 0;
   }

   @Override
   public float get(int k) {
      Int2FloatAVLTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public int firstIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public int lastIntKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Int2FloatMap.Entry>() {
            final Comparator<? super Int2FloatMap.Entry> comparator = Int2FloatAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Integer.compare(x.getIntKey(), y.getIntKey())
               : (x, y) -> Int2FloatAVLTreeMap.this.actualComparator.compare(x.getIntKey(), y.getIntKey());

            @Override
            public Comparator<? super Int2FloatMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator() {
               return Int2FloatAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator(Int2FloatMap.Entry from) {
               return Int2FloatAVLTreeMap.this.new EntryIterator(from.getIntKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Float) {
                     Int2FloatAVLTreeMap.Entry f = Int2FloatAVLTreeMap.this.findKey((Integer)e.getKey());
                     return e.equals(f);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }

            @Override
            public boolean remove(Object o) {
               if (!(o instanceof java.util.Map.Entry)) {
                  return false;
               } else {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (!(e.getValue() instanceof Float)) {
                     return false;
                  } else {
                     Int2FloatAVLTreeMap.Entry f = Int2FloatAVLTreeMap.this.findKey((Integer)e.getKey());
                     if (f != null && Float.floatToIntBits(f.getFloatValue()) == Float.floatToIntBits((Float)e.getValue())) {
                        Int2FloatAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Int2FloatAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2FloatAVLTreeMap.this.clear();
            }

            public Int2FloatMap.Entry first() {
               return Int2FloatAVLTreeMap.this.firstEntry;
            }

            public Int2FloatMap.Entry last() {
               return Int2FloatAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2FloatMap.Entry> subSet(Int2FloatMap.Entry from, Int2FloatMap.Entry to) {
               return Int2FloatAVLTreeMap.this.subMap(from.getIntKey(), to.getIntKey()).int2FloatEntrySet();
            }

            public ObjectSortedSet<Int2FloatMap.Entry> headSet(Int2FloatMap.Entry to) {
               return Int2FloatAVLTreeMap.this.headMap(to.getIntKey()).int2FloatEntrySet();
            }

            public ObjectSortedSet<Int2FloatMap.Entry> tailSet(Int2FloatMap.Entry from) {
               return Int2FloatAVLTreeMap.this.tailMap(from.getIntKey()).int2FloatEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2FloatAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            @Override
            public FloatIterator iterator() {
               return Int2FloatAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(float k) {
               return Int2FloatAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Int2FloatAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2FloatAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public IntComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Int2FloatSortedMap headMap(int to) {
      return new Int2FloatAVLTreeMap.Submap(0, true, to, false);
   }

   @Override
   public Int2FloatSortedMap tailMap(int from) {
      return new Int2FloatAVLTreeMap.Submap(from, false, 0, true);
   }

   @Override
   public Int2FloatSortedMap subMap(int from, int to) {
      return new Int2FloatAVLTreeMap.Submap(from, false, to, false);
   }

   public Int2FloatAVLTreeMap clone() {
      Int2FloatAVLTreeMap c;
      try {
         c = (Int2FloatAVLTreeMap)super.clone();
      } catch (CloneNotSupportedException var7) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.allocatePaths();
      if (this.count == 0) {
         return c;
      } else {
         Int2FloatAVLTreeMap.Entry rp = new Int2FloatAVLTreeMap.Entry();
         Int2FloatAVLTreeMap.Entry rq = new Int2FloatAVLTreeMap.Entry();
         Int2FloatAVLTreeMap.Entry p = rp;
         rp.left(this.tree);
         Int2FloatAVLTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Int2FloatAVLTreeMap.Entry e = p.left.clone();
               e.pred(q.left);
               e.succ(q);
               q.left(e);
               p = p.left;
               q = q.left;
            } else {
               while (p.succ()) {
                  p = p.right;
                  if (p == null) {
                     q.right = null;
                     c.tree = rq.left;
                     c.firstEntry = c.tree;

                     while (c.firstEntry.left != null) {
                        c.firstEntry = c.firstEntry.left;
                     }

                     c.lastEntry = c.tree;

                     while (c.lastEntry.right != null) {
                        c.lastEntry = c.lastEntry.right;
                     }

                     return c;
                  }

                  q = q.right;
               }

               p = p.right;
               q = q.right;
            }

            if (!p.succ()) {
               Int2FloatAVLTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Int2FloatAVLTreeMap.EntryIterator i = new Int2FloatAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Int2FloatAVLTreeMap.Entry e = i.nextEntry();
         s.writeInt(e.key);
         s.writeFloat(e.value);
      }
   }

   private Int2FloatAVLTreeMap.Entry readTree(ObjectInputStream s, int n, Int2FloatAVLTreeMap.Entry pred, Int2FloatAVLTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Int2FloatAVLTreeMap.Entry top = new Int2FloatAVLTreeMap.Entry(s.readInt(), s.readFloat());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Int2FloatAVLTreeMap.Entry top = new Int2FloatAVLTreeMap.Entry(s.readInt(), s.readFloat());
         top.right(new Int2FloatAVLTreeMap.Entry(s.readInt(), s.readFloat()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Int2FloatAVLTreeMap.Entry top = new Int2FloatAVLTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readInt();
         top.value = s.readFloat();
         top.right(this.readTree(s, rightN, top, succ));
         if (n == (n & -n)) {
            top.balance(1);
         }

         return top;
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.setActualComparator();
      this.allocatePaths();
      if (this.count != 0) {
         this.tree = this.readTree(s, this.count, null, null);
         Int2FloatAVLTreeMap.Entry e = this.tree;

         while (e.left() != null) {
            e = e.left();
         }

         this.firstEntry = e;
         e = this.tree;

         while (e.right() != null) {
            e = e.right();
         }

         this.lastEntry = e;
      }
   }

   private static final class Entry extends AbstractInt2FloatMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Int2FloatAVLTreeMap.Entry left;
      Int2FloatAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0, 0.0F);
      }

      Entry(int k, float v) {
         super(k, v);
         this.info = -1073741824;
      }

      Int2FloatAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2FloatAVLTreeMap.Entry right() {
         return (this.info & -2147483648) != 0 ? null : this.right;
      }

      boolean pred() {
         return (this.info & 1073741824) != 0;
      }

      boolean succ() {
         return (this.info & -2147483648) != 0;
      }

      void pred(boolean pred) {
         if (pred) {
            this.info |= 1073741824;
         } else {
            this.info &= -1073741825;
         }
      }

      void succ(boolean succ) {
         if (succ) {
            this.info |= Integer.MIN_VALUE;
         } else {
            this.info &= Integer.MAX_VALUE;
         }
      }

      void pred(Int2FloatAVLTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Int2FloatAVLTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Int2FloatAVLTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Int2FloatAVLTreeMap.Entry right) {
         this.info &= Integer.MAX_VALUE;
         this.right = right;
      }

      int balance() {
         return (byte)this.info;
      }

      void balance(int level) {
         this.info &= -256;
         this.info |= level & 0xFF;
      }

      void incBalance() {
         this.info = this.info & -256 | (byte)this.info + 1 & 0xFF;
      }

      protected void decBalance() {
         this.info = this.info & -256 | (byte)this.info - 1 & 0xFF;
      }

      Int2FloatAVLTreeMap.Entry next() {
         Int2FloatAVLTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Int2FloatAVLTreeMap.Entry prev() {
         Int2FloatAVLTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public float setValue(float value) {
         float oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Int2FloatAVLTreeMap.Entry clone() {
         Int2FloatAVLTreeMap.Entry c;
         try {
            c = (Int2FloatAVLTreeMap.Entry)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError();
         }

         c.key = this.key;
         c.value = this.value;
         c.info = this.info;
         return c;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<Integer, Float> e = (java.util.Map.Entry<Integer, Float>)o;
            return this.key == e.getKey() && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Int2FloatAVLTreeMap.TreeIterator implements ObjectListIterator<Int2FloatMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(int k) {
         super(k);
      }

      public Int2FloatMap.Entry next() {
         return this.nextEntry();
      }

      public Int2FloatMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Int2FloatMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Int2FloatMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Int2FloatAVLTreeMap.TreeIterator implements IntListIterator {
      public KeyIterator() {
      }

      public KeyIterator(int k) {
         super(k);
      }

      @Override
      public int nextInt() {
         return this.nextEntry().key;
      }

      @Override
      public int previousInt() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractInt2FloatSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return Int2FloatAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return Int2FloatAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractInt2FloatSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      int from;
      int to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Int2FloatMap.Entry> entries;
      protected transient IntSortedSet keys;
      protected transient FloatCollection values;

      public Submap(int from, boolean bottom, int to, boolean top) {
         if (!bottom && !top && Int2FloatAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Int2FloatAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Int2FloatAVLTreeMap.Submap.SubmapIterator i = new Int2FloatAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(int k) {
         return (this.bottom || Int2FloatAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Int2FloatAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2FloatMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator(Int2FloatMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getIntKey());
               }

               @Override
               public Comparator<? super Int2FloatMap.Entry> comparator() {
                  return Int2FloatAVLTreeMap.this.int2FloatEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Float) {
                        Int2FloatAVLTreeMap.Entry f = Int2FloatAVLTreeMap.this.findKey((Integer)e.getKey());
                        return f != null && Submap.this.in(f.key) && e.equals(f);
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public boolean remove(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Float) {
                        Int2FloatAVLTreeMap.Entry f = Int2FloatAVLTreeMap.this.findKey((Integer)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                           Submap.this.remove(f.key);
                        }

                        return f != null;
                     } else {
                        return false;
                     }
                  }
               }

               @Override
               public int size() {
                  int c = 0;
                  Iterator<?> i = this.iterator();

                  while (i.hasNext()) {
                     c++;
                     i.next();
                  }

                  return c;
               }

               @Override
               public boolean isEmpty() {
                  return !Submap.this.new SubmapIterator().hasNext();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }

               public Int2FloatMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Int2FloatMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Int2FloatMap.Entry> subSet(Int2FloatMap.Entry from, Int2FloatMap.Entry to) {
                  return Submap.this.subMap(from.getIntKey(), to.getIntKey()).int2FloatEntrySet();
               }

               public ObjectSortedSet<Int2FloatMap.Entry> headSet(Int2FloatMap.Entry to) {
                  return Submap.this.headMap(to.getIntKey()).int2FloatEntrySet();
               }

               public ObjectSortedSet<Int2FloatMap.Entry> tailSet(Int2FloatMap.Entry from) {
                  return Submap.this.tailMap(from.getIntKey()).int2FloatEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Int2FloatAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public FloatCollection values() {
         if (this.values == null) {
            this.values = new AbstractFloatCollection() {
               @Override
               public FloatIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(float k) {
                  return Submap.this.containsValue(k);
               }

               @Override
               public int size() {
                  return Submap.this.size();
               }

               @Override
               public void clear() {
                  Submap.this.clear();
               }
            };
         }

         return this.values;
      }

      @Override
      public boolean containsKey(int k) {
         return this.in(k) && Int2FloatAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(float v) {
         Int2FloatAVLTreeMap.Submap.SubmapIterator i = new Int2FloatAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            float ev = i.nextEntry().value;
            if (Float.floatToIntBits(ev) == Float.floatToIntBits(v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public float get(int k) {
         Int2FloatAVLTreeMap.Entry e;
         return this.in(k) && (e = Int2FloatAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public float put(int k, float v) {
         Int2FloatAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            float oldValue = Int2FloatAVLTreeMap.this.put(k, v);
            return Int2FloatAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public float remove(int k) {
         Int2FloatAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            float oldValue = Int2FloatAVLTreeMap.this.remove(k);
            return Int2FloatAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Int2FloatAVLTreeMap.Submap.SubmapIterator i = new Int2FloatAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Int2FloatAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public IntComparator comparator() {
         return Int2FloatAVLTreeMap.this.actualComparator;
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         if (this.top) {
            return Int2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Int2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? Int2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         if (this.bottom) {
            return Int2FloatAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Int2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? Int2FloatAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         if (this.top && this.bottom) {
            return Int2FloatAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Int2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Int2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Int2FloatAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Int2FloatAVLTreeMap.Entry firstEntry() {
         if (Int2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2FloatAVLTreeMap.Entry e;
            if (this.bottom) {
               e = Int2FloatAVLTreeMap.this.firstEntry;
            } else {
               e = Int2FloatAVLTreeMap.this.locateKey(this.from);
               if (Int2FloatAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Int2FloatAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Int2FloatAVLTreeMap.Entry lastEntry() {
         if (Int2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2FloatAVLTreeMap.Entry e;
            if (this.top) {
               e = Int2FloatAVLTreeMap.this.lastEntry;
            } else {
               e = Int2FloatAVLTreeMap.this.locateKey(this.to);
               if (Int2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Int2FloatAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public int firstIntKey() {
         Int2FloatAVLTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public int lastIntKey() {
         Int2FloatAVLTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractInt2FloatSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public IntBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public IntBidirectionalIterator iterator(int from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Int2FloatAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Int2FloatMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(int k) {
            super(k);
         }

         public Int2FloatMap.Entry next() {
            return this.nextEntry();
         }

         public Int2FloatMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Int2FloatAVLTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2FloatAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2FloatAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2FloatAVLTreeMap.this.locateKey(k);
                  if (Int2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                     this.prev = this.next;
                     this.next = this.next.next();
                  } else {
                     this.prev = this.next.prev();
                  }
               }
            }
         }

         @Override
         void updatePrevious() {
            this.prev = this.prev.prev();
            if (!Submap.this.bottom && this.prev != null && Int2FloatAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2FloatAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Int2FloatAVLTreeMap.Submap.SubmapIterator implements IntListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(int from) {
            super(from);
         }

         @Override
         public int nextInt() {
            return this.nextEntry().key;
         }

         @Override
         public int previousInt() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Int2FloatAVLTreeMap.Submap.SubmapIterator implements FloatListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public float nextFloat() {
            return this.nextEntry().value;
         }

         @Override
         public float previousFloat() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Int2FloatAVLTreeMap.Entry prev;
      Int2FloatAVLTreeMap.Entry next;
      Int2FloatAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Int2FloatAVLTreeMap.this.firstEntry;
      }

      TreeIterator(int k) {
         if ((this.next = Int2FloatAVLTreeMap.this.locateKey(k)) != null) {
            if (Int2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
               this.prev = this.next;
               this.next = this.next.next();
            } else {
               this.prev = this.next.prev();
            }
         }
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public boolean hasPrevious() {
         return this.prev != null;
      }

      void updateNext() {
         this.next = this.next.next();
      }

      Int2FloatAVLTreeMap.Entry nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev = this.next;
            this.index++;
            this.updateNext();
            return this.curr;
         }
      }

      void updatePrevious() {
         this.prev = this.prev.prev();
      }

      Int2FloatAVLTreeMap.Entry previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next = this.prev;
            this.index--;
            this.updatePrevious();
            return this.curr;
         }
      }

      public int nextIndex() {
         return this.index;
      }

      public int previousIndex() {
         return this.index - 1;
      }

      public void remove() {
         if (this.curr == null) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
            }

            this.next = this.prev = this.curr;
            this.updatePrevious();
            this.updateNext();
            Int2FloatAVLTreeMap.this.remove(this.curr.key);
            this.curr = null;
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while (i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }
   }

   private final class ValueIterator extends Int2FloatAVLTreeMap.TreeIterator implements FloatListIterator {
      private ValueIterator() {
      }

      @Override
      public float nextFloat() {
         return this.nextEntry().value;
      }

      @Override
      public float previousFloat() {
         return this.previousEntry().value;
      }
   }
}
