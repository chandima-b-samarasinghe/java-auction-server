import java.util.Calendar;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

public class Logger{
	//to handle file
	FileAppend fileAppend;
	//to get data time
	DateFormat dateFormat;
	Calendar cal;

	public Logger(String filename) throws IOException{
		fileAppend=new FileAppend(filename);
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cal = Calendar.getInstance();
	}
	public void writeLog(String client, String symbol,double bid) throws IOException{
		fileAppend.writeLine(
			dateFormat.format(cal.getTime())+","+
			symbol+","+
			bid+","+
			client
		);
	}
	public void close() throws IOException{
		fileAppend.closeConnection();
	}
}