import java.net.ServerSocket; //To make a server instance
import java.net.Socket; // to make sockets for each client
import java.io.PrintWriter; //to write into sockets output stream
import java.io.BufferedReader;//to read the socket input stream
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;//concurrent accessible hashmap


//GUI
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Server extends MyFrame{
	//Server Data
	private ServerSocket serverSocket;//the server instance? server socket...
	private int serverPort;
	private ConcurrentHashMap<String,StockItem> stock=null;
	//GUI elements
	private JLabel[] label,labelDisplay;
	private final String[] displaySymbols={"FB","VRTU","MSFT","GOOGL","YHOO","XLNX","TSLA","TXN"};
	private String sym;
	private final int
		LABEL_WIDTH=200,
		LABEL_HEIGHT=40,
		LABEL_GAP=40,
		LABEL_PADDING_HOR=40,
		LABEL_PADDING_VER=20;

	public Server(int serverPort) throws IOException{ //contructor
		super("Auction Server",
			520,
			500,
			false,true
		);//passing arguments to MyFrame constructor
		

		StockCsv stockCsv=new StockCsv("stocks.csv");
		this.stock=stockCsv.getData();//Return the HashMap<String,StockItem>

		this.serverPort=serverPort;
		this.serverSocket=new ServerSocket(serverPort); //creating a ServerSocket which is listening into serverPort
		
		init();//GUI call
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

	                	out.println("Hi, you have connected to the server! Please enter your name.");//welcome message
	                	String line,client_name=null;

	                	int transaction_state=0;
	                	/*
	                	0-client ready, client entered invalid sym
	                	1-client entered valid sym
	                	2-client bidded ->goto 0
	                	*/

	                	//here line null means [return]?
	                	StockItem s=null; double bid=null;
	                	for(line=br.readLine(); line!=null && !line.equals("quit");line=br.readLine()){
	                		if(client_name==null && !line.trim().equals("")){//non empty first string
	                			client_name=line.trim();//record client name
	                		}else{//client name entered and ready to bid
	                			if(transaction_state==0){
	                				//current line is clients sym input
	                				//if valid, display price, transaction_state=1;
	                				s=null; bid=null;//resetting states for new bid
	                				s=stock.get(line.trim());//get the client requested item
	                				if(s==null){
	                					out.println("Invalid Symbol");//prompt that the symbol is invalid
	                				}else{//valid symbol, proceed
	                					transaction_state=1;
	                				}
	                			}else if(transaction_state==1){
	                				//current line is client bid
	                				//if valid input,update bid, transaction_state=0->for new bid
	                				try{
	                					bid=Double.valueOf(line.trim())//parse double value:bid
	                				
	                					//proceed with bid update
	                					//here make sure to catch exceptions
	                					try{
	                						s.updateStockPrice(bid); //updating price
	                						//updated successfully
	                						out.println("Bid updated successfully");
	                						//changing state to new bid cycle
	                						transaction_state=0;
	                					}catch(Exception ex){
	                						out.println("Bid update Error!")
	                					}

	                				}catch(Exception ex){
	                					out.println("Invalid bid");
	                				}
	                			}
	                			
	                		}
	                		
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

	private void init(){
		this.setLayout(null); //absolute layout :)
		JPanel panel=new JPanel();
		panel.setLayout(null);
		panel.setSize(520,500);

		int len=displaySymbols.length;
		label=new JLabel[len];
		labelDisplay=new JLabel[len];

		for(int index=0;index<len;index++){
			sym=displaySymbols[index];
			//initializing new objects\
			label[index]=new JLabel();
			labelDisplay[index]=new JLabel();

			label[index].setText(sym);
				label[index].setLocation(LABEL_PADDING_HOR,LABEL_PADDING_VER*(index+1));
				label[index].setSize(LABEL_WIDTH,LABEL_HEIGHT);
				
			labelDisplay[index].setText(
				String.valueOf(this.stock.get(sym).getStockPrice())
			);
				labelDisplay[index].setLocation(LABEL_PADDING_HOR+LABEL_WIDTH+LABEL_GAP,LABEL_PADDING_VER*(index+1));
				labelDisplay[index].setSize(LABEL_WIDTH,LABEL_HEIGHT);

			panel.add(label[index]);
			panel.add(labelDisplay[index]);
		}
		this.add(panel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack(); //JFrame pack();
		System.out.println("GUI OK");
		updateUi();
	}

	private void updateUi(){
		Thread uiThread=new Thread(new Runnable(){
			@Override
            public void run(){
            	int len=displaySymbols.length;
            	while(true){
            		for(int index=0;index<len;index++){
						sym=displaySymbols[index];
						labelDisplay[index].setText(
							String.valueOf(stock.get(sym).getStockPrice())
						);
					}
	            	try{Thread.sleep(500);}catch(Exception ex){System.out.println("UiThread-delay-error!");}
            	}
            }
		});
	}

	public static void main(String[] args) throws IOException{
		Server server=new Server(2000);
	}
}