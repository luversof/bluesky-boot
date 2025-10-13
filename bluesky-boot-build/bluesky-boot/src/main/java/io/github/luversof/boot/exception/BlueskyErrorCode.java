package io.github.luversof.boot.exception;

public interface BlueskyErrorCode<E extends Enum<E> & BlueskyErrorCode<E>> {

	default BlueskyException exception() {
		return new BlueskyException((Enum<?>) this);
	}
	
	default BlueskyException exception(Object... errorMessageArgs) {
		return new BlueskyException((Enum<?>) this, errorMessageArgs);
	}
	
	default BlueskyException exceptionWithStatus(int status) {
		return new BlueskyException((Enum<?>) this, status);
	}
	
	default BlueskyException exceptionWithStatus(int status, Object... errorMessageArgs) {
		return new BlueskyException((Enum<?>) this, status, errorMessageArgs);
	}
	
	default void throwException() throws BlueskyException {
		throw exception();
	}
	
	default void throwException(Object... errorMessageArgs) throws BlueskyException {
		throw exception(errorMessageArgs);
	}
	
	default void throwExceptionWithStatus(int status) throws BlueskyException {
		throw exception(status);
	}
	
	default void throwExceptionWithStatus(int status, Object... errorMessageArgs) throws BlueskyException {
		throw exception(status, errorMessageArgs);
	}

}
