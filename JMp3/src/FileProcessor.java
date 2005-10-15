public interface FileProcessor {
	
	public String getCurrentFilename();
	
	public int getProgress();
	
	public boolean processFile(Mp3File file);
	
	public int getTotalFiles();
}
