package io.netty.handler.codec.compression;

final class Bzip2DivSufSort {
   private static final int STACK_SIZE = 64;
   private static final int BUCKET_A_SIZE = 256;
   private static final int BUCKET_B_SIZE = 65536;
   private static final int SS_BLOCKSIZE = 1024;
   private static final int INSERTIONSORT_THRESHOLD = 8;
   private static final int[] LOG_2_TABLE = new int[]{
      -1,
      0,
      1,
      1,
      2,
      2,
      2,
      2,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      3,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      4,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      5,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      6,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7,
      7
   };
   private final int[] SA;
   private final byte[] T;
   private final int n;

   Bzip2DivSufSort(byte[] block, int[] bwtBlock, int blockLength) {
      this.T = block;
      this.SA = bwtBlock;
      this.n = blockLength;
   }

   private static void swapElements(int[] array1, int idx1, int[] array2, int idx2) {
      int temp = array1[idx1];
      array1[idx1] = array2[idx2];
      array2[idx2] = temp;
   }

   private int ssCompare(int p1, int p2, int depth) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int U1n = SA[p1 + 1] + 2;
      int U2n = SA[p2 + 1] + 2;
      int U1 = depth + SA[p1];

      int U2;
      for (U2 = depth + SA[p2]; U1 < U1n && U2 < U2n && T[U1] == T[U2]; U2++) {
         U1++;
      }

      return U1 < U1n ? (U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1) : (U2 < U2n ? -1 : 0);
   }

   private int ssCompareLast(int pa, int p1, int p2, int depth, int size) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int U1 = depth + SA[p1];
      int U2 = depth + SA[p2];
      int U1n = size;

      int U2n;
      for (U2n = SA[p2 + 1] + 2; U1 < U1n && U2 < U2n && T[U1] == T[U2]; U2++) {
         U1++;
      }

      if (U1 < U1n) {
         return U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1;
      } else if (U2 == U2n) {
         return 1;
      } else {
         U1 %= size;

         for (U1n = SA[pa] + 2; U1 < U1n && U2 < U2n && T[U1] == T[U2]; U2++) {
            U1++;
         }

         return U1 < U1n ? (U2 < U2n ? (T[U1] & 0xFF) - (T[U2] & 0xFF) : 1) : (U2 < U2n ? -1 : 0);
      }
   }

   private void ssInsertionSort(int pa, int first, int last, int depth) {
      int[] SA = this.SA;

      for (int i = last - 2; first <= i; i--) {
         int t = SA[i];
         int j = i + 1;

         int r;
         while (0 < (r = this.ssCompare(pa + t, pa + SA[j], depth))) {
            do {
               SA[j - 1] = SA[j];
               j++;
            } while (j < last && SA[j] < 0);

            if (last <= j) {
               break;
            }
         }

         if (r == 0) {
            SA[j] = ~SA[j];
         }

         SA[j - 1] = t;
      }
   }

   private void ssFixdown(int td, int pa, int sa, int i, int size) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int v = SA[sa + i];
      int c = T[td + SA[pa + v]] & 255;

      int j;
      while ((j = 2 * i + 1) < size) {
         int k;
         int d = T[td + SA[pa + SA[sa + (k = j++)]]] & 255;
         int e;
         if (d < (e = T[td + SA[pa + SA[sa + j]]] & 255)) {
            k = j;
            d = e;
         }

         if (d <= c) {
            break;
         }

         SA[sa + i] = SA[sa + k];
         i = k;
      }

      SA[sa + i] = v;
   }

   private void ssHeapSort(int td, int pa, int sa, int size) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int m = size;
      if (size % 2 == 0) {
         m = size - 1;
         if ((T[td + SA[pa + SA[sa + m / 2]]] & 255) < (T[td + SA[pa + SA[sa + m]]] & 255)) {
            swapElements(SA, sa + m, SA, sa + m / 2);
         }
      }

      for (int i = m / 2 - 1; 0 <= i; i--) {
         this.ssFixdown(td, pa, sa, i, m);
      }

      if (size % 2 == 0) {
         swapElements(SA, sa, SA, sa + m);
         this.ssFixdown(td, pa, sa, 0, m);
      }

      for (int var10 = m - 1; 0 < var10; var10--) {
         int t = SA[sa];
         SA[sa] = SA[sa + var10];
         this.ssFixdown(td, pa, sa, 0, var10);
         SA[sa + var10] = t;
      }
   }

   private int ssMedian3(int td, int pa, int v1, int v2, int v3) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
      int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
      int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
      if (T_v1 > T_v2) {
         int temp = v1;
         v1 = v2;
         v2 = temp;
         int T_vtemp = T_v1;
         T_v1 = T_v2;
         T_v2 = T_vtemp;
      }

      if (T_v2 > T_v3) {
         return T_v1 > T_v3 ? v1 : v3;
      } else {
         return v2;
      }
   }

   private int ssMedian5(int td, int pa, int v1, int v2, int v3, int v4, int v5) {
      int[] SA = this.SA;
      byte[] T = this.T;
      int T_v1 = T[td + SA[pa + SA[v1]]] & 255;
      int T_v2 = T[td + SA[pa + SA[v2]]] & 255;
      int T_v3 = T[td + SA[pa + SA[v3]]] & 255;
      int T_v4 = T[td + SA[pa + SA[v4]]] & 255;
      int T_v5 = T[td + SA[pa + SA[v5]]] & 255;
      if (T_v2 > T_v3) {
         int temp = v2;
         v2 = v3;
         v3 = temp;
         int T_vtemp = T_v2;
         T_v2 = T_v3;
         T_v3 = T_vtemp;
      }

      if (T_v4 > T_v5) {
         int temp = v4;
         v4 = v5;
         v5 = temp;
         int T_vtemp = T_v4;
         T_v4 = T_v5;
         T_v5 = T_vtemp;
      }

      if (T_v2 > T_v4) {
         v4 = v2;
         T_v4 = T_v2;
         int var18 = v3;
         v3 = v5;
         v5 = var18;
         int var21 = T_v3;
         T_v3 = T_v5;
         T_v5 = var21;
      }

      if (T_v1 > T_v3) {
         int temp = v1;
         v1 = v3;
         v3 = temp;
         int T_vtemp = T_v1;
         T_v1 = T_v3;
         T_v3 = T_vtemp;
      }

      if (T_v1 > T_v4) {
         v4 = v1;
         T_v4 = T_v1;
         v3 = v5;
         T_v3 = T_v5;
      }

      return T_v3 > T_v4 ? v4 : v3;
   }

   private int ssPivot(int td, int pa, int first, int last) {
      int t = last - first;
      int middle = first + t / 2;
      if (t <= 512) {
         if (t <= 32) {
            return this.ssMedian3(td, pa, first, middle, last - 1);
         } else {
            t >>= 2;
            return this.ssMedian5(td, pa, first, first + t, middle, last - 1 - t, last - 1);
         }
      } else {
         t >>= 3;
         return this.ssMedian3(
            td,
            pa,
            this.ssMedian3(td, pa, first, first + t, first + (t << 1)),
            this.ssMedian3(td, pa, middle - t, middle, middle + t),
            this.ssMedian3(td, pa, last - 1 - (t << 1), last - 1 - t, last - 1)
         );
      }
   }

   private static int ssLog(int n) {
      return (n & 0xFF00) != 0 ? 8 + LOG_2_TABLE[n >> 8 & 0xFF] : LOG_2_TABLE[n & 0xFF];
   }

   private int ssSubstringPartition(int pa, int first, int last, int depth) {
      int[] SA = this.SA;
      int a = first - 1;
      int b = last;

      while (true) {
         a++;
         if (a < b && SA[pa + SA[a]] + depth >= SA[pa + SA[a] + 1] + 1) {
            SA[a] = ~SA[a];
         } else {
            b--;

            while (a < b && SA[pa + SA[b]] + depth < SA[pa + SA[b] + 1] + 1) {
               b--;
            }

            if (b <= a) {
               if (first < a) {
                  SA[first] = ~SA[first];
               }

               return a;
            }

            int t = ~SA[b];
            SA[b] = SA[a];
            SA[a] = t;
         }
      }
   }

   private void ssMultiKeyIntroSort(int pa, int first, int last, int depth) {
      int[] SA = this.SA;
      byte[] T = this.T;
      Bzip2DivSufSort.StackEntry[] stack = new Bzip2DivSufSort.StackEntry[64];
      int x = 0;
      int ssize = 0;
      int limit = ssLog(last - first);

      while (true) {
         label197:
         while (last - first > 8) {
            int Td = depth;
            if (limit-- == 0) {
               this.ssHeapSort(depth, pa, first, last - first);
            }

            if (limit < 0) {
               int a = first + 1;

               for (int v = T[depth + SA[pa + SA[first]]] & 255; a < last; a++) {
                  if ((x = T[Td + SA[pa + SA[a]]] & 255) != v) {
                     if (1 < a - first) {
                        if ((T[Td + SA[pa + SA[first]] - 1] & 255) < v) {
                           first = this.ssSubstringPartition(pa, first, a, depth);
                        }

                        if (a - first <= last - a) {
                           if (1 < a - first) {
                              stack[ssize++] = new Bzip2DivSufSort.StackEntry(a, last, depth, -1);
                              last = a;
                              depth++;
                              limit = ssLog(a - first);
                           } else {
                              first = a;
                              limit = -1;
                           }
                        } else if (1 < last - a) {
                           stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, depth + 1, ssLog(a - first));
                           first = a;
                           limit = -1;
                        } else {
                           last = a;
                           depth++;
                           limit = ssLog(a - first);
                        }
                        continue label197;
                     }

                     v = x;
                     first = a;
                  }
               }
               break;
            } else {
               int a = this.ssPivot(depth, pa, first, last);
               int vx = T[depth + SA[pa + SA[a]]] & 255;
               swapElements(SA, first, SA, a);
               int b = first + 1;

               while (b < last && (x = T[Td + SA[pa + SA[b]]] & 255) == vx) {
                  b++;
               }

               a = b;
               if (b < last && x < vx) {
                  while (true) {
                     b++;
                     if (b >= last || (x = T[Td + SA[pa + SA[b]]] & 255) > vx) {
                        break;
                     }

                     if (x == vx) {
                        swapElements(SA, b, SA, a);
                        a++;
                     }
                  }
               }

               int c = last - 1;

               while (b < c && (x = T[Td + SA[pa + SA[c]]] & 255) == vx) {
                  c--;
               }

               int d = c;
               if (b < c && x > vx) {
                  while (true) {
                     c--;
                     if (b >= c || (x = T[Td + SA[pa + SA[c]]] & 255) < vx) {
                        break;
                     }

                     if (x == vx) {
                        swapElements(SA, c, SA, d);
                        d--;
                     }
                  }
               }

               label160:
               while (b < c) {
                  swapElements(SA, b, SA, c);

                  while (true) {
                     b++;
                     if (b >= c || (x = T[Td + SA[pa + SA[b]]] & 255) > vx) {
                        while (true) {
                           c--;
                           if (b >= c || (x = T[Td + SA[pa + SA[c]]] & 255) < vx) {
                              continue label160;
                           }

                           if (x == vx) {
                              swapElements(SA, c, SA, d);
                              d--;
                           }
                        }
                     }

                     if (x == vx) {
                        swapElements(SA, b, SA, a);
                        a++;
                     }
                  }
               }

               if (a > d) {
                  limit++;
                  if ((T[Td + SA[pa + SA[first]] - 1] & 255) < vx) {
                     first = this.ssSubstringPartition(pa, first, last, depth);
                     limit = ssLog(last - first);
                  }

                  depth++;
               } else {
                  c = b - 1;
                  int s;
                  int t;
                  if ((s = a - first) > (t = b - a)) {
                     s = t;
                  }

                  int e = first;

                  for (int f = b - s; 0 < s; f++) {
                     swapElements(SA, e, SA, f);
                     s--;
                     e++;
                  }

                  if ((s = d - c) > (t = last - d - 1)) {
                     s = t;
                  }

                  e = b;

                  for (int var29 = last - s; 0 < s; var29++) {
                     swapElements(SA, e, SA, var29);
                     s--;
                     e++;
                  }

                  a = first + (b - a);
                  c = last - (d - c);
                  b = vx <= (T[Td + SA[pa + SA[a]] - 1] & 255) ? a : this.ssSubstringPartition(pa, a, c, depth);
                  if (a - first <= last - c) {
                     if (last - c <= c - b) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(b, c, depth + 1, ssLog(c - b));
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(c, last, depth, limit);
                        last = a;
                     } else if (a - first <= c - b) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(c, last, depth, limit);
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(b, c, depth + 1, ssLog(c - b));
                        last = a;
                     } else {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(c, last, depth, limit);
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, depth, limit);
                        first = b;
                        last = c;
                        depth++;
                        limit = ssLog(c - b);
                     }
                  } else if (a - first <= c - b) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(b, c, depth + 1, ssLog(c - b));
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, depth, limit);
                     first = c;
                  } else if (last - c <= c - b) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, depth, limit);
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(b, c, depth + 1, ssLog(c - b));
                     first = c;
                  } else {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, depth, limit);
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(c, last, depth, limit);
                     first = b;
                     last = c;
                     depth++;
                     limit = ssLog(c - b);
                  }
               }
            }
         }

         if (1 < last - first) {
            this.ssInsertionSort(pa, first, last, depth);
         }

         if (ssize == 0) {
            return;
         }

         Bzip2DivSufSort.StackEntry entry = stack[--ssize];
         first = entry.a;
         last = entry.b;
         depth = entry.c;
         limit = entry.d;
      }
   }

   private static void ssBlockSwap(int[] array1, int first1, int[] array2, int first2, int size) {
      int i = size;
      int a = first1;

      for (int b = first2; 0 < i; b++) {
         swapElements(array1, a, array2, b);
         i--;
         a++;
      }
   }

   private void ssMergeForward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
      int[] SA = this.SA;
      int bufend = bufoffset + (middle - first) - 1;
      ssBlockSwap(buf, bufoffset, SA, first, middle - first);
      int t = SA[first];
      int i = first;
      int j = bufoffset;
      int k = middle;

      while (true) {
         int r = this.ssCompare(pa + buf[j], pa + SA[k], depth);
         if (r < 0) {
            while (true) {
               SA[i++] = buf[j];
               if (bufend <= j) {
                  buf[j] = t;
                  return;
               }

               buf[j++] = SA[i];
               if (buf[j] >= 0) {
                  break;
               }
            }
         } else if (r > 0) {
            while (true) {
               SA[i++] = SA[k];
               SA[k++] = SA[i];
               if (last <= k) {
                  while (j < bufend) {
                     SA[i++] = buf[j];
                     buf[j++] = SA[i];
                  }

                  SA[i] = buf[j];
                  buf[j] = t;
                  return;
               }

               if (SA[k] >= 0) {
                  break;
               }
            }
         } else {
            SA[k] = ~SA[k];

            do {
               SA[i++] = buf[j];
               if (bufend <= j) {
                  buf[j] = t;
                  return;
               }

               buf[j++] = SA[i];
            } while (buf[j] < 0);

            while (true) {
               SA[i++] = SA[k];
               SA[k++] = SA[i];
               if (last <= k) {
                  while (j < bufend) {
                     SA[i++] = buf[j];
                     buf[j++] = SA[i];
                  }

                  SA[i] = buf[j];
                  buf[j] = t;
                  return;
               }

               if (SA[k] >= 0) {
                  break;
               }
            }
         }
      }
   }

   private void ssMergeBackward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
      int[] SA = this.SA;
      int bufend = bufoffset + (last - middle);
      ssBlockSwap(buf, bufoffset, SA, middle, last - middle);
      int x = 0;
      int p1;
      if (buf[bufend - 1] < 0) {
         x |= 1;
         p1 = pa + ~buf[bufend - 1];
      } else {
         p1 = pa + buf[bufend - 1];
      }

      int p2;
      if (SA[middle - 1] < 0) {
         x |= 2;
         p2 = pa + ~SA[middle - 1];
      } else {
         p2 = pa + SA[middle - 1];
      }

      int t = SA[last - 1];
      int i = last - 1;
      int j = bufend - 1;
      int k = middle - 1;

      while (true) {
         int r = this.ssCompare(p1, p2, depth);
         if (r > 0) {
            if ((x & 1) != 0) {
               do {
                  SA[i--] = buf[j];
                  buf[j--] = SA[i];
               } while (buf[j] < 0);

               x ^= 1;
            }

            SA[i--] = buf[j];
            if (j <= bufoffset) {
               buf[j] = t;
               return;
            }

            buf[j--] = SA[i];
            if (buf[j] < 0) {
               x |= 1;
               p1 = pa + ~buf[j];
            } else {
               p1 = pa + buf[j];
            }
         } else if (r < 0) {
            if ((x & 2) != 0) {
               do {
                  SA[i--] = SA[k];
                  SA[k--] = SA[i];
               } while (SA[k] < 0);

               x ^= 2;
            }

            SA[i--] = SA[k];
            SA[k--] = SA[i];
            if (k < first) {
               while (bufoffset < j) {
                  SA[i--] = buf[j];
                  buf[j--] = SA[i];
               }

               SA[i] = buf[j];
               buf[j] = t;
               return;
            }

            if (SA[k] < 0) {
               x |= 2;
               p2 = pa + ~SA[k];
            } else {
               p2 = pa + SA[k];
            }
         } else {
            if ((x & 1) != 0) {
               do {
                  SA[i--] = buf[j];
                  buf[j--] = SA[i];
               } while (buf[j] < 0);

               x ^= 1;
            }

            SA[i--] = ~buf[j];
            if (j <= bufoffset) {
               buf[j] = t;
               return;
            }

            buf[j--] = SA[i];
            if ((x & 2) != 0) {
               do {
                  SA[i--] = SA[k];
                  SA[k--] = SA[i];
               } while (SA[k] < 0);

               x ^= 2;
            }

            SA[i--] = SA[k];
            SA[k--] = SA[i];
            if (k < first) {
               while (bufoffset < j) {
                  SA[i--] = buf[j];
                  buf[j--] = SA[i];
               }

               SA[i] = buf[j];
               buf[j] = t;
               return;
            }

            if (buf[j] < 0) {
               x |= 1;
               p1 = pa + ~buf[j];
            } else {
               p1 = pa + buf[j];
            }

            if (SA[k] < 0) {
               x |= 2;
               p2 = pa + ~SA[k];
            } else {
               p2 = pa + SA[k];
            }
         }
      }
   }

   private static int getIDX(int a) {
      return 0 <= a ? a : ~a;
   }

   private void ssMergeCheckEqual(int pa, int depth, int a) {
      int[] SA = this.SA;
      if (0 <= SA[a] && this.ssCompare(pa + getIDX(SA[a - 1]), pa + SA[a], depth) == 0) {
         SA[a] = ~SA[a];
      }
   }

   private void ssMerge(int pa, int first, int middle, int last, int[] buf, int bufoffset, int bufsize, int depth) {
      int[] SA = this.SA;
      Bzip2DivSufSort.StackEntry[] stack = new Bzip2DivSufSort.StackEntry[64];
      int check = 0;
      int ssize = 0;

      while (true) {
         while (last - middle > bufsize) {
            if (middle - first <= bufsize) {
               if (first < middle) {
                  this.ssMergeForward(pa, buf, bufoffset, first, middle, last, depth);
               }

               if ((check & 1) != 0) {
                  this.ssMergeCheckEqual(pa, depth, first);
               }

               if ((check & 2) != 0) {
                  this.ssMergeCheckEqual(pa, depth, last);
               }

               if (ssize == 0) {
                  return;
               }

               Bzip2DivSufSort.StackEntry entry = stack[--ssize];
               first = entry.a;
               middle = entry.b;
               last = entry.c;
               check = entry.d;
            } else {
               int m = 0;
               int len = Math.min(middle - first, last - middle);

               for (int half = len >> 1; 0 < len; half >>= 1) {
                  if (this.ssCompare(pa + getIDX(SA[middle + m + half]), pa + getIDX(SA[middle - m - half - 1]), depth) < 0) {
                     m += half + 1;
                     half -= len & 1 ^ 1;
                  }

                  len = half;
               }

               if (0 >= m) {
                  if ((check & 1) != 0) {
                     this.ssMergeCheckEqual(pa, depth, first);
                  }

                  this.ssMergeCheckEqual(pa, depth, middle);
                  if ((check & 2) != 0) {
                     this.ssMergeCheckEqual(pa, depth, last);
                  }

                  if (ssize == 0) {
                     return;
                  }

                  Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                  first = entry.a;
                  middle = entry.b;
                  last = entry.c;
                  check = entry.d;
               } else {
                  ssBlockSwap(SA, middle - m, SA, middle, m);
                  int j = middle;
                  int i = middle;
                  int next = 0;
                  if (middle + m < last) {
                     if (SA[middle + m] < 0) {
                        while (SA[i - 1] < 0) {
                           i--;
                        }

                        SA[middle + m] = ~SA[middle + m];
                     }

                     j = middle;

                     while (SA[j] < 0) {
                        j++;
                     }

                     next = 1;
                  }

                  if (i - first <= last - j) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(j, middle + m, last, check & 2 | next & 1);
                     middle -= m;
                     last = i;
                     check &= 1;
                  } else {
                     if (i == middle && middle == j) {
                        next <<= 1;
                     }

                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, middle - m, i, check & 1 | next & 2);
                     first = j;
                     middle += m;
                     check = check & 2 | next & 1;
                  }
               }
            }
         }

         if (first < middle && middle < last) {
            this.ssMergeBackward(pa, buf, bufoffset, first, middle, last, depth);
         }

         if ((check & 1) != 0) {
            this.ssMergeCheckEqual(pa, depth, first);
         }

         if ((check & 2) != 0) {
            this.ssMergeCheckEqual(pa, depth, last);
         }

         if (ssize == 0) {
            return;
         }

         Bzip2DivSufSort.StackEntry entry = stack[--ssize];
         first = entry.a;
         middle = entry.b;
         last = entry.c;
         check = entry.d;
      }
   }

   private void subStringSort(int pa, int first, int last, int[] buf, int bufoffset, int bufsize, int depth, boolean lastsuffix, int size) {
      int[] SA = this.SA;
      if (lastsuffix) {
         first++;
      }

      int a = first;

      int i;
      for (i = 0; a + 1024 < last; i++) {
         this.ssMultiKeyIntroSort(pa, a, a + 1024, depth);
         int[] curbuf = SA;
         int curbufoffset = a + 1024;
         int curbufsize = last - (a + 1024);
         if (curbufsize <= bufsize) {
            curbufsize = bufsize;
            curbuf = buf;
            curbufoffset = bufoffset;
         }

         int b = a;
         int k = 1024;

         for (int j = i; (j & 1) != 0; j >>>= 1) {
            this.ssMerge(pa, b - k, b, b + k, curbuf, curbufoffset, curbufsize, depth);
            b -= k;
            k <<= 1;
         }

         a += 1024;
      }

      this.ssMultiKeyIntroSort(pa, a, last, depth);

      for (int k = 1024; i != 0; i >>= 1) {
         if ((i & 1) != 0) {
            this.ssMerge(pa, a - k, a, last, buf, bufoffset, bufsize, depth);
            a -= k;
         }

         k <<= 1;
      }

      if (lastsuffix) {
         a = first;
         i = SA[first - 1];

         int r;
         for (r = 1; a < last && (SA[a] < 0 || 0 < (r = this.ssCompareLast(pa, pa + i, pa + SA[a], depth, size))); a++) {
            SA[a - 1] = SA[a];
         }

         if (r == 0) {
            SA[a] = ~SA[a];
         }

         SA[a - 1] = i;
      }
   }

   private int trGetC(int isa, int isaD, int isaN, int p) {
      return isaD + p < isaN ? this.SA[isaD + p] : this.SA[isa + (isaD - isa + p) % (isaN - isa)];
   }

   private void trFixdown(int isa, int isaD, int isaN, int sa, int i, int size) {
      int[] SA = this.SA;
      int v = SA[sa + i];
      int c = this.trGetC(isa, isaD, isaN, v);

      int j;
      while ((j = 2 * i + 1) < size) {
         int k = j++;
         int d = this.trGetC(isa, isaD, isaN, SA[sa + k]);
         int e;
         if (d < (e = this.trGetC(isa, isaD, isaN, SA[sa + j]))) {
            k = j;
            d = e;
         }

         if (d <= c) {
            break;
         }

         SA[sa + i] = SA[sa + k];
         i = k;
      }

      SA[sa + i] = v;
   }

   private void trHeapSort(int isa, int isaD, int isaN, int sa, int size) {
      int[] SA = this.SA;
      int m = size;
      if (size % 2 == 0) {
         m = size - 1;
         if (this.trGetC(isa, isaD, isaN, SA[sa + m / 2]) < this.trGetC(isa, isaD, isaN, SA[sa + m])) {
            swapElements(SA, sa + m, SA, sa + m / 2);
         }
      }

      for (int i = m / 2 - 1; 0 <= i; i--) {
         this.trFixdown(isa, isaD, isaN, sa, i, m);
      }

      if (size % 2 == 0) {
         swapElements(SA, sa, SA, sa + m);
         this.trFixdown(isa, isaD, isaN, sa, 0, m);
      }

      for (int var10 = m - 1; 0 < var10; var10--) {
         int t = SA[sa];
         SA[sa] = SA[sa + var10];
         this.trFixdown(isa, isaD, isaN, sa, 0, var10);
         SA[sa + var10] = t;
      }
   }

   private void trInsertionSort(int isa, int isaD, int isaN, int first, int last) {
      int[] SA = this.SA;

      for (int a = first + 1; a < last; a++) {
         int t = SA[a];
         int b = a - 1;

         int r;
         while (0 > (r = this.trGetC(isa, isaD, isaN, t) - this.trGetC(isa, isaD, isaN, SA[b]))) {
            do {
               SA[b + 1] = SA[b];
               b--;
            } while (first <= b && SA[b] < 0);

            if (b < first) {
               break;
            }
         }

         if (r == 0) {
            SA[b] = ~SA[b];
         }

         SA[b + 1] = t;
      }
   }

   private static int trLog(int n) {
      return (n & -65536) != 0
         ? ((n & 0xFF000000) != 0 ? 24 + LOG_2_TABLE[n >> 24 & 0xFF] : LOG_2_TABLE[n >> 16 & 271])
         : ((n & 0xFF00) != 0 ? 8 + LOG_2_TABLE[n >> 8 & 0xFF] : LOG_2_TABLE[n & 0xFF]);
   }

   private int trMedian3(int isa, int isaD, int isaN, int v1, int v2, int v3) {
      int[] SA = this.SA;
      int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
      int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
      int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
      if (SA_v1 > SA_v2) {
         int temp = v1;
         v1 = v2;
         v2 = temp;
         int SA_vtemp = SA_v1;
         SA_v1 = SA_v2;
         SA_v2 = SA_vtemp;
      }

      if (SA_v2 > SA_v3) {
         return SA_v1 > SA_v3 ? v1 : v3;
      } else {
         return v2;
      }
   }

   private int trMedian5(int isa, int isaD, int isaN, int v1, int v2, int v3, int v4, int v5) {
      int[] SA = this.SA;
      int SA_v1 = this.trGetC(isa, isaD, isaN, SA[v1]);
      int SA_v2 = this.trGetC(isa, isaD, isaN, SA[v2]);
      int SA_v3 = this.trGetC(isa, isaD, isaN, SA[v3]);
      int SA_v4 = this.trGetC(isa, isaD, isaN, SA[v4]);
      int SA_v5 = this.trGetC(isa, isaD, isaN, SA[v5]);
      if (SA_v2 > SA_v3) {
         int temp = v2;
         v2 = v3;
         v3 = temp;
         int SA_vtemp = SA_v2;
         SA_v2 = SA_v3;
         SA_v3 = SA_vtemp;
      }

      if (SA_v4 > SA_v5) {
         int temp = v4;
         v4 = v5;
         v5 = temp;
         int SA_vtemp = SA_v4;
         SA_v4 = SA_v5;
         SA_v5 = SA_vtemp;
      }

      if (SA_v2 > SA_v4) {
         v4 = v2;
         SA_v4 = SA_v2;
         int var18 = v3;
         v3 = v5;
         v5 = var18;
         int var21 = SA_v3;
         SA_v3 = SA_v5;
         SA_v5 = var21;
      }

      if (SA_v1 > SA_v3) {
         int temp = v1;
         v1 = v3;
         v3 = temp;
         int SA_vtemp = SA_v1;
         SA_v1 = SA_v3;
         SA_v3 = SA_vtemp;
      }

      if (SA_v1 > SA_v4) {
         v4 = v1;
         SA_v4 = SA_v1;
         v3 = v5;
         SA_v3 = SA_v5;
      }

      return SA_v3 > SA_v4 ? v4 : v3;
   }

   private int trPivot(int isa, int isaD, int isaN, int first, int last) {
      int t = last - first;
      int middle = first + t / 2;
      if (t <= 512) {
         if (t <= 32) {
            return this.trMedian3(isa, isaD, isaN, first, middle, last - 1);
         } else {
            t >>= 2;
            return this.trMedian5(isa, isaD, isaN, first, first + t, middle, last - 1 - t, last - 1);
         }
      } else {
         t >>= 3;
         return this.trMedian3(
            isa,
            isaD,
            isaN,
            this.trMedian3(isa, isaD, isaN, first, first + t, first + (t << 1)),
            this.trMedian3(isa, isaD, isaN, middle - t, middle, middle + t),
            this.trMedian3(isa, isaD, isaN, last - 1 - (t << 1), last - 1 - t, last - 1)
         );
      }
   }

   private void lsUpdateGroup(int isa, int first, int last) {
      int[] SA = this.SA;

      for (int a = first; a < last; a++) {
         if (0 <= SA[a]) {
            int b = a;

            do {
               SA[isa + SA[a]] = a++;
            } while (a < last && 0 <= SA[a]);

            SA[b] = b - a;
            if (last <= a) {
               break;
            }
         }

         int b = a;

         do {
            SA[a] = ~SA[a];
         } while (SA[++a] < 0);

         int t = a;

         do {
            SA[isa + SA[b]] = t;
         } while (++b <= a);
      }
   }

   private void lsIntroSort(int isa, int isaD, int isaN, int first, int last) {
      int[] SA = this.SA;
      Bzip2DivSufSort.StackEntry[] stack = new Bzip2DivSufSort.StackEntry[64];
      int x = 0;
      int ssize = 0;
      int limit = trLog(last - first);

      while (true) {
         while (last - first > 8) {
            if (limit-- == 0) {
               this.trHeapSort(isa, isaD, isaN, first, last - first);
               int a = last - 1;

               while (first < a) {
                  x = this.trGetC(isa, isaD, isaN, SA[a]);

                  int b;
                  for (b = a - 1; first <= b && this.trGetC(isa, isaD, isaN, SA[b]) == x; b--) {
                     SA[b] = ~SA[b];
                  }

                  a = b;
               }

               this.lsUpdateGroup(isa, first, last);
               if (ssize == 0) {
                  return;
               }

               Bzip2DivSufSort.StackEntry entry = stack[--ssize];
               first = entry.a;
               last = entry.b;
               limit = entry.c;
            } else {
               int a = this.trPivot(isa, isaD, isaN, first, last);
               swapElements(SA, first, SA, a);
               int v = this.trGetC(isa, isaD, isaN, SA[first]);
               int b = first + 1;

               while (b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v) {
                  b++;
               }

               a = b;
               if (b < last && x < v) {
                  while (true) {
                     b++;
                     if (b >= last || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
                        break;
                     }

                     if (x == v) {
                        swapElements(SA, b, SA, a);
                        a++;
                     }
                  }
               }

               int c = last - 1;

               while (b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v) {
                  c--;
               }

               int d = c;
               if (b < c && x > v) {
                  while (true) {
                     c--;
                     if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
                        break;
                     }

                     if (x == v) {
                        swapElements(SA, c, SA, d);
                        d--;
                     }
                  }
               }

               label166:
               while (b < c) {
                  swapElements(SA, b, SA, c);

                  while (true) {
                     b++;
                     if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
                        while (true) {
                           c--;
                           if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
                              continue label166;
                           }

                           if (x == v) {
                              swapElements(SA, c, SA, d);
                              d--;
                           }
                        }
                     }

                     if (x == v) {
                        swapElements(SA, b, SA, a);
                        a++;
                     }
                  }
               }

               if (a > d) {
                  if (ssize == 0) {
                     return;
                  }

                  Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                  first = entry.a;
                  last = entry.b;
                  limit = entry.c;
               } else {
                  c = b - 1;
                  int s;
                  int t;
                  if ((s = a - first) > (t = b - a)) {
                     s = t;
                  }

                  int e = first;

                  for (int f = b - s; 0 < s; f++) {
                     swapElements(SA, e, SA, f);
                     s--;
                     e++;
                  }

                  if ((s = d - c) > (t = last - d - 1)) {
                     s = t;
                  }

                  e = b;

                  for (int var30 = last - s; 0 < s; var30++) {
                     swapElements(SA, e, SA, var30);
                     s--;
                     e++;
                  }

                  a = first + (b - a);
                  b = last - (d - c);
                  c = first;

                  for (int var33 = a - 1; c < a; c++) {
                     SA[isa + SA[c]] = var33;
                  }

                  if (b < last) {
                     c = a;

                     for (int var34 = b - 1; c < b; c++) {
                        SA[isa + SA[c]] = var34;
                     }
                  }

                  if (b - a == 1) {
                     SA[a] = -1;
                  }

                  if (a - first <= last - b) {
                     if (first < a) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(b, last, limit, 0);
                        last = a;
                     } else {
                        first = b;
                     }
                  } else if (b < last) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(first, a, limit, 0);
                     first = b;
                  } else {
                     last = a;
                  }
               }
            }
         }

         if (1 < last - first) {
            this.trInsertionSort(isa, isaD, isaN, first, last);
            this.lsUpdateGroup(isa, first, last);
         } else if (last - first == 1) {
            SA[first] = -1;
         }

         if (ssize == 0) {
            return;
         }

         Bzip2DivSufSort.StackEntry entry = stack[--ssize];
         first = entry.a;
         last = entry.b;
         limit = entry.c;
      }
   }

   private void lsSort(int isa, int n, int depth) {
      int[] SA = this.SA;

      for (int isaD = isa + depth; -n < SA[0]; isaD += isaD - isa) {
         int first = 0;
         int skip = 0;

         do {
            int t;
            if ((t = SA[first]) < 0) {
               first -= t;
               skip += t;
            } else {
               if (skip != 0) {
                  SA[first + skip] = skip;
                  skip = 0;
               }

               int last = SA[isa + t] + 1;
               this.lsIntroSort(isa, isaD, isa + n, first, last);
               first = last;
            }
         } while (first < n);

         if (skip != 0) {
            SA[first + skip] = skip;
         }

         if (n < isaD - isa) {
            first = 0;

            do {
               int var13;
               if ((var13 = SA[first]) < 0) {
                  first -= var13;
               } else {
                  int last = SA[isa + var13] + 1;
                  int i = first;

                  while (i < last) {
                     SA[isa + SA[i]] = i++;
                  }

                  first = last;
               }
            } while (first < n);

            return;
         }
      }
   }

   private Bzip2DivSufSort.PartitionResult trPartition(int isa, int isaD, int isaN, int first, int last, int v) {
      int[] SA = this.SA;
      int x = 0;
      int b = first;

      while (b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v) {
         b++;
      }

      int a = b;
      if (b < last && x < v) {
         while (true) {
            b++;
            if (b >= last || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
               break;
            }

            if (x == v) {
               swapElements(SA, b, SA, a);
               a++;
            }
         }
      }

      int c = last - 1;

      while (b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v) {
         c--;
      }

      int d = c;
      if (b < c && x > v) {
         while (true) {
            c--;
            if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
               break;
            }

            if (x == v) {
               swapElements(SA, c, SA, d);
               d--;
            }
         }
      }

      label85:
      while (b < c) {
         swapElements(SA, b, SA, c);

         while (true) {
            b++;
            if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
               while (true) {
                  c--;
                  if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
                     continue label85;
                  }

                  if (x == v) {
                     swapElements(SA, c, SA, d);
                     d--;
                  }
               }
            }

            if (x == v) {
               swapElements(SA, b, SA, a);
               a++;
            }
         }
      }

      if (a <= d) {
         c = b - 1;
         int t;
         int s;
         if ((s = a - first) > (t = b - a)) {
            s = t;
         }

         int e = first;

         for (int f = b - s; 0 < s; f++) {
            swapElements(SA, e, SA, f);
            s--;
            e++;
         }

         if ((s = d - c) > (t = last - d - 1)) {
            s = t;
         }

         e = b;

         for (int var19 = last - s; 0 < s; var19++) {
            swapElements(SA, e, SA, var19);
            s--;
            e++;
         }

         first += b - a;
         last -= d - c;
      }

      return new Bzip2DivSufSort.PartitionResult(first, last);
   }

   private void trCopy(int isa, int isaN, int first, int a, int b, int last, int depth) {
      int[] SA = this.SA;
      int v = b - 1;
      int c = first;

      int d;
      for (d = a - 1; c <= d; c++) {
         int s;
         if ((s = SA[c] - depth) < 0) {
            s += isaN - isa;
         }

         if (SA[isa + s] == v) {
            SA[++d] = s;
            SA[isa + s] = d;
         }
      }

      c = last - 1;
      int e = d + 1;

      for (int var15 = b; e < var15; c--) {
         int sx;
         if ((sx = SA[c] - depth) < 0) {
            sx += isaN - isa;
         }

         if (SA[isa + sx] == v) {
            SA[--var15] = sx;
            SA[isa + sx] = var15;
         }
      }
   }

   private void trIntroSort(int isa, int isaD, int isaN, int first, int last, Bzip2DivSufSort.TRBudget budget, int size) {
      int[] SA = this.SA;
      Bzip2DivSufSort.StackEntry[] stack = new Bzip2DivSufSort.StackEntry[64];
      int x = 0;
      int ssize = 0;
      int limit = trLog(last - first);

      while (true) {
         if (limit < 0) {
            if (limit == -1) {
               if (!budget.update(size, last - first)) {
                  break;
               }

               Bzip2DivSufSort.PartitionResult result = this.trPartition(isa, isaD - 1, isaN, first, last, last - 1);
               int a = result.first;
               int b = result.last;
               if (first >= a && b >= last) {
                  int c = first;

                  while (c < last) {
                     SA[isa + SA[c]] = c++;
                  }

                  if (ssize == 0) {
                     return;
                  }

                  Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                  isaD = entry.a;
                  first = entry.b;
                  last = entry.c;
                  limit = entry.d;
               } else {
                  if (a < last) {
                     int c = first;

                     for (int v = a - 1; c < a; c++) {
                        SA[isa + SA[c]] = v;
                     }
                  }

                  if (b < last) {
                     int c = a;

                     for (int v = b - 1; c < b; c++) {
                        SA[isa + SA[c]] = v;
                     }
                  }

                  stack[ssize++] = new Bzip2DivSufSort.StackEntry(0, a, b, 0);
                  stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD - 1, first, last, -2);
                  if (a - first <= last - b) {
                     if (1 < a - first) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, trLog(last - b));
                        last = a;
                        limit = trLog(a - first);
                     } else if (1 < last - b) {
                        first = b;
                        limit = trLog(last - b);
                     } else {
                        if (ssize == 0) {
                           return;
                        }

                        Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                        isaD = entry.a;
                        first = entry.b;
                        last = entry.c;
                        limit = entry.d;
                     }
                  } else if (1 < last - b) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, trLog(a - first));
                     first = b;
                     limit = trLog(last - b);
                  } else if (1 < a - first) {
                     last = a;
                     limit = trLog(a - first);
                  } else {
                     if (ssize == 0) {
                        return;
                     }

                     Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                     isaD = entry.a;
                     first = entry.b;
                     last = entry.c;
                     limit = entry.d;
                  }
               }
            } else if (limit == -2) {
               int a = stack[--ssize].b;
               int b = stack[ssize].c;
               this.trCopy(isa, isaN, first, a, b, last, isaD - isa);
               if (ssize == 0) {
                  return;
               }

               Bzip2DivSufSort.StackEntry entry = stack[--ssize];
               isaD = entry.a;
               first = entry.b;
               last = entry.c;
               limit = entry.d;
            } else {
               if (0 <= SA[first]) {
                  int a = first;

                  do {
                     SA[isa + SA[a]] = a++;
                  } while (a < last && 0 <= SA[a]);

                  first = a;
               }

               if (first >= last) {
                  if (ssize == 0) {
                     return;
                  }

                  Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                  isaD = entry.a;
                  first = entry.b;
                  last = entry.c;
                  limit = entry.d;
               } else {
                  int a = first;

                  do {
                     SA[a] = ~SA[a];
                  } while (SA[++a] < 0);

                  int next = SA[isa + SA[a]] != SA[isaD + SA[a]] ? trLog(a - first + 1) : -1;
                  if (++a < last) {
                     int b = first;

                     for (int v = a - 1; b < a; b++) {
                        SA[isa + SA[b]] = v;
                     }
                  }

                  if (a - first <= last - a) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, a, last, -3);
                     isaD++;
                     last = a;
                     limit = next;
                  } else if (1 < last - a) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, first, a, next);
                     first = a;
                     limit = -3;
                  } else {
                     isaD++;
                     last = a;
                     limit = next;
                  }
               }
            }
         } else if (last - first <= 8) {
            if (!budget.update(size, last - first)) {
               break;
            }

            this.trInsertionSort(isa, isaD, isaN, first, last);
            limit = -3;
         } else if (limit-- == 0) {
            if (!budget.update(size, last - first)) {
               break;
            }

            this.trHeapSort(isa, isaD, isaN, first, last - first);
            int a = last - 1;

            while (first < a) {
               x = this.trGetC(isa, isaD, isaN, SA[a]);

               int b;
               for (b = a - 1; first <= b && this.trGetC(isa, isaD, isaN, SA[b]) == x; b--) {
                  SA[b] = ~SA[b];
               }

               a = b;
            }

            limit = -3;
         } else {
            int a = this.trPivot(isa, isaD, isaN, first, last);
            swapElements(SA, first, SA, a);
            int v = this.trGetC(isa, isaD, isaN, SA[first]);
            int b = first + 1;

            while (b < last && (x = this.trGetC(isa, isaD, isaN, SA[b])) == v) {
               b++;
            }

            a = b;
            if (b < last && x < v) {
               while (true) {
                  b++;
                  if (b >= last || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
                     break;
                  }

                  if (x == v) {
                     swapElements(SA, b, SA, a);
                     a++;
                  }
               }
            }

            int c = last - 1;

            while (b < c && (x = this.trGetC(isa, isaD, isaN, SA[c])) == v) {
               c--;
            }

            int d = c;
            if (b < c && x > v) {
               while (true) {
                  c--;
                  if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
                     break;
                  }

                  if (x == v) {
                     swapElements(SA, c, SA, d);
                     d--;
                  }
               }
            }

            label353:
            while (b < c) {
               swapElements(SA, b, SA, c);

               while (true) {
                  b++;
                  if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[b])) > v) {
                     while (true) {
                        c--;
                        if (b >= c || (x = this.trGetC(isa, isaD, isaN, SA[c])) < v) {
                           continue label353;
                        }

                        if (x == v) {
                           swapElements(SA, c, SA, d);
                           d--;
                        }
                     }
                  }

                  if (x == v) {
                     swapElements(SA, b, SA, a);
                     a++;
                  }
               }
            }

            if (a > d) {
               if (!budget.update(size, last - first)) {
                  break;
               }

               limit++;
               isaD++;
            } else {
               c = b - 1;
               int s;
               int t;
               if ((s = a - first) > (t = b - a)) {
                  s = t;
               }

               int e = first;

               for (int f = b - s; 0 < s; f++) {
                  swapElements(SA, e, SA, f);
                  s--;
                  e++;
               }

               if ((s = d - c) > (t = last - d - 1)) {
                  s = t;
               }

               e = b;

               for (int var45 = last - s; 0 < s; var45++) {
                  swapElements(SA, e, SA, var45);
                  s--;
                  e++;
               }

               a = first + (b - a);
               b = last - (d - c);
               int nextx = SA[isa + SA[a]] != v ? trLog(b - a) : -1;
               c = first;

               for (int var49 = a - 1; c < a; c++) {
                  SA[isa + SA[c]] = var49;
               }

               if (b < last) {
                  c = a;

                  for (int var50 = b - 1; c < b; c++) {
                     SA[isa + SA[c]] = var50;
                  }
               }

               if (a - first <= last - b) {
                  if (last - b <= b - a) {
                     if (1 < a - first) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                        last = a;
                     } else if (1 < last - b) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                        first = b;
                     } else if (1 < b - a) {
                        isaD++;
                        first = a;
                        last = b;
                        limit = nextx;
                     } else {
                        if (ssize == 0) {
                           return;
                        }

                        Bzip2DivSufSort.StackEntry entry = stack[--ssize];
                        isaD = entry.a;
                        first = entry.b;
                        last = entry.c;
                        limit = entry.d;
                     }
                  } else if (a - first <= b - a) {
                     if (1 < a - first) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                        last = a;
                     } else if (1 < b - a) {
                        stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                        isaD++;
                        first = a;
                        last = b;
                        limit = nextx;
                     } else {
                        first = b;
                     }
                  } else if (1 < b - a) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                     isaD++;
                     first = a;
                     last = b;
                     limit = nextx;
                  } else {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                     last = a;
                  }
               } else if (a - first <= b - a) {
                  if (1 < last - b) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                     first = b;
                  } else if (1 < a - first) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                     last = a;
                  } else if (1 < b - a) {
                     isaD++;
                     first = a;
                     last = b;
                     limit = nextx;
                  } else {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, last, limit);
                  }
               } else if (last - b <= b - a) {
                  if (1 < last - b) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD + 1, a, b, nextx);
                     first = b;
                  } else if (1 < b - a) {
                     stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                     isaD++;
                     first = a;
                     last = b;
                     limit = nextx;
                  } else {
                     last = a;
                  }
               } else if (1 < b - a) {
                  stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                  stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, b, last, limit);
                  isaD++;
                  first = a;
                  last = b;
                  limit = nextx;
               } else {
                  stack[ssize++] = new Bzip2DivSufSort.StackEntry(isaD, first, a, limit);
                  first = b;
               }
            }
         }
      }

      for (int sx = 0; sx < ssize; sx++) {
         if (stack[sx].d == -3) {
            this.lsUpdateGroup(isa, stack[sx].b, stack[sx].c);
         }
      }
   }

   private void trSort(int isa, int n, int depth) {
      int[] SA = this.SA;
      int first = 0;
      if (-n < SA[0]) {
         Bzip2DivSufSort.TRBudget budget = new Bzip2DivSufSort.TRBudget(n, trLog(n) * 2 / 3 + 1);

         do {
            int t;
            if ((t = SA[first]) < 0) {
               first -= t;
            } else {
               int last = SA[isa + t] + 1;
               if (1 < last - first) {
                  this.trIntroSort(isa, isa + depth, isa + n, first, last, budget, n);
                  if (budget.chance == 0) {
                     if (0 < first) {
                        SA[0] = -first;
                     }

                     this.lsSort(isa, n, depth);
                     break;
                  }
               }

               first = last;
            }
         } while (first < n);
      }
   }

   private static int BUCKET_B(int c0, int c1) {
      return c1 << 8 | c0;
   }

   private static int BUCKET_BSTAR(int c0, int c1) {
      return c0 << 8 | c1;
   }

   private int sortTypeBstar(int[] bucketA, int[] bucketB) {
      byte[] T = this.T;
      int[] SA = this.SA;
      int n = this.n;
      int[] tempbuf = new int[256];
      int i = 1;

      int flag;
      for (flag = 1; i < n; i++) {
         if (T[i - 1] != T[i]) {
            if ((T[i - 1] & 255) > (T[i] & 255)) {
               flag = 0;
            }
            break;
         }
      }

      i = n - 1;
      int m = n;
      int ti;
      int t0;
      if ((ti = T[i] & 255) < (t0 = T[0] & 255) || T[i] == T[0] && flag != 0) {
         if (flag == 0) {
            bucketB[BUCKET_BSTAR(ti, t0)]++;
            m = n - 1;
            SA[m] = i;
         } else {
            bucketB[BUCKET_B(ti, t0)]++;
         }

         i--;

         int ti1;
         while (0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255)) {
            bucketB[BUCKET_B(ti, ti1)]++;
            i--;
         }
      }

      while (0 <= i) {
         do {
            bucketA[T[i] & 255]++;
            i--;
         } while (0 <= i && (T[i] & 255) >= (T[i + 1] & 255));

         if (0 <= i) {
            bucketB[BUCKET_BSTAR(T[i] & 255, T[i + 1] & 255)]++;

            int ti1;
            for (SA[--m] = i--; 0 <= i && (ti = T[i] & 255) <= (ti1 = T[i + 1] & 255); i--) {
               bucketB[BUCKET_B(ti, ti1)]++;
            }
         }
      }

      m = n - m;
      if (m == 0) {
         i = 0;

         while (i < n) {
            SA[i] = i++;
         }

         return 0;
      } else {
         int c0 = 0;
         i = -1;

         for (int j = 0; c0 < 256; c0++) {
            int t = i + bucketA[c0];
            bucketA[c0] = i + j;
            i = t + bucketB[BUCKET_B(c0, c0)];

            for (int c1 = c0 + 1; c1 < 256; c1++) {
               j += bucketB[BUCKET_BSTAR(c0, c1)];
               bucketB[c0 << 8 | c1] = j;
               i += bucketB[BUCKET_B(c0, c1)];
            }
         }

         int PAb = n - m;
         int ISAb = m;
         i = m - 2;

         while (0 <= i) {
            int t = SA[PAb + i];
            c0 = T[t] & 255;
            int c1 = T[t + 1] & 255;
            SA[--bucketB[BUCKET_BSTAR(c0, c1)]] = i--;
         }

         int t = SA[PAb + m - 1];
         c0 = T[t] & 255;
         int c1 = T[t + 1] & 255;
         SA[--bucketB[BUCKET_BSTAR(c0, c1)]] = m - 1;
         int[] buf = SA;
         int bufoffset = m;
         int bufsize = n - 2 * m;
         if (bufsize <= 256) {
            buf = tempbuf;
            bufoffset = 0;
            bufsize = 256;
         }

         c0 = 255;

         for (int var31 = m; 0 < var31; c0--) {
            for (int var47 = 255; c0 < var47; var47--) {
               i = bucketB[BUCKET_BSTAR(c0, var47)];
               if (1 < var31 - i) {
                  this.subStringSort(PAb, i, var31, buf, bufoffset, bufsize, 2, SA[i] == m - 1, n);
               }

               var31 = i;
            }
         }

         for (int var27 = m - 1; 0 <= var27; var27--) {
            if (0 <= SA[var27]) {
               int var32 = var27;

               do {
                  SA[ISAb + SA[var27]] = var27--;
               } while (0 <= var27 && 0 <= SA[var27]);

               SA[var27 + 1] = var27 - var32;
               if (var27 <= 0) {
                  break;
               }
            }

            int var33 = var27;

            do {
               SA[ISAb + (SA[var27] = ~SA[var27])] = var33;
            } while (SA[--var27] < 0);

            SA[ISAb + SA[var27]] = var33;
         }

         this.trSort(ISAb, m, 1);
         i = n - 1;
         int var34 = m;
         if ((T[i] & 255) < (T[0] & 255) || T[i] == T[0] && flag != 0) {
            if (flag == 0) {
               var34 = m - 1;
               SA[SA[ISAb + var34]] = i;
            }

            i--;

            while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
               i--;
            }
         }

         while (0 <= i) {
            i--;

            while (0 <= i && (T[i] & 255) >= (T[i + 1] & 255)) {
               i--;
            }

            if (0 <= i) {
               SA[SA[ISAb + --var34]] = i--;

               while (0 <= i && (T[i] & 255) <= (T[i + 1] & 255)) {
                  i--;
               }
            }
         }

         c0 = 255;
         i = n - 1;

         for (int k = m - 1; 0 <= c0; c0--) {
            for (int var48 = 255; c0 < var48; var48--) {
               t = i - bucketB[BUCKET_B(c0, var48)];
               bucketB[BUCKET_B(c0, var48)] = i + 1;
               i = t;

               for (int var35 = bucketB[BUCKET_BSTAR(c0, var48)]; var35 <= k; k--) {
                  SA[i] = SA[k];
                  i--;
               }
            }

            t = i - bucketB[BUCKET_B(c0, c0)];
            bucketB[BUCKET_B(c0, c0)] = i + 1;
            if (c0 < 255) {
               bucketB[BUCKET_BSTAR(c0, c0 + 1)] = t + 1;
            }

            i = bucketA[c0];
         }

         return m;
      }
   }

   private int constructBWT(int[] bucketA, int[] bucketB) {
      byte[] T = this.T;
      int[] SA = this.SA;
      int n = this.n;
      int t = 0;
      int c2 = 0;
      int orig = -1;

      for (int c1 = 254; 0 <= c1; c1--) {
         int i = bucketB[BUCKET_BSTAR(c1, c1 + 1)];
         int j = bucketA[c1 + 1];
         t = 0;

         for (c2 = -1; i <= j; j--) {
            int s;
            int s1;
            if (0 <= (s1 = s = SA[j])) {
               if (--s < 0) {
                  s = n - 1;
               }

               int c0;
               if ((c0 = T[s] & 255) <= c1) {
                  SA[j] = ~s1;
                  if (0 < s && (T[s - 1] & 255) > c0) {
                     s = ~s;
                  }

                  if (c2 == c0) {
                     SA[--t] = s;
                  } else {
                     if (0 <= c2) {
                        bucketB[BUCKET_B(c2, c1)] = t;
                     }

                     c2 = c0;
                     SA[t = bucketB[BUCKET_B(c0, c1)] - 1] = s;
                  }
               }
            } else {
               SA[j] = ~s;
            }
         }
      }

      for (int i = 0; i < n; i++) {
         int s;
         int s1;
         if (0 <= (s1 = s = SA[i])) {
            if (--s < 0) {
               s = n - 1;
            }

            int c0;
            if ((c0 = T[s] & 255) >= (T[s + 1] & 255)) {
               if (0 < s && (T[s - 1] & 255) < c0) {
                  s = ~s;
               }

               if (c0 == c2) {
                  SA[++t] = s;
               } else {
                  if (c2 != -1) {
                     bucketA[c2] = t;
                  }

                  c2 = c0;
                  SA[t = bucketA[c0] + 1] = s;
               }
            }
         } else {
            s1 = ~s1;
         }

         if (s1 == 0) {
            SA[i] = T[n - 1];
            orig = i;
         } else {
            SA[i] = T[s1 - 1];
         }
      }

      return orig;
   }

   public int bwt() {
      int[] SA = this.SA;
      byte[] T = this.T;
      int n = this.n;
      int[] bucketA = new int[256];
      int[] bucketB = new int[65536];
      if (n == 0) {
         return 0;
      } else if (n == 1) {
         SA[0] = T[0];
         return 0;
      } else {
         int m = this.sortTypeBstar(bucketA, bucketB);
         return 0 < m ? this.constructBWT(bucketA, bucketB) : 0;
      }
   }

   private static class PartitionResult {
      final int first;
      final int last;

      PartitionResult(int first, int last) {
         this.first = first;
         this.last = last;
      }
   }

   private static class StackEntry {
      final int a;
      final int b;
      final int c;
      final int d;

      StackEntry(int a, int b, int c, int d) {
         this.a = a;
         this.b = b;
         this.c = c;
         this.d = d;
      }
   }

   private static class TRBudget {
      int budget;
      int chance;

      TRBudget(int budget, int chance) {
         this.budget = budget;
         this.chance = chance;
      }

      boolean update(int size, int n) {
         this.budget -= n;
         if (this.budget <= 0) {
            if (--this.chance == 0) {
               return false;
            }

            this.budget += size;
         }

         return true;
      }
   }
}
