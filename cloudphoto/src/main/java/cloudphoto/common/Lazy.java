package cloudphoto.common;

import org.apache.commons.lang3.concurrent.*;
import org.apache.commons.lang3.function.*;

import java.util.function.*;

public class Lazy<T> extends LazyInitializer<T> {
	private final FailableSupplier<T, Exception> initializer;

	public Lazy(FailableSupplier<T, Exception> initializer) {
		this.initializer = initializer;
	}

	@Override
	protected T initialize() {
		try {
			return initializer.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get() {
		try {
			return super.get();
		} catch (ConcurrentException e) {
			throw new RuntimeException(e);
		}
	}
}
