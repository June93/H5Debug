package com.mysoft.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by June on 2016/6/16.
 */
public class UrlUtil {

    static UriCodec ENCODER = new UriCodec() {
        @Override
        protected boolean isRetained(char c) {
            return ":/.~@&+=-?#".indexOf(c) != -1;
        }
    };

    public static String encodeUrl(String url) {
        //可以传url编码的也可以不传url编码，客户端拿到url后，先解码再编码
        String encodeUrl = "";
        try {
            encodeUrl = encode(StringUtils.getNoneNullString(url), "UTF-8");
        } catch (Exception e) {
            encodeUrl = url;
        }
        return StringUtils.getNoneNullString(encodeUrl);
    }

    public static String decodeUrl(String url) {
        String decodeUrl = "";
        try {
            decodeUrl = decode(StringUtils.getNoneNullString(url), "UTF-8");
        } catch (Exception e) {
            decodeUrl = url;
        }
        return StringUtils.getNoneNullString(decodeUrl);
    }

    //局部进行编码
    public static String encode(String s, String charsetName) throws UnsupportedEncodingException {
        return ENCODER.encode(s, Charset.forName(charsetName));
    }


    public static String decode(String s, String charsetName) throws UnsupportedEncodingException {
        return UriCodec.decode(s, true, Charset.forName(charsetName), true);
    }

}
