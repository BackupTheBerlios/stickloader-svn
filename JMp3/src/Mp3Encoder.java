import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Mp3Encoder implements FileProcessor {

	private String lamePath;
	private String [] args; //= "--priority 1 -f --abr 100";

	private String filename;
	private File destDir;
	private int totalEnc = 0;
	
	private int progress = 0;
	
	public Mp3Encoder(File destDir, String lamePath, String args) {
		filename = "NOTHING";
		this.lamePath = lamePath;
		this.destDir = destDir;
		setLameArgs(args);
	}
	
	public String getCurrentFilename() {
		return filename;
	}

	public int getProgress() {
		return progress;
	}
	
	public void setDestDir(File f) {
		destDir = f;
	}
	
	public void setLameArgs(String s) {
		this.args = parseArgs("--priority --nohist --disptime 0.5 " + s);
	}

	public boolean processFile(Mp3File file) {
		File targetDir = new File(destDir, file.getPath());
		if (!targetDir.exists()) {
			targetDir.mkdirs();
			targetDir.deleteOnExit();
		}
		File targetFile = new File(targetDir, file.getSrcFile().getName());
		
		filename = targetFile.getName();
		
		try {
			progress = 0;
			String [] newargs = new String[args.length+3];
			System.arraycopy(args, 0, newargs, 1, args.length);
			newargs[0] = lamePath;
			newargs[args.length+1] = file.getSrcFile().getAbsolutePath();
			newargs[args.length+2] = targetFile.getAbsolutePath();
			
			Process ps = Runtime.getRuntime().exec(newargs);
			
			
			//Process ps = Runtime.getRuntime().exec(new String [] { lamePath, "--priority", "--nohist", "--disptime", "0.5", "-f", "--abr", "100", , targetFile.getAbsolutePath()});
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
	
	private String [] parseArgs(String args) {
		return args.split(" ");
	}
	
	
	public void setLamePath(String s) {
		lamePath = s;
	}
	
	public String getLamePath() {
		return lamePath;
	}
	
	public int getTotalFiles() {
		return totalEnc;
	}
}
