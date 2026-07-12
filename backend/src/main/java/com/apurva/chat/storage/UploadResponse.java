package com.apurva.chat.storage;

/** Returned after uploading a file: where it lives, whether it's an image, and its name. */
public record UploadResponse(String url, String type, String name) {
}
