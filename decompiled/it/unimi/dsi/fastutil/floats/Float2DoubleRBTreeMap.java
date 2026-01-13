package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
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

public class Float2DoubleRBTreeMap extends AbstractFloat2DoubleSortedMap implements Serializable, Cloneable {
   protected transient Float2DoubleRBTreeMap.Entry tree;
   protected int count;
   protected transient Float2DoubleRBTreeMap.Entry firstEntry;
   protected transient Float2DoubleRBTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Float2DoubleMap.Entry> entries;
   protected transient FloatSortedSet keys;
   protected transient DoubleCollection values;
   protected transient boolean modified;
   protected Comparator<? super Float> storedComparator;
   protected transient FloatComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;
   private transient Float2DoubleRBTreeMap.Entry[] nodePath;

   public Float2DoubleRBTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = FloatComparators.asFloatComparator(this.storedComparator);
   }

   public Float2DoubleRBTreeMap(Comparator<? super Float> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Float2DoubleRBTreeMap(Map<? extends Float, ? extends Double> m) {
      this();
      this.putAll(m);
   }

   public Float2DoubleRBTreeMap(SortedMap<Float, Double> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2DoubleRBTreeMap(Float2DoubleMap m) {
      this();
      this.putAll(m);
   }

   public Float2DoubleRBTreeMap(Float2DoubleSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2DoubleRBTreeMap(float[] k, double[] v, Comparator<? super Float> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2DoubleRBTreeMap(float[] k, double[] v) {
      this(k, v, null);
   }

   final int compare(float k1, float k2) {
      return this.actualComparator == null ? Float.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Float2DoubleRBTreeMap.Entry findKey(float k) {
      Float2DoubleRBTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Float2DoubleRBTreeMap.Entry locateKey(float k) {
      Float2DoubleRBTreeMap.Entry e = this.tree;
      Float2DoubleRBTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[64];
      this.nodePath = new Float2DoubleRBTreeMap.Entry[64];
   }

   public double addTo(float k, double incr) {
      Float2DoubleRBTreeMap.Entry e = this.add(k);
      double oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public double put(float k, double v) {
      Float2DoubleRBTreeMap.Entry e = this.add(k);
      double oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Float2DoubleRBTreeMap.Entry add(float k) {
      this.modified = false;
      int maxDepth = 0;
      Float2DoubleRBTreeMap.Entry e;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Float2DoubleRBTreeMap.Entry(k, this.defRetValue);
      } else {
         Float2DoubleRBTreeMap.Entry p = this.tree;
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
               e = new Float2DoubleRBTreeMap.Entry(k, this.defRetValue);
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
               e = new Float2DoubleRBTreeMap.Entry(k, this.defRetValue);
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
                  Float2DoubleRBTreeMap.Entry y = this.nodePath[i - 1].right;
                  if (this.nodePath[i - 1].succ() || y.black()) {
                     if (!this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Float2DoubleRBTreeMap.Entry x = this.nodePath[i];
                        y = x.right;
                        x.right = y.left;
                        y.left = x;
                        this.nodePath[i - 1].left = y;
                        if (y.pred()) {
                           y.pred(false);
                           x.succ(y);
                        }
                     }

                     Float2DoubleRBTreeMap.Entry xx = this.nodePath[i - 1];
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
                  Float2DoubleRBTreeMap.Entry y = this.nodePath[i - 1].left;
                  if (this.nodePath[i - 1].pred() || y.black()) {
                     if (this.dirPath[i]) {
                        y = this.nodePath[i];
                     } else {
                        Float2DoubleRBTreeMap.Entry x = this.nodePath[i];
                        y = x.left;
                        x.left = y.right;
                        y.right = x;
                        this.nodePath[i - 1].right = y;
                        if (y.succ()) {
                           y.succ(false);
                           x.pred(y);
                        }
                     }

                     Float2DoubleRBTreeMap.Entry xxx = this.nodePath[i - 1];
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
   public double remove(float k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Float2DoubleRBTreeMap.Entry p = this.tree;
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
            Float2DoubleRBTreeMap.Entry r = p.right;
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
                  Float2DoubleRBTreeMap.Entry s = r.left;
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
                  Float2DoubleRBTreeMap.Entry x = this.dirPath[i - 1] ? this.nodePath[i - 1].right : this.nodePath[i - 1].left;
                  if (!x.black()) {
                     x.black(true);
                     break;
                  }
               }

               if (!this.dirPath[i - 1]) {
                  Float2DoubleRBTreeMap.Entry w = this.nodePath[i - 1].right;
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
                        Float2DoubleRBTreeMap.Entry y = w.left;
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
                  Float2DoubleRBTreeMap.Entry wx = this.nodePath[i - 1].left;
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
                        Float2DoubleRBTreeMap.Entry y = wx.right;
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
   public boolean containsValue(double v) {
      Float2DoubleRBTreeMap.ValueIterator i = new Float2DoubleRBTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         double ev = i.nextDouble();
         if (Double.doubleToLongBits(ev) == Double.doubleToLongBits(v)) {
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
   public double get(float k) {
      Float2DoubleRBTreeMap.Entry e = this.findKey(k);
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
   public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Float2DoubleMap.Entry>() {
            final Comparator<? super Float2DoubleMap.Entry> comparator = Float2DoubleRBTreeMap.this.actualComparator == null
               ? (x, y) -> Float.compare(x.getFloatKey(), y.getFloatKey())
               : (x, y) -> Float2DoubleRBTreeMap.this.actualComparator.compare(x.getFloatKey(), y.getFloatKey());

            @Override
            public Comparator<? super Float2DoubleMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Float2DoubleMap.Entry> iterator() {
               return Float2DoubleRBTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Float2DoubleMap.Entry> iterator(Float2DoubleMap.Entry from) {
               return Float2DoubleRBTreeMap.this.new EntryIterator(from.getFloatKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Float)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Double) {
                     Float2DoubleRBTreeMap.Entry f = Float2DoubleRBTreeMap.this.findKey((Float)e.getKey());
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
                  } else if (e.getValue() != null && e.getValue() instanceof Double) {
                     Float2DoubleRBTreeMap.Entry f = Float2DoubleRBTreeMap.this.findKey((Float)e.getKey());
                     if (f != null && Double.doubleToLongBits(f.getDoubleValue()) == Double.doubleToLongBits((Double)e.getValue())) {
                        Float2DoubleRBTreeMap.this.remove(f.key);
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
               return Float2DoubleRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2DoubleRBTreeMap.this.clear();
            }

            public Float2DoubleMap.Entry first() {
               return Float2DoubleRBTreeMap.this.firstEntry;
            }

            public Float2DoubleMap.Entry last() {
               return Float2DoubleRBTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Float2DoubleMap.Entry> subSet(Float2DoubleMap.Entry from, Float2DoubleMap.Entry to) {
               return Float2DoubleRBTreeMap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2DoubleEntrySet();
            }

            public ObjectSortedSet<Float2DoubleMap.Entry> headSet(Float2DoubleMap.Entry to) {
               return Float2DoubleRBTreeMap.this.headMap(to.getFloatKey()).float2DoubleEntrySet();
            }

            public ObjectSortedSet<Float2DoubleMap.Entry> tailSet(Float2DoubleMap.Entry from) {
               return Float2DoubleRBTreeMap.this.tailMap(from.getFloatKey()).float2DoubleEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2DoubleRBTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            @Override
            public DoubleIterator iterator() {
               return Float2DoubleRBTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(double k) {
               return Float2DoubleRBTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Float2DoubleRBTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2DoubleRBTreeMap.this.clear();
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
   public Float2DoubleSortedMap headMap(float to) {
      return new Float2DoubleRBTreeMap.Submap(0.0F, true, to, false);
   }

   @Override
   public Float2DoubleSortedMap tailMap(float from) {
      return new Float2DoubleRBTreeMap.Submap(from, false, 0.0F, true);
   }

   @Override
   public Float2DoubleSortedMap subMap(float from, float to) {
      return new Float2DoubleRBTreeMap.Submap(from, false, to, false);
   }

   public Float2DoubleRBTreeMap clone() {
      Float2DoubleRBTreeMap c;
      try {
         c = (Float2DoubleRBTreeMap)super.clone();
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
         Float2DoubleRBTreeMap.Entry rp = new Float2DoubleRBTreeMap.Entry();
         Float2DoubleRBTreeMap.Entry rq = new Float2DoubleRBTreeMap.Entry();
         Float2DoubleRBTreeMap.Entry p = rp;
         rp.left(this.tree);
         Float2DoubleRBTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Float2DoubleRBTreeMap.Entry e = p.left.clone();
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
               Float2DoubleRBTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Float2DoubleRBTreeMap.EntryIterator i = new Float2DoubleRBTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Float2DoubleRBTreeMap.Entry e = i.nextEntry();
         s.writeFloat(e.key);
         s.writeDouble(e.value);
      }
   }

   private Float2DoubleRBTreeMap.Entry readTree(ObjectInputStream s, int n, Float2DoubleRBTreeMap.Entry pred, Float2DoubleRBTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Float2DoubleRBTreeMap.Entry top = new Float2DoubleRBTreeMap.Entry(s.readFloat(), s.readDouble());
         top.pred(pred);
         top.succ(succ);
         top.black(true);
         return top;
      } else if (n == 2) {
         Float2DoubleRBTreeMap.Entry top = new Float2DoubleRBTreeMap.Entry(s.readFloat(), s.readDouble());
         top.black(true);
         top.right(new Float2DoubleRBTreeMap.Entry(s.readFloat(), s.readDouble()));
         top.right.pred(top);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Float2DoubleRBTreeMap.Entry top = new Float2DoubleRBTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readFloat();
         top.value = s.readDouble();
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
         Float2DoubleRBTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractFloat2DoubleMap.BasicEntry implements Cloneable {
      private static final int BLACK_MASK = 1;
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      Float2DoubleRBTreeMap.Entry left;
      Float2DoubleRBTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0F, 0.0);
      }

      Entry(float k, double v) {
         super(k, v);
         this.info = -1073741824;
      }

      Float2DoubleRBTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Float2DoubleRBTreeMap.Entry right() {
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

      void pred(Float2DoubleRBTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Float2DoubleRBTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Float2DoubleRBTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Float2DoubleRBTreeMap.Entry right) {
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

      Float2DoubleRBTreeMap.Entry next() {
         Float2DoubleRBTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Float2DoubleRBTreeMap.Entry prev() {
         Float2DoubleRBTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public double setValue(double value) {
         double oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Float2DoubleRBTreeMap.Entry clone() {
         Float2DoubleRBTreeMap.Entry c;
         try {
            c = (Float2DoubleRBTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Float, Double> e = (java.util.Map.Entry<Float, Double>)o;
            return Float.floatToIntBits(this.key) == Float.floatToIntBits(e.getKey())
               && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Float2DoubleRBTreeMap.TreeIterator implements ObjectListIterator<Float2DoubleMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(float k) {
         super(k);
      }

      public Float2DoubleMap.Entry next() {
         return this.nextEntry();
      }

      public Float2DoubleMap.Entry previous() {
         return this.previousEntry();
      }
   }

   private final class KeyIterator extends Float2DoubleRBTreeMap.TreeIterator implements FloatListIterator {
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

   private class KeySet extends AbstractFloat2DoubleSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return Float2DoubleRBTreeMap.this.new KeyIterator();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return Float2DoubleRBTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractFloat2DoubleSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      float from;
      float to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Float2DoubleMap.Entry> entries;
      protected transient FloatSortedSet keys;
      protected transient DoubleCollection values;

      public Submap(float from, boolean bottom, float to, boolean top) {
         if (!bottom && !top && Float2DoubleRBTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Float2DoubleRBTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Float2DoubleRBTreeMap.Submap.SubmapIterator i = new Float2DoubleRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(float k) {
         return (this.bottom || Float2DoubleRBTreeMap.this.compare(k, this.from) >= 0) && (this.top || Float2DoubleRBTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Float2DoubleMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Float2DoubleMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Float2DoubleMap.Entry> iterator(Float2DoubleMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getFloatKey());
               }

               @Override
               public Comparator<? super Float2DoubleMap.Entry> comparator() {
                  return Float2DoubleRBTreeMap.this.float2DoubleEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Double) {
                        Float2DoubleRBTreeMap.Entry f = Float2DoubleRBTreeMap.this.findKey((Float)e.getKey());
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
                     } else if (e.getValue() != null && e.getValue() instanceof Double) {
                        Float2DoubleRBTreeMap.Entry f = Float2DoubleRBTreeMap.this.findKey((Float)e.getKey());
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

               public Float2DoubleMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Float2DoubleMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Float2DoubleMap.Entry> subSet(Float2DoubleMap.Entry from, Float2DoubleMap.Entry to) {
                  return Submap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2DoubleEntrySet();
               }

               public ObjectSortedSet<Float2DoubleMap.Entry> headSet(Float2DoubleMap.Entry to) {
                  return Submap.this.headMap(to.getFloatKey()).float2DoubleEntrySet();
               }

               public ObjectSortedSet<Float2DoubleMap.Entry> tailSet(Float2DoubleMap.Entry from) {
                  return Submap.this.tailMap(from.getFloatKey()).float2DoubleEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Float2DoubleRBTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public DoubleCollection values() {
         if (this.values == null) {
            this.values = new AbstractDoubleCollection() {
               @Override
               public DoubleIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(double k) {
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
         return this.in(k) && Float2DoubleRBTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(double v) {
         Float2DoubleRBTreeMap.Submap.SubmapIterator i = new Float2DoubleRBTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            double ev = i.nextEntry().value;
            if (Double.doubleToLongBits(ev) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public double get(float k) {
         Float2DoubleRBTreeMap.Entry e;
         return this.in(k) && (e = Float2DoubleRBTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public double put(float k, double v) {
         Float2DoubleRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            double oldValue = Float2DoubleRBTreeMap.this.put(k, v);
            return Float2DoubleRBTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public double remove(float k) {
         Float2DoubleRBTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            double oldValue = Float2DoubleRBTreeMap.this.remove(k);
            return Float2DoubleRBTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Float2DoubleRBTreeMap.Submap.SubmapIterator i = new Float2DoubleRBTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Float2DoubleRBTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public FloatComparator comparator() {
         return Float2DoubleRBTreeMap.this.actualComparator;
      }

      @Override
      public Float2DoubleSortedMap headMap(float to) {
         if (this.top) {
            return Float2DoubleRBTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Float2DoubleRBTreeMap.this.compare(to, this.to) < 0 ? Float2DoubleRBTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Float2DoubleSortedMap tailMap(float from) {
         if (this.bottom) {
            return Float2DoubleRBTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Float2DoubleRBTreeMap.this.compare(from, this.from) > 0 ? Float2DoubleRBTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Float2DoubleSortedMap subMap(float from, float to) {
         if (this.top && this.bottom) {
            return Float2DoubleRBTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Float2DoubleRBTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Float2DoubleRBTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Float2DoubleRBTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Float2DoubleRBTreeMap.Entry firstEntry() {
         if (Float2DoubleRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2DoubleRBTreeMap.Entry e;
            if (this.bottom) {
               e = Float2DoubleRBTreeMap.this.firstEntry;
            } else {
               e = Float2DoubleRBTreeMap.this.locateKey(this.from);
               if (Float2DoubleRBTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Float2DoubleRBTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Float2DoubleRBTreeMap.Entry lastEntry() {
         if (Float2DoubleRBTreeMap.this.tree == null) {
            return null;
         } else {
            Float2DoubleRBTreeMap.Entry e;
            if (this.top) {
               e = Float2DoubleRBTreeMap.this.lastEntry;
            } else {
               e = Float2DoubleRBTreeMap.this.locateKey(this.to);
               if (Float2DoubleRBTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Float2DoubleRBTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public float firstFloatKey() {
         Float2DoubleRBTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public float lastFloatKey() {
         Float2DoubleRBTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractFloat2DoubleSortedMap.KeySet {
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

      private class SubmapEntryIterator extends Float2DoubleRBTreeMap.Submap.SubmapIterator implements ObjectListIterator<Float2DoubleMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(float k) {
            super(k);
         }

         public Float2DoubleMap.Entry next() {
            return this.nextEntry();
         }

         public Float2DoubleMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Float2DoubleRBTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(float k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Float2DoubleRBTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Float2DoubleRBTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Float2DoubleRBTreeMap.this.locateKey(k);
                  if (Float2DoubleRBTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Float2DoubleRBTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Float2DoubleRBTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Float2DoubleRBTreeMap.Submap.SubmapIterator implements FloatListIterator {
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

      private final class SubmapValueIterator extends Float2DoubleRBTreeMap.Submap.SubmapIterator implements DoubleListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public double nextDouble() {
            return this.nextEntry().value;
         }

         @Override
         public double previousDouble() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Float2DoubleRBTreeMap.Entry prev;
      Float2DoubleRBTreeMap.Entry next;
      Float2DoubleRBTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Float2DoubleRBTreeMap.this.firstEntry;
      }

      TreeIterator(float k) {
         if ((this.next = Float2DoubleRBTreeMap.this.locateKey(k)) != null) {
            if (Float2DoubleRBTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Float2DoubleRBTreeMap.Entry nextEntry() {
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

      Float2DoubleRBTreeMap.Entry previousEntry() {
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
            Float2DoubleRBTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Float2DoubleRBTreeMap.TreeIterator implements DoubleListIterator {
      private ValueIterator() {
      }

      @Override
      public double nextDouble() {
         return this.nextEntry().value;
      }

      @Override
      public double previousDouble() {
         return this.previousEntry().value;
      }
   }
}
