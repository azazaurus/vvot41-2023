package cloudphoto.common.result;

public class Result {
	public static final Result success = new Result(true);
	public static final Result fail = new Result(false);

	private final boolean isSuccess;

	private Result(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public boolean isFailure() {
		return !isSuccess;
	}

	public static Result create(boolean isSuccess) {
		return isSuccess ? success : fail;
	}
}
