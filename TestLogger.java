import java.io.IOException;

public class TestLogger{
	public static void main(String[] args) throws IOException{
		Logger logger=new Logger("log.csv");
		logger.writeLog("chandima","FB",1.25d);
		logger.close();
	}
}