package io.github.luversof.boot.core;

import org.springframework.beans.BeanUtils;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * parent properties 의 공통 부분: ModuleInfo 프리셋 위에 설정 값을 얹고, 그래도 비어 있는 값만 기본값으로 채운다.
 *
 * <p>합치는 규칙은 {@link BlueskyPropertiesOverlay} 하나다. 기본값은 {@link #applyDefaults()} 에서 맨 마지막에만 채운다 -
 * 예전에는 기본값을 담은 builder 를 parent 값 위에 다시 덮어, parent 에 준 값이 모듈에서 기본값으로 돌아갔다.
 *
 * @param <P> properties 타입
 */
public abstract class AbstractBlueskyProperties<P extends AbstractBlueskyProperties<P>>
    implements BlueskyProperties {

  private static final long serialVersionUID = 1L;

  @Override
  public void load() {
    loadCoreFirst();
    var merged = newInstance();
    BlueskyPropertiesOverlay.overlay(
        preset(BlueskyBootContextHolder.getContext().getParentModuleInfo()), merged);
    BlueskyPropertiesOverlay.overlay(this, merged);
    merged.applyDefaults();
    // merged 는 this 를 다 담고 있으므로 null 아닌 값만 되돌려 넣으면 된다
    BlueskyPropertiesOverlay.overlay(merged, this);
  }

  /**
   * ModuleInfo 가 이 properties 에 주는 프리셋.
   *
   * @param moduleInfo moduleInfo
   * @return 프리셋(설정한 값만 채운 객체), 없으면 null
   */
  protected abstract P presetOf(ModuleInfo moduleInfo);

  /** 비어 있는(null) 값을 기본값으로 채운다. 합치기가 끝난 뒤 한 번 부른다. */
  protected void applyDefaults() {}

  /** parent 의 ModuleInfo 는 {@link CoreProperties} 가 등록한다. */
  void loadCoreFirst() {
    CoreLoadOrder.loadFirst(CoreProperties.class);
  }

  P preset(ModuleInfo moduleInfo) {
    return moduleInfo == null ? null : presetOf(moduleInfo);
  }

  @SuppressWarnings("unchecked")
  P newInstance() {
    return (P) BeanUtils.instantiateClass(getClass());
  }
}
