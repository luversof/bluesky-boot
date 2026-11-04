package io.github.luversof.boot.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * BlueskyProperties를 구현한 ConfigurationProperties 중 모듈을 구현하지 않고 모듈에 대한 공통 설정을 관리하는 용도의 properties
 */
@ConfigurationProperties(prefix = CoreBaseProperties.PREFIX)
public class CoreBaseProperties implements BlueskyProperties {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.core";

  /** Bean 생성 시 지정할 이름 */
  public static final String BEAN_NAME = "blueskyCoreBaseProperties";

  /**
   * Define module invocation criteria
   *
   * <p>[domain (default), addPathPattern, moduleNameResolver]
   */
  private CoreResolveType resolveType = CoreResolveType.DOMAIN;

  /**
   * 별다른 설정 없이 moduleName만 지정하고자 하는 경우 사용 CoreProperties의 module 설정과 이 moduleNames 설정을 병합하여
   * moduleNameSet을 관리함
   */
  private Set<String> moduleNames = new HashSet<>();

  public CoreBaseProperties() {}

  public CoreResolveType getResolveType() {
    return resolveType;
  }

  public void setResolveType(CoreResolveType resolveType) {
    this.resolveType = resolveType;
  }

  public Set<String> getModuleNames() {
    return moduleNames;
  }

  public void setModuleNames(Set<String> moduleNames) {
    this.moduleNames = moduleNames;
  }

  public Map<String, List<String>> getModuleGroups() {
    return moduleGroups;
  }

  public void setModuleGroups(Map<String, List<String>> moduleGroups) {
    this.moduleGroups = moduleGroups;
  }

  public List<String> getLogExcludedExceptions() {
    return logExcludedExceptions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CoreBaseProperties that = (CoreBaseProperties) o;
    return resolveType == that.resolveType
        && (moduleNames != null ? moduleNames.equals(that.moduleNames) : that.moduleNames == null)
        && (moduleGroups != null
            ? moduleGroups.equals(that.moduleGroups)
            : that.moduleGroups == null)
        && (logExcludedExceptions != null
            ? logExcludedExceptions.equals(that.logExcludedExceptions)
            : that.logExcludedExceptions == null);
  }

  @Override
  public int hashCode() {
    int result = resolveType != null ? resolveType.hashCode() : 0;
    result = 31 * result + (moduleNames != null ? moduleNames.hashCode() : 0);
    result = 31 * result + (moduleGroups != null ? moduleGroups.hashCode() : 0);
    result = 31 * result + (logExcludedExceptions != null ? logExcludedExceptions.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CoreBaseProperties{"
        + "resolveType="
        + resolveType
        + ", moduleNames="
        + moduleNames
        + ", moduleGroups="
        + moduleGroups
        + ", logExcludedExceptions="
        + logExcludedExceptions
        + '}';
  }

  /**
   * 여러 모듈이 반복된 설정을 사용하는 경우 BlueskyGroupProperties를 설정하고 이 groupModules 로 각 module이 속한 group을 지정하여
   * group properties를 module properties로 전파 처리함
   */
  private Map<String, List<String>> moduleGroups = new HashMap<>();

  /**
   * Exception log 제외 대상 목록 로그 확인이 불필요한 에러 항목에 대해 exception log 제외 처리 목록을 추가하고자 하는 경우
   * bluesky-boot.core.log-except-exception-additional-list로 설정하면 합산처리 됨.
   */
  private List<String> logExcludedExceptions = new ArrayList<>();

  public void setLogExcludedExceptions(List<String> list) {
    for (String value : list) {
      if (!this.logExcludedExceptions.contains(value)) {
        this.logExcludedExceptions.add(value);
      }
    }
  }

  /** logExcludedExceptions 에 추가하고자 하는 경우 사용 */
  public void setLogExceptExceptionAdditionalList(String... values) {
    setLogExcludedExceptions(Arrays.asList(values));
  }

  public String[] getLogExceptExceptionAdditionalList() {
    return new String[] {};
  }

  /** BlueskyProperties 중 제일 먼저 호출되기 때문에 refresh 초기화 관련 처리를 여기에서 담당함 */
  @Override
  public void load() {
    var blueskyBootContext = BlueskyBootContextHolder.getContext();
    blueskyBootContext.clear();
    blueskyBootContext.getModuleNames().addAll(getModuleNames());
    blueskyBootContext.getModuleGroups().putAll(getModuleGroups());
  }
}
