package io.github.luversof.boot.core;

import io.github.luversof.boot.context.ApplicationContextUtil;

/**
 * properties 를 합치기 전에 먼저 load 되어 있어야 하는 core 설정을 초기화한다.
 *
 * <p>순서: {@link CoreBaseProperties}(컨텍스트를 비우고 모듈 이름 목록을 채운다) &rarr; {@link CoreProperties}·{@link
 * CoreGroupProperties}·{@link CoreModuleProperties}(parent·그룹·모듈별 ModuleInfo 를 등록한다) &rarr; 나머지.
 * 순서를 bean 생성 순서에 맡기면, 나중에 load 된 CoreBaseProperties 가 등록을 지우거나 core 보다 먼저 합친 properties 가 프리셋을 못
 * 봤다.
 */
final class CoreLoadOrder {

  private CoreLoadOrder() {}

  static void loadFirst(Class<?> coreType) {
    var applicationContext = ApplicationContextUtil.getApplicationContext();
    if (applicationContext != null) {
      applicationContext.getBeanProvider(coreType).getIfAvailable();
    }
  }
}
