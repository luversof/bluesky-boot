package io.github.luversof.boot.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * 그룹별 core 설정. 다른 그룹별 properties 가 쓰는 그룹별 ModuleInfo 를 여기서 컨텍스트에 등록한 뒤 합친다.
 *
 * @author bluesky
 */
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreGroupProperties extends AbstractBlueskyGroupProperties<CoreProperties> {

  private static final long serialVersionUID = 1L;

  /** Bean 생성 시 지정할 이름 */
  public static final String BEAN_NAME = "blueskyCoreGroupProperties";

  @Override
  @Autowired
  public void setParent(CoreProperties parent) {
    super.setParent(parent);
  }

  @Override
  public void load() {
    loadCoreFirst();
    var context = BlueskyBootContextHolder.getContext();
    getGroups()
        .forEach((groupName, coreProperties) -> {
          if (context.getModuleGroups().containsKey(groupName)
              && coreProperties.getModuleInfo() != null) {
            context.getGroupModuleInfoMap().put(groupName, coreProperties.getModuleInfo());
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
