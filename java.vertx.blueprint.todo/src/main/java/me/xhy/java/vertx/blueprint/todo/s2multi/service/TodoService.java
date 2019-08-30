package me.xhy.java.vertx.blueprint.todo.s2multi.service;

import io.vertx.core.Future;
import me.xhy.java.vertx.blueprint.todo.entity.Todo;

import java.util.List;
import java.util.Optional;

/**
 * Created by xuhuaiyu on 2017/6/6.
 */
public interface TodoService {

  Future<Boolean> initData(); // 初始化数据（或数据库）

  Future<Boolean> insert(Todo todo);

  Future<List<Todo>> getAll();

  Future<Optional<Todo>> getCertain(String todoID);

  Future<Todo> update(String todoId, Todo newTodo);

  Future<Boolean> delete(String todoId);

  Future<Boolean> deleteAll();
}
