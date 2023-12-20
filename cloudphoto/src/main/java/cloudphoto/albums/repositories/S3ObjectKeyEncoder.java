package cloudphoto.albums.repositories;

public interface S3ObjectKeyEncoder {
	String encode(String name);

	String decode(String objectKey);
}
