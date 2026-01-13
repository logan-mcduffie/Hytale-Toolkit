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

public class Int2FloatRBTreeMap extends AbstractInt2FloatSortedMap implements Serializable, Cloneable {
   protected transient Int2FloatRBTreeMap.Entry tree;
   protected int count;
   protected transient Int2FloatRBTreeMap.Entry firstEntry;
   protected transient Int2FloatRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Int2FloatMap.Entry> entries;
   protected transient IntSortedSet keys;
   protected transient FloatCollection values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Int2FloatRBTreeMap.Entry[] nodePath;

   public Int2FloatRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2FloatRBTreeMap(Comparator<? super Integer> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Int2FloatRBTreeMap(Map<? extends Integer, ? extends Float> m) {
      this();
      this.putAll(m);
   }

   public Int2FloatRBTreeMap(SortedMap<Integer, Float> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2FloatRBTreeMap(Int2FloatMap m) {
      this();
      this.putAll(m);
   }

   public Int2FloatRBTreeMap(Int2FloatSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2FloatRBTreeMap(int[] k, float[] v, Comparator<? super Integer> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2FloatRBTreeMap(int[] k, float[] v) {
      this(k, v, null);
   }

   final int compare(int k1, int k2) {
      return this.actualComparator == null ? Integer.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Int2FloatRBTreeMap.Entry findKey(int k) {
      Int2FloatRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Int2FloatRBTreeMap.Entry locateKey(int k) {
      Int2FloatRBTreeMap.Entry e = this.tree;
      Int2FloatRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Int2FloatRBTreeMap.Entry[64];
   }

   public float addTo(int k, float incr) {
      Int2FloatRBTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public float put(int k, float v) {
      Int2FloatRBTreeMap.Entry e = this.add(k);
      float oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Int2FloatRBTreeMap.Entry add(int k) {
      this.modified = false;
      int maxDepth = 0;
      Int2FloatRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Int2FloatRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Int2FloatRBTreeMap.Entry p = this.tree;
         int i = 0;

         label123:
         while (true) {
            int cmp;
            if ((cmp = this.compare(k, p.key)) == 0) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return p;
            }

            this.nodePath[i] = p;
            if (this.dirPath[i++] = cmp > 0) {
               if (!p.succ()) {
                  p = p.right;
                  continue;
               }

               this.count++;
               e = new Int2FloatRBTreeMap.Entry(k, this.defRetValue);
               if (p.right == null) {
                  this.lastEntry = e;
               }

               e.left = p;
               e.right = p.right;
               p.right(e);
            } else {
               if (!p.pred()) {
                  p = p.left;
                  continue;
               }

               this.count++;
               e = new Int2FloatRBTreeMap.Entry(k, this.defRetValue);
               if (p.left == null) {
                  this.firstEntry = e;
               }

               e.right = p;
               e.left = p.left;
               p.left(e);
            }

            this.modified = true;
            maxDepth = i--;

            while (true) {
               if (i <= 0 || this.nodePath[i].black()) {
                  break label123;
               }

               if (!this.dirPath[i - 1]) {
                  Int2FloatRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Int2FloatRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Int2FloatRBTreeMap.Entry xx = this.nodePath[i - 1];
                     xx.black(false);
                     y.black(true);
                     xx.left = y.right;
                     y.right = xx;
                     if (i < 2) {
                        this.tree = y;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = y;
                     } else {
                        this.nodePath[i - 2].left = y;
                     }

                     if (y.succ()) {
                        y.succ(false);
                        xx.pred(y);
                     }
                     break label123;
                  }

                  this.nodePath[i].black(true);
                  y.black(true);
                  this.nodePath[i - 1].black(false);
                  i -= 2;
               } else {
                  Int2FloatRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Int2FloatRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Int2FloatRBTreeMap.Entry xxx = this.nodePath[i - 1];
                     xxx.black(false);
                     y.black(true);
                     xxx.right = y.left;
                     y.left = xxx;
                     if (i < 2) {
                        this.tree = y;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = y;
                     } else {
                        this.nodePath[i - 2].left = y;
                     }

                     if (y.pred()) {
                        y.pred(false);
                        xxx.succ(y);
                     }
                     break label123;
                  }

                  this.nodePath[i].black(true);
                  y.black(true);
                  this.nodePath[i - 1].black(false);
                  i -= 2;
               }
            }
         }
      }

      this.tree.black(true);

      while (maxDepth-- != 0) {
         this.nodePath[maxDepth] = null;
      }

      return e;
   }

   @Override
   public float remove(int k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2FloatRBTreeMap.Entry p = this.tree;
         int i = 0;
         int kk = k;

         int cmp;
         while ((cmp = this.compare(kk, p.key)) != 0) {
            this.dirPath[i] = cmp > 0;
            this.nodePath[i] = p;
            if (this.dirPath[i++]) {
               if ((p = p.right()) == null) {
                  while (i-- != 0) {
                     this.nodePath[i] = null;
                  }

                  return this.defRetValue;
               }
            } else if ((p = p.left()) == null) {
               while (i-- != 0) {
                  this.nodePath[i] = null;
               }

               return this.defRetValue;
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
               if (i == 0) {
                  this.tree = p.left;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].succ(p.right);
               } else {
                  this.nodePath[i - 1].pred(p.left);
               }
            } else {
               p.prev().right = p.right;
               if (i == 0) {
                  this.tree = p.left;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].right = p.left;
               } else {
                  this.nodePath[i - 1].left = p.left;
               }
            }
         } else {
            Int2FloatRBTreeMap.Entry r = p.right;
            if (r.pred()) {
               r.left = p.left;
               r.pred(p.pred());
               if (!r.pred()) {
                  r.prev().right = r;
               }

               if (i == 0) {
                  this.tree = r;
               } else if (this.dirPath[i - 1]) {
                  this.nodePath[i - 1].right = r;
               } else {
                  this.nodePath[i - 1].left = r;
               }

               boolean color = r.black();
               r.black(p.black());
               p.black(color);
               this.dirPath[i] = true;
               this.nodePath[i++] = r;
            } else {
               int j = i++;

               while (true) {
                  this.dirPath[i] = false;
                  this.nodePath[i++] = r;
                  Int2FloatRBTreeMap.Entry s = r.left;
                  if (s.pred()) {
                     this.dirPath[j] = true;
                     this.nodePath[j] = s;
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

                     s.right(p.right);
                     boolean color = s.black();
                     s.black(p.black());
                     p.black(color);
                     if (j == 0) {
                        this.tree = s;
                     } else if (this.dirPath[j - 1]) {
                        this.nodePath[j - 1].right = s;
                     } else {
                        this.nodePath[j - 1].left = s;
                     }
                     break;
                  }

                  r = s;
               }
            }
         }

         int maxDepth = i;
         if (p.black()) {
            for (; i > 0; i--) {
               if (this.dirPath[i - 1] && !this.nodePath[i - 1].succ() || !this.dirPath[i - 1] && !this.nodePath[i - 1].pred()) {
                  Int2FloatRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Int2FloatRBTreeMap.Entry w = this.nodePath[i - 1].right;
                  if (!w.black()) {
                     w.black(true);
                     this.nodePath[i - 1].black(false);
                     this.nodePath[i - 1].right = w.left;
                     w.left = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = w;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                     } else {
                        this.nodePath[i - 2].left = w;
                     }

                     this.nodePath[i] = this.nodePath[i - 1];
                     this.dirPath[i] = false;
                     this.nodePath[i - 1] = w;
                     if (maxDepth == i++) {
                        maxDepth++;
                     }

                     w = this.nodePath[i - 1].right;
                  }

                  if (!w.pred() && !w.left.black() || !w.succ() && !w.right.black()) {
                     if (w.succ() || w.right.black()) {
                        Int2FloatRBTreeMap.Entry y = w.left;
                        y.black(true);
                        w.black(false);
                        w.left = y.right;
                        y.right = w;
                        w = this.nodePath[i - 1].right = y;
                        if (w.succ()) {
                           w.succ(false);
                           w.right.pred(w);
                        }
                     }

                     w.black(this.nodePath[i - 1].black());
                     this.nodePath[i - 1].black(true);
                     w.right.black(true);
                     this.nodePath[i - 1].right = w.left;
                     w.left = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = w;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = w;
                     } else {
                        this.nodePath[i - 2].left = w;
                     }

                     if (w.pred()) {
                        w.pred(false);
                        this.nodePath[i - 1].succ(w);
                     }
                     break;
                  }

                  w.black(false);
               } else {
                  Int2FloatRBTreeMap.Entry wx = this.nodePath[i - 1].left;
                  if (!wx.black()) {
                     wx.black(true);
                     this.nodePath[i - 1].black(false);
                     this.nodePath[i - 1].left = wx.right;
                     wx.right = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = wx;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = wx;
                     } else {
                        this.nodePath[i - 2].left = wx;
                     }

                     this.nodePath[i] = this.nodePath[i - 1];
                     this.dirPath[i] = true;
                     this.nodePath[i - 1] = wx;
                     if (maxDepth == i++) {
                        maxDepth++;
                     }

                     wx = this.nodePath[i - 1].left;
                  }

                  if (!wx.pred() && !wx.left.black() || !wx.succ() && !wx.right.black()) {
                     if (wx.pred() || wx.left.black()) {
                        Int2FloatRBTreeMap.Entry y = wx.right;
                        y.black(true);
                        wx.black(false);
                        wx.right = y.left;
                        y.left = wx;
                        wx = this.nodePath[i - 1].left = y;
                        if (wx.pred()) {
                           wx.pred(false);
                           wx.left.succ(wx);
                        }
                     }

                     wx.black(this.nodePath[i - 1].black());
                     this.nodePath[i - 1].black(true);
                     wx.left.black(true);
                     this.nodePath[i - 1].left = wx.right;
                     wx.right = this.nodePath[i - 1];
                     if (i < 2) {
                        this.tree = wx;
                     } else if (this.dirPath[i - 2]) {
                        this.nodePath[i - 2].right = wx;
                     } else {
                        this.nodePath[i - 2].left = wx;
                     }

                     if (wx.succ()) {
                        wx.succ(false);
                        this.nodePath[i - 1].pred(wx);
                     }
                     break;
                  }

                  wx.black(false);
               }
            }

            if (this.tree != null) {
               this.tree.black(true);
            }
         }

         this.modified = true;
         this.count--;

         while (maxDepth-- != 0) {
            this.nodePath[maxDepth] = null;
         }

         return p.value;
      }
   }

   @Override
   public boolean containsValue(float v) {
      Int2FloatRBTreeMap.ValueIterator i = new Int2FloatRBTreeMap.ValueIterator();
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
      Int2FloatRBTreeMap.Entry e = this.findKey(k);
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
            final Comparator<? super Int2FloatMap.Entry> comparator = Int2FloatRBTreeMap.this.actualComparator == null
               ? (x, y) -> Integer.compare(x.getIntKey(), y.getIntKey())
               : (x, y) -> Int2FloatRBTreeMap.this.actualComparator.compare(x.getIntKey(), y.getIntKey());

            @Override
            public Comparator<? super Int2FloatMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator() {
               return Int2FloatRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2FloatMap.Entry> iterator(Int2FloatMap.Entry from) {
               return Int2FloatRBTreeMap.this.new EntryIterator(from.getIntKey());
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
                     Int2FloatRBTreeMap.Entry f = Int2FloatRBTreeMap.this.findKey((Integer)e.getKey());
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
                  } else if (!(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Float) {
                     Int2FloatRBTreeMap.Entry f = Int2FloatRBTreeMap.this.findKey((Integer)e.getKey());
                     if (f != null && Float.floatToIntBits(f.getFloatValue()) == Float.floatToIntBits((Float)e.getValue())) {
                        Int2FloatRBTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            }

            @Override
            public int size() {
               return Int2FloatRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2FloatRBTreeMap.this.clear();
            }

            public Int2FloatMap.Entry first() {
               return Int2FloatRBTreeMap.this.firstEntry;
            }

            public Int2FloatMap.Entry last() {
               return Int2FloatRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2FloatMap.Entry> subSet(Int2FloatMap.Entry from, Int2FloatMap.Entry to) {
               return Int2FloatRBTreeMap.this.subMap(from.getIntKey(), to.getIntKey()).int2FloatEntrySet();
            }

            public ObjectSortedSet<Int2FloatMap.Entry> headSet(Int2FloatMap.Entry to) {
               return Int2FloatRBTreeMap.this.headMap(to.getIntKey()).int2FloatEntrySet();
            }

            public ObjectSortedSet<Int2FloatMap.Entry> tailSet(Int2FloatMap.Entry from) {
               return Int2FloatRBTreeMap.this.tailMap(from.getIntKey()).int2FloatEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2FloatRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            @Override
            public FloatIterator iterator() {
               return Int2FloatRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(float k) {
               return Int2FloatRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Int2FloatRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2FloatRBTreeMap.this.clear();
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
      return new Int2FloatRBTreeMap.Submap(0, true, to, false);
   }

   @Override
   public Int2FloatSortedMap tailMap(int from) {
      return new Int2FloatRBTreeMap.Submap(from, false, 0, true);
   }

   @Override
   public Int2FloatSortedMap subMap(int from, int to) {
      return new Int2FloatRBTreeMap.Submap(from, false, to, false);
   }

   public Int2FloatRBTreeMap clone() {
      Int2FloatRBTreeMap c;
      try {
         c = (Int2FloatRBTreeMap)super.clone();
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
         Int2FloatRBTreeMap.Entry rp = new Int2FloatRBTreeMap.Entry();
         Int2FloatRBTreeMap.Entry rq = new Int2FloatRBTreeMap.Entry();
         Int2FloatRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Int2FloatRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Int2FloatRBTreeMap.Entry e = p.left.clone();
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
               Int2FloatRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Int2FloatRBTreeMap.EntryIterator i = new Int2FloatRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Int2FloatRBTreeMap.Entry e = i.nextEntry();
         s.writeInt(e.key);
         s.writeFloat(e.value);
      }
   }

   private Int2FloatRBTreeMap.Entry readTree(ObjectInputStream s, int n, Int2FloatRBTreeMap.Entry pred, Int2FloatRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Int2FloatRBTreeMap.Entry top = new Int2FloatRBTreeMap.Entry(s.readInt(), s.readFloat());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Int2FloatRBTreeMap.Entry top = new Int2FloatRBTreeMap.Entry(s.readInt(), s.readFloat());
         top.black(true);
         top.right(new Int2FloatRBTreeMap.Entry(s.readInt(), s.readFloat()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Int2FloatRBTreeMap.Entry top = new Int2FloatRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readInt();
         top.value = s.readFloat();
         top.black(true);
         top.right(this.readTree(s, rightN, top, succ));
         if (n + 2 == (n + 2 & -(n + 2))) {
            top.right.black(false);
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
         Int2FloatRBTreeMap.Entry e = this.tree;

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
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Int2FloatRBTreeMap.Entry left;
      Int2FloatRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0, 0.0F);
      }

      Entry(int k, float v) {
         super(k, v);
         this.info = -1073741824;
      }

      Int2FloatRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2FloatRBTreeMap.Entry right() {
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

      void pred(Int2FloatRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Int2FloatRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Int2FloatRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Int2FloatRBTreeMap.Entry right) {
         this.info &= Integer.MAX_VALUE;
         this.right = right;
      }

      boolean black() {
         return (this.info & 1) != 0;
      }

      void black(boolean black) {
         if (black) {
            this.info |= 1;
         } else {
            this.info &= -2;
         }
      }

      Int2FloatRBTreeMap.Entry next() {
         Int2FloatRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Int2FloatRBTreeMap.Entry prev() {
         Int2FloatRBTreeMap.Entry prev = this.left;
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

      public Int2FloatRBTreeMap.Entry clone() {
         Int2FloatRBTreeMap.Entry c;
         try {
            c = (Int2FloatRBTreeMap.Entry)super.clone();
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

   private class EntryIterator extends Int2FloatRBTreeMap.TreeIterator implements ObjectListIterator<Int2FloatMap.Entry> {
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
   }

   private final class KeyIterator extends Int2FloatRBTreeMap.TreeIterator implements IntListIterator {
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
         return Int2FloatRBTreeMap.this.new KeyIterator();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return Int2FloatRBTreeMap.this.new KeyIterator(from);
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
         if (!bottom && !top && Int2FloatRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Int2FloatRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Int2FloatRBTreeMap.Submap.SubmapIterator i = new Int2FloatRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(int k) {
         return (this.bottom || Int2FloatRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Int2FloatRBTreeMap.this.compare(k, this.to) < 0);
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
                  return Int2FloatRBTreeMap.this.int2FloatEntrySet().comparator();
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
                        Int2FloatRBTreeMap.Entry f = Int2FloatRBTreeMap.this.findKey((Integer)e.getKey());
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
                        Int2FloatRBTreeMap.Entry f = Int2FloatRBTreeMap.this.findKey((Integer)e.getKey());
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
            this.keys = new Int2FloatRBTreeMap.Submap.KeySet();
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
         return this.in(k) && Int2FloatRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(float v) {
         Int2FloatRBTreeMap.Submap.SubmapIterator i = new Int2FloatRBTreeMap.Submap.SubmapIterator();

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
         Int2FloatRBTreeMap.Entry e;
         return this.in(k) && (e = Int2FloatRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public float put(int k, float v) {
         Int2FloatRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            float oldValue = Int2FloatRBTreeMap.this.put(k, v);
            return Int2FloatRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public float remove(int k) {
         Int2FloatRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            float oldValue = Int2FloatRBTreeMap.this.remove(k);
            return Int2FloatRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Int2FloatRBTreeMap.Submap.SubmapIterator i = new Int2FloatRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Int2FloatRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public IntComparator comparator() {
         return Int2FloatRBTreeMap.this.actualComparator;
      }

      @Override
      public Int2FloatSortedMap headMap(int to) {
         if (this.top) {
            return Int2FloatRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Int2FloatRBTreeMap.this.compare(to, this.to) < 0 ? Int2FloatRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Int2FloatSortedMap tailMap(int from) {
         if (this.bottom) {
            return Int2FloatRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Int2FloatRBTreeMap.this.compare(from, this.from) > 0 ? Int2FloatRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Int2FloatSortedMap subMap(int from, int to) {
         if (this.top && this.bottom) {
            return Int2FloatRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Int2FloatRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Int2FloatRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Int2FloatRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Int2FloatRBTreeMap.Entry firstEntry() {
         if (Int2FloatRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2FloatRBTreeMap.Entry e;
            if (this.bottom) {
               e = Int2FloatRBTreeMap.this.firstEntry;
            } else {
               e = Int2FloatRBTreeMap.this.locateKey(this.from);
               if (Int2FloatRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Int2FloatRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Int2FloatRBTreeMap.Entry lastEntry() {
         if (Int2FloatRBTreeMap.this.tree == null) {
            return null;
         } else {
            Int2FloatRBTreeMap.Entry e;
            if (this.top) {
               e = Int2FloatRBTreeMap.this.lastEntry;
            } else {
               e = Int2FloatRBTreeMap.this.locateKey(this.to);
               if (Int2FloatRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Int2FloatRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public int firstIntKey() {
         Int2FloatRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public int lastIntKey() {
         Int2FloatRBTreeMap.Entry e = this.lastEntry();
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

      private class SubmapEntryIterator extends Int2FloatRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Int2FloatMap.Entry> {
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

      private class SubmapIterator extends Int2FloatRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2FloatRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2FloatRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2FloatRBTreeMap.this.locateKey(k);
                  if (Int2FloatRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Int2FloatRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2FloatRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Int2FloatRBTreeMap.Submap.SubmapIterator implements IntListIterator {
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

      private final class SubmapValueIterator extends Int2FloatRBTreeMap.Submap.SubmapIterator implements FloatListIterator {
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
      Int2FloatRBTreeMap.Entry prev;
      Int2FloatRBTreeMap.Entry next;
      Int2FloatRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Int2FloatRBTreeMap.this.firstEntry;
      }

      TreeIterator(int k) {
         if ((this.next = Int2FloatRBTreeMap.this.locateKey(k)) != null) {
            if (Int2FloatRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Int2FloatRBTreeMap.Entry nextEntry() {
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

      Int2FloatRBTreeMap.Entry previousEntry() {
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
            Int2FloatRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Int2FloatRBTreeMap.TreeIterator implements FloatListIterator {
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
