package me.xhy.java.vertx.blueprint.todo.constant;

/**
 * Created by xuhuaiyu on 2017/6/2.
 */
public final class Constants {

  private Constants() {
  }

  /**
   * API Route
   * 添加待办事项: POST /todos
   * 获取某一待办事项: GET /todos/:todoId
   * 获取所有待办事项: GET /todos
   * 更新待办事项: PATCH /todos/:todoId
   * 删除某一待办事项: DELETE /todos/:todoId
   * 删除所有待办事项: DELETE /todos
   */
  public static final String API_GET = "/todos/:todoId";
  public static final String API_LIST_ALL = "/todos";
  public static final String API_CREATE = "/todos";
  public static final String API_UPDATE = "/todos/:todoId";
  public static final String API_DELETE = "/todos/:todoId";
  public static final String API_DELETE_ALL = "/todos";

  public static final String REDIS_TODO_KEY = "VERT_TODO";

}
