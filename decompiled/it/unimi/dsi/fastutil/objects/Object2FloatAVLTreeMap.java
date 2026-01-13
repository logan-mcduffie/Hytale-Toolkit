package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

public class Object2FloatAVLTreeMap<K> extends AbstractObject2FloatSortedMap<K> implements Serializable, Cloneable {
   protected transient Object2FloatAVLTreeMap.Entry<K> tree;
   protected int count;
   protected transient Object2FloatAVLTreeMap.Entry<K> firstEntry;
   protected transient Object2FloatAVLTreeMap.Entry<K> lastEntry;
   protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient FloatCollection values;
   protected transient boolean modified;
   protected Comparator<? super K> storedComparator;
   protected transient Comparator<? super K> actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Object2FloatAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = this.storedComparator;
   }

   public Object2FloatAVLTreeMap(Comparator<? super K> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Object2FloatAVLTreeMap(Map<? extends K, ? extends Float> m) {
      this();
      this.putAll(m);
   }

   public Object2FloatAVLTreeMap(SortedMap<K, Float> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2FloatAVLTreeMap(Object2FloatMap<? extends K> m) {
      this();
      this.putAll(m);
   }

   public Object2FloatAVLTreeMap(Object2FloatSortedMap<K> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2FloatAVLTreeMap(K[] k, float[] v, Comparator<? super K> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Object2FloatAVLTreeMap(K[] k, float[] v) {
      this(k, v, null);
   }

   final int compare(K k1, K k2) {
      return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
   }

   final Object2FloatAVLTreeMap.Entry<K> findKey(K k) {
      Object2FloatAVLTreeMap.Entry<K> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Object2FloatAVLTreeMap.Entry<K> locateKey(K k) {
      Object2FloatAVLTreeMap.Entry<K> e = this.tree;
      Object2FloatAVLTreeMap.Entry<K> last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public float addTo(K k, float incr) {
      Object2FloatAVLTreeMap.Entry<K> e = this.add(k);
      float oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public float put(K k, float v) {
      Object2FloatAVLTreeMap.Entry<K> e = this.add(k);
      float oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Object2FloatAVLTreeMap.Entry<K> add(K k) {
      Objects.requireNonNull(k);
      this.modified = false;
      Object2FloatAVLTreeMap.Entry<K> e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Object2FloatAVLTreeMap.Entry<>(k, this.defRetValue);
         this.modified = true;
      } else {
         Object2FloatAVLTreeMap.Entry<K> p = this.tree;
         Object2FloatAVLTreeMap.Entry<K> q = null;
         Object2FloatAVLTreeMap.Entry<K> y = this.tree;
         Object2FloatAVLTreeMap.Entry<K> z = null;
         Object2FloatAVLTreeMap.Entry<K> w = null;
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
                  e = new Object2FloatAVLTreeMap.Entry<>(k, this.defRetValue);
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
                  e = new Object2FloatAVLTreeMap.Entry<>(k, this.defRetValue);
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
            Object2FloatAVLTreeMap.Entry<K> x = y.left;
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

            Object2FloatAVLTreeMap.Entry<K> x = y.right;
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

   private Object2FloatAVLTreeMap.Entry<K> parent(Object2FloatAVLTreeMap.Entry<K> e) {
      if (e == this.tree) {
         return null;
      } else {
         Object2FloatAVLTreeMap.Entry<K> y = e;

         Object2FloatAVLTreeMap.Entry<K> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Object2FloatAVLTreeMap.Entry<K> p = x.left;
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

         Object2FloatAVLTreeMap.Entry<K> p = y.right;
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
   public float removeFloat(Object k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Object2FloatAVLTreeMap.Entry<K> p = this.tree;
         Object2FloatAVLTreeMap.Entry<K> q = null;
         boolean dir = false;
         K kk = (K)k;

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
            Object2FloatAVLTreeMap.Entry<K> r = p.right;
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
                  Object2FloatAVLTreeMap.Entry<K> s = r.left;
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
            Object2FloatAVLTreeMap.Entry<K> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Object2FloatAVLTreeMap.Entry<K> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Object2FloatAVLTreeMap.Entry<K> w = x.left;
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
                  Object2FloatAVLTreeMap.Entry<K> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Object2FloatAVLTreeMap.Entry<K> wx = xx.right;
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
      Object2FloatAVLTreeMap<K>.ValueIterator i = new Object2FloatAVLTreeMap.ValueIterator();
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
   public boolean containsKey(Object k) {
      return k == null ? false : this.findKey((K)k) != null;
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
   public float getFloat(Object k) {
      Object2FloatAVLTreeMap.Entry<K> e = this.findKey((K)k);
      return e == null ? this.defRetValue : e.value;
   }

   @Override
   public K firstKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.firstEntry.key;
      }
   }

   @Override
   public K lastKey() {
      if (this.tree == null) {
         throw new NoSuchElementException();
      } else {
         return this.lastEntry.key;
      }
   }

   @Override
   public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>() {
            final Comparator<? super Object2FloatMap.Entry<K>> comparator = Object2FloatAVLTreeMap.this.actualComparator == null
               ? (x, y) -> ((Comparable)x.getKey()).compareTo(y.getKey())
               : (x, y) -> Object2FloatAVLTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());

            @Override
            public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
               return Object2FloatAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(Object2FloatMap.Entry<K> from) {
               return Object2FloatAVLTreeMap.this.new EntryIterator(from.getKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Float) {
                     Object2FloatAVLTreeMap.Entry<K> f = (Object2FloatAVLTreeMap.Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
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
                  } else if (!(e.getValue() instanceof Float)) {
                     return false;
                  } else {
                     Object2FloatAVLTreeMap.Entry<K> f = (Object2FloatAVLTreeMap.Entry<K>)Object2FloatAVLTreeMap.this.findKey(e.getKey());
                     if (f != null && Float.floatToIntBits(f.getFloatValue()) == Float.floatToIntBits((Float)e.getValue())) {
                        Object2FloatAVLTreeMap.this.removeFloat(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Object2FloatAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2FloatAVLTreeMap.this.clear();
            }

            public Object2FloatMap.Entry<K> first() {
               return Object2FloatAVLTreeMap.this.firstEntry;
            }

            public Object2FloatMap.Entry<K> last() {
               return Object2FloatAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(Object2FloatMap.Entry<K> from, Object2FloatMap.Entry<K> to) {
               return Object2FloatAVLTreeMap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
            }

            public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(Object2FloatMap.Entry<K> to) {
               return Object2FloatAVLTreeMap.this.headMap(to.getKey()).object2FloatEntrySet();
            }

            public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(Object2FloatMap.Entry<K> from) {
               return Object2FloatAVLTreeMap.this.tailMap(from.getKey()).object2FloatEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2FloatAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            @Override
            public FloatIterator iterator() {
               return Object2FloatAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(float k) {
               return Object2FloatAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Object2FloatAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2FloatAVLTreeMap.this.clear();
            }
         };
      }

      return this.values;
   }

   @Override
   public Comparator<? super K> comparator() {
      return this.actualComparator;
   }

   @Override
   public Object2FloatSortedMap<K> headMap(K to) {
      return new Object2FloatAVLTreeMap.Submap(null, true, to, false);
   }

   @Override
   public Object2FloatSortedMap<K> tailMap(K from) {
      return new Object2FloatAVLTreeMap.Submap(from, false, null, true);
   }

   @Override
   public Object2FloatSortedMap<K> subMap(K from, K to) {
      return new Object2FloatAVLTreeMap.Submap(from, false, to, false);
   }

   public Object2FloatAVLTreeMap<K> clone() {
      Object2FloatAVLTreeMap<K> c;
      try {
         c = (Object2FloatAVLTreeMap<K>)super.clone();
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
         Object2FloatAVLTreeMap.Entry<K> rp = new Object2FloatAVLTreeMap.Entry<>();
         Object2FloatAVLTreeMap.Entry<K> rq = new Object2FloatAVLTreeMap.Entry<>();
         Object2FloatAVLTreeMap.Entry<K> p = rp;
         rp.left(this.tree);
         Object2FloatAVLTreeMap.Entry<K> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Object2FloatAVLTreeMap.Entry<K> e = p.left.clone();
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
               Object2FloatAVLTreeMap.Entry<K> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Object2FloatAVLTreeMap<K>.EntryIterator i = new Object2FloatAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Object2FloatAVLTreeMap.Entry<K> e = i.nextEntry();
         s.writeObject(e.key);
         s.writeFloat(e.value);
      }
   }

   private Object2FloatAVLTreeMap.Entry<K> readTree(ObjectInputStream s, int n, Object2FloatAVLTreeMap.Entry<K> pred, Object2FloatAVLTreeMap.Entry<K> succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Object2FloatAVLTreeMap.Entry<K> top = new Object2FloatAVLTreeMap.Entry<>((K)s.readObject(), s.readFloat());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Object2FloatAVLTreeMap.Entry<K> top = new Object2FloatAVLTreeMap.Entry<>((K)s.readObject(), s.readFloat());
         top.right(new Object2FloatAVLTreeMap.Entry<>((K)s.readObject(), s.readFloat()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Object2FloatAVLTreeMap.Entry<K> top = new Object2FloatAVLTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = (K)s.readObject();
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
         Object2FloatAVLTreeMap.Entry<K> e = this.tree;

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

   private static final class Entry<K> extends AbstractObject2FloatMap.BasicEntry<K> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Object2FloatAVLTreeMap.Entry<K> left;
      Object2FloatAVLTreeMap.Entry<K> right;
      int info;

      Entry() {
         super(null, 0.0F);
      }

      Entry(K k, float v) {
         super(k, v);
         this.info = -1073741824;
      }

      Object2FloatAVLTreeMap.Entry<K> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Object2FloatAVLTreeMap.Entry<K> right() {
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

      void pred(Object2FloatAVLTreeMap.Entry<K> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Object2FloatAVLTreeMap.Entry<K> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Object2FloatAVLTreeMap.Entry<K> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Object2FloatAVLTreeMap.Entry<K> right) {
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

      Object2FloatAVLTreeMap.Entry<K> next() {
         Object2FloatAVLTreeMap.Entry<K> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Object2FloatAVLTreeMap.Entry<K> prev() {
         Object2FloatAVLTreeMap.Entry<K> prev = this.left;
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

      public Object2FloatAVLTreeMap.Entry<K> clone() {
         Object2FloatAVLTreeMap.Entry<K> c;
         try {
            c = (Object2FloatAVLTreeMap.Entry<K>)super.clone();
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
            java.util.Map.Entry<K, Float> e = (java.util.Map.Entry<K, Float>)o;
            return Objects.equals(this.key, e.getKey()) && Float.floatToIntBits(this.value) == Float.floatToIntBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode() ^ HashCommon.float2int(this.value);
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Object2FloatAVLTreeMap<K>.TreeIterator implements ObjectListIterator<Object2FloatMap.Entry<K>> {
      EntryIterator() {
      }

      EntryIterator(K k) {
         super(k);
      }

      public Object2FloatMap.Entry<K> next() {
         return this.nextEntry();
      }

      public Object2FloatMap.Entry<K> previous() {
         return this.previousEntry();
      }

      public void set(Object2FloatMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2FloatMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Object2FloatAVLTreeMap<K>.TreeIterator implements ObjectListIterator<K> {
      public KeyIterator() {
      }

      public KeyIterator(K k) {
         super(k);
      }

      @Override
      public K next() {
         return this.nextEntry().key;
      }

      @Override
      public K previous() {
         return this.previousEntry().key;
      }
   }

   private class KeySet extends AbstractObject2FloatSortedMap<K>.KeySet {
      private KeySet() {
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return Object2FloatAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return Object2FloatAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractObject2FloatSortedMap<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      K from;
      K to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Object2FloatMap.Entry<K>> entries;
      protected transient ObjectSortedSet<K> keys;
      protected transient FloatCollection values;

      public Submap(K from, boolean bottom, K to, boolean top) {
         if (!bottom && !top && Object2FloatAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2FloatAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Object2FloatAVLTreeMap<K>.Submap.SubmapIterator i = new Object2FloatAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(K k) {
         return (this.bottom || Object2FloatAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Object2FloatAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Object2FloatMap.Entry<K>> object2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2FloatMap.Entry<K>>() {
               @Override
               public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> iterator(Object2FloatMap.Entry<K> from) {
                  return Submap.this.new SubmapEntryIterator(from.getKey());
               }

               @Override
               public Comparator<? super Object2FloatMap.Entry<K>> comparator() {
                  return Object2FloatAVLTreeMap.this.object2FloatEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getValue() != null && e.getValue() instanceof Float) {
                        Object2FloatAVLTreeMap.Entry<K> f = Object2FloatAVLTreeMap.this.findKey((K)e.getKey());
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
                     if (e.getValue() != null && e.getValue() instanceof Float) {
                        Object2FloatAVLTreeMap.Entry<K> f = Object2FloatAVLTreeMap.this.findKey((K)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                           Submap.this.removeFloat(f.key);
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

               public Object2FloatMap.Entry<K> first() {
                  return Submap.this.firstEntry();
               }

               public Object2FloatMap.Entry<K> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Object2FloatMap.Entry<K>> subSet(Object2FloatMap.Entry<K> from, Object2FloatMap.Entry<K> to) {
                  return Submap.this.subMap(from.getKey(), to.getKey()).object2FloatEntrySet();
               }

               public ObjectSortedSet<Object2FloatMap.Entry<K>> headSet(Object2FloatMap.Entry<K> to) {
                  return Submap.this.headMap(to.getKey()).object2FloatEntrySet();
               }

               public ObjectSortedSet<Object2FloatMap.Entry<K>> tailSet(Object2FloatMap.Entry<K> from) {
                  return Submap.this.tailMap(from.getKey()).object2FloatEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = new Object2FloatAVLTreeMap.Submap.KeySet();
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
      public boolean containsKey(Object k) {
         return k == null ? false : this.in((K)k) && Object2FloatAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(float v) {
         Object2FloatAVLTreeMap<K>.Submap.SubmapIterator i = new Object2FloatAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            float ev = i.nextEntry().value;
            if (Float.floatToIntBits(ev) == Float.floatToIntBits(v)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public float getFloat(Object k) {
         Object2FloatAVLTreeMap.Entry<K> e;
         return this.in((K)k) && (e = Object2FloatAVLTreeMap.this.findKey((K)k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public float put(K k, float v) {
         Object2FloatAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            float oldValue = Object2FloatAVLTreeMap.this.put(k, v);
            return Object2FloatAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public float removeFloat(Object k) {
         Object2FloatAVLTreeMap.this.modified = false;
         if (!this.in((K)k)) {
            return this.defRetValue;
         } else {
            float oldValue = Object2FloatAVLTreeMap.this.removeFloat(k);
            return Object2FloatAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Object2FloatAVLTreeMap<K>.Submap.SubmapIterator i = new Object2FloatAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Object2FloatAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public Comparator<? super K> comparator() {
         return Object2FloatAVLTreeMap.this.actualComparator;
      }

      @Override
      public Object2FloatSortedMap<K> headMap(K to) {
         if (this.top) {
            return Object2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Object2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? Object2FloatAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Object2FloatSortedMap<K> tailMap(K from) {
         if (this.bottom) {
            return Object2FloatAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Object2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? Object2FloatAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Object2FloatSortedMap<K> subMap(K from, K to) {
         if (this.top && this.bottom) {
            return Object2FloatAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Object2FloatAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Object2FloatAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Object2FloatAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Object2FloatAVLTreeMap.Entry<K> firstEntry() {
         if (Object2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2FloatAVLTreeMap.Entry<K> e;
            if (this.bottom) {
               e = Object2FloatAVLTreeMap.this.firstEntry;
            } else {
               e = Object2FloatAVLTreeMap.this.locateKey(this.from);
               if (Object2FloatAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Object2FloatAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Object2FloatAVLTreeMap.Entry<K> lastEntry() {
         if (Object2FloatAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2FloatAVLTreeMap.Entry<K> e;
            if (this.top) {
               e = Object2FloatAVLTreeMap.this.lastEntry;
            } else {
               e = Object2FloatAVLTreeMap.this.locateKey(this.to);
               if (Object2FloatAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Object2FloatAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public K firstKey() {
         Object2FloatAVLTreeMap.Entry<K> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public K lastKey() {
         Object2FloatAVLTreeMap.Entry<K> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractObject2FloatSortedMap<K>.KeySet {
         private KeySet() {
         }

         @Override
         public ObjectBidirectionalIterator<K> iterator() {
            return Submap.this.new SubmapKeyIterator();
         }

         @Override
         public ObjectBidirectionalIterator<K> iterator(K from) {
            return Submap.this.new SubmapKeyIterator(from);
         }
      }

      private class SubmapEntryIterator extends Object2FloatAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<Object2FloatMap.Entry<K>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(K k) {
            super(k);
         }

         public Object2FloatMap.Entry<K> next() {
            return this.nextEntry();
         }

         public Object2FloatMap.Entry<K> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Object2FloatAVLTreeMap<K>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(K k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Object2FloatAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Object2FloatAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Object2FloatAVLTreeMap.this.locateKey(k);
                  if (Object2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Object2FloatAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Object2FloatAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Object2FloatAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<K> {
         public SubmapKeyIterator() {
         }

         public SubmapKeyIterator(K from) {
            super(from);
         }

         @Override
         public K next() {
            return this.nextEntry().key;
         }

         @Override
         public K previous() {
            return this.previousEntry().key;
         }
      }

      private final class SubmapValueIterator extends Object2FloatAVLTreeMap<K>.Submap.SubmapIterator implements FloatListIterator {
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
      Object2FloatAVLTreeMap.Entry<K> prev;
      Object2FloatAVLTreeMap.Entry<K> next;
      Object2FloatAVLTreeMap.Entry<K> curr;
      int index = 0;

      TreeIterator() {
         this.next = Object2FloatAVLTreeMap.this.firstEntry;
      }

      TreeIterator(K k) {
         if ((this.next = Object2FloatAVLTreeMap.this.locateKey(k)) != null) {
            if (Object2FloatAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Object2FloatAVLTreeMap.Entry<K> nextEntry() {
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

      Object2FloatAVLTreeMap.Entry<K> previousEntry() {
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
            Object2FloatAVLTreeMap.this.removeFloat(this.curr.key);
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

   private final class ValueIterator extends Object2FloatAVLTreeMap<K>.TreeIterator implements FloatListIterator {
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
