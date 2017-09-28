import java.net.ServerSocket; //To make a server instance
import java.net.Socket; // to make sockets for each client
import java.io.PrintWriter; //to write into sockets output stream
import java.io.BufferedReader;//to read the socket input stream
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;//concurrent accessible hashmap

public class Server{
	private ServerSocket serverSocket;//the server instance? server socket...
	private int serverPort;
	private ConcurrentHashMap<String,StockItem> stock=null;

	public Server(int serverPort) throws IOException{
		StockCsv stockCsv=new StockCsv("stocks.csv");
		this.stock=stockCsv.getData();

		this.serverPort=serverPort;
		this.serverSocket=new ServerSocket(serverPort); //creating a ServerSocket which is listening into serverPort
		startService();
	}
	private void startService() throws IOException{
		try{
			while(true){
				handleClient();
				Thread.sleep(500);
			}
		}catch(Exception ex){
			System.out.println("startService error \n"+ex.getMessage());
		}
	}
	private void handleClient(){
		Thread t1=new Thread(new Runnable(){
            @Override
            public void run(){
            	try{
            		Socket socket=serverSocket.accept(); //to handle individual client, if any
            		try{
            			BufferedReader br=new BufferedReader(
            				new InputStreamReader(
            					socket.getInputStream()
            				)
            			);

	                	PrintWriter out =new PrintWriter(socket.getOutputStream(), true);

	                	out.println("you are connected!");//welcome message

	                	String line;
	                	for(line=br.readLine(); line!=null && !line.equals("quit");line=br.readLine()){
	                		
	                		StockItem s=stock.get(line.trim());
	                		out.println(s.getStockSymbol()+"\t"+s.getStockName()+"\t"+s.getStockPrice());
	                	}

	                    
	                }catch(Exception ex){

	                }finally{
	                	socket.close(); //closing the socket at the end, to save resources
	                }
            	}catch(Exception ex){
            		System.out.println("client handle error \n"+ex.getMessage());
            	}
                
            }   
        });
		t1.start();
	}

	public static void main(String[] args) throws IOException{
		Server server=new Server(9600);
	}
}