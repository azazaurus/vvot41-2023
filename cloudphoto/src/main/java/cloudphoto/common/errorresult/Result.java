package cloudphoto.common.errorresult;

import java.util.*;

public class Result<Error> {
	private static final Result success = new Result();

	private final boolean isSuccess;
	private final Error error;

	private Result() {
		isSuccess = true;
		error = null;
	}

	private Result(Error error) {
		isSuccess = false;
		this.error = error;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public boolean isFailure() {
		return !isSuccess;
	}

	public Error getError() {
		if (isSuccess)
			throw new NoSuchElementException("Result is not failure");
		return error;
	}

	public static <Error> Result<Error> success() {
		return success;
	}

	public static <Error> Result<Error> fail(Error error) {
		return new Result<>(error);
	}
}
