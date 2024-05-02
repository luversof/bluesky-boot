package io.github.luversof.boot.context;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BlueskyBootContext {

	private final List<String> moduleNameList = new ArrayList<>();

}
