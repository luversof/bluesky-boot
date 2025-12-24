package io.github.luversof.boot.data.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PageResponse<T>(
		List<T> content,
		int number,
		int size,
		long totalElements,
		int totalPages,
		boolean first,
		boolean last,
		int numberOfElements,
		boolean empty) {
	
	/**
	 * PageResponse → Page (도메인 내부용)
	 */
	public Page<T> toPage() {
		return new PageImpl<>(
				content == null ? List.of() : content,
				PageRequest.of(number, size),
				totalElements);
	}
}
