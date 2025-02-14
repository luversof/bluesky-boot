package io.github.luversof.boot.web;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web")
public class WebProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Whether to check unsupported browsers
	 */
	private Boolean checkNotSupportedBrowser = true;
	
	/**
	 * Unsupported browser check pattern list
	 */
	private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");
	
	/**
	 * Registering an exception list address pattern when checking for unsupported browser
	 */
	private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		var builder = parentModuleInfo == null ? WebProperties.builder() : parentModuleInfo.getWebPropertiesBuilder();
		
		propertyMapper.from(this::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
		propertyMapper.from(this::getNotSupportedBrowserRegPatternList).to(builder::notSupportedBrowserRegPatternList);
		propertyMapper.from(this::getNotSupportedBrowserExcludePathPatternList).to(builder::notSupportedBrowserExcludePathPatternList);
		
		BeanUtils.copyProperties(builder.build(), this);
	}

	public static WebPropertiesBuilder builder() {
		return new WebPropertiesBuilder();
	}

	public static class WebPropertiesBuilder {
		
		private Boolean checkNotSupportedBrowser = true;
		
		private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");
		
		private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");
		
		public WebPropertiesBuilder checkNotSupportedBrowser(Boolean checkNotSupportedBrowser) {
			this.checkNotSupportedBrowser = checkNotSupportedBrowser;
			return this;
		}
		
		public WebPropertiesBuilder notSupportedBrowserRegPatternList(List<String> notSupportedBrowserRegPatternList) {
			this.notSupportedBrowserRegPatternList = notSupportedBrowserRegPatternList;
			return this;
		}
		
		public WebPropertiesBuilder notSupportedBrowserExcludePathPatternList(List<String> notSupportedBrowserExcludePathPatternList) {
			this.notSupportedBrowserExcludePathPatternList = notSupportedBrowserExcludePathPatternList;
			return this;
		}
		
		public WebProperties build() {
			return new WebProperties(
				checkNotSupportedBrowser,
				notSupportedBrowserRegPatternList,
				notSupportedBrowserExcludePathPatternList
			);
		}
	}
}
