package cloudphoto.albums.repositories;

import java.nio.charset.*;
import java.util.*;

public class Base64S3ObjectKeyEncoder implements S3ObjectKeyEncoder {
	private static final Charset encodingCharset = StandardCharsets.UTF_8;

	// Equal sign is not safe to use for S3 object keys
	private static final char base64EqualsReplacer = '.';

	private final Base64.Encoder base64Encoder;
	private final Base64.Decoder base64Decoder;

	public Base64S3ObjectKeyEncoder(Base64.Encoder base64Encoder, Base64.Decoder base64Decoder) {
		this.base64Encoder = base64Encoder;
		this.base64Decoder = base64Decoder;
	}

	public String encode(String name) {
		return base64Encoder
			.encodeToString(name.getBytes(encodingCharset))
			.replace('=', base64EqualsReplacer);
	}

	public String decode(String objectKey) {
		var base64EncodedKey = objectKey.replace(base64EqualsReplacer, '=');
		var decodedBytes = base64Decoder.decode(base64EncodedKey);
		return new String(decodedBytes, encodingCharset);
	}
}
