package io.github.luversof.boot.autoconfigure.jdbc;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration properties for DataSource. */
@ConfigurationProperties("bluesky-boot.datasource")
public class DataSourceProperties {

  private boolean enabled;

  private String defaultDatasource;

  /** 처음 보는 lookupKey 의 DataSource 를 connection-info 로더로 그때 만든다. */
  private final LazyLoadRouting lazyLoadRouting = new LazyLoadRouting();

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getDefaultDatasource() {
    return defaultDatasource;
  }

  public void setDefaultDatasource(String defaultDatasource) {
    this.defaultDatasource = defaultDatasource;
  }

  public LazyLoadRouting getLazyLoadRouting() {
    return lazyLoadRouting;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataSourceProperties that = (DataSourceProperties) o;
    return enabled == that.enabled
        && lazyLoadRouting.enabled == that.lazyLoadRouting.enabled
        && Objects.equals(defaultDatasource, that.defaultDatasource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(enabled, defaultDatasource, lazyLoadRouting.enabled);
  }

  @Override
  public String toString() {
    return "DataSourceProperties{"
        + "enabled="
        + enabled
        + ", defaultDatasource='"
        + defaultDatasource
        + '\''
        + ", lazyLoadRouting.enabled="
        + lazyLoadRouting.enabled
        + '}';
  }

  public static class LazyLoadRouting {

    private boolean enabled;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
