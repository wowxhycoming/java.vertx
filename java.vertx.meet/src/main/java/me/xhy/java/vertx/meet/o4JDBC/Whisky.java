package me.xhy.java.vertx.meet.o4JDBC;

import io.vertx.core.json.JsonObject;

public class Whisky {

    private String id;

    private String name;

    private String origin;

    public Whisky(String name, String origin) {
        this.name = name;
        this.origin = origin;
        this.id = "";
    }

    public Whisky(JsonObject json) {
        this.name = json.getString("NAME");
        this.origin = json.getString("ORIGIN");
        this.id = json.getString("ID");
    }

    public Whisky() {
        this.id = "";
    }

    public Whisky(String id, String name, String origin) {
        this.id = id;
        this.name = name;
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "Whisky{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", origin='" + origin + '\'' +
                '}';
    }
}
