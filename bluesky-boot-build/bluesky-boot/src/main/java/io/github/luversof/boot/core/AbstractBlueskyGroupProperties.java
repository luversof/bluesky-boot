package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * 그룹별 properties 의 공통 부분. 하위 클래스는 parent 주입(필요하면 {@code @Qualifier})만 정한다.
 *
 * <p>그룹 값 = parent &larr; 그룹의 ModuleInfo 프리셋 &larr; 그룹 설정 순으로 겹친 뒤(뒤가 이긴다) 빈 값만 기본값으로 채운다.
 *
 * @param <P> 그룹 하나의 properties 타입
 */
public abstract class AbstractBlueskyGroupProperties<P extends AbstractBlueskyProperties<P>>
    implements BlueskyGroupProperties<P>, BeanNameAware {

  private static final long serialVersionUID = 1L;

  private String beanName;

  private P parent;

  private Map<String, P> groups = new HashMap<>();

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
  public Map<String, P> getGroups() {
    return groups;
  }

  public void setGroups(Map<String, P> groups) {
    this.groups = groups;
  }

  @Override
  public void load() {
    loadCoreFirst();
    parentReload();

    var context = BlueskyBootContextHolder.getContext();
    for (var groupName : context.getModuleGroups().keySet()) {
      var merged = getParent().newInstance();
      BlueskyPropertiesOverlay.overlay(getParent(), merged);
      BlueskyPropertiesOverlay.overlay(
          getParent().preset(context.getGroupModuleInfoMap().get(groupName)), merged);
      BlueskyPropertiesOverlay.overlay(getGroups().get(groupName), merged);
      merged.applyDefaults();
      getGroups().put(groupName, merged);
    }
  }

  /** 그룹별 ModuleInfo 는 {@link CoreGroupProperties} 가 등록한다. */
  void loadCoreFirst() {
    CoreLoadOrder.loadFirst(CoreGroupProperties.class);
  }

  // equals/hashCode 는 Object 의 동일성 비교 그대로다(예전 하위 클래스들도 super.equals 로 결국 동일성이었다).

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "{beanName='"
        + beanName
        + "', parent="
        + parent
        + ", groups="
        + groups
        + '}';
  }
}
