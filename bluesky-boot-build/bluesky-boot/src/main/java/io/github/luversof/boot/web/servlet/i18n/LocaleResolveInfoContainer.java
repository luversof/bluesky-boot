package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;

/**
 * Container containing locale information to use
 */
public class LocaleResolveInfoContainer {

	private List<LocaleResolveInfo> resolveList = new ArrayList<>();

	/**
	 * 최종적으로 사용할 LocaleResolveInfo 제공자
	 */
	private Supplier<LocaleResolveInfo> representativeSupplier;

	/**
	 * 최종 결과 localeContext
	 * representativeSupplier가 지정되지 않으면 defaultLocale을 사용
	 * 
	 * @return
	 */
	public Locale getLocale() {
		if (representativeSupplier == null) {
			return getDefaultLocale();
		}

		var resolveLocale = representativeSupplier.get().getResolveLocale();
		return resolveLocale == null ? getDefaultLocale() : resolveLocale;
	}

	private Locale getDefaultLocale() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, LocaleProperties.DEFAULT_BEAN_NAME)
				.getDefaultLocale();
	}

	public List<LocaleResolveInfo> getResolveList() {
		return resolveList;
	}

	public void setResolveList(List<LocaleResolveInfo> resolveList) {
		this.resolveList = resolveList;
	}

	public Supplier<LocaleResolveInfo> getRepresentativeSupplier() {
		return representativeSupplier;
	}

	public void setRepresentativeSupplier(Supplier<LocaleResolveInfo> representativeSupplier) {
		this.representativeSupplier = representativeSupplier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LocaleResolveInfoContainer that = (LocaleResolveInfoContainer) o;
		return Objects.equals(resolveList, that.resolveList)
				&& Objects.equals(representativeSupplier, that.representativeSupplier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resolveList, representativeSupplier);
	}

	@Override
	public String toString() {
		return "LocaleResolveInfoContainer{" +
				"resolveList=" + resolveList +
				", representativeSupplier=" + representativeSupplier +
				'}';
	}
}
