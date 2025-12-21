package com.yunhang.forum.util;

import javafx.application.Platform;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局任务执行器：使用 Java 25 虚拟线程执行后台任务，避免散落 new Thread(...)。
 */
public final class TaskRunner {
  private static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

  private TaskRunner() {}

  /**
   * 在虚拟线程中执行任务；自动捕获异常并记录日志。
   */
  public static void runAsync(Runnable task) {
    Objects.requireNonNull(task, "task");
    EXECUTOR.submit(() -> {
      try {
        task.run();
      } catch (Throwable t) {
        LogUtil.error("Async task failed", t);
      }
    });
  }

  /**
   * 在 JavaFX UI 线程执行任务。
   */
  public static void runOnUI(Runnable task) {
    Objects.requireNonNull(task, "task");
    Platform.runLater(() -> {
      try {
        task.run();
      } catch (Throwable t) {
        LogUtil.error("UI task failed", t);
      }
    });
  }
}
