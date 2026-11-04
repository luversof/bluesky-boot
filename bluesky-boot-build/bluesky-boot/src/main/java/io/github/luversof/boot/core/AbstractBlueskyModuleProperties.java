package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * 모듈별 properties 의 공통 부분. 하위 클래스는 parent 주입(필요하면 {@code @Qualifier})만 정한다.
 *
 * <p>모듈 값 = parent &larr; 그룹 &larr; 모듈의 ModuleInfo 프리셋 &larr; 모듈 설정 순으로 겹친 뒤(뒤가 이긴다) 빈 값만 기본값으로
 * 채운다.
 *
 * @param <P> 모듈 하나의 properties 타입
 */
public abstract class AbstractBlueskyModuleProperties<P extends AbstractBlueskyProperties<P>>
    implements BlueskyModuleProperties<P>, BeanNameAware {

  private static final long serialVersionUID = 1L;

  private String beanName;

  private P parent;

  private Map<String, P> modules = new HashMap<>();

  @Override
  public String getBeanName() {
    return beanName;
  }

  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @Override
  public P getParent() {
    return parent;
  }

  @Override
  public void setParent(P parent) {
    this.parent = parent;
  }

  @Override
  public Map<String, P> getModules() {
    return modules;
  }

  public void setModules(Map<String, P> modules) {
    this.modules = modules;
  }

  @Override
  public void load() {
    loadCoreFirst();
    parentReload();

    var context = BlueskyBootContextHolder.getContext();
    for (var moduleName : context.getModuleNames()) {
      var merged = getParent().newInstance();
      BlueskyPropertiesOverlay.overlay(getParent(), merged);
      BlueskyPropertiesOverlay.overlay(getGroup(moduleName), merged);
      BlueskyPropertiesOverlay.overlay(
          getParent().preset(context.getModuleInfoMap().get(moduleName)), merged);
      BlueskyPropertiesOverlay.overlay(getModules().get(moduleName), merged);
      merged.applyDefaults();
      getModules().put(moduleName, merged);
    }
  }

  /** 모듈 이름 목록과 모듈별 ModuleInfo 는 {@link CoreModuleProperties} 가 등록한다. */
  void loadCoreFirst() {
    CoreLoadOrder.loadFirst(CoreModuleProperties.class);
  }

  // equals/hashCode 는 Object 의 동일성 비교 그대로다(예전 하위 클래스들도 super.equals 로 결국 동일성이었다).

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "{beanName='"
        + beanName
        + "', parent="
        + parent
        + ", modules="
        + modules
        + '}';
  }
}
