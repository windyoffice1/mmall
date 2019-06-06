package com.mmall.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class ServletResponseWrapper extends HttpServletResponseWrapper {

    private  volatile MonitorOutoutStream mos;
    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public ServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        if(mos==null){
            synchronized (this) {
                if(mos==null){
                    mos = new  MonitorOutoutStream(super.getOutputStream());
                }
            }
        }
        return mos;
    }

    public String getResponseBody(){

        return new String(mos.getWroteInfo());


    }
}
