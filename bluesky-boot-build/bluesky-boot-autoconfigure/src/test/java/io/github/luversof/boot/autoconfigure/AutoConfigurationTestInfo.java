package io.github.luversof.boot.autoconfigure;

import java.util.HashSet;

import io.github.luversof.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import io.github.luversof.boot.autoconfigure.context.i18n.LocaleAutoConfiguration;
import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

public class AutoConfigurationTestInfo {

  public static final String[] BASE_PROPERTY = {"spring.profiles.active=localdev"};

  public static final Class<?>[] CORE_USER_CONFIGURATION = {
    CoreAutoConfiguration.class, LocaleAutoConfiguration.class
  };

  public static final Class<?>[] CORE_MESSAGESOURCE_CONFIGURATION = {
    org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class
  };

  public static final Class<?>[] CORE_MESSAGESOURCE_USER_CONFIGURATION =
      addClassAll(CORE_USER_CONFIGURATION, MessageSourceAutoConfiguration.class);

  public static final Class<?>[] JDBC_CONFIGURATION = {
    org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration.class
  };

  public static final Class<?>[] JDBC_USER_CONFIGURATION = {DataSourceAutoConfiguration.class};

  @SuppressWarnings("unused")
  private static String[] addAll(String[] target, String... strings) {
    var set = new HashSet<>();

    for (var str : target) {
      set.add(str);
    }

    for (var str : strings) {
      set.add(str);
    }

    return set.toArray(new String[set.size()]);
  }

  public static Class<?>[] addClassAll(Class<?>[] target, Class<?>... classes) {
    var set = new HashSet<>();

    for (var clazz : target) {
      set.add(clazz);
    }

    for (var clazz : classes) {
      set.add(clazz);
    }
    return set.toArray(new Class<?>[set.size()]);
  }

  public static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>... classes) {
    return addClassAll(target, addClassAll(target2, classes));
  }

  public static Class<?>[] addClassAll(
      Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>... classes) {
    return addClassAll(target, addClassAll(target2, target3, classes));
  }

  public static Class<?>[] addClassAll(
      Class<?>[] target,
      Class<?>[] target2,
      Class<?>[] target3,
      Class<?>[] target4,
      Class<?>... classes) {
    return addClassAll(target, addClassAll(target2, target3, target4, classes));
  }

  public static Class<?>[] addClassAll(
      Class<?>[] target,
      Class<?>[] target2,
      Class<?>[] target3,
      Class<?>[] target4,
      Class<?>[] target5,
      Class<?>... classes) {
    return addClassAll(target, addClassAll(target2, target3, target4, target5, classes));
  }

  public static Class<?>[] addClassAll(
      Class<?>[] target,
      Class<?>[] target2,
      Class<?>[] target3,
      Class<?>[] target4,
      Class<?>[] target5,
      Class<?>[] target6,
      Class<?>... classes) {
    return addClassAll(target, addClassAll(target2, target3, target4, target5, target6, classes));
  }
}
