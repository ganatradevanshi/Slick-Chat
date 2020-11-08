package com.neu.prattle.service.mediaservice;

/**
 * This interface represents a MediaService to handling uploading user media files.
 */
public interface MediaService {

    /**
     * Handle uploading user media file.
     *
     * @param bytes    - media file in form of byte array.
     * @param fileName - name of the file.
     * @return - url of the uploaded file.
     */
    String upload(byte[] bytes, String fileName);

    /**
     * Handle deleting the file.
     *
     * @param fileName - file to be deleted.
     */
    void delete(String fileName);

    /**
     * Return true if a file with the provided name exists, false otherwise.
     *
     * @param fileName - name of the file to be checked against.
     * @return - true if the file exists, false otherwise.
     */
    boolean mediaExists(String fileName);
}
