package cloudphoto.common.valueerrorresult;

import java.util.*;

public class Result<Value, Error> {
	private final boolean isSuccess;
	private final Value value;
	private final Error error;

	private Result(Value value) {
		this.isSuccess = true;
		this.value = value;
		this.error = null;
	}

	public Result(Value value, Error error) {
		this.isSuccess = false;
		this.value = value;
		this.error = error;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public boolean isFailure() {
		return !isSuccess;
	}

	public Value getValue() {
		return value;
	}

	public Error getError() {
		if (isSuccess)
			throw new NoSuchElementException("Result is not failure");
		return error;
	}

	public static <Value, Error> Result<Value, Error> success(Value value) {
		return new Result<>(value);
	}

	public static <Value, Error> Result<Value, Error> fail(Error error) {
		return new Result<>(null, error);
	}
}
