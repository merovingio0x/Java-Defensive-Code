package it.uniba.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.Part;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class SanityFile {

	private HashMap<String, String> fileInfo = new HashMap<>();
	private Part file;
	//Match 1 or more (<), anything, 1 or more (>)
	public SanityFile(Part filePart, String contentType) throws IOException, SAXException, TikaException {

		this.file = filePart;
		getFileMetadata();
		if (!checkFileExtension(contentType)) {
			throw new IllegalArgumentException("Invalid file extension!");
		}

		if (fileContainsXss()) {
			throw new IllegalArgumentException("File contains xss script!");
		}

	}

	private boolean fileContainsXss() throws IOException {

		StringBuilder fileText = new StringBuilder();
		byte[] buffer = new byte[getFileSize()];

		try (InputStream inputStream = this.file.getInputStream();) {

			while ((inputStream.read(buffer)) != -1) {
				fileText.append(new String(buffer)).append("\n");
			}

			// Blacklisting
			if (fileText.toString().contains("script")) {
				return true;
			}
		}

		return false;
	}

	public Part getFile() {
		return this.file;

	}

	private boolean checkFileExtension(String contentType)

	{
		return fileInfo.get("Content-Type").contains(contentType);

	}

	public int getFileSize()

	{
		Optional<String> size = Optional.ofNullable(fileInfo.get("File Size"));
		return Integer.parseInt(size.map(p -> p.replaceAll("\\D+", "")).orElse("1000000"));

	}

	private void getFileMetadata() throws IOException, SAXException, TikaException {

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		try (InputStream fileContent = this.file.getInputStream()) {
			parser.parse(fileContent, handler, metadata, new ParseContext());
			for (String name : metadata.names()) {
				System.out.println(name + ":\t" + metadata.get(name));
				fileInfo.put(name, metadata.get(name));

			}

		}

	}

}
