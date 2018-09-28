package com.firebase.client.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.firebase.client.utilities.encoding.JsonHelpers;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;

class JsonBasicResponseHandler implements ResponseHandler<Map<String, Object>> {

    /* renamed from: com.firebase.client.authentication.JsonBasicResponseHandler.1 */
    class C10861 extends TypeReference<Map<String, Object>> {
        C10861() {
        }
    }

    JsonBasicResponseHandler() {
    }

    public Map<String, Object> handleResponse(HttpResponse response) throws IOException {
        Map<String, Object> map = null;
        if (response != null) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = entity.getContent();
                try {
                    map = (Map) JsonHelpers.getMapper().readValue(is, new C10861());
                } finally {
                    is.close();
                }
            }
        }
        return map;
    }
}
