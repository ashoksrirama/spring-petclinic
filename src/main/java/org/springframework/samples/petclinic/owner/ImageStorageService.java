package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service to handle file storage operations
 */
@Service
public class ImageStorageService {

	private final Path imageStorageLocation;

	public ImageStorageService(@Value("${file.storage.location:/var/app/petclinic/}") String imageStorageLocation) {
		this.imageStorageLocation = Paths.get(imageStorageLocation).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.imageStorageLocation);
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	/**
	 * Store file to the system
	 * @param file the file to store
	 * @return the name of the file as stored
	 */
	public String storeImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}

		// Generate a unique file name to prevent conflicts
		String originalFileName = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFileName != null && originalFileName.contains(".")) {
			fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
		}
		String fileName = UUID.randomUUID().toString() + fileExtension;

		try {
			Path targetLocation = this.imageStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not store file " + originalFileName, ex);
		}
	}

	/**
	 * Delete a file from the storage
	 * @param fileName the name of the file to delete
	 */
	public void deleteImage(String fileName) {
		if (fileName == null || fileName.isEmpty()) {
			return;
		}

		try {
			Path filePath = this.imageStorageLocation.resolve(fileName);
			Files.deleteIfExists(filePath);
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not delete file " + fileName, ex);
		}
	}

	/**
	 * Get the full path of a stored file
	 * @param fileName the name of the file
	 * @return the path to the file
	 */
	public Path getImagePath(String fileName) {
		return this.imageStorageLocation.resolve(fileName);
	}

}
