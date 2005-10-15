 berimport java.io.File;

public class Mp3File {
	private File srcFile;
	private String path;
	
	public Mp3File(File srcFile, String path) {
		this.srcFile = srcFile;
		this.path = path;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public String getPath() {
		return path;
	}

}
