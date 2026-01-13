package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
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

public class Object2ByteAVLTreeMap<K> extends AbstractObject2ByteSortedMap<K> implements Serializable, Cloneable {
   protected transient Object2ByteAVLTreeMap.Entry<K> tree;
   protected int count;
   protected transient Object2ByteAVLTreeMap.Entry<K> firstEntry;
   protected transient Object2ByteAVLTreeMap.Entry<K> lastEntry;
   protected transient ObjectSortedSet<Object2ByteMap.Entry<K>> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient ByteCollection values;
   protected transient boolean modified;
   protected Comparator<? super K> storedComparator;
   protected transient Comparator<? super K> actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Object2ByteAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = this.storedComparator;
   }

   public Object2ByteAVLTreeMap(Comparator<? super K> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Object2ByteAVLTreeMap(Map<? extends K, ? extends Byte> m) {
      this();
      this.putAll(m);
   }

   public Object2ByteAVLTreeMap(SortedMap<K, Byte> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2ByteAVLTreeMap(Object2ByteMap<? extends K> m) {
      this();
      this.putAll(m);
   }

   public Object2ByteAVLTreeMap(Object2ByteSortedMap<K> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Object2ByteAVLTreeMap(K[] k, byte[] v, Comparator<? super K> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Object2ByteAVLTreeMap(K[] k, byte[] v) {
      this(k, v, null);
   }

   final int compare(K k1, K k2) {
      return this.actualComparator == null ? ((Comparable)k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
   }

   final Object2ByteAVLTreeMap.Entry<K> findKey(K k) {
      Object2ByteAVLTreeMap.Entry<K> e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Object2ByteAVLTreeMap.Entry<K> locateKey(K k) {
      Object2ByteAVLTreeMap.Entry<K> e = this.tree;
      Object2ByteAVLTreeMap.Entry<K> last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public byte addTo(K k, byte incr) {
      Object2ByteAVLTreeMap.Entry<K> e = this.add(k);
      byte oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public byte put(K k, byte v) {
      Object2ByteAVLTreeMap.Entry<K> e = this.add(k);
      byte oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Object2ByteAVLTreeMap.Entry<K> add(K k) {
      Objects.requireNonNull(k);
      this.modified = false;
      Object2ByteAVLTreeMap.Entry<K> e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Object2ByteAVLTreeMap.Entry<>(k, this.defRetValue);
         this.modified = true;
      } else {
         Object2ByteAVLTreeMap.Entry<K> p = this.tree;
         Object2ByteAVLTreeMap.Entry<K> q = null;
         Object2ByteAVLTreeMap.Entry<K> y = this.tree;
         Object2ByteAVLTreeMap.Entry<K> z = null;
         Object2ByteAVLTreeMap.Entry<K> w = null;
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
                  e = new Object2ByteAVLTreeMap.Entry<>(k, this.defRetValue);
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
                  e = new Object2ByteAVLTreeMap.Entry<>(k, this.defRetValue);
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
            Object2ByteAVLTreeMap.Entry<K> x = y.left;
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

            Object2ByteAVLTreeMap.Entry<K> x = y.right;
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

   private Object2ByteAVLTreeMap.Entry<K> parent(Object2ByteAVLTreeMap.Entry<K> e) {
      if (e == this.tree) {
         return null;
      } else {
         Object2ByteAVLTreeMap.Entry<K> y = e;

         Object2ByteAVLTreeMap.Entry<K> x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Object2ByteAVLTreeMap.Entry<K> p = x.left;
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

         Object2ByteAVLTreeMap.Entry<K> p = y.right;
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
   public byte removeByte(Object k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Object2ByteAVLTreeMap.Entry<K> p = this.tree;
         Object2ByteAVLTreeMap.Entry<K> q = null;
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
            Object2ByteAVLTreeMap.Entry<K> r = p.right;
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
                  Object2ByteAVLTreeMap.Entry<K> s = r.left;
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
            Object2ByteAVLTreeMap.Entry<K> y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Object2ByteAVLTreeMap.Entry<K> x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Object2ByteAVLTreeMap.Entry<K> w = x.left;
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
                  Object2ByteAVLTreeMap.Entry<K> xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Object2ByteAVLTreeMap.Entry<K> wx = xx.right;
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
   public boolean containsValue(byte v) {
      Object2ByteAVLTreeMap<K>.ValueIterator i = new Object2ByteAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         byte ev = i.nextByte();
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
   public byte getByte(Object k) {
      Object2ByteAVLTreeMap.Entry<K> e = this.findKey((K)k);
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
   public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Object2ByteMap.Entry<K>>() {
            final Comparator<? super Object2ByteMap.Entry<K>> comparator = Object2ByteAVLTreeMap.this.actualComparator == null
               ? (x, y) -> ((Comparable)x.getKey()).compareTo(y.getKey())
               : (x, y) -> Object2ByteAVLTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());

            @Override
            public Comparator<? super Object2ByteMap.Entry<K>> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator() {
               return Object2ByteAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> from) {
               return Object2ByteAVLTreeMap.this.new EntryIterator(from.getKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Byte) {
                     Object2ByteAVLTreeMap.Entry<K> f = (Object2ByteAVLTreeMap.Entry<K>)Object2ByteAVLTreeMap.this.findKey(e.getKey());
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
                  } else if (!(e.getValue() instanceof Byte)) {
                     return false;
                  } else {
                     Object2ByteAVLTreeMap.Entry<K> f = (Object2ByteAVLTreeMap.Entry<K>)Object2ByteAVLTreeMap.this.findKey(e.getKey());
                     if (f != null && f.getByteValue() == (Byte)e.getValue()) {
                        Object2ByteAVLTreeMap.this.removeByte(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Object2ByteAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2ByteAVLTreeMap.this.clear();
            }

            public Object2ByteMap.Entry<K> first() {
               return Object2ByteAVLTreeMap.this.firstEntry;
            }

            public Object2ByteMap.Entry<K> last() {
               return Object2ByteAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> from, Object2ByteMap.Entry<K> to) {
               return Object2ByteAVLTreeMap.this.subMap(from.getKey(), to.getKey()).object2ByteEntrySet();
            }

            public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> to) {
               return Object2ByteAVLTreeMap.this.headMap(to.getKey()).object2ByteEntrySet();
            }

            public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> from) {
               return Object2ByteAVLTreeMap.this.tailMap(from.getKey()).object2ByteEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ByteAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            @Override
            public ByteIterator iterator() {
               return Object2ByteAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(byte k) {
               return Object2ByteAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Object2ByteAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Object2ByteAVLTreeMap.this.clear();
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
   public Object2ByteSortedMap<K> headMap(K to) {
      return new Object2ByteAVLTreeMap.Submap(null, true, to, false);
   }

   @Override
   public Object2ByteSortedMap<K> tailMap(K from) {
      return new Object2ByteAVLTreeMap.Submap(from, false, null, true);
   }

   @Override
   public Object2ByteSortedMap<K> subMap(K from, K to) {
      return new Object2ByteAVLTreeMap.Submap(from, false, to, false);
   }

   public Object2ByteAVLTreeMap<K> clone() {
      Object2ByteAVLTreeMap<K> c;
      try {
         c = (Object2ByteAVLTreeMap<K>)super.clone();
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
         Object2ByteAVLTreeMap.Entry<K> rp = new Object2ByteAVLTreeMap.Entry<>();
         Object2ByteAVLTreeMap.Entry<K> rq = new Object2ByteAVLTreeMap.Entry<>();
         Object2ByteAVLTreeMap.Entry<K> p = rp;
         rp.left(this.tree);
         Object2ByteAVLTreeMap.Entry<K> q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Object2ByteAVLTreeMap.Entry<K> e = p.left.clone();
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
               Object2ByteAVLTreeMap.Entry<K> e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Object2ByteAVLTreeMap<K>.EntryIterator i = new Object2ByteAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Object2ByteAVLTreeMap.Entry<K> e = i.nextEntry();
         s.writeObject(e.key);
         s.writeByte(e.value);
      }
   }

   private Object2ByteAVLTreeMap.Entry<K> readTree(ObjectInputStream s, int n, Object2ByteAVLTreeMap.Entry<K> pred, Object2ByteAVLTreeMap.Entry<K> succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Object2ByteAVLTreeMap.Entry<K> top = new Object2ByteAVLTreeMap.Entry<>((K)s.readObject(), s.readByte());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Object2ByteAVLTreeMap.Entry<K> top = new Object2ByteAVLTreeMap.Entry<>((K)s.readObject(), s.readByte());
         top.right(new Object2ByteAVLTreeMap.Entry<>((K)s.readObject(), s.readByte()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Object2ByteAVLTreeMap.Entry<K> top = new Object2ByteAVLTreeMap.Entry<>();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = (K)s.readObject();
         top.value = s.readByte();
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
         Object2ByteAVLTreeMap.Entry<K> e = this.tree;

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

   private static final class Entry<K> extends AbstractObject2ByteMap.BasicEntry<K> implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Object2ByteAVLTreeMap.Entry<K> left;
      Object2ByteAVLTreeMap.Entry<K> right;
      int info;

      Entry() {
         super(null, (byte)0);
      }

      Entry(K k, byte v) {
         super(k, v);
         this.info = -1073741824;
      }

      Object2ByteAVLTreeMap.Entry<K> left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Object2ByteAVLTreeMap.Entry<K> right() {
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

      void pred(Object2ByteAVLTreeMap.Entry<K> pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Object2ByteAVLTreeMap.Entry<K> succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Object2ByteAVLTreeMap.Entry<K> left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Object2ByteAVLTreeMap.Entry<K> right) {
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

      Object2ByteAVLTreeMap.Entry<K> next() {
         Object2ByteAVLTreeMap.Entry<K> next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Object2ByteAVLTreeMap.Entry<K> prev() {
         Object2ByteAVLTreeMap.Entry<K> prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public byte setValue(byte value) {
         byte oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Object2ByteAVLTreeMap.Entry<K> clone() {
         Object2ByteAVLTreeMap.Entry<K> c;
         try {
            c = (Object2ByteAVLTreeMap.Entry<K>)super.clone();
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
            java.util.Map.Entry<K, Byte> e = (java.util.Map.Entry<K, Byte>)o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode() ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Object2ByteAVLTreeMap<K>.TreeIterator implements ObjectListIterator<Object2ByteMap.Entry<K>> {
      EntryIterator() {
      }

      EntryIterator(K k) {
         super(k);
      }

      public Object2ByteMap.Entry<K> next() {
         return this.nextEntry();
      }

      public Object2ByteMap.Entry<K> previous() {
         return this.previousEntry();
      }

      public void set(Object2ByteMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2ByteMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Object2ByteAVLTreeMap<K>.TreeIterator implements ObjectListIterator<K> {
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

   private class KeySet extends AbstractObject2ByteSortedMap<K>.KeySet {
      private KeySet() {
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return Object2ByteAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return Object2ByteAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractObject2ByteSortedMap<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      K from;
      K to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSortedSet<K> keys;
      protected transient ByteCollection values;

      public Submap(K from, boolean bottom, K to, boolean top) {
         if (!bottom && !top && Object2ByteAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2ByteAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Object2ByteAVLTreeMap<K>.Submap.SubmapIterator i = new Object2ByteAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(K k) {
         return (this.bottom || Object2ByteAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Object2ByteAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Object2ByteMap.Entry<K>>() {
               @Override
               public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> iterator(Object2ByteMap.Entry<K> from) {
                  return Submap.this.new SubmapEntryIterator(from.getKey());
               }

               @Override
               public Comparator<? super Object2ByteMap.Entry<K>> comparator() {
                  return Object2ByteAVLTreeMap.this.object2ByteEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getValue() != null && e.getValue() instanceof Byte) {
                        Object2ByteAVLTreeMap.Entry<K> f = Object2ByteAVLTreeMap.this.findKey((K)e.getKey());
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
                     if (e.getValue() != null && e.getValue() instanceof Byte) {
                        Object2ByteAVLTreeMap.Entry<K> f = Object2ByteAVLTreeMap.this.findKey((K)e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                           Submap.this.removeByte(f.key);
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

               public Object2ByteMap.Entry<K> first() {
                  return Submap.this.firstEntry();
               }

               public Object2ByteMap.Entry<K> last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Object2ByteMap.Entry<K>> subSet(Object2ByteMap.Entry<K> from, Object2ByteMap.Entry<K> to) {
                  return Submap.this.subMap(from.getKey(), to.getKey()).object2ByteEntrySet();
               }

               public ObjectSortedSet<Object2ByteMap.Entry<K>> headSet(Object2ByteMap.Entry<K> to) {
                  return Submap.this.headMap(to.getKey()).object2ByteEntrySet();
               }

               public ObjectSortedSet<Object2ByteMap.Entry<K>> tailSet(Object2ByteMap.Entry<K> from) {
                  return Submap.this.tailMap(from.getKey()).object2ByteEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = new Object2ByteAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public ByteCollection values() {
         if (this.values == null) {
            this.values = new AbstractByteCollection() {
               @Override
               public ByteIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(byte k) {
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
         return k == null ? false : this.in((K)k) && Object2ByteAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(byte v) {
         Object2ByteAVLTreeMap<K>.Submap.SubmapIterator i = new Object2ByteAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            byte ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public byte getByte(Object k) {
         Object2ByteAVLTreeMap.Entry<K> e;
         return this.in((K)k) && (e = Object2ByteAVLTreeMap.this.findKey((K)k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public byte put(K k, byte v) {
         Object2ByteAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            byte oldValue = Object2ByteAVLTreeMap.this.put(k, v);
            return Object2ByteAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public byte removeByte(Object k) {
         Object2ByteAVLTreeMap.this.modified = false;
         if (!this.in((K)k)) {
            return this.defRetValue;
         } else {
            byte oldValue = Object2ByteAVLTreeMap.this.removeByte(k);
            return Object2ByteAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Object2ByteAVLTreeMap<K>.Submap.SubmapIterator i = new Object2ByteAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Object2ByteAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public Comparator<? super K> comparator() {
         return Object2ByteAVLTreeMap.this.actualComparator;
      }

      @Override
      public Object2ByteSortedMap<K> headMap(K to) {
         if (this.top) {
            return Object2ByteAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Object2ByteAVLTreeMap.this.compare(to, this.to) < 0 ? Object2ByteAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Object2ByteSortedMap<K> tailMap(K from) {
         if (this.bottom) {
            return Object2ByteAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Object2ByteAVLTreeMap.this.compare(from, this.from) > 0 ? Object2ByteAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Object2ByteSortedMap<K> subMap(K from, K to) {
         if (this.top && this.bottom) {
            return Object2ByteAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Object2ByteAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Object2ByteAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Object2ByteAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Object2ByteAVLTreeMap.Entry<K> firstEntry() {
         if (Object2ByteAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2ByteAVLTreeMap.Entry<K> e;
            if (this.bottom) {
               e = Object2ByteAVLTreeMap.this.firstEntry;
            } else {
               e = Object2ByteAVLTreeMap.this.locateKey(this.from);
               if (Object2ByteAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Object2ByteAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Object2ByteAVLTreeMap.Entry<K> lastEntry() {
         if (Object2ByteAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Object2ByteAVLTreeMap.Entry<K> e;
            if (this.top) {
               e = Object2ByteAVLTreeMap.this.lastEntry;
            } else {
               e = Object2ByteAVLTreeMap.this.locateKey(this.to);
               if (Object2ByteAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Object2ByteAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public K firstKey() {
         Object2ByteAVLTreeMap.Entry<K> e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public K lastKey() {
         Object2ByteAVLTreeMap.Entry<K> e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractObject2ByteSortedMap<K>.KeySet {
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

      private class SubmapEntryIterator extends Object2ByteAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<Object2ByteMap.Entry<K>> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(K k) {
            super(k);
         }

         public Object2ByteMap.Entry<K> next() {
            return this.nextEntry();
         }

         public Object2ByteMap.Entry<K> previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Object2ByteAVLTreeMap<K>.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(K k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Object2ByteAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Object2ByteAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Object2ByteAVLTreeMap.this.locateKey(k);
                  if (Object2ByteAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Object2ByteAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Object2ByteAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Object2ByteAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<K> {
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

      private final class SubmapValueIterator extends Object2ByteAVLTreeMap<K>.Submap.SubmapIterator implements ByteListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public byte nextByte() {
            return this.nextEntry().value;
         }

         @Override
         public byte previousByte() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Object2ByteAVLTreeMap.Entry<K> prev;
      Object2ByteAVLTreeMap.Entry<K> next;
      Object2ByteAVLTreeMap.Entry<K> curr;
      int index = 0;

      TreeIterator() {
         this.next = Object2ByteAVLTreeMap.this.firstEntry;
      }

      TreeIterator(K k) {
         if ((this.next = Object2ByteAVLTreeMap.this.locateKey(k)) != null) {
            if (Object2ByteAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Object2ByteAVLTreeMap.Entry<K> nextEntry() {
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

      Object2ByteAVLTreeMap.Entry<K> previousEntry() {
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
            Object2ByteAVLTreeMap.this.removeByte(this.curr.key);
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

   private final class ValueIterator extends Object2ByteAVLTreeMap<K>.TreeIterator implements ByteListIterator {
      private ValueIterator() {
      }

      @Override
      public byte nextByte() {
         return this.nextEntry().value;
      }

      @Override
      public byte previousByte() {
         return this.previousEntry().value;
      }
   }
}
