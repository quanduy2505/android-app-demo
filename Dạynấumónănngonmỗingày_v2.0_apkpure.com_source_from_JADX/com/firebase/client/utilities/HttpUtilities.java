package com.firebase.client.utilities;

import com.google.android.gms.common.ConnectionResult;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import rx.internal.operators.OnSubscribeConcatMap;

public class HttpUtilities {

    /* renamed from: com.firebase.client.utilities.HttpUtilities.1 */
    static /* synthetic */ class C05901 {
        static final /* synthetic */ int[] f11xaa443286;

        static {
            f11xaa443286 = new int[HttpRequestType.values().length];
            try {
                f11xaa443286[HttpRequestType.GET.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f11xaa443286[HttpRequestType.DELETE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f11xaa443286[HttpRequestType.POST.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f11xaa443286[HttpRequestType.PUT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum HttpRequestType {
        GET,
        POST,
        DELETE,
        PUT
    }

    public static URI buildUrl(String server, String path, Map<String, String> params) {
        try {
            URI serverURI = new URI(server);
            URI uri = new URI(serverURI.getScheme(), serverURI.getAuthority(), path, null, null);
            String query = null;
            if (params != null) {
                StringBuilder queryBuilder = new StringBuilder();
                boolean first = true;
                for (Entry<String, String> entry : params.entrySet()) {
                    if (!first) {
                        queryBuilder.append("&");
                    }
                    first = false;
                    queryBuilder.append(URLEncoder.encode((String) entry.getKey(), "utf-8"));
                    queryBuilder.append("=");
                    queryBuilder.append(URLEncoder.encode((String) entry.getValue(), "utf-8"));
                }
                query = queryBuilder.toString();
            }
            if (query != null) {
                return new URI(uri.toASCIIString() + "?" + query);
            }
            return uri;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Couldn't build valid auth URI.", e);
        } catch (URISyntaxException e2) {
            throw new RuntimeException("Couldn't build valid auth URI.", e2);
        }
    }

    private static void addMethodParams(HttpEntityEnclosingRequestBase request, Map<String, String> params) {
        if (params != null) {
            List<NameValuePair> postParams = new ArrayList();
            for (Entry<String, String> entry : params.entrySet()) {
                postParams.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
            try {
                request.setEntity(new UrlEncodedFormEntity(postParams, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Didn't find utf-8 encoding", e);
            }
        }
    }

    public static HttpUriRequest requestWithType(String server, String path, HttpRequestType type, Map<String, String> urlParams, Map<String, String> requestParams) {
        switch (C05901.f11xaa443286[type.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
                Map<String, String> urlParams2 = new HashMap(urlParams);
                urlParams2.putAll(requestParams);
                urlParams = urlParams2;
                break;
        }
        if (type == HttpRequestType.DELETE) {
            urlParams.put("_method", "delete");
        }
        URI url = buildUrl(server, path, urlParams);
        switch (C05901.f11xaa443286[type.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return new HttpGet(url);
            case OnSubscribeConcatMap.END /*2*/:
                return new HttpDelete(url);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                HttpUriRequest post = new HttpPost(url);
                if (requestParams == null) {
                    return post;
                }
                addMethodParams(post, requestParams);
                return post;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                HttpPut put = new HttpPut(url);
                if (requestParams != null) {
                    addMethodParams(put, requestParams);
                }
                return put;
            default:
                throw new IllegalStateException("Shouldn't reach here!");
        }
    }
}
