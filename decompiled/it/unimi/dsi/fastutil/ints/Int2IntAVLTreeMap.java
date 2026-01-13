package it.unimi.dsi.fastutil.ints;

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

public class Int2IntAVLTreeMap extends AbstractInt2IntSortedMap implements Serializable, Cloneable {
   protected transient Int2IntAVLTreeMap.Entry tree;
   protected int count;
   protected transient Int2IntAVLTreeMap.Entry firstEntry;
   protected transient Int2IntAVLTreeMap.Entry lastEntry;
   protected transient ObjectSortedSet<Int2IntMap.Entry> entries;
   protected transient IntSortedSet keys;
   protected transient IntCollection values;
   protected transient boolean modified;
   protected Comparator<? super Integer> storedComparator;
   protected transient IntComparator actualComparator;
   private static final long serialVersionUID = -7046029254386353129L;
   private transient boolean[] dirPath;

   public Int2IntAVLTreeMap() {
      this.allocatePaths();
      this.tree = null;
      this.count = 0;
   }

   private void setActualComparator() {
      this.actualComparator = IntComparators.asIntComparator(this.storedComparator);
   }

   public Int2IntAVLTreeMap(Comparator<? super Integer> c) {
      this();
      this.storedComparator = c;
      this.setActualComparator();
   }

   public Int2IntAVLTreeMap(Map<? extends Integer, ? extends Integer> m) {
      this();
      this.putAll(m);
   }

   public Int2IntAVLTreeMap(SortedMap<Integer, Integer> m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2IntAVLTreeMap(Int2IntMap m) {
      this();
      this.putAll(m);
   }

   public Int2IntAVLTreeMap(Int2IntSortedMap m) {
      this(m.comparator());
      this.putAll(m);
   }

   public Int2IntAVLTreeMap(int[] k, int[] v, Comparator<? super Integer> c) {
      this(c);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2IntAVLTreeMap(int[] k, int[] v) {
      this(k, v, null);
   }

   final int compare(int k1, int k2) {
      return this.actualComparator == null ? Integer.compare(k1, k2) : this.actualComparator.compare(k1, k2);
   }

   final Int2IntAVLTreeMap.Entry findKey(int k) {
      Int2IntAVLTreeMap.Entry e = this.tree;

      int cmp;
      while (e != null && (cmp = this.compare(k, e.key)) != 0) {
         e = cmp < 0 ? e.left() : e.right();
      }

      return e;
   }

   final Int2IntAVLTreeMap.Entry locateKey(int k) {
      Int2IntAVLTreeMap.Entry e = this.tree;
      Int2IntAVLTreeMap.Entry last = this.tree;

      int cmp;
      for (cmp = 0; e != null && (cmp = this.compare(k, e.key)) != 0; e = cmp < 0 ? e.left() : e.right()) {
         last = e;
      }

      return cmp == 0 ? e : last;
   }

   private void allocatePaths() {
      this.dirPath = new boolean[48];
   }

   public int addTo(int k, int incr) {
      Int2IntAVLTreeMap.Entry e = this.add(k);
      int oldValue = e.value;
      e.value += incr;
      return oldValue;
   }

   @Override
   public int put(int k, int v) {
      Int2IntAVLTreeMap.Entry e = this.add(k);
      int oldValue = e.value;
      e.value = v;
      return oldValue;
   }

   private Int2IntAVLTreeMap.Entry add(int k) {
      this.modified = false;
      Int2IntAVLTreeMap.Entry e = null;
      if (this.tree == null) {
         this.count++;
         e = this.tree = this.lastEntry = this.firstEntry = new Int2IntAVLTreeMap.Entry(k, this.defRetValue);
         this.modified = true;
      } else {
         Int2IntAVLTreeMap.Entry p = this.tree;
         Int2IntAVLTreeMap.Entry q = null;
         Int2IntAVLTreeMap.Entry y = this.tree;
         Int2IntAVLTreeMap.Entry z = null;
         Int2IntAVLTreeMap.Entry w = null;
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
                  e = new Int2IntAVLTreeMap.Entry(k, this.defRetValue);
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
                  e = new Int2IntAVLTreeMap.Entry(k, this.defRetValue);
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
            Int2IntAVLTreeMap.Entry x = y.left;
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

            Int2IntAVLTreeMap.Entry x = y.right;
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

   private Int2IntAVLTreeMap.Entry parent(Int2IntAVLTreeMap.Entry e) {
      if (e == this.tree) {
         return null;
      } else {
         Int2IntAVLTreeMap.Entry y = e;

         Int2IntAVLTreeMap.Entry x;
         for (x = e; !y.succ(); y = y.right) {
            if (x.pred()) {
               Int2IntAVLTreeMap.Entry p = x.left;
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

         Int2IntAVLTreeMap.Entry p = y.right;
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
   public int remove(int k) {
      this.modified = false;
      if (this.tree == null) {
         return this.defRetValue;
      } else {
         Int2IntAVLTreeMap.Entry p = this.tree;
         Int2IntAVLTreeMap.Entry q = null;
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
            Int2IntAVLTreeMap.Entry r = p.right;
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
                  Int2IntAVLTreeMap.Entry s = r.left;
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
            Int2IntAVLTreeMap.Entry y = q;
            q = this.parent(q);
            if (!dir) {
               dir = q != null && q.left != y;
               y.incBalance();
               if (y.balance() == 1) {
                  break;
               }

               if (y.balance() == 2) {
                  Int2IntAVLTreeMap.Entry x = y.right;

                  assert x != null;

                  if (x.balance() == -1) {
                     assert x.balance() == -1;

                     Int2IntAVLTreeMap.Entry w = x.left;
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
                  Int2IntAVLTreeMap.Entry xx = y.left;

                  assert xx != null;

                  if (xx.balance() == 1) {
                     assert xx.balance() == 1;

                     Int2IntAVLTreeMap.Entry wx = xx.right;
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
   public boolean containsValue(int v) {
      Int2IntAVLTreeMap.ValueIterator i = new Int2IntAVLTreeMap.ValueIterator();
      int j = this.count;

      while (j-- != 0) {
         int ev = i.nextInt();
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
   public int get(int k) {
      Int2IntAVLTreeMap.Entry e = this.findKey(k);
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
   public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new AbstractObjectSortedSet<Int2IntMap.Entry>() {
            final Comparator<? super Int2IntMap.Entry> comparator = Int2IntAVLTreeMap.this.actualComparator == null
               ? (x, y) -> Integer.compare(x.getIntKey(), y.getIntKey())
               : (x, y) -> Int2IntAVLTreeMap.this.actualComparator.compare(x.getIntKey(), y.getIntKey());

            @Override
            public Comparator<? super Int2IntMap.Entry> comparator() {
               return this.comparator;
            }

            @Override
            public ObjectBidirectionalIterator<Int2IntMap.Entry> iterator() {
               return Int2IntAVLTreeMap.this.new EntryIterator();
            }

            public ObjectBidirectionalIterator<Int2IntMap.Entry> iterator(Int2IntMap.Entry from) {
               return Int2IntAVLTreeMap.this.new EntryIterator(from.getIntKey());
            }

            @Override
            public boolean contains(Object o) {
               if (o != null && o instanceof java.util.Map.Entry) {
                  java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                  if (e.getKey() == null) {
                     return false;
                  } else if (!(e.getKey() instanceof Integer)) {
                     return false;
                  } else if (e.getValue() != null && e.getValue() instanceof Integer) {
                     Int2IntAVLTreeMap.Entry f = Int2IntAVLTreeMap.this.findKey((Integer)e.getKey());
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
                  } else if (!(e.getValue() instanceof Integer)) {
                     return false;
                  } else {
                     Int2IntAVLTreeMap.Entry f = Int2IntAVLTreeMap.this.findKey((Integer)e.getKey());
                     if (f != null && f.getIntValue() == (Integer)e.getValue()) {
                        Int2IntAVLTreeMap.this.remove(f.key);
                        return true;
                     } else {
                        return false;
                     }
                  }
               }
            }

            @Override
            public int size() {
               return Int2IntAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2IntAVLTreeMap.this.clear();
            }

            public Int2IntMap.Entry first() {
               return Int2IntAVLTreeMap.this.firstEntry;
            }

            public Int2IntMap.Entry last() {
               return Int2IntAVLTreeMap.this.lastEntry;
            }

            public ObjectSortedSet<Int2IntMap.Entry> subSet(Int2IntMap.Entry from, Int2IntMap.Entry to) {
               return Int2IntAVLTreeMap.this.subMap(from.getIntKey(), to.getIntKey()).int2IntEntrySet();
            }

            public ObjectSortedSet<Int2IntMap.Entry> headSet(Int2IntMap.Entry to) {
               return Int2IntAVLTreeMap.this.headMap(to.getIntKey()).int2IntEntrySet();
            }

            public ObjectSortedSet<Int2IntMap.Entry> tailSet(Int2IntMap.Entry from) {
               return Int2IntAVLTreeMap.this.tailMap(from.getIntKey()).int2IntEntrySet();
            }
         };
      }

      return this.entries;
   }

   @Override
   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2IntAVLTreeMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            @Override
            public IntIterator iterator() {
               return Int2IntAVLTreeMap.this.new ValueIterator();
            }

            @Override
            public boolean contains(int k) {
               return Int2IntAVLTreeMap.this.containsValue(k);
            }

            @Override
            public int size() {
               return Int2IntAVLTreeMap.this.count;
            }

            @Override
            public void clear() {
               Int2IntAVLTreeMap.this.clear();
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
   public Int2IntSortedMap headMap(int to) {
      return new Int2IntAVLTreeMap.Submap(0, true, to, false);
   }

   @Override
   public Int2IntSortedMap tailMap(int from) {
      return new Int2IntAVLTreeMap.Submap(from, false, 0, true);
   }

   @Override
   public Int2IntSortedMap subMap(int from, int to) {
      return new Int2IntAVLTreeMap.Submap(from, false, to, false);
   }

   public Int2IntAVLTreeMap clone() {
      Int2IntAVLTreeMap c;
      try {
         c = (Int2IntAVLTreeMap)super.clone();
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
         Int2IntAVLTreeMap.Entry rp = new Int2IntAVLTreeMap.Entry();
         Int2IntAVLTreeMap.Entry rq = new Int2IntAVLTreeMap.Entry();
         Int2IntAVLTreeMap.Entry p = rp;
         rp.left(this.tree);
         Int2IntAVLTreeMap.Entry q = rq;
         rq.pred(null);

         while (true) {
            if (!p.pred()) {
               Int2IntAVLTreeMap.Entry e = p.left.clone();
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
               Int2IntAVLTreeMap.Entry e = p.right.clone();
               e.succ(q.right);
               e.pred(q);
               q.right(e);
            }
         }
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int n = this.count;
      Int2IntAVLTreeMap.EntryIterator i = new Int2IntAVLTreeMap.EntryIterator();
      s.defaultWriteObject();

      while (n-- != 0) {
         Int2IntAVLTreeMap.Entry e = i.nextEntry();
         s.writeInt(e.key);
         s.writeInt(e.value);
      }
   }

   private Int2IntAVLTreeMap.Entry readTree(ObjectInputStream s, int n, Int2IntAVLTreeMap.Entry pred, Int2IntAVLTreeMap.Entry succ) throws IOException, ClassNotFoundException {
      if (n == 1) {
         Int2IntAVLTreeMap.Entry top = new Int2IntAVLTreeMap.Entry(s.readInt(), s.readInt());
         top.pred(pred);
         top.succ(succ);
         return top;
      } else if (n == 2) {
         Int2IntAVLTreeMap.Entry top = new Int2IntAVLTreeMap.Entry(s.readInt(), s.readInt());
         top.right(new Int2IntAVLTreeMap.Entry(s.readInt(), s.readInt()));
         top.right.pred(top);
         top.balance(1);
         top.pred(pred);
         top.right.succ(succ);
         return top;
      } else {
         int rightN = n / 2;
         int leftN = n - rightN - 1;
         Int2IntAVLTreeMap.Entry top = new Int2IntAVLTreeMap.Entry();
         top.left(this.readTree(s, leftN, pred, top));
         top.key = s.readInt();
         top.value = s.readInt();
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
         Int2IntAVLTreeMap.Entry e = this.tree;

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

   private static final class Entry extends AbstractInt2IntMap.BasicEntry implements Cloneable {
      private static final int SUCC_MASK = Integer.MIN_VALUE;
      private static final int PRED_MASK = 1073741824;
      private static final int BALANCE_MASK = 255;
      Int2IntAVLTreeMap.Entry left;
      Int2IntAVLTreeMap.Entry right;
      int info;

      Entry() {
         super(0, 0);
      }

      Entry(int k, int v) {
         super(k, v);
         this.info = -1073741824;
      }

      Int2IntAVLTreeMap.Entry left() {
         return (this.info & 1073741824) != 0 ? null : this.left;
      }

      Int2IntAVLTreeMap.Entry right() {
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

      void pred(Int2IntAVLTreeMap.Entry pred) {
         this.info |= 1073741824;
         this.left = pred;
      }

      void succ(Int2IntAVLTreeMap.Entry succ) {
         this.info |= Integer.MIN_VALUE;
         this.right = succ;
      }

      void left(Int2IntAVLTreeMap.Entry left) {
         this.info &= -1073741825;
         this.left = left;
      }

      void right(Int2IntAVLTreeMap.Entry right) {
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

      Int2IntAVLTreeMap.Entry next() {
         Int2IntAVLTreeMap.Entry next = this.right;
         if ((this.info & -2147483648) == 0) {
            while ((next.info & 1073741824) == 0) {
               next = next.left;
            }
         }

         return next;
      }

      Int2IntAVLTreeMap.Entry prev() {
         Int2IntAVLTreeMap.Entry prev = this.left;
         if ((this.info & 1073741824) == 0) {
            while ((prev.info & -2147483648) == 0) {
               prev = prev.right;
            }
         }

         return prev;
      }

      @Override
      public int setValue(int value) {
         int oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public Int2IntAVLTreeMap.Entry clone() {
         Int2IntAVLTreeMap.Entry c;
         try {
            c = (Int2IntAVLTreeMap.Entry)super.clone();
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
            java.util.Map.Entry<Integer, Integer> e = (java.util.Map.Entry<Integer, Integer>)o;
            return this.key == e.getKey() && this.value == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return this.key ^ this.value;
      }

      @Override
      public String toString() {
         return this.key + "=>" + this.value;
      }
   }

   private class EntryIterator extends Int2IntAVLTreeMap.TreeIterator implements ObjectListIterator<Int2IntMap.Entry> {
      EntryIterator() {
      }

      EntryIterator(int k) {
         super(k);
      }

      public Int2IntMap.Entry next() {
         return this.nextEntry();
      }

      public Int2IntMap.Entry previous() {
         return this.previousEntry();
      }

      public void set(Int2IntMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Int2IntMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends Int2IntAVLTreeMap.TreeIterator implements IntListIterator {
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

   private class KeySet extends AbstractInt2IntSortedMap.KeySet {
      private KeySet() {
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return Int2IntAVLTreeMap.this.new KeyIterator();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return Int2IntAVLTreeMap.this.new KeyIterator(from);
      }
   }

   private final class Submap extends AbstractInt2IntSortedMap implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      int from;
      int to;
      boolean bottom;
      boolean top;
      protected transient ObjectSortedSet<Int2IntMap.Entry> entries;
      protected transient IntSortedSet keys;
      protected transient IntCollection values;

      public Submap(int from, boolean bottom, int to, boolean top) {
         if (!bottom && !top && Int2IntAVLTreeMap.this.compare(from, to) > 0) {
            throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
         } else {
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Int2IntAVLTreeMap.this.defRetValue;
         }
      }

      @Override
      public void clear() {
         Int2IntAVLTreeMap.Submap.SubmapIterator i = new Int2IntAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            i.nextEntry();
            i.remove();
         }
      }

      final boolean in(int k) {
         return (this.bottom || Int2IntAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Int2IntAVLTreeMap.this.compare(k, this.to) < 0);
      }

      @Override
      public ObjectSortedSet<Int2IntMap.Entry> int2IntEntrySet() {
         if (this.entries == null) {
            this.entries = new AbstractObjectSortedSet<Int2IntMap.Entry>() {
               @Override
               public ObjectBidirectionalIterator<Int2IntMap.Entry> iterator() {
                  return Submap.this.new SubmapEntryIterator();
               }

               public ObjectBidirectionalIterator<Int2IntMap.Entry> iterator(Int2IntMap.Entry from) {
                  return Submap.this.new SubmapEntryIterator(from.getIntKey());
               }

               @Override
               public Comparator<? super Int2IntMap.Entry> comparator() {
                  return Int2IntAVLTreeMap.this.int2IntEntrySet().comparator();
               }

               @Override
               public boolean contains(Object o) {
                  if (!(o instanceof java.util.Map.Entry)) {
                     return false;
                  } else {
                     java.util.Map.Entry<?, ?> e = (java.util.Map.Entry<?, ?>)o;
                     if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
                        return false;
                     } else if (e.getValue() != null && e.getValue() instanceof Integer) {
                        Int2IntAVLTreeMap.Entry f = Int2IntAVLTreeMap.this.findKey((Integer)e.getKey());
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
                     } else if (e.getValue() != null && e.getValue() instanceof Integer) {
                        Int2IntAVLTreeMap.Entry f = Int2IntAVLTreeMap.this.findKey((Integer)e.getKey());
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

               public Int2IntMap.Entry first() {
                  return Submap.this.firstEntry();
               }

               public Int2IntMap.Entry last() {
                  return Submap.this.lastEntry();
               }

               public ObjectSortedSet<Int2IntMap.Entry> subSet(Int2IntMap.Entry from, Int2IntMap.Entry to) {
                  return Submap.this.subMap(from.getIntKey(), to.getIntKey()).int2IntEntrySet();
               }

               public ObjectSortedSet<Int2IntMap.Entry> headSet(Int2IntMap.Entry to) {
                  return Submap.this.headMap(to.getIntKey()).int2IntEntrySet();
               }

               public ObjectSortedSet<Int2IntMap.Entry> tailSet(Int2IntMap.Entry from) {
                  return Submap.this.tailMap(from.getIntKey()).int2IntEntrySet();
               }
            };
         }

         return this.entries;
      }

      @Override
      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = new Int2IntAVLTreeMap.Submap.KeySet();
         }

         return this.keys;
      }

      @Override
      public IntCollection values() {
         if (this.values == null) {
            this.values = new AbstractIntCollection() {
               @Override
               public IntIterator iterator() {
                  return Submap.this.new SubmapValueIterator();
               }

               @Override
               public boolean contains(int k) {
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
         return this.in(k) && Int2IntAVLTreeMap.this.containsKey(k);
      }

      @Override
      public boolean containsValue(int v) {
         Int2IntAVLTreeMap.Submap.SubmapIterator i = new Int2IntAVLTreeMap.Submap.SubmapIterator();

         while (i.hasNext()) {
            int ev = i.nextEntry().value;
            if (ev == v) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int get(int k) {
         Int2IntAVLTreeMap.Entry e;
         return this.in(k) && (e = Int2IntAVLTreeMap.this.findKey(k)) != null ? e.value : this.defRetValue;
      }

      @Override
      public int put(int k, int v) {
         Int2IntAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            throw new IllegalArgumentException(
               "Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")"
            );
         } else {
            int oldValue = Int2IntAVLTreeMap.this.put(k, v);
            return Int2IntAVLTreeMap.this.modified ? this.defRetValue : oldValue;
         }
      }

      @Override
      public int remove(int k) {
         Int2IntAVLTreeMap.this.modified = false;
         if (!this.in(k)) {
            return this.defRetValue;
         } else {
            int oldValue = Int2IntAVLTreeMap.this.remove(k);
            return Int2IntAVLTreeMap.this.modified ? oldValue : this.defRetValue;
         }
      }

      @Override
      public int size() {
         Int2IntAVLTreeMap.Submap.SubmapIterator i = new Int2IntAVLTreeMap.Submap.SubmapIterator();
         int n = 0;

         while (i.hasNext()) {
            n++;
            i.nextEntry();
         }

         return n;
      }

      @Override
      public boolean isEmpty() {
         return !new Int2IntAVLTreeMap.Submap.SubmapIterator().hasNext();
      }

      @Override
      public IntComparator comparator() {
         return Int2IntAVLTreeMap.this.actualComparator;
      }

      @Override
      public Int2IntSortedMap headMap(int to) {
         if (this.top) {
            return Int2IntAVLTreeMap.this.new Submap(this.from, this.bottom, to, false);
         } else {
            return Int2IntAVLTreeMap.this.compare(to, this.to) < 0 ? Int2IntAVLTreeMap.this.new Submap(this.from, this.bottom, to, false) : this;
         }
      }

      @Override
      public Int2IntSortedMap tailMap(int from) {
         if (this.bottom) {
            return Int2IntAVLTreeMap.this.new Submap(from, false, this.to, this.top);
         } else {
            return Int2IntAVLTreeMap.this.compare(from, this.from) > 0 ? Int2IntAVLTreeMap.this.new Submap(from, false, this.to, this.top) : this;
         }
      }

      @Override
      public Int2IntSortedMap subMap(int from, int to) {
         if (this.top && this.bottom) {
            return Int2IntAVLTreeMap.this.new Submap(from, false, to, false);
         } else {
            if (!this.top) {
               to = Int2IntAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }

            if (!this.bottom) {
               from = Int2IntAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }

            return !this.top && !this.bottom && from == this.from && to == this.to ? this : Int2IntAVLTreeMap.this.new Submap(from, false, to, false);
         }
      }

      public Int2IntAVLTreeMap.Entry firstEntry() {
         if (Int2IntAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2IntAVLTreeMap.Entry e;
            if (this.bottom) {
               e = Int2IntAVLTreeMap.this.firstEntry;
            } else {
               e = Int2IntAVLTreeMap.this.locateKey(this.from);
               if (Int2IntAVLTreeMap.this.compare(e.key, this.from) < 0) {
                  e = e.next();
               }
            }

            return e != null && (this.top || Int2IntAVLTreeMap.this.compare(e.key, this.to) < 0) ? e : null;
         }
      }

      public Int2IntAVLTreeMap.Entry lastEntry() {
         if (Int2IntAVLTreeMap.this.tree == null) {
            return null;
         } else {
            Int2IntAVLTreeMap.Entry e;
            if (this.top) {
               e = Int2IntAVLTreeMap.this.lastEntry;
            } else {
               e = Int2IntAVLTreeMap.this.locateKey(this.to);
               if (Int2IntAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                  e = e.prev();
               }
            }

            return e != null && (this.bottom || Int2IntAVLTreeMap.this.compare(e.key, this.from) >= 0) ? e : null;
         }
      }

      @Override
      public int firstIntKey() {
         Int2IntAVLTreeMap.Entry e = this.firstEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      @Override
      public int lastIntKey() {
         Int2IntAVLTreeMap.Entry e = this.lastEntry();
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            return e.key;
         }
      }

      private class KeySet extends AbstractInt2IntSortedMap.KeySet {
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

      private class SubmapEntryIterator extends Int2IntAVLTreeMap.Submap.SubmapIterator implements ObjectListIterator<Int2IntMap.Entry> {
         SubmapEntryIterator() {
         }

         SubmapEntryIterator(int k) {
            super(k);
         }

         public Int2IntMap.Entry next() {
            return this.nextEntry();
         }

         public Int2IntMap.Entry previous() {
            return this.previousEntry();
         }
      }

      private class SubmapIterator extends Int2IntAVLTreeMap.TreeIterator {
         SubmapIterator() {
            this.next = Submap.this.firstEntry();
         }

         SubmapIterator(int k) {
            this();
            if (this.next != null) {
               if (!Submap.this.bottom && Int2IntAVLTreeMap.this.compare(k, this.next.key) < 0) {
                  this.prev = null;
               } else if (!Submap.this.top && Int2IntAVLTreeMap.this.compare(k, (this.prev = Submap.this.lastEntry()).key) >= 0) {
                  this.next = null;
               } else {
                  this.next = Int2IntAVLTreeMap.this.locateKey(k);
                  if (Int2IntAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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
            if (!Submap.this.bottom && this.prev != null && Int2IntAVLTreeMap.this.compare(this.prev.key, Submap.this.from) < 0) {
               this.prev = null;
            }
         }

         @Override
         void updateNext() {
            this.next = this.next.next();
            if (!Submap.this.top && this.next != null && Int2IntAVLTreeMap.this.compare(this.next.key, Submap.this.to) >= 0) {
               this.next = null;
            }
         }
      }

      private final class SubmapKeyIterator extends Int2IntAVLTreeMap.Submap.SubmapIterator implements IntListIterator {
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

      private final class SubmapValueIterator extends Int2IntAVLTreeMap.Submap.SubmapIterator implements IntListIterator {
         private SubmapValueIterator() {
         }

         @Override
         public int nextInt() {
            return this.nextEntry().value;
         }

         @Override
         public int previousInt() {
            return this.previousEntry().value;
         }
      }
   }

   private class TreeIterator {
      Int2IntAVLTreeMap.Entry prev;
      Int2IntAVLTreeMap.Entry next;
      Int2IntAVLTreeMap.Entry curr;
      int index = 0;

      TreeIterator() {
         this.next = Int2IntAVLTreeMap.this.firstEntry;
      }

      TreeIterator(int k) {
         if ((this.next = Int2IntAVLTreeMap.this.locateKey(k)) != null) {
            if (Int2IntAVLTreeMap.this.compare(this.next.key, k) <= 0) {
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

      Int2IntAVLTreeMap.Entry nextEntry() {
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

      Int2IntAVLTreeMap.Entry previousEntry() {
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
            Int2IntAVLTreeMap.this.remove(this.curr.key);
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

   private final class ValueIterator extends Int2IntAVLTreeMap.TreeIterator implements IntListIterator {
      private ValueIterator() {
      }

      @Override
      public int nextInt() {
         return this.nextEntry().value;
      }

      @Override
      public int previousInt() {
         return this.previousEntry().value;
      }
   }
}
