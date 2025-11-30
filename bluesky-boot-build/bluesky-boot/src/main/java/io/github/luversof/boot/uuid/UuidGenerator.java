package io.github.luversof.boot.uuid;

import java.util.UUID;

@FunctionalInterface
public interface UuidGenerator {

	UUID create();

}
