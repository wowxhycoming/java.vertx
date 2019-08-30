package me.xhy.java.vertx.blueprint.kue.core.queue;

import io.vertx.codegen.annotations.VertxGen;

@VertxGen
public enum JobState {
  INACTIVE,
  ACTIVE,
  COMPLETE,
  FAILED,
  DELAYED
}
