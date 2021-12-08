package ru.kpfu.itis.protocol;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private Map<String, String> headers;
    private MessageType type;
    private String body;

    public void addHeader(String type, String header) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(type, header);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "headers=" + headers +
                ", type=" + type +
                ", body='" + body + '\'' +
                '}';
    }
}
