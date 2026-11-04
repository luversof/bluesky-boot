package io.github.luversof.boot.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

/**
 * properties 를 겹쳐 합치는 규칙 하나: source 의 필드 중 null 이 아닌 값만 target 의 같은 필드에 덮어쓴다.
 *
 * <p>null 은 "설정하지 않음" 이다. 맵은 키 단위로 합치고(target 에 있던 키는 남는다), 목록·집합은 통째로 바꾼다. 값은 복사해 넣어 한쪽을 고쳐도 다른 쪽이
 * 바뀌지 않는다.
 *
 * <p>getter/setter 가 아니라 필드로 합친다 - 설정 바인딩용 세터(SpEL 문자열을 받는 {@code setModuleInfo(String)}, 목록에 하나
 * 더하는 {@code setWeb(URI)})나 계산해 돌려주는 getter 가 섞여 있어도 실제 상태만 옮긴다.
 */
public final class BlueskyPropertiesOverlay {

  private BlueskyPropertiesOverlay() {}

  public static void overlay(Object source, Object target) {
    if (source == null) {
      return;
    }
    ReflectionUtils.doWithFields(
        source.getClass(),
        field -> {
          var targetField = ReflectionUtils.findField(target.getClass(), field.getName());
          if (targetField == null || !targetField.equals(field)) {
            return;
          }
          ReflectionUtils.makeAccessible(field);
          var value = ReflectionUtils.getField(field, source);
          if (value == null) {
            return;
          }
          if (value instanceof Map<?, ?> map) {
            var merged = new LinkedHashMap<Object, Object>();
            if (ReflectionUtils.getField(field, target) instanceof Map<?, ?> existing) {
              merged.putAll(existing);
            }
            merged.putAll(map);
            ReflectionUtils.setField(field, target, merged);
          } else {
            ReflectionUtils.setField(field, target, copyOf(value));
          }
        },
        BlueskyPropertiesOverlay::isState);
  }

  private static boolean isState(Field field) {
    int modifiers = field.getModifiers();
    return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
  }

  private static Object copyOf(Object value) {
    if (value instanceof List<?> list) {
      return new ArrayList<>(list);
    }
    if (value instanceof Set<?> set) {
      return new LinkedHashSet<>(set);
    }
    if (value instanceof Collection<?> collection) {
      return new ArrayList<>(collection);
    }
    return value;
  }
}
