import java.io.FileWriter;	
import java.io.IOException;

public class FileAppend{
	FileWriter fw=null;
	public FileAppend(String filename) throws IOException{
		filename=FileAppend.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20"," ")+"/"+filename;
		fw=new FileWriter(filename,true);
	}

	public void writeLine(String line) throws IOException{
		fw.append(line);
		fw.append(System.getProperty("line.separator"));//new line
	}

	public void closeConnection(){
		try{
			fw.flush();
			fw.close();
		}catch(Exception ex){	
			System.out.println("CloseConnectionError!:\n"+ex.getMessage());
		}
	}

	public void clearFile() throws IOException{
		fw.write("");
	}
}