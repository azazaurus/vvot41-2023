package ru.itiscl.task01.facedetection;

public class Configuration {
	public String cloudFolderId;
	public String staticAccessKeyId;
	public String secretStaticAccessKey;
	public String apiKey;
	public String taskQueueUrl;

	public Configuration() {
	}

	public Configuration(
			String cloudFolderId,
			String staticAccessKeyId,
			String secretStaticAccessKey,
			String apiKey,
			String taskQueueUrl) {
		this.cloudFolderId = cloudFolderId;
		this.staticAccessKeyId = staticAccessKeyId;
		this.secretStaticAccessKey = secretStaticAccessKey;
		this.apiKey = apiKey;
		this.taskQueueUrl = taskQueueUrl;
	}
}
