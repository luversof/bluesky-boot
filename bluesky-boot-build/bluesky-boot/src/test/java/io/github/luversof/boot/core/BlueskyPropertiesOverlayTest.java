package io.github.luversof.boot.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.uuid.UuidGeneratorProperties;
import io.github.luversof.boot.uuid.UuidGeneratorProperties.UuidVersion;
import io.github.luversof.boot.web.WebProperties;

/** null 은 건너뛰고, 맵은 키 단위로 합치고, 목록은 통째로 바꾸고, 값은 복사해 넣는다. */
class BlueskyPropertiesOverlayTest {

  @Test
  void nullDoesNotOverwrite() {
    var target = new UuidGeneratorProperties();
    target.setVersion(UuidVersion.V4);

    BlueskyPropertiesOverlay.overlay(new UuidGeneratorProperties(), target);

    assertThat(target.getVersion()).isEqualTo(UuidVersion.V4);
  }

  @Test
  void mapsMergeByKey() {
    var target = new CoreProperties();
    target.setProperties(new HashMap<>(Map.of("a", "parent-a", "b", "parent-b")));
    var source = new CoreProperties();
    source.setProperties(new HashMap<>(Map.of("b", "module-b")));

    BlueskyPropertiesOverlay.overlay(source, target);

    assertThat(target.getProperties())
        .containsOnly(Map.entry("a", "parent-a"), Map.entry("b", "module-b"));
  }

  @Test
  void listsAreReplacedAndCopied() {
    var target = new WebProperties();
    target.setNotSupportedBrowserRegPatterns(List.of("parent"));
    var source = new WebProperties();
    var sourceList = new ArrayList<>(List.of("module"));
    source.setNotSupportedBrowserRegPatterns(sourceList);

    BlueskyPropertiesOverlay.overlay(source, target);
    sourceList.add("changed later");

    assertThat(target.getNotSupportedBrowserRegPatterns()).containsExactly("module");
  }

  @Test
  void defaultsFillOnlyEmptyValues() {
    var empty = new CoreProperties();
    var configured = new CoreProperties();
    configured.setProperties(new HashMap<>(Map.of("a", "set")));

    empty.applyDefaults();
    configured.applyDefaults();

    assertThat(empty.getProperties()).isEmpty();
    assertThat(configured.getProperties()).containsOnly(Map.entry("a", "set"));
  }
}
