package com.coremobile.coreyhealth;

import java.io.IOException;
import java.io.InputStream;

// decorator around InputStream to print whatever is read from the input stream
public class TeeInputStream extends InputStream
{
    private InputStream mDelegate;

    TeeInputStream(InputStream stream)
    {
        mDelegate = stream;
    }

    public int available() throws IOException
    {
        return mDelegate.available();
    }

    public void close() throws IOException
    {
        mDelegate.close();
    }

    public void mark(int readlimit)
    {
        mDelegate.mark(readlimit);
    }

    public boolean markSupported()
    {
        return mDelegate.markSupported();
    }

    public int read(byte[] buffer) throws IOException
    {
        int numRead = mDelegate.read(buffer);
        if (numRead >= 0)
        {
            splitAndPrint("TeeInputStream.read1 " +  numRead + ", ", new String(buffer, 0, numRead, "UTF-8"));
        }
        return numRead;
    }

    public int read() throws IOException
    {
        int byteRead = mDelegate.read();
        if (byteRead >= 0)
        {
            System.out.println("TeeInputStream.read2 " +  (char)byteRead);
        }
        return byteRead;
    }

    public int read(byte[] buffer, int offset, int length) throws IOException
    {
        int numRead = mDelegate.read(buffer, offset, length);
        if (numRead >= 0)
        {
            splitAndPrint("TeeInputStream.read3 " +  numRead + ", ", new String(buffer, offset, numRead, "UTF-8"));
        }
        return numRead;
    }

    public synchronized void reset() throws IOException
    {
        mDelegate.reset();
    }

    public long skip(long byteCount) throws IOException
    {
        return mDelegate.skip(byteCount);
    }

    private void splitAndPrint(String prefix, String msg)
    {
        int lineLen = 1024;
        int totalLen = msg.length();
        int end;
        for (int start = 0; start < totalLen; start += lineLen)
        {
            end = Math.min(start+lineLen, totalLen);
      //      System.out.println(prefix + (end-start) + ", " + msg.substring(start, end));
        }
        
    }
    
    
}
