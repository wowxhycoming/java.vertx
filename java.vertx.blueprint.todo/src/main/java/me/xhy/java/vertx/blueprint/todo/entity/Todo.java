package me.xhy.java.vertx.blueprint.todo.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuhuaiyu on 2017/6/2.
 */
@DataObject(generateConverter = true)
public class Todo {

  private int id;
  private String title;
  private Boolean completed;
  private Integer order;
  private String url;

  private static final AtomicInteger acc = new AtomicInteger(0); // counter

  public Todo(int id, String title, boolean isCompleted, int order, String url) {
    this.id = id;
    this.title = title;
    this.completed = isCompleted;
    this.order = order;
    this.url = url;
  }

  public Todo(Todo other) {
    this.id = other.id;
    this.title = other.title;
    this.completed = other.completed;
    this.order = other.order;
    this.url = other.url;
  }

  public Todo(JsonObject obj) {
    TodoConverter.fromJson(obj, this);
  }


  public Todo(@Nullable String jsonStr) {
    TodoConverter.fromJson(new JsonObject(jsonStr), this);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    TodoConverter.toJson(this, json);
    return json;
  }

  private <T> T getOrElse(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }

  public Todo merge(Todo todo) {
    return new Todo(id,
        getOrElse(todo.title, title),
        getOrElse(todo.completed, completed),
        getOrElse(todo.order, order),
        url);
  }

  @Override
  public String toString() {
    return "Todo{" +
        "id='" + id + '\'' +
        ", title='" + title + '\'' +
        ", completed=" + completed +
        ", order=" + order +
        ", url='" + url + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Todo todo = (Todo) o;

    if (id != todo.id) return false;
    if (!title.equals(todo.title)) return false;
    if (completed != null ? !completed.equals(todo.completed) : todo.completed != null) return false;
    return order != null ? order.equals(todo.order) : todo.order == null;

  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + title.hashCode();
    result = 31 * result + (completed != null ? completed.hashCode() : 0);
    result = 31 * result + (order != null ? order.hashCode() : 0);
    return result;
  }


  public static int getIncId() {
    return acc.get();
  }

  public static void setIncIdWith(int n) {
    acc.set(n);
  }

  public void setIncId() {
    this.id = acc.incrementAndGet();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
