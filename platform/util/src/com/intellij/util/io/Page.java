// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.util.io;

import com.intellij.util.SystemProperties;
import com.intellij.util.containers.LimitedPool;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.BitSet;

public class Page {
  public static final int PAGE_SIZE = SystemProperties.getIntProperty("idea.io.page.size", 8 * 1024);

  private static final LimitedPool<ByteBuffer> ourBufferPool = new LimitedPool.Sync<>(10, () -> ByteBuffer.allocate(PAGE_SIZE));

  private final PoolPageKey myKey;

  private ByteBuffer buf;
  private boolean read;
  private boolean dirty;
  private int myFinalizationId;
  private BitSet myWriteMask;

  private static class PageLock {}
  private final PageLock lock = new PageLock();

  public Page(PoolPageKey key) {
    myKey = key;
    read = false;
    dirty = false;
    myWriteMask = null;

    assert key.getOffset() >= 0 : "offset = " + key.getOffset();
  }

  private void ensureRead() {
    if (!read) {
      if (myWriteMask != null) {
        byte[] content = new byte[PAGE_SIZE];
        final ByteBuffer b = getBuf();
        b.position(0);
        b.get(content, 0, PAGE_SIZE);

        getOwner().loadPage(this);
        for(int i=myWriteMask.nextSetBit(0); i>=0; i=myWriteMask.nextSetBit(i+1)) {
          b.put(i, content[i]);
        }
        myWriteMask = null;
      }
      else {
        getOwner().loadPage(this);
      }

      read = true;
    }
  }

  private void ensureReadOrWriteMaskExists() {
    dirty = true;
    if (read || myWriteMask != null) return;
    myWriteMask = new BitSet(PAGE_SIZE);
  }

  private static class Range {
    int start;
    int end;
  }
  private final Range myContinuousRange = new Range();

  @Nullable
  private Range calcContinuousRange(final BitSet mask) {
    int lowestByte = mask.nextSetBit(0);
    int highestByte;
    if (lowestByte >= 0) {
      highestByte = mask.nextClearBit(lowestByte);
      if (highestByte > 0) {
        int nextChunk = mask.nextSetBit(highestByte);
        if (nextChunk < 0) {
          myContinuousRange.start = lowestByte;
          myContinuousRange.end = highestByte;
          return myContinuousRange;
        }
        else {
          return null;
        }
      }
      else {
        myContinuousRange.start = lowestByte;
        myContinuousRange.end = PAGE_SIZE;
        return myContinuousRange;
      }
    }
    else {
      return null;
    }

  }

  public void flush() {
    synchronized (lock) {
      if (dirty) {
        int start = 0;
        int end = PAGE_SIZE;
        if (myWriteMask != null) {
          Range range = calcContinuousRange(myWriteMask);
          if (range == null) {
  //          System.out.println("Discontinuous write of: " + myWriteMask.cardinality() + " bytes. Performing ensure read before flush.");
            ensureRead();
          }
          else {
            start = range.start;
            end = range.end;
          }
          myWriteMask = null;
        }

        if (end - start > 0) {
          getOwner().flushPage(this, start, end);
        }

        dirty = false;
      }
    }
  }

  public ByteBuffer getBuf() {
    synchronized (lock) {
      if (buf == null) {
        buf = ourBufferPool.alloc();
      }
      return buf;
    }
  }

  private void recycle() {
    ByteBuffer buf = this.buf;
    if (buf != null) {
      ourBufferPool.recycle(buf);
    }

    this.buf = null;
    read = false;
    dirty = false;
    myWriteMask = null;
  }

  public long getOffset() {
    return myKey.getOffset();
  }

  public int put(long index, byte[] bytes, int off, int length) {
    synchronized (lock) {
      myFinalizationId = 0;
      ensureReadOrWriteMaskExists();

      final int start = (int)(index - getOffset());
      ensureIndexInRange(index);
      final ByteBuffer b = getBuf();
      b.position(start);

      int count = Math.min(length, PAGE_SIZE - start);
      b.put(bytes, off, count);

      if (myWriteMask != null) {
        myWriteMask.set(start, start + count);
      }
      return count;
    }
  }

  private void ensureIndexInRange(long index) {
    if (index < getOffset()) {
      throw new IllegalArgumentException("index < offset: index = " + index + ", offset = " + getOffset());
    }
  }

  public int get(long index, byte[] bytes, int off, int length) {
    synchronized (lock) {
      myFinalizationId = 0;
      ensureRead();

      ensureIndexInRange(index);
      int start = (int)(index - getOffset());
      ByteBuffer b = getBuf();
      b.position(start);

      int count = Math.min(length, PAGE_SIZE - start);
      b.get(bytes, off, count);

      return count;
    }
  }

  @Nullable
  public FinalizationRequest prepareForFinalization(int finalizationId) {
    synchronized (lock) {
      if (dirty) {
        myFinalizationId = finalizationId;
        return new FinalizationRequest(this, finalizationId);
      }
      else {
        recycle();
        return null;
      }
    }
  }

  public RandomAccessDataFile getOwner() {
    return getKey().getOwner();
  }

  public PoolPageKey getKey() {
    return myKey;
  }

  public boolean flushIfFinalizationIdIsEqualTo(final long finalizationId) {
    synchronized (lock) {
      if (myFinalizationId == finalizationId) {
        flush();
        return true;
      }

      return false;
    }
  }

  public boolean recycleIfFinalizationIdIsEqualTo(final long finalizationId) {
    synchronized (lock) {
      if (myFinalizationId == finalizationId) {
        recycle();
        return true;
      }
      return false;
    }
  }

  @Override
  public String toString() {
    synchronized (lock) {
      return "Page[" + getOwner() + ", dirty: " + dirty + ", offset=" + getOffset() + "]";
    }
  }
}