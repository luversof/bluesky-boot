package io.github.luversof.boot.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * 모듈별 core 설정. 다른 모듈별 properties 가 쓰는 모듈 이름 목록과 모듈별 ModuleInfo 를 여기서 컨텍스트에 등록한 뒤 합친다.
 *
 * @author bluesky
 */
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreModuleProperties extends AbstractBlueskyModuleProperties<CoreProperties> {

  private static final long serialVersionUID = 1L;

  /** Bean 생성 시 지정할 이름 */
  public static final String BEAN_NAME = "blueskyCoreModuleProperties";

  @Override
  @Autowired
  public void setParent(CoreProperties parent) {
    super.setParent(parent);
  }

  @Override
  public void load() {
    loadCoreFirst();
    var context = BlueskyBootContextHolder.getContext();
    // coreProperties 의 경우 moduleNames 과 modules 의 key 를 병합한다.
    context.getModuleNames().addAll(getModules().keySet());
    getModules()
        .forEach((moduleName, coreProperties) -> {
          if (coreProperties.getModuleInfo() != null) {
            context.getModuleInfoMap().put(moduleName, coreProperties.getModuleInfo());
          }
        });
    super.load();
  }

  /** 등록하기 전에 CoreBaseProperties 가 먼저 load 되어야 한다(나중에 load 되면 컨텍스트를 비워 등록이 지워진다). */
  @Override
  void loadCoreFirst() {
    CoreLoadOrder.loadFirst(CoreBaseProperties.class);
  }
}
