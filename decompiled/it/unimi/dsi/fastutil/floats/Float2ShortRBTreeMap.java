package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class Float2ShortRBTreeMap extends AbstractFloat2ShortSortedMap implements Serializable, Cloneable {
   protected transient Float2ShortRBTreeMap.Entry tree;
   protected int count;
   protected transient Float2ShortRBTreeMap.Entry firstEntry;
   protected transient Float2ShortRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Float2ShortMap.Entry> entries;
   protected transient FloatSortedSet keys;
   protected transient ShortCollection values;
   protected transient boolean modified;
   protected Comparator<? super Float> storedComparator;
   protected transient FloatComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Float2ShortRBTreeMap.Entry[] nodePath;

   public Float2ShortRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = FloatComparators.asFloatComparator(this.storedComparator);
   }

   public Float2ShortRBTreeMap(Comparator<? super Float> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Float2ShortRBTreeMap(Map<? extends Float, ? extends Short> m) {
      this();
      this.putAll(m);
   }

   public Float2ShortRBTreeMap(SortedMap<Float, Short> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2ShortRBTreeMap(Float2ShortMap m) {
      this();
      this.putAll(m);
   }

   public Float2ShortRBTreeMap(Float2ShortSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2ShortRBTreeMap(float[] k, short[] v, Comparator<? super Float> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2ShortRBTreeMap(float[] k, short[] v) {
      this(k, v, null);
   }

   final int compare(float k1, float k2) {
      return this.actualComparator == null ? Float.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Float2ShortRBTreeMap.Entry findKey(float k) {
      Float2ShortRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Float2ShortRBTreeMap.Entry locateKey(float k) {
      Float2ShortRBTreeMap.Entry e = this.tree;
      Float2ShortRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Float2ShortRBTreeMap.Entry[64];
   }

   public short addTo(float k, short incr) {
      Float2ShortRBTreeMap.Entry e = this.add(k);
      short oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public short put(float k, short v) {
      Float2ShortRBTreeMap.Entry e = this.add(k);
      short oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Float2ShortRBTreeMap.Entry add(float k) {
      this.modified = false;
      int maxDepth = 0;
      Float2ShortRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Float2ShortRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Float2ShortRBTreeMap.Entry p = this.tree;
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
               e = new Float2ShortRBTreeMap.Entry(k, this.defRetValue);
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
               e = new Float2ShortRBTreeMap.Entry(k, this.defRetValue);
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
                  Float2ShortRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Float2ShortRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Float2ShortRBTreeMap.Entry xx = this.nodePath[i - 1];
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
                  Float2ShortRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Float2ShortRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Float2ShortRBTreeMap.Entry xxx = this.nodePath[i - 1];
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
   public short remove(float k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Float2ShortRBTreeMap.Entry p = this.tree;
         int i = 0;
         float kk = k;

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
            Float2ShortRBTreeMap.Entry r = p.right;
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
                  Float2ShortRBTreeMap.Entry s = r.left;
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
                  Float2ShortRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Float2ShortRBTreeMap.Entry w = this.nodePath[i - 1].right;
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
                        Float2ShortRBTreeMap.Entry y = w.left;
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
                  Float2ShortRBTreeMap.Entry wx = this.nodePath[i - 1].left;
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
                        Float2ShortRBTreeMap.Entry y = wx.right;
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
   public boolean containsValue(short v) {
      Float2ShortRBTreeMap.ValueIterator i = new Float2ShortRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         short ev = i.nextShort();
         if (ev == v) {
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
   public boolean containsKey(float k) {
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
   public short get(float k) {
      Float2ShortRBTreeMap.Entry e = this.findKey(k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public float firstFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public float lastFloatKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Float2ShortMap.Entry>() {
            final Comparator<? super Float2ShortMap.Entry> comparator = Float2ShortRBTreeMap.this.actualComparator == null
               ? (x, y) -> Float.compare(x.getFloatKey(), y.getFloatKey())
               : (x, y) -> Float2ShortRBTreeMap.this.actualComparator.compare(x.getFloatKey(), y.getFloatKey());

            @Override
            public Comparator<? super Float2ShortMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator() {
               return Float2ShortRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator(Float2ShortMap.Entry from) {
               return Float2ShortRBTreeMap.this.new EntryIterator(from.getFloatKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Float)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Short) {
                     Float2ShortRBTreeMap.Entry f = Float2ShortRBTreeMap.this.findKey((Float)e.getKey());
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
                  } else if (!(e.getKey() instanceof Float)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Short) {
                     Float2ShortRBTreeMap.Entry f = Float2ShortRBTreeMap.this.findKey((Float)e.getKey());
                     if (f != null && f.getShortValue() == (Short)e.getValue()) {
                        Float2ShortRBTreeMap.this.remove(f.key);
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
               return Float2ShortRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2ShortRBTreeMap.this.clear();
            }

            public Float2ShortMap.Entry first() {
               return Float2ShortRBTreeMap.this.firstEntry;
            }

            public Float2ShortMap.Entry last() {
               return Float2ShortRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Float2ShortMap.Entry> subSet(Float2ShortMap.Entry from, Float2ShortMap.Entry to) {
               return Float2ShortRBTreeMap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2ShortEntrySet();
            }

            public ObjectSortedSet<Float2ShortMap.Entry> headSet(Float2ShortMap.Entry to) {
               return Float2ShortRBTreeMap.this.headMap(to.getFloatKey()).float2ShortEntrySet();
            }

            public ObjectSortedSet<Float2ShortMap.Entry> tailSet(Float2ShortMap.Entry from) {
               return Float2ShortRBTreeMap.this.tailMap(from.getFloatKey()).float2ShortEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ShortRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ShortCollection values() {
      if (this.values == null) {
         this.values = new AbstractShortCollection() {
            @Override
            public ShortIterator iterator() {
               return Float2ShortRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(short k) {
               return Float2ShortRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Float2ShortRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2ShortRBTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public FloatComparator comparator() {
      return this.actualComparator;
   }

   @Override
   public Float2ShortSortedMap headMap(float to) {
      return new Float2ShortRBTreeMap.Submap(0.0F, true, to, false);
   }

   @Override
   public Float2ShortSortedMap tailMap(float from) {
      return new Float2ShortRBTreeMap.Submap(from, false, 0.0F, true);
   }

   @Override
   public Float2ShortSortedMap subMap(float from, float to) {
      return new Float2ShortRBTreeMap.Submap(from, false, to, false);
   }

   public Float2ShortRBTreeMap clone() {
      Float2ShortRBTreeMap c;
      try {
         c = (Float2ShortRBTreeMap)super.clone();
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
         Float2ShortRBTreeMap.Entry rp = new Float2ShortRBTreeMap.Entry();
         Float2ShortRBTreeMap.Entry rq = new Float2ShortRBTreeMap.Entry();
         Float2ShortRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Float2ShortRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Float2ShortRBTreeMap.Entry e = p.left.clone();
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
               Float2ShortRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Float2ShortRBTreeMap.EntryIterator i = new Float2ShortRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Float2ShortRBTreeMap.Entry e = i.nextEntry();
         s.writeFloat(e.key);
         s.writeShort(e.value);
      }
   }

   private Float2ShortRBTreeMap.Entry readTree(ObjectInputStream s, int n, Float2ShortRBTreeMap.Entry pred, Float2ShortRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Float2ShortRBTreeMap.Entry top = new Float2ShortRBTreeMap.Entry(s.readFloat(), s.readShort());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Float2ShortRBTreeMap.Entry top = new Float2ShortRBTreeMap.Entry(s.readFloat(), s.readShort());
         top.black(true);
         top.right(new Float2ShortRBTreeMap.Entry(s.readFloat(), s.readShort()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Float2ShortRBTreeMap.Entry top = new Float2ShortRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readFloat();
         top.value = s.readShort();
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
         Float2ShortRBTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractFloat2ShortMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Float2ShortRBTreeMap.Entry left;
      Float2ShortRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0F, (short)0);
      }

      Entry(float k, short v) {
         super(k, v);
         this.info = -1073741824;
      }

      Float2ShortRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Float2ShortRBTreeMap.Entry right() {
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

      void pred(Float2ShortRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Float2ShortRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Float2ShortRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Float2ShortRBTreeMap.Entry right) {
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

      Float2ShortRBTreeMap.Entry next() {
         Float2ShortRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Float2ShortRBTreeMap.Entry prev() {
         Float2ShortRBTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public short setValue(short value) {
         short oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Float2ShortRBTreeMap.Entry clone() {
         Float2ShortRBTreeMap.Entry c;
         try {
            c = (Float2ShortRBTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Float, Short> e = (java.util.Map.Entry<Float, Short>)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getKey()) && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Float2ShortRBTreeMap.TreeIterator implements ObjectListIterator<Float2ShortMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(float k) {
         super(k);
      }

      public Float2ShortMap.Entry next() {
         return this.nextEntry();
      }

      public Float2ShortMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Float2ShortRBTreeMap.TreeIterator implements FloatListIterator {
      public KeyIterator() {
      }

      public KeyIterator(float k) {
         super(k);
      }

      @Override
      public float nextFloat() {
         return this.nextEntry().key;
      }

      @Override
      public float previousFloat() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractFloat2ShortSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return Float2ShortRBTreeMap.this.new KeyIterator();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return Float2ShortRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractFloat2ShortSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      float from;
      float to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Float2ShortMap.Entry> entries;
      protected transient FloatSortedSet keys;
      protected transient ShortCollection values;

      public Submap(float from, boolean bottom, float to, boolean top) {
         if (!bottom && !top && Float2ShortRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Float2ShortRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Float2ShortRBTreeMap.Submap.SubmapIterator i = new Float2ShortRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(float k) {
         return (this.bottom || Float2ShortRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Float2ShortRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Float2ShortMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator(Float2ShortMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getFloatKey());
               }

               @Override
               public Comparator<? super Float2ShortMap.Entry> comparator() {
                  return Float2ShortRBTreeMap.this.float2ShortEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Short) {
                        Float2ShortRBTreeMap.Entry f = Float2ShortRBTreeMap.this.findKey((Float)e.getKey());
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
                     if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Short) {
                        Float2ShortRBTreeMap.Entry f = Float2ShortRBTreeMap.this.findKey((Float)e.getKey());
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

               public Float2ShortMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Float2ShortMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Float2ShortMap.Entry> subSet(Float2ShortMap.Entry from, Float2ShortMap.Entry to) {
                  return Submap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2ShortEntrySet();
               }

               public ObjectSortedSet<Float2ShortMap.Entry> headSet(Float2ShortMap.Entry to) {
                  return Submap.this.headMap(to.getFloatKey()).float2ShortEntrySet();
               }

               public ObjectSortedSet<Float2ShortMap.Entry> tailSet(Float2ShortMap.Entry from) {
                  return Submap.this.tailMap(from.getFloatKey()).float2ShortEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Float2ShortRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ShortCollection values() {
         if (this.values == null) {
            this.values = new AbstractShortCollection() {
               @Override
               public ShortIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(short k) {
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
      public boolean containsKey(float k) {
         return this.in(k) && Float2ShortRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(short v) {
         Float2ShortRBTreeMap.Submap.SubmapIterator i = new Float2ShortRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            short ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public short get(float k) {
         Float2ShortRBTreeMap.Entry e;
         return this.in(k) && (e = Float2ShortRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public short put(float k, short v) {
         Float2ShortRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            short oldValue = Float2ShortRBTreeMap.this.put(k, v);
            return Float2ShortRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public short remove(float k) {
         Float2ShortRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            short oldValue = Float2ShortRBTreeMap.this.remove(k);
            return Float2ShortRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Float2ShortRBTreeMap.Submap.SubmapIterator i = new Float2ShortRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Float2ShortRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public FloatComparator comparator() {
         return Float2ShortRBTreeMap.this.actualComparator;
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         if (this.top) {
            return Float2ShortRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Float2ShortRBTreeMap.this.compare(to, this.to) < 0 ? Float2ShortRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         if (this.bottom) {
            return Float2ShortRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Float2ShortRBTreeMap.this.compare(from, this.from) > 0 ? Float2ShortRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         if (this.top && this.bottom) {
            return Float2ShortRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Float2ShortRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Float2ShortRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Float2ShortRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Float2ShortRBTreeMap.Entry firstEntry() {
         if (Float2ShortRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ShortRBTreeMap.Entry e;
            if (this.bottom) {
               e = Float2ShortRBTreeMap.this.firstEntry;
            } else {
               e = Float2ShortRBTreeMap.this.locateKey(this.from);
               if (Float2ShortRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Float2ShortRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Float2ShortRBTreeMap.Entry lastEntry() {
         if (Float2ShortRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ShortRBTreeMap.Entry e;
            if (this.top) {
               e = Float2ShortRBTreeMap.this.lastEntry;
            } else {
               e = Float2ShortRBTreeMap.this.locateKey(this.to);
               if (Float2ShortRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Float2ShortRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public float firstFloatKey() {
         Float2ShortRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public float lastFloatKey() {
         Float2ShortRBTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractFloat2ShortSortedMap.KeySet {
         private KeySet() {
         }

         @Override
         public FloatBidirectionalIterator iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public FloatBidirectionalIterator iterator(float from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Float2ShortRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Float2ShortMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(float k) {
            super(k);
         }

         public Float2ShortMap.Entry next() {
            return this.nextEntry();
         }

         public Float2ShortMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Float2ShortRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(float k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Float2ShortRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Float2ShortRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Float2ShortRBTreeMap.this.locateKey(k);
                  if (Float2ShortRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Float2ShortRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Float2ShortRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Float2ShortRBTreeMap.Submap.SubmapIterator implements FloatListIterator {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(float from) {
            super(from);
         }

         @Override
         public float nextFloat() {
            return this.nextEntry().key;
         }

         @Override
         public float previousFloat() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Float2ShortRBTreeMap.Submap.SubmapIterator implements ShortListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public short nextShort() {
            return this.nextEntry().value;
         }

         @Override
         public short previousShort() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Float2ShortRBTreeMap.Entry prev;
      Float2ShortRBTreeMap.Entry next;
      Float2ShortRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Float2ShortRBTreeMap.this.firstEntry;
      }

      TreeIterator(float k) {
         if ((this.next = Float2ShortRBTreeMap.this.locateKey(k)) != null) {
            if (Float2ShortRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Float2ShortRBTreeMap.Entry nextEntry() {
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

      Float2ShortRBTreeMap.Entry previousEntry() {
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
            Float2ShortRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Float2ShortRBTreeMap.TreeIterator implements ShortListIterator {
      private ValueIterator() {
      }

      @Override
      public short nextShort() {
         return this.nextEntry().value;
      }

      @Override
      public short previousShort() {
         return this.previousEntry().value;
      }
   }
}
