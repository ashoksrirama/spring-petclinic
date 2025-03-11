package org.springframework.samples.petclinic.owner;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Controller to serve pet images
 */
@Controller
@RequestMapping("/images")
public class ImageController {

	private final ImageStorageService imageStorageService;

	public ImageController(ImageStorageService imageStorageService) {
		this.imageStorageService = imageStorageService;
	}

	/**
	 * Serve pet images
	 * @param fileName the image file name
	 * @return the image resource
	 */
	@GetMapping("/{fileName:.+}")
	public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
		try {
			Path filePath = imageStorageService.getImagePath(fileName);
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
					.contentType(MediaType.IMAGE_JPEG)
					.body(resource);
			}
			else {
				return ResponseEntity.notFound().build();
			}
		}
		catch (MalformedURLException e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
