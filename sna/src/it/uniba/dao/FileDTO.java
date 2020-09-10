package it.uniba.dao;

public class FileDTO {

	private String content="";
	private String name;
	private String id;

	FileDTO(String name, String string) {
		this.name = name;
		this.id = string;

	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}



}
