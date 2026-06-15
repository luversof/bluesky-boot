package io.github.luversof.boot.core;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

public abstract class AbstractBlueskyModuleProperties<
        P extends AbstractBlueskyProperties<P, B>, B extends BlueskyPropertiesBuilder<P>>
    implements BlueskyModuleProperties<P> {

  private static final long serialVersionUID = 1L;

  @Override
  public void load() {
    parentReload();

    BlueskyBootContextHolder.getContext()
        .getModuleNameSet()
        .forEach(
            moduleName -> {
              var builder = getBuilder(moduleName);

              // 모듈별 moduleInfo(ServiceInfo) 기본값. parent/group 적용 이후 다시 얹어
              // parent > group > 모듈별 moduleInfo > 모듈별 설정 의 우선순위를 보장한다.
              var moduleInfoDefaults = this.getBuilder(moduleName).build();

              if (!getModules().containsKey(moduleName)) {
                getModules().put(moduleName, builder.build());
              }

              var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
              propertyMapperConsumer.accept(getParent(), builder);
              propertyMapperConsumer.accept(getGroup(moduleName), builder);
              propertyMapperConsumer.accept(moduleInfoDefaults, builder);
              propertyMapperConsumer.accept(getModules().get(moduleName), builder);

              getModules().put(moduleName, builder.build());
            });

  }

  protected abstract B getBuilder(String moduleName);
}
