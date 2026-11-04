package io.github.luversof.boot.uuid;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.ModuleInfo;

@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorProperties extends AbstractBlueskyProperties<UuidGeneratorProperties> {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.uuid-generator";

  private UuidVersion version;

  public UuidGeneratorProperties() {}

  public UuidVersion getVersion() {
    return version;
  }

  public void setVersion(UuidVersion version) {
    this.version = version;
  }

  public enum UuidVersion {
    V1,
    V4,
    V6,
    V7
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    UuidGeneratorProperties that = (UuidGeneratorProperties) o;
    return version == that.version;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), version);
  }

  @Override
  public String toString() {
    return "UuidGeneratorProperties{" + "version=" + version + '}';
  }

  @Override
  protected UuidGeneratorProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getUuidGeneratorProperties();
  }

  @Override
  protected void applyDefaults() {
    if (version == null) {
      version = UuidVersion.V7;
    }
  }
}
