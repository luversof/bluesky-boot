package io.github.luversof.boot.context;

/** Holder of BlueskyBootContext */
public final class BlueskyBootContextHolder {

  /** Handling utility class constructors */
  private BlueskyBootContextHolder() {}

  private static final BlueskyBootContext CONTEXT = new BlueskyBootContext();

  /**
   * Return BlueskyBootContext
   *
   * @return BlueskyBootContext
   */
  public static BlueskyBootContext getContext() {
    return CONTEXT;
  }
}
