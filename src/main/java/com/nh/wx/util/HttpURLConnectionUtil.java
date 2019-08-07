package com.nh.wx.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class HttpURLConnectionUtil {
    
    private static final int BUFFER_SIZE = 4096;

    public static HttpURLConnection openConnection(String url) throws IOException {
        
        return openConnection(url, null);
    }
    

    public static HttpURLConnection openConnection(URL url) throws IOException {
        
        return openConnection(url, null);
    }
    

    public static HttpURLConnection openConnection(String url, Proxy proxy) throws IOException {
        
        return openConnection(new URL(url), proxy);
    }
    

    public static HttpURLConnection openConnection(URL url, Proxy proxy) throws IOException {
        
        if ("https".equals(url.getProtocol())) {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection((proxy != null) ? proxy : Proxy.NO_PROXY);
            
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
            } catch (GeneralSecurityException e) {
                throw new IOException("SSLContext error!", e);
            }
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            
            return conn;
        } else {
            return (HttpURLConnection) url.openConnection((proxy != null) ? proxy : Proxy.NO_PROXY);
        }
    }
    

    public static void closeConnection(HttpURLConnection conn) {
        
        try {
            conn.disconnect();
        } catch (Exception e) {}
    }
    

    public static byte[] read(HttpURLConnection conn, boolean close) throws IOException {
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        read(conn, os, close);
        
        return os.toByteArray();
    }
    

    public static void read(HttpURLConnection conn, OutputStream os, boolean close) throws IOException {
        
        InputStream is = null;
        try {
            is = new BufferedInputStream(conn.getInputStream());
            byte[] b = new byte[BUFFER_SIZE];
            int len = -1;
            while ((len = is.read(b, 0, b.length)) != -1) os.write(b, 0, len);
            os.flush();
        } finally {
            if (close) {
                try {
                    is.close();
                } catch (Exception e) {}
            }
        }
    }
    

    public static void write(HttpURLConnection conn, byte[] bytes, boolean close) throws IOException {
        
        write(conn, new ByteArrayInputStream(bytes), close);
    }
    

    public static void write(HttpURLConnection conn, InputStream is, boolean close) throws IOException {
        
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(conn.getOutputStream());
            byte[] b = new byte[BUFFER_SIZE];
            int len = -1;
            while ((len = is.read(b, 0, b.length)) != -1) os.write(b, 0, len);
            os.flush();
        } finally {
            if (close) {
                try {
                    os.close();
                } catch (Exception e) {}
            }
        }
    }
    
    
    

    private static class TrustAnyTrustManager implements X509TrustManager {
        
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[]{};}
    }
    
    
    

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        
        public boolean verify(String hostname, SSLSession session) {return true;}
    }
}
