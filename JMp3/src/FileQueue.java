import java.util.LinkedList;

public class FileQueue extends LinkedList<Mp3File> {
	
	public int getFiles() {
		return size();
	}
	
	public Mp3File getNextFile() {
		return poll();
	}
	
	public void addFile(Mp3File file) {
		add(file);
	}

}
