package me.xhy.java.vertx.blueprint.kue.core.queue;

import io.vertx.codegen.annotations.VertxGen;

@VertxGen
public enum Priority {

  LOW(10),
  NORMAL(0),
  MEDIUM(-5),
  HIGH(-10),
  CRITICAL(-15);

  private int value;

  Priority(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
