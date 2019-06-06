package com.mmall.interceptor;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MonitorOutoutStream extends ServletOutputStream {
    private ServletOutputStream output;
    private ByteArrayOutputStream copy=new ByteArrayOutputStream();
    public MonitorOutoutStream(ServletOutputStream output) {
        super();
        this.output = output;
    }

    @Override
    public void write(int b) throws IOException {
        // TODO Auto-generated method stub
        output.write(b);
        copy.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        // TODO Auto-generated method stub
        output.write(b);
        copy.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        // TODO Auto-generated method stub
        output.write(b,off,len);
        copy.write(b,off,len);
    }

    public byte[] getWroteInfo() {
        return copy.toByteArray();
    }

    @Override
    public void flush() throws IOException {
        // TODO Auto-generated method stub
        output.flush();
        copy.close();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        output.close();
        copy.close();
    }
}
