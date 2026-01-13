package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.util.Arrays;

class HarakaSBase {
   protected long[][] haraka512_rc = new long[][]{
      {
            2652350495371256459L,
            -4767360454786055294L,
            -2778808723033108313L,
            -6138960262205972599L,
            4944264682582508575L,
            5312892415214084856L,
            390034814247088728L,
            2584105839607850161L
      },
      {
            -2829930801980875922L,
            9137660425067592590L,
            7974068014816832049L,
            -4665944065725157058L,
            2602240152241800734L,
            -1525694355931290902L,
            8634660511727056099L,
            1757945485816280992L
      },
      {
            1181946526362588450L,
            -2765192619992380293L,
            3395396416743122529L,
            -5116273100549372423L,
            -1285454309797503998L,
            -3363297609815171261L,
            -8360835858392998991L,
            -2371352336613968487L
      },
      {
            -2500853454776756032L,
            8465221333286591414L,
            8817016078209461823L,
            9067727467981428858L,
            4244107674518258433L,
            -4347326460570889538L,
            1711371409274742987L,
            6486926172609168623L
      },
      {
            1689001080716996467L,
            -491496126278250673L,
            1273395568185090836L,
            5805238412293617850L,
            -3441289770925384855L,
            4592753210857527691L,
            7062886034259989751L,
            -7974393977033172556L
      },
      {
            -797818098819718290L,
            -41460260651793472L,
            476036171179798187L,
            7391697506481003962L,
            -855662275170689475L,
            -3489340839585811635L,
            -4891525734487956488L,
            9110006695579921767L
      },
      {
            -886938081943560790L,
            4212830408327159617L,
            -3546674487567282635L,
            -1955379422127038289L,
            3174578079917510314L,
            5156046680874954380L,
            -318545805834821831L,
            -6176414008149462342L
      },
      {
            2529785914229181047L,
            2966313764524854080L,
            6363694428402697361L,
            8292109690175819701L,
            -8497546332135459587L,
            -3211108476154815616L,
            -5526938793786642321L,
            -4975969843627057770L
      },
      {
            3357847021085574721L,
            -4764837212565187058L,
            -626391829400648692L,
            2124133995575340009L,
            7425858999829294301L,
            -3432032868905637771L,
            1119301198758921294L,
            1907812968586478892L
      },
      {
            -8986524826712832802L,
            3356175496741300052L,
            -5764600317639896362L,
            4002747967109689317L,
            -8718925159733497197L,
            -1938063772587374661L,
            -8003749789895945835L,
            7302960353763723932L
      }
   };
   protected int[][] haraka256_rc = new int[10][8];
   protected final byte[] buffer = new byte[64];
   protected int off = 0;

   protected HarakaSBase() {
   }

   protected void reset() {
      this.off = 0;
      Arrays.clear(this.buffer);
   }

   private void brRangeDec32Le(byte[] var1, int[] var2, int var3) {
      for (int var5 = 0; var5 < var2.length; var5++) {
         int var4 = var3 + (var5 << 2);
         var2[var5] = var1[var4] & 255 | var1[var4 + 1] << 8 & 0xFF00 | var1[var4 + 2] << 16 & 0xFF0000 | var1[var4 + 3] << 24;
      }
   }

   protected void interleaveConstant(long[] var1, byte[] var2, int var3) {
      int[] var4 = new int[16];
      this.brRangeDec32Le(var2, var4, var3);

      for (int var5 = 0; var5 < 4; var5++) {
         this.brAesCt64InterleaveIn(var1, var5, var4, var5 << 2);
      }

      this.brAesCt64Ortho(var1);
   }

   protected void interleaveConstant32(int[] var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 < 4; var4++) {
         var1[var4 << 1] = this.brDec32Le(var2, var3 + (var4 << 2));
         var1[(var4 << 1) + 1] = this.brDec32Le(var2, var3 + (var4 << 2) + 16);
      }

      this.brAesCtOrtho(var1);
   }

   private int brDec32Le(byte[] var1, int var2) {
      return var1[var2] & 0xFF | var1[var2 + 1] << 8 & 0xFF00 | var1[var2 + 2] << 16 & 0xFF0000 | var1[var2 + 3] << 24;
   }

   protected void haraka512Perm(byte[] var1) {
      int[] var2 = new int[16];
      long[] var3 = new long[8];
      this.brRangeDec32Le(this.buffer, var2, 0);

      for (int var6 = 0; var6 < 4; var6++) {
         this.brAesCt64InterleaveIn(var3, var6, var2, var6 << 2);
      }

      this.brAesCt64Ortho(var3);

      for (int var8 = 0; var8 < 5; var8++) {
         for (int var7 = 0; var7 < 2; var7++) {
            this.brAesCt64BitsliceSbox(var3);
            this.shiftRows(var3);
            this.mixColumns(var3);
            this.addRoundKey(var3, this.haraka512_rc[(var8 << 1) + var7]);
         }

         for (int var11 = 0; var11 < 8; var11++) {
            long var4 = var3[var11];
            var3[var11] = (var4 & 281479271743489L) << 5
               | (var4 & 562958543486978L) << 12
               | (var4 & 1125917086973956L) >>> 1
               | (var4 & 2251834173947912L) << 6
               | (var4 & 9007336695791648L) << 9
               | (var4 & 18014673391583296L) >>> 4
               | (var4 & 36029346783166592L) << 3
               | (var4 & 2377936887688995072L) >>> 5
               | (var4 & 148621055480562192L) << 2
               | (var4 & 576469548530665472L) << 4
               | (var4 & 1152939097061330944L) >>> 12
               | (var4 & 4611756388245323776L) >>> 10
               | (var4 & -8934996522953571328L) >>> 3;
         }
      }

      this.brAesCt64Ortho(var3);

      for (int var9 = 0; var9 < 4; var9++) {
         this.brAesCt64InterleaveOut(var2, var3, var9);
      }

      for (int var10 = 0; var10 < 16; var10++) {
         for (int var12 = 0; var12 < 4; var12++) {
            var1[(var10 << 2) + var12] = (byte)(var2[var10] >>> (var12 << 3) & 0xFF);
         }
      }
   }

   protected void haraka256Perm(byte[] var1) {
      int[] var2 = new int[8];
      this.interleaveConstant32(var2, this.buffer, 0);

      for (int var4 = 0; var4 < 5; var4++) {
         for (int var5 = 0; var5 < 2; var5++) {
            brAesCtBitsliceSbox(var2);
            this.shiftRows32(var2);
            this.mixColumns32(var2);
            this.addRoundKey32(var2, this.haraka256_rc[(var4 << 1) + var5]);
         }

         for (int var7 = 0; var7 < 8; var7++) {
            int var3 = var2[var7];
            var2[var7] = var3 & -2122219135
               | (var3 & 33686018) << 1
               | (var3 & 67372036) << 2
               | (var3 & 134744072) << 3
               | (var3 & 269488144) >>> 3
               | (var3 & 538976288) >>> 2
               | (var3 & 1077952576) >>> 1;
         }
      }

      this.brAesCtOrtho(var2);

      for (int var6 = 0; var6 < 4; var6++) {
         this.brEnc32Le(var1, var2[var6 << 1], var6 << 2);
         this.brEnc32Le(var1, var2[(var6 << 1) + 1], (var6 << 2) + 16);
      }
   }

   private void brEnc32Le(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < 4; var4++) {
         var1[var3 + var4] = (byte)(var2 >> (var4 << 3));
      }
   }

   private void brAesCt64InterleaveIn(long[] var1, int var2, int[] var3, int var4) {
      long var5 = var3[var4] & 4294967295L;
      long var7 = var3[var4 + 1] & 4294967295L;
      long var9 = var3[var4 + 2] & 4294967295L;
      long var11 = var3[var4 + 3] & 4294967295L;
      var5 |= var5 << 16;
      var7 |= var7 << 16;
      var9 |= var9 << 16;
      var11 |= var11 << 16;
      var5 &= 281470681808895L;
      var7 &= 281470681808895L;
      var9 &= 281470681808895L;
      var11 &= 281470681808895L;
      var5 |= var5 << 8;
      var7 |= var7 << 8;
      var9 |= var9 << 8;
      var11 |= var11 << 8;
      var5 &= 71777214294589695L;
      var7 &= 71777214294589695L;
      var9 &= 71777214294589695L;
      var11 &= 71777214294589695L;
      var1[var2] = var5 | var9 << 8;
      var1[var2 + 4] = var7 | var11 << 8;
   }

   private static void brAesCtBitsliceSbox(int[] var0) {
      int var1 = var0[7];
      int var2 = var0[6];
      int var3 = var0[5];
      int var4 = var0[4];
      int var5 = var0[3];
      int var6 = var0[2];
      int var7 = var0[1];
      int var8 = var0[0];
      int var22 = var4 ^ var6;
      int var21 = var1 ^ var7;
      int var17 = var1 ^ var4;
      int var16 = var1 ^ var6;
      int var48 = var2 ^ var3;
      int var9 = var48 ^ var8;
      int var12 = var9 ^ var4;
      int var20 = var21 ^ var22;
      int var10 = var9 ^ var1;
      int var13 = var9 ^ var7;
      int var11 = var13 ^ var16;
      int var49 = var5 ^ var20;
      int var23 = var49 ^ var6;
      int var28 = var49 ^ var2;
      int var14 = var23 ^ var8;
      int var18 = var23 ^ var48;
      int var19 = var28 ^ var17;
      int var15 = var8 ^ var19;
      int var25 = var18 ^ var19;
      int var27 = var18 ^ var16;
      int var24 = var48 ^ var19;
      int var29 = var21 ^ var24;
      int var26 = var1 ^ var24;
      int var50 = var20 & var23;
      int var51 = var11 & var14;
      int var52 = var51 ^ var50;
      int var53 = var12 & var8;
      int var54 = var53 ^ var50;
      int var55 = var21 & var24;
      int var56 = var13 & var9;
      int var57 = var56 ^ var55;
      int var58 = var10 & var15;
      int var59 = var58 ^ var55;
      int var60 = var17 & var19;
      int var61 = var22 & var25;
      int var62 = var61 ^ var60;
      int var63 = var16 & var18;
      int var64 = var63 ^ var60;
      int var65 = var52 ^ var62;
      int var66 = var54 ^ var64;
      int var67 = var57 ^ var62;
      int var68 = var59 ^ var64;
      int var69 = var65 ^ var28;
      int var70 = var66 ^ var27;
      int var71 = var67 ^ var29;
      int var72 = var68 ^ var26;
      int var73 = var69 ^ var70;
      int var74 = var69 & var71;
      int var75 = var72 ^ var74;
      int var76 = var73 & var75;
      int var77 = var76 ^ var70;
      int var78 = var71 ^ var72;
      int var79 = var70 ^ var74;
      int var80 = var79 & var78;
      int var81 = var80 ^ var72;
      int var82 = var71 ^ var81;
      int var83 = var75 ^ var81;
      int var84 = var72 & var83;
      int var85 = var84 ^ var82;
      int var86 = var75 ^ var84;
      int var87 = var77 & var86;
      int var88 = var73 ^ var87;
      int var89 = var88 ^ var85;
      int var90 = var77 ^ var81;
      int var91 = var77 ^ var88;
      int var92 = var81 ^ var85;
      int var93 = var90 ^ var89;
      int var30 = var92 & var23;
      int var31 = var85 & var14;
      int var32 = var81 & var8;
      int var33 = var91 & var24;
      int var34 = var88 & var9;
      int var35 = var77 & var15;
      int var36 = var90 & var19;
      int var37 = var93 & var25;
      int var38 = var89 & var18;
      int var39 = var92 & var20;
      int var40 = var85 & var11;
      int var41 = var81 & var12;
      int var42 = var91 & var21;
      int var43 = var88 & var13;
      int var44 = var77 & var10;
      int var45 = var90 & var17;
      int var46 = var93 & var22;
      int var47 = var89 & var16;
      int var94 = var45 ^ var46;
      int var95 = var40 ^ var41;
      int var96 = var35 ^ var43;
      int var97 = var39 ^ var40;
      int var98 = var32 ^ var42;
      int var99 = var32 ^ var35;
      int var100 = var37 ^ var38;
      int var101 = var30 ^ var33;
      int var102 = var36 ^ var37;
      int var103 = var46 ^ var47;
      int var104 = var42 ^ var96;
      int var105 = var98 ^ var101;
      int var106 = var34 ^ var94;
      int var107 = var33 ^ var102;
      int var108 = var94 ^ var105;
      int var109 = var44 ^ var105;
      int var110 = var100 ^ var106;
      int var111 = var97 ^ var106;
      int var112 = var34 ^ var107;
      int var113 = var109 ^ var110;
      int var114 = var31 ^ var111;
      int var116 = var107 ^ var111;
      int var122 = var104 ^ ~var110;
      int var123 = var96 ^ ~var108;
      int var115 = var112 ^ var113;
      int var119 = var101 ^ var114;
      int var120 = var99 ^ var114;
      int var121 = var95 ^ var113;
      int var117 = var112 ^ ~var119;
      int var118 = var103 ^ ~var115;
      var0[7] = var116;
      var0[6] = var117;
      var0[5] = var118;
      var0[4] = var119;
      var0[3] = var120;
      var0[2] = var121;
      var0[1] = var122;
      var0[0] = var123;
   }

   private void shiftRows32(int[] var1) {
      for (int var3 = 0; var3 < 8; var3++) {
         int var2 = var1[var3];
         var1[var3] = var2 & 0xFF
            | (var2 & 64512) >>> 2
            | (var2 & 768) << 6
            | (var2 & 15728640) >>> 4
            | (var2 & 983040) << 4
            | (var2 & -1073741824) >>> 6
            | (var2 & 1056964608) << 2;
      }
   }

   private void mixColumns32(int[] var1) {
      int var2 = var1[0];
      int var3 = var1[1];
      int var4 = var1[2];
      int var5 = var1[3];
      int var6 = var1[4];
      int var7 = var1[5];
      int var8 = var1[6];
      int var9 = var1[7];
      int var10 = var2 >>> 8 | var2 << 24;
      int var11 = var3 >>> 8 | var3 << 24;
      int var12 = var4 >>> 8 | var4 << 24;
      int var13 = var5 >>> 8 | var5 << 24;
      int var14 = var6 >>> 8 | var6 << 24;
      int var15 = var7 >>> 8 | var7 << 24;
      int var16 = var8 >>> 8 | var8 << 24;
      int var17 = var9 >>> 8 | var9 << 24;
      var1[0] = var9 ^ var17 ^ var10 ^ this.rotr16(var2 ^ var10);
      var1[1] = var2 ^ var10 ^ var9 ^ var17 ^ var11 ^ this.rotr16(var3 ^ var11);
      var1[2] = var3 ^ var11 ^ var12 ^ this.rotr16(var4 ^ var12);
      var1[3] = var4 ^ var12 ^ var9 ^ var17 ^ var13 ^ this.rotr16(var5 ^ var13);
      var1[4] = var5 ^ var13 ^ var9 ^ var17 ^ var14 ^ this.rotr16(var6 ^ var14);
      var1[5] = var6 ^ var14 ^ var15 ^ this.rotr16(var7 ^ var15);
      var1[6] = var7 ^ var15 ^ var16 ^ this.rotr16(var8 ^ var16);
      var1[7] = var8 ^ var16 ^ var17 ^ this.rotr16(var9 ^ var17);
   }

   private void addRoundKey32(int[] var1, int[] var2) {
      var1[0] ^= var2[0];
      var1[1] ^= var2[1];
      var1[2] ^= var2[2];
      var1[3] ^= var2[3];
      var1[4] ^= var2[4];
      var1[5] ^= var2[5];
      var1[6] ^= var2[6];
      var1[7] ^= var2[7];
   }

   private int rotr16(int var1) {
      return var1 << 16 | var1 >>> 16;
   }

   private void brAesCt64Ortho(long[] var1) {
      this.Swapn(var1, 1, 0, 1);
      this.Swapn(var1, 1, 2, 3);
      this.Swapn(var1, 1, 4, 5);
      this.Swapn(var1, 1, 6, 7);
      this.Swapn(var1, 2, 0, 2);
      this.Swapn(var1, 2, 1, 3);
      this.Swapn(var1, 2, 4, 6);
      this.Swapn(var1, 2, 5, 7);
      this.Swapn(var1, 4, 0, 4);
      this.Swapn(var1, 4, 1, 5);
      this.Swapn(var1, 4, 2, 6);
      this.Swapn(var1, 4, 3, 7);
   }

   private void brAesCtOrtho(int[] var1) {
      this.Swapn32(var1, 1, 0, 1);
      this.Swapn32(var1, 1, 2, 3);
      this.Swapn32(var1, 1, 4, 5);
      this.Swapn32(var1, 1, 6, 7);
      this.Swapn32(var1, 2, 0, 2);
      this.Swapn32(var1, 2, 1, 3);
      this.Swapn32(var1, 2, 4, 6);
      this.Swapn32(var1, 2, 5, 7);
      this.Swapn32(var1, 4, 0, 4);
      this.Swapn32(var1, 4, 1, 5);
      this.Swapn32(var1, 4, 2, 6);
      this.Swapn32(var1, 4, 3, 7);
   }

   private void Swapn32(int[] var1, int var2, int var3, int var4) {
      int var5 = 0;
      int var6 = 0;
      switch (var2) {
         case 1:
            var5 = 1431655765;
            var6 = -1431655766;
            break;
         case 2:
            var5 = 858993459;
            var6 = -858993460;
         case 3:
         default:
            break;
         case 4:
            var5 = 252645135;
            var6 = -252645136;
      }

      int var7 = var1[var3];
      int var8 = var1[var4];
      var1[var3] = var7 & var5 | (var8 & var5) << var2;
      var1[var4] = (var7 & var6) >>> var2 | var8 & var6;
   }

   private void Swapn(long[] var1, int var2, int var3, int var4) {
      long var5 = 0L;
      long var7 = 0L;
      switch (var2) {
         case 1:
            var5 = 6148914691236517205L;
            var7 = -6148914691236517206L;
            break;
         case 2:
            var5 = 3689348814741910323L;
            var7 = -3689348814741910324L;
            break;
         case 3:
         default:
            return;
         case 4:
            var5 = 1085102592571150095L;
            var7 = -1085102592571150096L;
      }

      long var9 = var1[var3];
      long var11 = var1[var4];
      var1[var3] = var9 & var5 | (var11 & var5) << var2;
      var1[var4] = (var9 & var7) >>> var2 | var11 & var7;
   }

   private void brAesCt64BitsliceSbox(long[] var1) {
      long var2 = var1[7];
      long var4 = var1[6];
      long var6 = var1[5];
      long var8 = var1[4];
      long var10 = var1[3];
      long var12 = var1[2];
      long var14 = var1[1];
      long var16 = var1[0];
      long var44 = var8 ^ var12;
      long var42 = var2 ^ var14;
      long var34 = var2 ^ var8;
      long var32 = var2 ^ var12;
      long var96 = var4 ^ var6;
      long var18 = var96 ^ var16;
      long var24 = var18 ^ var8;
      long var40 = var42 ^ var44;
      long var20 = var18 ^ var2;
      long var26 = var18 ^ var14;
      long var22 = var26 ^ var32;
      long var98 = var10 ^ var40;
      long var46 = var98 ^ var12;
      long var56 = var98 ^ var4;
      long var28 = var46 ^ var16;
      long var36 = var46 ^ var96;
      long var38 = var56 ^ var34;
      long var30 = var16 ^ var38;
      long var50 = var36 ^ var38;
      long var54 = var36 ^ var32;
      long var48 = var96 ^ var38;
      long var58 = var42 ^ var48;
      long var52 = var2 ^ var48;
      long var100 = var40 & var46;
      long var102 = var22 & var28;
      long var104 = var102 ^ var100;
      long var106 = var24 & var16;
      long var108 = var106 ^ var100;
      long var110 = var42 & var48;
      long var112 = var26 & var18;
      long var114 = var112 ^ var110;
      long var116 = var20 & var30;
      long var118 = var116 ^ var110;
      long var120 = var34 & var38;
      long var122 = var44 & var50;
      long var124 = var122 ^ var120;
      long var126 = var32 & var36;
      long var128 = var126 ^ var120;
      long var130 = var104 ^ var124;
      long var132 = var108 ^ var128;
      long var134 = var114 ^ var124;
      long var136 = var118 ^ var128;
      long var138 = var130 ^ var56;
      long var140 = var132 ^ var54;
      long var142 = var134 ^ var58;
      long var144 = var136 ^ var52;
      long var146 = var138 ^ var140;
      long var148 = var138 & var142;
      long var150 = var144 ^ var148;
      long var152 = var146 & var150;
      long var154 = var152 ^ var140;
      long var156 = var142 ^ var144;
      long var158 = var140 ^ var148;
      long var160 = var158 & var156;
      long var162 = var160 ^ var144;
      long var164 = var142 ^ var162;
      long var166 = var150 ^ var162;
      long var168 = var144 & var166;
      long var170 = var168 ^ var164;
      long var172 = var150 ^ var168;
      long var174 = var154 & var172;
      long var176 = var146 ^ var174;
      long var178 = var176 ^ var170;
      long var180 = var154 ^ var162;
      long var182 = var154 ^ var176;
      long var184 = var162 ^ var170;
      long var186 = var180 ^ var178;
      long var60 = var184 & var46;
      long var62 = var170 & var28;
      long var64 = var162 & var16;
      long var66 = var182 & var48;
      long var68 = var176 & var18;
      long var70 = var154 & var30;
      long var72 = var180 & var38;
      long var74 = var186 & var50;
      long var76 = var178 & var36;
      long var78 = var184 & var40;
      long var80 = var170 & var22;
      long var82 = var162 & var24;
      long var84 = var182 & var42;
      long var86 = var176 & var26;
      long var88 = var154 & var20;
      long var90 = var180 & var34;
      long var92 = var186 & var44;
      long var94 = var178 & var32;
      long var188 = var90 ^ var92;
      long var190 = var80 ^ var82;
      long var192 = var70 ^ var86;
      long var194 = var78 ^ var80;
      long var196 = var64 ^ var84;
      long var198 = var64 ^ var70;
      long var200 = var74 ^ var76;
      long var202 = var60 ^ var66;
      long var204 = var72 ^ var74;
      long var206 = var92 ^ var94;
      long var208 = var84 ^ var192;
      long var210 = var196 ^ var202;
      long var212 = var68 ^ var188;
      long var214 = var66 ^ var204;
      long var216 = var188 ^ var210;
      long var218 = var88 ^ var210;
      long var220 = var200 ^ var212;
      long var222 = var194 ^ var212;
      long var224 = var68 ^ var214;
      long var226 = var218 ^ var220;
      long var228 = var62 ^ var222;
      long var232 = var214 ^ var222;
      long var244 = var208 ^ ~var220;
      long var246 = var192 ^ ~var216;
      long var230 = var224 ^ var226;
      long var238 = var202 ^ var228;
      long var240 = var198 ^ var228;
      long var242 = var190 ^ var226;
      long var234 = var224 ^ ~var238;
      long var236 = var206 ^ ~var230;
      var1[7] = var232;
      var1[6] = var234;
      var1[5] = var236;
      var1[4] = var238;
      var1[3] = var240;
      var1[2] = var242;
      var1[1] = var244;
      var1[0] = var246;
   }

   private void shiftRows(long[] var1) {
      for (int var4 = 0; var4 < var1.length; var4++) {
         long var2 = var1[var4];
         var1[var4] = var2 & 65535L
            | (var2 & 4293918720L) >>> 4
            | (var2 & 983040L) << 12
            | (var2 & 280375465082880L) >>> 8
            | (var2 & 1095216660480L) << 8
            | (var2 & -1152921504606846976L) >>> 12
            | (var2 & 1152640029630136320L) << 4;
      }
   }

   private void mixColumns(long[] var1) {
      long var2 = var1[0];
      long var4 = var1[1];
      long var6 = var1[2];
      long var8 = var1[3];
      long var10 = var1[4];
      long var12 = var1[5];
      long var14 = var1[6];
      long var16 = var1[7];
      long var18 = var2 >>> 16 | var2 << 48;
      long var20 = var4 >>> 16 | var4 << 48;
      long var22 = var6 >>> 16 | var6 << 48;
      long var24 = var8 >>> 16 | var8 << 48;
      long var26 = var10 >>> 16 | var10 << 48;
      long var28 = var12 >>> 16 | var12 << 48;
      long var30 = var14 >>> 16 | var14 << 48;
      long var32 = var16 >>> 16 | var16 << 48;
      var1[0] = var16 ^ var32 ^ var18 ^ this.rotr32(var2 ^ var18);
      var1[1] = var2 ^ var18 ^ var16 ^ var32 ^ var20 ^ this.rotr32(var4 ^ var20);
      var1[2] = var4 ^ var20 ^ var22 ^ this.rotr32(var6 ^ var22);
      var1[3] = var6 ^ var22 ^ var16 ^ var32 ^ var24 ^ this.rotr32(var8 ^ var24);
      var1[4] = var8 ^ var24 ^ var16 ^ var32 ^ var26 ^ this.rotr32(var10 ^ var26);
      var1[5] = var10 ^ var26 ^ var28 ^ this.rotr32(var12 ^ var28);
      var1[6] = var12 ^ var28 ^ var30 ^ this.rotr32(var14 ^ var30);
      var1[7] = var14 ^ var30 ^ var32 ^ this.rotr32(var16 ^ var32);
   }

   private long rotr32(long var1) {
      return var1 << 32 | var1 >>> 32;
   }

   private void addRoundKey(long[] var1, long[] var2) {
      var1[0] ^= var2[0];
      var1[1] ^= var2[1];
      var1[2] ^= var2[2];
      var1[3] ^= var2[3];
      var1[4] ^= var2[4];
      var1[5] ^= var2[5];
      var1[6] ^= var2[6];
      var1[7] ^= var2[7];
   }

   private void brAesCt64InterleaveOut(int[] var1, long[] var2, int var3) {
      long var4 = var2[var3] & 71777214294589695L;
      long var6 = var2[var3 + 4] & 71777214294589695L;
      long var8 = var2[var3] >>> 8 & 71777214294589695L;
      long var10 = var2[var3 + 4] >>> 8 & 71777214294589695L;
      var4 |= var4 >>> 8;
      var6 |= var6 >>> 8;
      var8 |= var8 >>> 8;
      var10 |= var10 >>> 8;
      var4 &= 281470681808895L;
      var6 &= 281470681808895L;
      var8 &= 281470681808895L;
      var10 &= 281470681808895L;
      var3 <<= 2;
      var1[var3] = (int)(var4 | var4 >>> 16);
      var1[var3 + 1] = (int)(var6 | var6 >>> 16);
      var1[var3 + 2] = (int)(var8 | var8 >>> 16);
      var1[var3 + 3] = (int)(var10 | var10 >>> 16);
   }

   protected static void xor(byte[] var0, int var1, byte[] var2, int var3, byte[] var4, int var5, int var6) {
      for (int var7 = 0; var7 < var6; var7++) {
         var4[var5 + var7] = (byte)(var0[var1 + var7] ^ var2[var3 + var7]);
      }
   }
}
