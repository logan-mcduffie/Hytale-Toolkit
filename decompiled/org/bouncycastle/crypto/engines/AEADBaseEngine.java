package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

abstract class AEADBaseEngine implements AEADCipher {
   protected boolean forEncryption;
   protected String algorithmName;
   protected int KEY_SIZE;
   protected int IV_SIZE;
   protected int MAC_SIZE;
   protected int macSizeLowerBound = 0;
   protected byte[] initialAssociatedText;
   protected byte[] mac;
   protected byte[] m_buf;
   protected byte[] m_aad;
   protected int m_bufPos;
   protected int m_aadPos;
   protected int AADBufferSize;
   protected int BlockSize;
   protected AEADBaseEngine.State m_state = AEADBaseEngine.State.Uninitialized;
   protected int m_bufferSizeDecrypt;
   protected AEADBaseEngine.AADProcessingBuffer processor;
   protected AEADBaseEngine.AADOperator aadOperator;
   protected AEADBaseEngine.DataOperator dataOperator;
   protected AEADBaseEngine.DecryptionFailureCounter decryptionFailureCounter = null;
   protected AEADBaseEngine.DataLimitCounter dataLimitCounter = null;

   @Override
   public String getAlgorithmName() {
      return this.algorithmName;
   }

   public int getKeyBytesSize() {
      return this.KEY_SIZE;
   }

   public int getIVBytesSize() {
      return this.IV_SIZE;
   }

   @Override
   public byte[] getMac() {
      return this.mac;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      this.forEncryption = var1;
      KeyParameter var3;
      byte[] var4;
      if (var2 instanceof AEADParameters) {
         AEADParameters var6 = (AEADParameters)var2;
         var3 = var6.getKey();
         var4 = var6.getNonce();
         this.initialAssociatedText = var6.getAssociatedText();
         int var7 = var6.getMacSize();
         if (this.macSizeLowerBound == 0) {
            if (var7 != this.MAC_SIZE << 3) {
               throw new IllegalArgumentException("Invalid value for MAC size: " + var7);
            }
         } else {
            if (var7 > 128 || var7 < this.macSizeLowerBound << 3 || (var7 & 7) != 0) {
               throw new IllegalArgumentException("MAC size must be between " + (this.macSizeLowerBound << 3) + " and 128 bits for " + this.algorithmName);
            }

            this.MAC_SIZE = var7 >>> 3;
         }
      } else {
         if (!(var2 instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("invalid parameters passed to " + this.algorithmName);
         }

         ParametersWithIV var8 = (ParametersWithIV)var2;
         var3 = (KeyParameter)var8.getParameters();
         var4 = var8.getIV();
         this.initialAssociatedText = null;
      }

      if (var3 == null) {
         throw new IllegalArgumentException(this.algorithmName + " Init parameters must include a key");
      } else if (var4 != null && var4.length == this.IV_SIZE) {
         byte[] var5 = var3.getKey();
         if (var5.length != this.KEY_SIZE) {
            throw new IllegalArgumentException(this.algorithmName + " key must be " + this.KEY_SIZE + " bytes long");
         } else {
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, var2, Utils.getPurpose(var1)));
            this.m_state = var1 ? AEADBaseEngine.State.EncInit : AEADBaseEngine.State.DecInit;
            this.init(var5, var4);
            if (this.dataLimitCounter != null) {
               this.dataLimitCounter.increment(var4.length);
            }

            this.reset(true);
            if (this.initialAssociatedText != null) {
               this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
         }
      } else {
         throw new IllegalArgumentException(this.algorithmName + " requires exactly " + this.IV_SIZE + " bytes of IV");
      }
   }

   @Override
   public void reset() {
      this.reset(true);
   }

   protected void reset(boolean var1) {
      this.ensureInitialized();
      if (var1) {
         this.mac = null;
      }

      if (this.m_buf != null) {
         Arrays.fill(this.m_buf, (byte)0);
         this.m_bufPos = 0;
      }

      if (this.m_aad != null) {
         Arrays.fill(this.m_aad, (byte)0);
         this.m_aadPos = 0;
      }

      switch (this.m_state.ord) {
         case 2:
         case 3:
         case 4:
            this.m_state = AEADBaseEngine.State.EncFinal;
            return;
         case 6:
         case 7:
         case 8:
            this.m_state = AEADBaseEngine.State.DecFinal;
         case 1:
         case 5:
            this.aadOperator.reset();
            this.dataOperator.reset();
            return;
         default:
            throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
      }
   }

   protected void setInnerMembers(AEADBaseEngine.ProcessingBufferType var1, AEADBaseEngine.AADOperatorType var2, AEADBaseEngine.DataOperatorType var3) {
      switch (var1.ord) {
         case 0:
            this.processor = new AEADBaseEngine.BufferedAADProcessor();
            break;
         case 1:
            this.processor = new AEADBaseEngine.ImmediateAADProcessor();
      }

      this.m_bufferSizeDecrypt = this.BlockSize + this.MAC_SIZE;
      switch (var2.ord) {
         case 0:
            this.m_aad = new byte[this.AADBufferSize];
            this.aadOperator = new AEADBaseEngine.DefaultAADOperator();
            break;
         case 1:
            this.m_aad = new byte[this.AADBufferSize];
            this.aadOperator = new AEADBaseEngine.CounterAADOperator();
            break;
         case 2:
            this.AADBufferSize = 0;
            this.aadOperator = new AEADBaseEngine.StreamAADOperator();
            break;
         case 3:
            this.m_aad = new byte[this.AADBufferSize];
            this.dataLimitCounter = new AEADBaseEngine.DataLimitCounter();
            this.aadOperator = new AEADBaseEngine.DataLimitAADOperator();
      }

      switch (var3.ord) {
         case 0:
            this.m_buf = new byte[this.m_bufferSizeDecrypt];
            this.dataOperator = new AEADBaseEngine.DefaultDataOperator();
            break;
         case 1:
            this.m_buf = new byte[this.m_bufferSizeDecrypt];
            this.dataOperator = new AEADBaseEngine.CounterDataOperator();
            break;
         case 2:
            this.m_buf = new byte[this.MAC_SIZE];
            this.dataOperator = new AEADBaseEngine.StreamDataOperator();
            break;
         case 3:
            this.BlockSize = 0;
            this.m_buf = new byte[this.m_bufferSizeDecrypt];
            this.dataOperator = new AEADBaseEngine.StreamCipherOperator();
            break;
         case 4:
            this.m_buf = new byte[this.m_bufferSizeDecrypt];
            this.dataOperator = new AEADBaseEngine.DataLimitDataOperator();
      }
   }

   @Override
   public void processAADByte(byte var1) {
      this.checkAAD();
      this.aadOperator.processAADByte(var1);
   }

   @Override
   public void processAADBytes(byte[] var1, int var2, int var3) {
      this.ensureSufficientInputBuffer(var1, var2, var3);
      if (var3 > 0) {
         this.checkAAD();
         this.aadOperator.processAADBytes(var1, var2, var3);
      }
   }

   private void processAadBytes(byte[] var1, int var2, int var3) {
      if (this.m_aadPos > 0) {
         int var4 = this.AADBufferSize - this.m_aadPos;
         if (this.processor.isLengthWithinAvailableSpace(var3, var4)) {
            System.arraycopy(var1, var2, this.m_aad, this.m_aadPos, var3);
            this.m_aadPos += var3;
            return;
         }

         System.arraycopy(var1, var2, this.m_aad, this.m_aadPos, var4);
         var2 += var4;
         var3 -= var4;
         this.processBufferAAD(this.m_aad, 0);
      }

      while (this.processor.isLengthExceedingBlockSize(var3, this.AADBufferSize)) {
         this.processBufferAAD(var1, var2);
         var2 += this.AADBufferSize;
         var3 -= this.AADBufferSize;
      }

      System.arraycopy(var1, var2, this.m_aad, 0, var3);
      this.m_aadPos = var3;
   }

   @Override
   public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException {
      return this.dataOperator.processByte(var1, var2, var3);
   }

   protected int processEncDecByte(byte[] var1, int var2) {
      int var3 = 0;
      int var4 = (this.forEncryption ? this.BlockSize : this.m_bufferSizeDecrypt) - this.m_bufPos;
      if (var4 == 0) {
         this.ensureSufficientOutputBuffer(var1, var2, this.BlockSize);
         if (this.forEncryption) {
            this.processBufferEncrypt(this.m_buf, 0, var1, var2);
         } else {
            this.processBufferDecrypt(this.m_buf, 0, var1, var2);
            System.arraycopy(this.m_buf, this.BlockSize, this.m_buf, 0, this.m_bufPos - this.BlockSize);
         }

         this.m_bufPos = this.m_bufPos - this.BlockSize;
         var3 = this.BlockSize;
      }

      return var3;
   }

   @Override
   public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
      this.ensureSufficientInputBuffer(var1, var2, var3);
      return this.dataOperator.processBytes(var1, var2, var3, var4, var5);
   }

   protected int processEncDecBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      boolean var6 = this.checkData(false);
      int var7 = (var6 ? this.BlockSize : this.m_bufferSizeDecrypt) - this.m_bufPos;
      if (this.processor.isLengthWithinAvailableSpace(var3, var7)) {
         System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var3);
         this.m_bufPos += var3;
         return 0;
      } else {
         int var9 = this.processor.getUpdateOutputSize(var3);
         int var8 = var9 + this.m_bufPos - (var6 ? 0 : this.MAC_SIZE);
         this.ensureSufficientOutputBuffer(var4, var5, var8 - var8 % this.BlockSize);
         var8 = 0;
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, var9)) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         if (var6) {
            if (this.m_bufPos > 0) {
               System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var7);
               var2 += var7;
               var3 -= var7;
               this.processBufferEncrypt(this.m_buf, 0, var4, var5);
               var8 = this.BlockSize;
            }

            while (this.processor.isLengthExceedingBlockSize(var3, this.BlockSize)) {
               this.processBufferEncrypt(var1, var2, var4, var5 + var8);
               var2 += this.BlockSize;
               var3 -= this.BlockSize;
               var8 += this.BlockSize;
            }
         } else {
            while (
               this.processor.isLengthExceedingBlockSize(this.m_bufPos, this.BlockSize)
                  && this.processor.isLengthExceedingBlockSize(var3 + this.m_bufPos, this.m_bufferSizeDecrypt)
            ) {
               this.processBufferDecrypt(this.m_buf, var8, var4, var5 + var8);
               this.m_bufPos = this.m_bufPos - this.BlockSize;
               var8 += this.BlockSize;
            }

            if (this.m_bufPos > 0) {
               System.arraycopy(this.m_buf, var8, this.m_buf, 0, this.m_bufPos);
               if (this.processor.isLengthWithinAvailableSpace(this.m_bufPos + var3, this.m_bufferSizeDecrypt)) {
                  System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var3);
                  this.m_bufPos += var3;
                  return var8;
               }

               var7 = Math.max(this.BlockSize - this.m_bufPos, 0);
               System.arraycopy(var1, var2, this.m_buf, this.m_bufPos, var7);
               var2 += var7;
               var3 -= var7;
               this.processBufferDecrypt(this.m_buf, 0, var4, var5 + var8);
               var8 += this.BlockSize;
            }

            while (this.processor.isLengthExceedingBlockSize(var3, this.m_bufferSizeDecrypt)) {
               this.processBufferDecrypt(var1, var2, var4, var5 + var8);
               var2 += this.BlockSize;
               var3 -= this.BlockSize;
               var8 += this.BlockSize;
            }
         }

         System.arraycopy(var1, var2, this.m_buf, 0, var3);
         this.m_bufPos = var3;
         return var8;
      }
   }

   @Override
   public int doFinal(byte[] var1, int var2) throws IllegalStateException, InvalidCipherTextException {
      boolean var3 = this.checkData(true);
      int var4;
      if (var3) {
         var4 = this.m_bufPos + this.MAC_SIZE;
      } else {
         if (this.m_bufPos < this.MAC_SIZE) {
            throw new InvalidCipherTextException("data too short");
         }

         this.m_bufPos = this.m_bufPos - this.MAC_SIZE;
         var4 = this.m_bufPos;
      }

      this.ensureSufficientOutputBuffer(var1, var2, var4);
      this.mac = new byte[this.MAC_SIZE];
      this.processFinalBlock(var1, var2);
      if (var3) {
         System.arraycopy(this.mac, 0, var1, var2 + var4 - this.MAC_SIZE, this.MAC_SIZE);
      } else if (!Arrays.constantTimeAreEqual(this.MAC_SIZE, this.mac, 0, this.m_buf, this.m_bufPos)) {
         if (this.decryptionFailureCounter != null && this.decryptionFailureCounter.increment()) {
            throw new InvalidCipherTextException(this.algorithmName + " decryption failure limit exceeded");
         }

         throw new InvalidCipherTextException(this.algorithmName + " mac does not match");
      }

      this.reset(!var3);
      return var4;
   }

   public final int getBlockSize() {
      return this.BlockSize;
   }

   @Override
   public int getUpdateOutputSize(int var1) {
      int var2 = this.getTotalBytesForUpdate(var1);
      return var2 - var2 % this.BlockSize;
   }

   protected int getTotalBytesForUpdate(int var1) {
      int var2 = this.processor.getUpdateOutputSize(var1);
      switch (this.m_state.ord) {
         case 3:
         case 4:
            var2 = Math.max(0, var2 + this.m_bufPos);
            break;
         case 5:
         case 6:
         case 7:
         case 8:
            var2 = Math.max(0, var2 + this.m_bufPos - this.MAC_SIZE);
      }

      return var2;
   }

   @Override
   public int getOutputSize(int var1) {
      int var2 = Math.max(0, var1);
      switch (this.m_state.ord) {
         case 3:
         case 4:
            return var2 + this.m_bufPos + this.MAC_SIZE;
         case 5:
         case 6:
         case 7:
         case 8:
            return Math.max(0, var2 + this.m_bufPos - this.MAC_SIZE);
         default:
            return var2 + this.MAC_SIZE;
      }
   }

   protected void checkAAD() {
      switch (this.m_state.ord) {
         case 1:
            this.m_state = AEADBaseEngine.State.EncAad;
         case 2:
         case 6:
            break;
         case 3:
         default:
            throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
         case 4:
            throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
         case 5:
            this.m_state = AEADBaseEngine.State.DecAad;
      }
   }

   protected boolean checkData(boolean var1) {
      switch (this.m_state.ord) {
         case 1:
         case 2:
            this.finishAAD(AEADBaseEngine.State.EncData, var1);
            return true;
         case 3:
            return true;
         case 4:
            throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
         case 5:
         case 6:
            this.finishAAD(AEADBaseEngine.State.DecData, var1);
            return false;
         case 7:
            return false;
         default:
            throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
      }
   }

   protected final void ensureSufficientOutputBuffer(byte[] var1, int var2, int var3) {
      if (var2 + var3 > var1.length) {
         throw new OutputLengthException("output buffer too short");
      }
   }

   protected final void ensureSufficientInputBuffer(byte[] var1, int var2, int var3) {
      if (var2 + var3 > var1.length) {
         throw new DataLengthException("input buffer too short");
      }
   }

   protected final void ensureInitialized() {
      if (this.m_state == AEADBaseEngine.State.Uninitialized) {
         throw new IllegalStateException("Need to call init function before operation");
      }
   }

   protected void finishAAD1(AEADBaseEngine.State var1) {
      switch (this.m_state.ord) {
         case 1:
         case 2:
         case 5:
         case 6:
            this.processFinalAAD();
         case 3:
         case 4:
         default:
            this.m_state = var1;
      }
   }

   protected void finishAAD2(AEADBaseEngine.State var1) {
      switch (this.m_state.ord) {
         case 2:
         case 6:
            this.processFinalAAD();
         default:
            this.m_aadPos = 0;
            this.m_state = var1;
      }
   }

   protected void finishAAD3(AEADBaseEngine.State var1, boolean var2) {
      switch (this.m_state.ord) {
         case 5:
         case 6:
            if (!var2 && this.dataOperator.getLen() <= this.MAC_SIZE) {
               return;
            }
         case 1:
         case 2:
            this.processFinalAAD();
         case 3:
         case 4:
         default:
            this.m_aadPos = 0;
            this.m_state = var1;
      }
   }

   protected abstract void finishAAD(AEADBaseEngine.State var1, boolean var2);

   protected abstract void init(byte[] var1, byte[] var2);

   protected abstract void processFinalBlock(byte[] var1, int var2);

   protected abstract void processBufferAAD(byte[] var1, int var2);

   protected abstract void processFinalAAD();

   protected abstract void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4);

   protected abstract void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4);

   protected interface AADOperator {
      void processAADByte(byte var1);

      void processAADBytes(byte[] var1, int var2, int var3);

      void reset();

      int getLen();
   }

   protected static class AADOperatorType {
      public static final int DEFAULT = 0;
      public static final int COUNTER = 1;
      public static final int STREAM = 2;
      public static final int DATA_LIMIT = 3;
      public static final AEADBaseEngine.AADOperatorType Default = new AEADBaseEngine.AADOperatorType(0);
      public static final AEADBaseEngine.AADOperatorType Counter = new AEADBaseEngine.AADOperatorType(1);
      public static final AEADBaseEngine.AADOperatorType Stream = new AEADBaseEngine.AADOperatorType(2);
      public static final AEADBaseEngine.AADOperatorType DataLimit = new AEADBaseEngine.AADOperatorType(3);
      private final int ord;

      AADOperatorType(int var1) {
         this.ord = var1;
      }
   }

   private interface AADProcessingBuffer {
      void processAADByte(byte var1);

      int processByte(byte var1, byte[] var2, int var3);

      int getUpdateOutputSize(int var1);

      boolean isLengthWithinAvailableSpace(int var1, int var2);

      boolean isLengthExceedingBlockSize(int var1, int var2);
   }

   private class BufferedAADProcessor implements AEADBaseEngine.AADProcessingBuffer {
      private BufferedAADProcessor() {
      }

      @Override
      public void processAADByte(byte var1) {
         if (AEADBaseEngine.this.m_aadPos == AEADBaseEngine.this.AADBufferSize) {
            AEADBaseEngine.this.processBufferAAD(AEADBaseEngine.this.m_aad, 0);
            AEADBaseEngine.this.m_aadPos = 0;
         }

         AEADBaseEngine.this.m_aad[AEADBaseEngine.this.m_aadPos++] = var1;
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         AEADBaseEngine.this.checkData(false);
         int var4 = AEADBaseEngine.this.processEncDecByte(var2, var3);
         AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = var1;
         return var4;
      }

      @Override
      public boolean isLengthWithinAvailableSpace(int var1, int var2) {
         return var1 <= var2;
      }

      @Override
      public boolean isLengthExceedingBlockSize(int var1, int var2) {
         return var1 > var2;
      }

      @Override
      public int getUpdateOutputSize(int var1) {
         return Math.max(0, var1) - 1;
      }
   }

   private class CounterAADOperator implements AEADBaseEngine.AADOperator {
      private int aadLen;

      private CounterAADOperator() {
      }

      @Override
      public void processAADByte(byte var1) {
         this.aadLen++;
         AEADBaseEngine.this.processor.processAADByte(var1);
      }

      @Override
      public void processAADBytes(byte[] var1, int var2, int var3) {
         this.aadLen += var3;
         AEADBaseEngine.this.processAadBytes(var1, var2, var3);
      }

      @Override
      public int getLen() {
         return this.aadLen;
      }

      @Override
      public void reset() {
         this.aadLen = 0;
      }
   }

   private class CounterDataOperator implements AEADBaseEngine.DataOperator {
      private int messegeLen;

      private CounterDataOperator() {
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         this.messegeLen++;
         return AEADBaseEngine.this.processor.processByte(var1, var2, var3);
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
         this.messegeLen += var3;
         return AEADBaseEngine.this.processEncDecBytes(var1, var2, var3, var4, var5);
      }

      @Override
      public int getLen() {
         return this.messegeLen;
      }

      @Override
      public void reset() {
         this.messegeLen = 0;
      }
   }

   private class DataLimitAADOperator implements AEADBaseEngine.AADOperator {
      private DataLimitAADOperator() {
      }

      @Override
      public void processAADByte(byte var1) {
         AEADBaseEngine.this.dataLimitCounter.increment();
         AEADBaseEngine.this.processor.processAADByte(var1);
      }

      @Override
      public void processAADBytes(byte[] var1, int var2, int var3) {
         AEADBaseEngine.this.dataLimitCounter.increment(var3);
         AEADBaseEngine.this.processAadBytes(var1, var2, var3);
      }

      @Override
      public void reset() {
      }

      @Override
      public int getLen() {
         return AEADBaseEngine.this.m_aadPos;
      }
   }

   protected static class DataLimitCounter {
      private long count;
      private long max;
      private int n;

      public void init(int var1) {
         this.n = var1;
         this.max = 1L << var1;
      }

      public void increment() {
         if (++this.count > this.max) {
            throw new IllegalStateException("Total data limit exceeded: maximum 2^" + this.n + " bytes per key (including nonce, AAD, and message)");
         }
      }

      public void increment(int var1) {
         this.count += var1;
         if (this.count > this.max) {
            throw new IllegalStateException("Total data limit exceeded: maximum 2^" + var1 + " bytes per key (including nonce, AAD, and message)");
         }
      }

      public void reset() {
         this.count = 0L;
      }
   }

   private class DataLimitDataOperator implements AEADBaseEngine.DataOperator {
      private DataLimitDataOperator() {
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         AEADBaseEngine.this.dataLimitCounter.increment();
         return AEADBaseEngine.this.processor.processByte(var1, var2, var3);
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
         AEADBaseEngine.this.dataLimitCounter.increment(var3);
         return AEADBaseEngine.this.processEncDecBytes(var1, var2, var3, var4, var5);
      }

      @Override
      public int getLen() {
         return AEADBaseEngine.this.m_bufPos;
      }

      @Override
      public void reset() {
      }
   }

   protected interface DataOperator {
      int processByte(byte var1, byte[] var2, int var3);

      int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5);

      int getLen();

      void reset();
   }

   protected static class DataOperatorType {
      public static final int DEFAULT = 0;
      public static final int COUNTER = 1;
      public static final int STREAM = 2;
      public static final int STREAM_CIPHER = 3;
      public static final int DATA_LIMIT = 4;
      public static final AEADBaseEngine.DataOperatorType Default = new AEADBaseEngine.DataOperatorType(0);
      public static final AEADBaseEngine.DataOperatorType Counter = new AEADBaseEngine.DataOperatorType(1);
      public static final AEADBaseEngine.DataOperatorType Stream = new AEADBaseEngine.DataOperatorType(2);
      public static final AEADBaseEngine.DataOperatorType StreamCipher = new AEADBaseEngine.DataOperatorType(3);
      public static final AEADBaseEngine.DataOperatorType DataLimit = new AEADBaseEngine.DataOperatorType(4);
      private final int ord;

      DataOperatorType(int var1) {
         this.ord = var1;
      }
   }

   protected static class DecryptionFailureCounter {
      private int n;
      private int[] counter;

      public void init(int var1) {
         if (this.n != var1) {
            this.n = var1;
            int var2 = var1 + 31 >>> 5;
            if (this.counter != null && var2 == this.counter.length) {
               this.reset();
            } else {
               this.counter = new int[var2];
            }
         }
      }

      public boolean increment() {
         int var1 = this.counter.length;

         do {
            var1--;
         } while (var1 >= 0 && ++this.counter[var1] == 0);

         int var2 = this.n & 31;
         return var1 <= 0 && this.counter[0] == (var2 == 0 ? 0 : 1 << var2);
      }

      public void reset() {
         Arrays.fill(this.counter, 0);
      }
   }

   private class DefaultAADOperator implements AEADBaseEngine.AADOperator {
      private DefaultAADOperator() {
      }

      @Override
      public void processAADByte(byte var1) {
         AEADBaseEngine.this.processor.processAADByte(var1);
      }

      @Override
      public void processAADBytes(byte[] var1, int var2, int var3) {
         AEADBaseEngine.this.processAadBytes(var1, var2, var3);
      }

      @Override
      public void reset() {
      }

      @Override
      public int getLen() {
         return AEADBaseEngine.this.m_aadPos;
      }
   }

   private class DefaultDataOperator implements AEADBaseEngine.DataOperator {
      private DefaultDataOperator() {
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         return AEADBaseEngine.this.processor.processByte(var1, var2, var3);
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
         return AEADBaseEngine.this.processEncDecBytes(var1, var2, var3, var4, var5);
      }

      @Override
      public int getLen() {
         return AEADBaseEngine.this.m_bufPos;
      }

      @Override
      public void reset() {
      }
   }

   protected static final class ErasableOutputStream extends ByteArrayOutputStream {
      public ErasableOutputStream() {
      }

      public byte[] getBuf() {
         return this.buf;
      }
   }

   private class ImmediateAADProcessor implements AEADBaseEngine.AADProcessingBuffer {
      private ImmediateAADProcessor() {
      }

      @Override
      public void processAADByte(byte var1) {
         AEADBaseEngine.this.m_aad[AEADBaseEngine.this.m_aadPos++] = var1;
         if (AEADBaseEngine.this.m_aadPos == AEADBaseEngine.this.AADBufferSize) {
            AEADBaseEngine.this.processBufferAAD(AEADBaseEngine.this.m_aad, 0);
            AEADBaseEngine.this.m_aadPos = 0;
         }
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         AEADBaseEngine.this.checkData(false);
         AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = var1;
         return AEADBaseEngine.this.processEncDecByte(var2, var3);
      }

      @Override
      public int getUpdateOutputSize(int var1) {
         return Math.max(0, var1);
      }

      @Override
      public boolean isLengthWithinAvailableSpace(int var1, int var2) {
         return var1 < var2;
      }

      @Override
      public boolean isLengthExceedingBlockSize(int var1, int var2) {
         return var1 >= var2;
      }
   }

   protected static class ProcessingBufferType {
      public static final int BUFFERED = 0;
      public static final int IMMEDIATE = 1;
      public static final AEADBaseEngine.ProcessingBufferType Buffered = new AEADBaseEngine.ProcessingBufferType(0);
      public static final AEADBaseEngine.ProcessingBufferType Immediate = new AEADBaseEngine.ProcessingBufferType(1);
      private final int ord;

      ProcessingBufferType(int var1) {
         this.ord = var1;
      }
   }

   protected static class State {
      public static final int UNINITIALIZED = 0;
      public static final int ENC_INIT = 1;
      public static final int ENC_AAD = 2;
      public static final int ENC_DATA = 3;
      public static final int ENC_FINAL = 4;
      public static final int DEC_INIT = 5;
      public static final int DEC_AAD = 6;
      public static final int DEC_DATA = 7;
      public static final int DEC_FINAL = 8;
      public static final AEADBaseEngine.State Uninitialized = new AEADBaseEngine.State(0);
      public static final AEADBaseEngine.State EncInit = new AEADBaseEngine.State(1);
      public static final AEADBaseEngine.State EncAad = new AEADBaseEngine.State(2);
      public static final AEADBaseEngine.State EncData = new AEADBaseEngine.State(3);
      public static final AEADBaseEngine.State EncFinal = new AEADBaseEngine.State(4);
      public static final AEADBaseEngine.State DecInit = new AEADBaseEngine.State(5);
      public static final AEADBaseEngine.State DecAad = new AEADBaseEngine.State(6);
      public static final AEADBaseEngine.State DecData = new AEADBaseEngine.State(7);
      public static final AEADBaseEngine.State DecFinal = new AEADBaseEngine.State(8);
      final int ord;

      State(int var1) {
         this.ord = var1;
      }
   }

   protected static class StreamAADOperator implements AEADBaseEngine.AADOperator {
      private final AEADBaseEngine.ErasableOutputStream stream = new AEADBaseEngine.ErasableOutputStream();

      @Override
      public void processAADByte(byte var1) {
         this.stream.write(var1);
      }

      @Override
      public void processAADBytes(byte[] var1, int var2, int var3) {
         this.stream.write(var1, var2, var3);
      }

      public byte[] getBytes() {
         return this.stream.getBuf();
      }

      @Override
      public void reset() {
         this.stream.reset();
      }

      @Override
      public int getLen() {
         return this.stream.size();
      }
   }

   private class StreamCipherOperator implements AEADBaseEngine.DataOperator {
      private int len;

      private StreamCipherOperator() {
      }

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         boolean var4 = AEADBaseEngine.this.checkData(false);
         if (var4) {
            this.len = 1;
            AEADBaseEngine.this.processBufferEncrypt(new byte[]{var1}, 0, var2, var3);
            return 1;
         } else if (AEADBaseEngine.this.m_bufPos == AEADBaseEngine.this.MAC_SIZE) {
            this.len = 1;
            AEADBaseEngine.this.processBufferDecrypt(AEADBaseEngine.this.m_buf, 0, var2, var3);
            System.arraycopy(AEADBaseEngine.this.m_buf, 1, AEADBaseEngine.this.m_buf, 0, AEADBaseEngine.this.m_bufPos - 1);
            AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos - 1] = var1;
            return 1;
         } else {
            AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = var1;
            return 0;
         }
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
         if (var1 == var4 && Arrays.segmentsOverlap(var2, var3, var5, AEADBaseEngine.this.processor.getUpdateOutputSize(var3))) {
            var1 = new byte[var3];
            System.arraycopy(var4, var2, var1, 0, var3);
            var2 = 0;
         }

         boolean var6 = AEADBaseEngine.this.checkData(false);
         if (var6) {
            this.len = var3;
            AEADBaseEngine.this.processBufferEncrypt(var1, var2, var4, var5);
            return var3;
         } else {
            int var7 = Math.max(AEADBaseEngine.this.m_bufPos + var3 - AEADBaseEngine.this.MAC_SIZE, 0);
            int var8 = 0;
            if (AEADBaseEngine.this.m_bufPos > 0) {
               this.len = Math.min(var7, AEADBaseEngine.this.m_bufPos);
               var8 = this.len;
               AEADBaseEngine.this.processBufferDecrypt(AEADBaseEngine.this.m_buf, 0, var4, var5);
               var7 -= var8;
               AEADBaseEngine.this.m_bufPos -= var8;
               System.arraycopy(AEADBaseEngine.this.m_buf, var8, AEADBaseEngine.this.m_buf, 0, AEADBaseEngine.this.m_bufPos);
            }

            if (var7 > 0) {
               this.len = var7;
               AEADBaseEngine.this.processBufferDecrypt(var1, var2, var4, var5);
               var8 += var7;
               var3 -= var7;
               var2 += var7;
            }

            System.arraycopy(var1, var2, AEADBaseEngine.this.m_buf, AEADBaseEngine.this.m_bufPos, var3);
            AEADBaseEngine.this.m_bufPos += var3;
            return var8;
         }
      }

      @Override
      public int getLen() {
         return this.len;
      }

      @Override
      public void reset() {
      }
   }

   protected class StreamDataOperator implements AEADBaseEngine.DataOperator {
      private final AEADBaseEngine.ErasableOutputStream stream = new AEADBaseEngine.ErasableOutputStream();

      @Override
      public int processByte(byte var1, byte[] var2, int var3) {
         AEADBaseEngine.this.checkData(false);
         AEADBaseEngine.this.ensureInitialized();
         this.stream.write(var1);
         AEADBaseEngine.this.m_bufPos = this.stream.size();
         return 0;
      }

      @Override
      public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) {
         AEADBaseEngine.this.checkData(false);
         AEADBaseEngine.this.ensureInitialized();
         this.stream.write(var1, var2, var3);
         AEADBaseEngine.this.m_bufPos = this.stream.size();
         return 0;
      }

      public byte[] getBytes() {
         return this.stream.getBuf();
      }

      @Override
      public int getLen() {
         return this.stream.size();
      }

      @Override
      public void reset() {
         this.stream.reset();
      }
   }
}
