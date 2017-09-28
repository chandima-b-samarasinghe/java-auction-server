import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.StringBuilder;

public class FileRead{
	BufferedReader br=null;
    FileReader fr=null;
    String currentRecord;
    StringBuilder sb=null;
	public FileRead(String filename){
		filename=FileRead.class.getProtectionDomain().
        	getCodeSource().getLocation().getPath().replace("%20"," ")+"/"+filename;
        sb=new StringBuilder();
        try{
            fr=new FileReader(filename);
            br=new BufferedReader(fr);

            while((currentRecord=br.readLine())!=null){
            	sb.append(currentRecord+System.getProperty("line.separator"));
	        }
	        fr.close();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
	}
	public String getContent(){
		return this.sb.toString();
	}
}