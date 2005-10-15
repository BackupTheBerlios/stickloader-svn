import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopier implements FileProcessor {
	
	private String filename;
	private int totalCop = 0;
	private File destDir;
	private File tmpDir;
	private int progress;

	public FileCopier(File tmpDir, File dir) {
		destDir = dir;
		this.tmpDir = tmpDir;
		filename = "NOTHING";
	}
	
	public String getCurrentFilename() {
		return filename;
	}

	public int getProgress() {
		return progress;
	}

	public boolean processFile(Mp3File file) {
			File srcFile = new File(new File(tmpDir, file.getPath()), file.getSrcFile().getName());
			File targetDir = new File(destDir, file.getPath());
			if (!targetDir.exists()) targetDir.mkdirs();
			File destFile = new File(targetDir, file.getSrcFile().getName());
			filename = destFile.getName();
			JMp3.debug("copying "+filename);
			try {
				JMp3.debug(destFile.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			if (destFile.exists()) destFile.delete();		
			
			byte [] buffer = new byte[8192];
			
			try {
				progress = 0;
				destFile.createNewFile();
				InputStream in = new BufferedInputStream( new FileInputStream(srcFile));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
				
				long max = srcFile.length();
				
				int length = 0;
				int pos = 0;
				while ((length = in.read(buffer)) >= 0) {
					out.write(buffer, 0, length);
					pos += length;
					progress = 100 * (int)(pos/max);
				}
				in.close();
				out.flush();
				out.close();
				totalCop++;
				filename = "NOTHING";
				return true;
			} catch (FileNotFoundException exc) {
				exc.printStackTrace();
				return false;
			} catch (IOException exc) {
				exc.printStackTrace();
				return false;
			}
	}

	public int getTotalFiles() {
		return totalCop;
	}

	public void setDestDir(File destDir) {
		this.destDir = destDir;
	}

	public void setTempDir(File tmpDir) {
		this.tmpDir = tmpDir;
	}

}
