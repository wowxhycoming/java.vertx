package me.xhy.java.vertx.web.o4eventBus;

import io.vertx.core.json.JsonObject;

/**
 * Created by xuhuaiyu on 2016/11/19.
 */

public class User {
    private String name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    // 对象 转 JSON
    public JsonObject tojson() {
        final JsonObject message = new JsonObject();
        message.put("name", this.name);
        message.put("email", this.email);
        return message;
    }

    // JSON 转 对象
    public User toUser(JsonObject message) {
        this.name = message.getString("name");
        this.email = message.getString("email");
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
