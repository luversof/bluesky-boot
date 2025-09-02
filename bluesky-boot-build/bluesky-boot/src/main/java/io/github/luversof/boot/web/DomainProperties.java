package io.github.luversof.boot.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.web.domain";

	/**
	 * When using multi-modules, if you make an add-path-pattern declaration, you need an addPathPatterns declaration for each module.
	 * 
	 * Registering the AntPathMatcher Pattern
	 */
	private List<String> addPathPatternList = new ArrayList<>();

	/**
	 * If the site address is multi, use.
	 * 
	 * If your site doesn't have a PC web/mobile web distinction, declare only WEB and use it.
	 */
	private List<URI> webList = new ArrayList<>();
	
	/**
	 * Use the mobile web address for your site if it is multi-use
	 * 
	 * Mobile web address must be declared if there is a PC web/mobile web distinction
	 * 
	 * Used separately in unified notification processing
	 */
	private List<URI> mobileWebList = new ArrayList<>();
	
	/**
	 * When declared and used by domain in a multi-module project, the domain is a variable for local development that is set aside for development purposes.
	 * 
	 * You can use development domains in the form of a list by declaring them in succession with ",".
	 * 
	 * ex) devDomainList=http://local.a.com,http://local.b.com
	 */
	private List<URI> devDomainList = new ArrayList<>();

	/**
	 * List of static paths (not redirect targets)
	 */
	private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
	
	/**
	 * List of exception paths (list that are not redirect targets)
	 */
	private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
	
	/**
	 * Set the request root path
	 * 
	 * Default is "/" (full)
	 */
	private String requestPath = "/";
	
	/**
	 * Set the forward root path
	 */
	private String forwardPath = "/";
	
	/**
	 * The address of the site
	 * 
	 * If your site doesn't have a PC web/mobile web distinction, declare only WEB and use the
	 */
	public void setWeb(URI uri) {
		if (this.webList.contains(uri)) {
			return;
		}
		this.webList.add(uri);
	}
	
	/**
	 * Mobile web address for your site
	 * 
	 * Mobile web address must be declared if there is a PC web/mobile web distinction
	 */
	public void setMobileWeb(URI uri) {
		if (this.mobileWebList.contains(uri)) {
			return;
		}
		this.mobileWebList.add(uri);
	}
	
	public URI getWeb() {
		// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
		return this.getWebList().isEmpty() ? null : this.getWebList().get(0);
	}
	
	public URI getMobileWeb() {
		// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
		return mobileWebList.isEmpty() ? getWeb() : mobileWebList.get(0);
	}
	
	protected BiConsumer<DomainProperties, DomainPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(properties::getAddPathPatternList).to(builder::addPathPatternList);
			propertyMapper.from(properties::getWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::webList);
			propertyMapper.from(properties::getMobileWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::mobileWebList);
			propertyMapper.from(properties::getDevDomainList).whenNot(x -> x == null || x.isEmpty()).to(builder::devDomainList);
			propertyMapper.from(properties::getStaticPathList).whenNot(x -> x == null || x.isEmpty()).to(builder::staticPathList);
			propertyMapper.from(properties::getExcludePathList).whenNot(x -> x == null || x.isEmpty()).to(builder::excludePathList);
			propertyMapper.from(properties::getRequestPath).to(builder::requestPath);
			propertyMapper.from(properties::getForwardPath).to(builder::forwardPath);
		};
	}
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		
		var builder = parentModuleInfo == null ? DomainProperties.builder() : parentModuleInfo.getDomainPropertiesBuilder();
		
		getPropertyMapperConsumer().accept(this, builder);
		
		BeanUtils.copyProperties(builder.build(), this);
	}
	
	public static DomainPropertiesBuilder builder() {
		return new DomainPropertiesBuilder();
	}
	
	public static class DomainPropertiesBuilder implements BlueskyPropertiesBuilder<DomainProperties> {
		
		private List<String> addPathPatternList = new ArrayList<>();

		private List<URI> webList = new ArrayList<>();
		
		private List<URI> mobileWebList = new ArrayList<>();
		
		private List<URI> devDomainList = new ArrayList<>();

		private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
		
		private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
		
		private String requestPath = "/";
		
		private String forwardPath = "/";
		
		public DomainPropertiesBuilder addPathPatternList(List<String> addPathPatternList) {
			this.addPathPatternList = addPathPatternList;
			return this;
		}
		
		public DomainPropertiesBuilder webList(List<URI> webList) {
			this.webList = webList;
			return this;
		}
		
		public DomainPropertiesBuilder mobileWebList(List<URI> mobileWebList) {
			this.mobileWebList = mobileWebList;
			return this;
		}
		
		public DomainPropertiesBuilder devDomainList(List<URI> devDomainList) {
			this.devDomainList = devDomainList;
			return this;
		}
		
		public DomainPropertiesBuilder staticPathList(List<String> staticPathList) {
			this.staticPathList = staticPathList;
			return this;
		}
		
		public DomainPropertiesBuilder excludePathList(List<String> excludePathList) {
			this.excludePathList = excludePathList;
			return this;
		}
		
		public DomainPropertiesBuilder requestPath(String requestPath) {
			this.requestPath = requestPath;
			return this;
		}
		
		public DomainPropertiesBuilder forwardPath(String forwardPath) {
			this.forwardPath = forwardPath;
			return this;
		}
		
		@Override
		public DomainProperties build() {
			return new DomainProperties(
				addPathPatternList,
				webList,
				mobileWebList,
				devDomainList,
				staticPathList,
				excludePathList,
				requestPath,
				forwardPath
			);
		}
		
	}
}
