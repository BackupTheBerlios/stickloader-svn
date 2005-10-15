import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Mp3Encoder implements FileProcessor {

	final static public String LAME = "d:\\tools\\lame\\lame.exe";
	final static public String ARGS = "--priority 1 -f --abr 100";

	private String filename;
	private File destDir;
	private int totalEnc = 0;
	
	private int progress = 0;
	
	public Mp3Encoder(File destDir) {
		filename = "NOTHING";
		this.destDir = destDir;
		
	}
	
	public String getCurrentFilename() {
		return filename;
	}

	public int getProgress() {
		return progress;
	}

	public boolean processFile(Mp3File file) {
		File targetDir = new File(destDir, file.getPath());
		if (!targetDir.exists()) targetDir.mkdirs();
		File targetFile = new File(targetDir, file.getSrcFile().getName());
		
		filename = targetFile.getName();
		
		try {
			progress = 0;
			Process ps = Runtime.getRuntime().exec(new String [] { LAME, "--priority", "--nohist", "--disptime", "0.5", "-f", "--abr", "100", file.getSrcFile().getAbsolutePath(), targetFile.getAbsolutePath()});
			//final BufferedReader input = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			ps.getInputStream().close();
			final BufferedReader error = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
			ps.getOutputStream().close();
			Thread myThread = new Thread() {
				public void run() {
					String in = "", err = "";
					while (!isInterrupted()) {
						try {
							/*if (input.ready()) {
								in = input.readLine();
								System.out.println(in);
							}*/
							if (error.ready()) {
								err = error.readLine();
								for (String s : err.split("[()]")) {
									if (s.endsWith("%")) {
										try {
											progress = Integer.parseInt(s.replace("%", ""));
										} catch (Exception e) {
											//...
										}
									}
								}
								//System.err.println(err);
							}							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			myThread.start();
			ps.waitFor();
			totalEnc++;
			filename = "NOTHING";
			myThread.interrupt();
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public int getTotalFiles() {
		return totalEnc;
	}
}
