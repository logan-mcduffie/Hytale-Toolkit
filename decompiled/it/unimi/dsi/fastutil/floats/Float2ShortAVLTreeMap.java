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

public class Float2ShortAVLTreeMap extends AbstractFloat2ShortSortedMap implements Serializable, Cloneable {
   protected transient Float2ShortAVLTreeMap.Entry tree;
   protected int count;
   protected transient Float2ShortAVLTreeMap.Entry firstEntry;
   protected transient Float2ShortAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Float2ShortMap.Entry> entries;
   protected transient FloatSortedSet keys;
   protected transient ShortCollection values;
   protected transient boolean modified;
   protected Comparator<? super Float> storedComparator;
   protected transient FloatComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Float2ShortAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = FloatComparators.asFloatComparator(this.storedComparator);
   }

   public Float2ShortAVLTreeMap(Comparator<? super Float> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Float2ShortAVLTreeMap(Map<? extends Float, ? extends Short> m) {
      this();
      this.putAll(m);
   }

   public Float2ShortAVLTreeMap(SortedMap<Float, Short> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2ShortAVLTreeMap(Float2ShortMap m) {
      this();
      this.putAll(m);
   }

   public Float2ShortAVLTreeMap(Float2ShortSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Float2ShortAVLTreeMap(float[] k, short[] v, Comparator<? super Float> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2ShortAVLTreeMap(float[] k, short[] v) {
      this(k, v, null);
   }

   final int compare(float k1, float k2) {
      return this.actualComparator == null ? Float.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Float2ShortAVLTreeMap.Entry findKey(float k) {
      Float2ShortAVLTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Float2ShortAVLTreeMap.Entry locateKey(float k) {
      Float2ShortAVLTreeMap.Entry e = this.tree;
      Float2ShortAVLTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public short addTo(float k, short incr) {
      Float2ShortAVLTreeMap.Entry e = this.add(k);
      short oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public short put(float k, short v) {
      Float2ShortAVLTreeMap.Entry e = this.add(k);
      short oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Float2ShortAVLTreeMap.Entry add(float k) {
      this.modified = false;
      Float2ShortAVLTreeMap.Entry e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Float2ShortAVLTreeMap.Entry(k, this.defRetValue);
         this.modified = true;
      } else {
         Float2ShortAVLTreeMap.Entry p = this.tree;
         Float2ShortAVLTreeMap.Entry q = null;
         Float2ShortAVLTreeMap.Entry y = this.tree;
         Float2ShortAVLTreeMap.Entry z = null;
         Float2ShortAVLTreeMap.Entry w = null;
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
                  e = new Float2ShortAVLTreeMap.Entry(k, this.defRetValue);
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
                  e = new Float2ShortAVLTreeMap.Entry(k, this.defRetValue);
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
            Float2ShortAVLTreeMap.Entry x = y.left;
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

            Float2ShortAVLTreeMap.Entry x = y.right;
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

   private Float2ShortAVLTreeMap.Entry parent(Float2ShortAVLTreeMap.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         Float2ShortAVLTreeMap.Entry y = e;

         Float2ShortAVLTreeMap.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Float2ShortAVLTreeMap.Entry p = x.left;
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

         Float2ShortAVLTreeMap.Entry p = y.right;
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
   public short remove(float k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Float2ShortAVLTreeMap.Entry p = this.tree;
         Float2ShortAVLTreeMap.Entry q = null;
         boolean dir = false;
         float kk = k;

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
            Float2ShortAVLTreeMap.Entry r = p.right;
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
                  Float2ShortAVLTreeMap.Entry s = r.left;
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
            Float2ShortAVLTreeMap.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Float2ShortAVLTreeMap.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Float2ShortAVLTreeMap.Entry w = x.left;
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
                  Float2ShortAVLTreeMap.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Float2ShortAVLTreeMap.Entry wx = xx.right;
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
   public boolean containsValue(short v) {
      Float2ShortAVLTreeMap.ValueIterator i = new Float2ShortAVLTreeMap.ValueIterator();
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
      Float2ShortAVLTreeMap.Entry e = this.findKey(k);
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
            final Comparator<? super Float2ShortMap.Entry> comparator = Float2ShortAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Float.compare(x.getFloatKey(), y.getFloatKey())
               : (x, y) -> Float2ShortAVLTreeMap.this.actualComparator.compare(x.getFloatKey(), y.getFloatKey());

            @Override
            public Comparator<? super Float2ShortMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator() {
               return Float2ShortAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator(Float2ShortMap.Entry from) {
               return Float2ShortAVLTreeMap.this.new EntryIterator(from.getFloatKey());
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
                     Float2ShortAVLTreeMap.Entry f = Float2ShortAVLTreeMap.this.findKey((Float)e.getKey());
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
                  } else if (e.getKey() == null || !(e.getKey() instanceof Float)) {
                     return false;
                  } else if (!(e.getValue() instanceof Short)) {
                     return false;
                  } else {
                     Float2ShortAVLTreeMap.Entry f = Float2ShortAVLTreeMap.this.findKey((Float)e.getKey());
                     if (f != null && f.getShortValue() == (Short)e.getValue()) {
                        Float2ShortAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Float2ShortAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2ShortAVLTreeMap.this.clear();
            }

            public Float2ShortMap.Entry first() {
               return Float2ShortAVLTreeMap.this.firstEntry;
            }

            public Float2ShortMap.Entry last() {
               return Float2ShortAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Float2ShortMap.Entry> subSet(Float2ShortMap.Entry from, Float2ShortMap.Entry to) {
               return Float2ShortAVLTreeMap.this.subMap(from.getFloatKey(), to.getFloatKey()).float2ShortEntrySet();
            }

            public ObjectSortedSet<Float2ShortMap.Entry> headSet(Float2ShortMap.Entry to) {
               return Float2ShortAVLTreeMap.this.headMap(to.getFloatKey()).float2ShortEntrySet();
            }

            public ObjectSortedSet<Float2ShortMap.Entry> tailSet(Float2ShortMap.Entry from) {
               return Float2ShortAVLTreeMap.this.tailMap(from.getFloatKey()).float2ShortEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ShortAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ShortCollection values() {
      if (this.values == null) {
         this.values = new AbstractShortCollection() {
            @Override
            public ShortIterator iterator() {
               return Float2ShortAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(short k) {
               return Float2ShortAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Float2ShortAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Float2ShortAVLTreeMap.this.clear();
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
      return new Float2ShortAVLTreeMap.Submap(0.0F, true, to, false);
   }

   @Override
   public Float2ShortSortedMap tailMap(float from) {
      return new Float2ShortAVLTreeMap.Submap(from, false, 0.0F, true);
   }

   @Override
   public Float2ShortSortedMap subMap(float from, float to) {
      return new Float2ShortAVLTreeMap.Submap(from, false, to, false);
   }

   public Float2ShortAVLTreeMap clone() {
      Float2ShortAVLTreeMap c;
      try {
         c = (Float2ShortAVLTreeMap)super.clone();
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
         Float2ShortAVLTreeMap.Entry rp = new Float2ShortAVLTreeMap.Entry();
         Float2ShortAVLTreeMap.Entry rq = new Float2ShortAVLTreeMap.Entry();
         Float2ShortAVLTreeMap.Entry p = rp;
         rp.left(this.tree);
         Float2ShortAVLTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Float2ShortAVLTreeMap.Entry e = p.left.clone();
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
               Float2ShortAVLTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Float2ShortAVLTreeMap.EntryIterator i = new Float2ShortAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Float2ShortAVLTreeMap.Entry e = i.nextEntry();
         s.writeFloat(e.key);
         s.writeShort(e.value);
      }
   }

   private Float2ShortAVLTreeMap.Entry readTree(ObjectInputStream s, int n, Float2ShortAVLTreeMap.Entry pred, Float2ShortAVLTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Float2ShortAVLTreeMap.Entry top = new Float2ShortAVLTreeMap.Entry(s.readFloat(), s.readShort());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Float2ShortAVLTreeMap.Entry top = new Float2ShortAVLTreeMap.Entry(s.readFloat(), s.readShort());
         top.right(new Float2ShortAVLTreeMap.Entry(s.readFloat(), s.readShort()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Float2ShortAVLTreeMap.Entry top = new Float2ShortAVLTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readFloat();
         top.value = s.readShort();
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
         Float2ShortAVLTreeMap.Entry e = this.tree;

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
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Float2ShortAVLTreeMap.Entry left;
      Float2ShortAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0.0F, (short)0);
      }

      Entry(float k, short v) {
         super(k, v);
         this.info = -1073741824;
      }

      Float2ShortAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Float2ShortAVLTreeMap.Entry right() {
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

      void pred(Float2ShortAVLTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Float2ShortAVLTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Float2ShortAVLTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Float2ShortAVLTreeMap.Entry right) {
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

      Float2ShortAVLTreeMap.Entry next() {
         Float2ShortAVLTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Float2ShortAVLTreeMap.Entry prev() {
         Float2ShortAVLTreeMap.Entry prev = this.left;
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

      public Float2ShortAVLTreeMap.Entry clone() {
         Float2ShortAVLTreeMap.Entry c;
         try {
            c = (Float2ShortAVLTreeMap.Entry)super.clone();
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

   private class EntryIterator extends Float2ShortAVLTreeMap.TreeIterator implements ObjectListIterator<Float2ShortMap.Entry> {
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

      public void set(Float2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Float2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Float2ShortAVLTreeMap.TreeIterator implements FloatListIterator {
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
         return Float2ShortAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return Float2ShortAVLTreeMap.this.new KeyIterator(from);
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
         if (!bottom && !top && Float2ShortAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Float2ShortAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Float2ShortAVLTreeMap.Submap.SubmapIterator i = new Float2ShortAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(float k) {
         return (this.bottom || Float2ShortAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Float2ShortAVLTreeMap.this.compare(k, this.to) < 0);
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
                  return Float2ShortAVLTreeMap.this.float2ShortEntrySet().comparator();
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
                        Float2ShortAVLTreeMap.Entry f = Float2ShortAVLTreeMap.this.findKey((Float)e.getKey());
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
                        Float2ShortAVLTreeMap.Entry f = Float2ShortAVLTreeMap.this.findKey((Float)e.getKey());
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
            this.keys = new Float2ShortAVLTreeMap.Submap.KeySet();
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
         return this.in(k) && Float2ShortAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(short v) {
         Float2ShortAVLTreeMap.Submap.SubmapIterator i = new Float2ShortAVLTreeMap.Submap.SubmapIterator();

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
         Float2ShortAVLTreeMap.Entry e;
         return this.in(k) && (e = Float2ShortAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public short put(float k, short v) {
         Float2ShortAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            short oldValue = Float2ShortAVLTreeMap.this.put(k, v);
            return Float2ShortAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public short remove(float k) {
         Float2ShortAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            short oldValue = Float2ShortAVLTreeMap.this.remove(k);
            return Float2ShortAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Float2ShortAVLTreeMap.Submap.SubmapIterator i = new Float2ShortAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Float2ShortAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public FloatComparator comparator() {
         return Float2ShortAVLTreeMap.this.actualComparator;
      }

      @Override
      public Float2ShortSortedMap headMap(float to) {
         if (this.top) {
            return Float2ShortAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Float2ShortAVLTreeMap.this.compare(to, this.to) < 0 ? Float2ShortAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Float2ShortSortedMap tailMap(float from) {
         if (this.bottom) {
            return Float2ShortAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Float2ShortAVLTreeMap.this.compare(from, this.from) > 0 ? Float2ShortAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Float2ShortSortedMap subMap(float from, float to) {
         if (this.top && this.bottom) {
            return Float2ShortAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Float2ShortAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Float2ShortAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Float2ShortAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Float2ShortAVLTreeMap.Entry firstEntry() {
         if (Float2ShortAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ShortAVLTreeMap.Entry e;
            if (this.bottom) {
               e = Float2ShortAVLTreeMap.this.firstEntry;
            } else {
               e = Float2ShortAVLTreeMap.this.locateKey(this.from);
               if (Float2ShortAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Float2ShortAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Float2ShortAVLTreeMap.Entry lastEntry() {
         if (Float2ShortAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Float2ShortAVLTreeMap.Entry e;
            if (this.top) {
               e = Float2ShortAVLTreeMap.this.lastEntry;
            } else {
               e = Float2ShortAVLTreeMap.this.locateKey(this.to);
               if (Float2ShortAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Float2ShortAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public float firstFloatKey() {
         Float2ShortAVLTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public float lastFloatKey() {
         Float2ShortAVLTreeMap.Entry e = this.lastEntry();
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

      private class SubmapEntryIterator extends Float2ShortAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Float2ShortMap.Entry> {
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

      private class SubmapIterator extends Float2ShortAVLTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(float k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Float2ShortAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Float2ShortAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Float2ShortAVLTreeMap.this.locateKey(k);
                  if (Float2ShortAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Float2ShortAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Float2ShortAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Float2ShortAVLTreeMap.Submap.SubmapIterator implements FloatListIterator {
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

      private final class SubmapValueIterator extends Float2ShortAVLTreeMap.Submap.SubmapIterator implements ShortListIterator {
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
      Float2ShortAVLTreeMap.Entry prev;
      Float2ShortAVLTreeMap.Entry next;
      Float2ShortAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Float2ShortAVLTreeMap.this.firstEntry;
      }

      TreeIterator(float k) {
         if ((this.next = Float2ShortAVLTreeMap.this.locateKey(k)) != null) {
            if (Float2ShortAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Float2ShortAVLTreeMap.Entry nextEntry() {
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

      Float2ShortAVLTreeMap.Entry previousEntry() {
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
            Float2ShortAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Float2ShortAVLTreeMap.TreeIterator implements ShortListIterator {
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
