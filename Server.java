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

public class Server implements ActionListener{
	//Server Data
	private ServerSocket serverSocket;//the server instance? server socket...
	private int serverPort;
	private ConcurrentHashMap<String,StockItem> stock=null;
	//Log handling
	private Logger logger;
	//GUI elements
	public MyFrame myframe;
	private JLabel[] label,labelDisplay;
	private JButton buttonLog;
	private final String[] displaySymbols={"FB","VRTU","MSFT","GOOGL","YHOO","XLNX","TSLA","TXN"};
	private String sym;
	private final int
		LABEL_WIDTH=150,
		LABEL_HEIGHT=40,
		LABEL_GAP=20,
		LABEL_PADDING_HOR=40,
		LABEL_PADDING_VER=30;

	public Server(int serverPort) throws IOException{ //contructor
		myframe=new MyFrame("Auction Server",
			320,
			400,
			false,true
		);//passing arguments to MyFrame constructor
		
		myframe.addWindowListener(new WindowAdapter(){//closing action listener
            @Override
            public void windowClosing(WindowEvent e){
            	try{
					atClose();//clean up at close
            	}catch(Exception ex){
            		System.out.println("Cleanup Error at Closing\n"+ex.getMessage());
            	}
                e.getWindow().dispose();
            }
        });

		StockCsv stockCsv=new StockCsv("stocks.csv");
		this.stock=stockCsv.getData();//Return the HashMap<String,StockItem>

		this.serverPort=serverPort;
		this.serverSocket=new ServerSocket(serverPort); //creating a ServerSocket which is listening into serverPort
		
		this.logger=new Logger("log.csv");//log file

		init();//GUI call
		startService();//Server Service
	}
	private void startService() throws IOException{
		try{
			while(true){
				handleClient();
				Thread.sleep(100);//safty delay
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
	                	out.println(printServerWelcome());

	                	out.println("Hi, you have connected to the server! Please enter your name to proceed.");//welcome message
	                	String line,client_name=null;

	                	int transaction_state=0;
	                	/*
	                	0-client ready, client entered invalid sym
	                	1-client entered valid sym
	                	2-client bidded ->goto 0
	                	*/

	                	//here line null means [return]?
	                	StockItem s=null; double bid=0;
	                	for(line=br.readLine(); line!=null && !line.equals("quit");line=br.readLine()){
	                		if(client_name==null && !line.trim().equals("")){//non empty first string
	                			client_name=line.trim();//record client name
	                			out.println("Hi "+client_name+", Enter a Symbol to bid");
	                		}else{//client name entered and ready to bid

	                			if(transaction_state==0){
	                				//current line is clients sym input
	                				//if valid, display price, transaction_state=1;
	                				s=null; bid=0;//resetting states for new bid
	                				s=stock.get(line.trim());//get the client requested item
	                				if(s==null){
	                					//out.println("Invalid Symbol");//prompt that the symbol is invalid
	                					out.println("-1");//show -1
	                				}else{//valid symbol, proceed
	                					//out.println("The Symbol is selected, current Price="+s.getStockPrice()+" ; Enter your bid");
	                					out.println(s.getStockPrice());//show current price
	                					transaction_state=1;
	                				}
	                			}else if(transaction_state==1){
	                				//current line is client bid
	                				//if valid input,update bid, transaction_state=0->for new bid
	                				try{
	                					bid=Double.valueOf(line.trim());//parse double value:bid
	                					//out.println("Valid bid amount");
	                					//proceed with bid update
	                					//here make sure to catch exceptions
	                					try{
	                						s.updateStockPrice(bid); //updating price
	                						logger.writeLog(client_name,s.getStockSymbol(),bid);	
	                						//updated successfully
	                						//out.println("Bid updated successfully");
	                						//changing state to new bid cycle
	                						out.println("Bid is placed successfully, enter a Symbol to bid");
	                						transaction_state=0;
	                					}catch(Exception ex){
	                						out.println("Bid update Error!");
	                					}

	                				}catch(Exception ex){
	                					out.println("Invalid Bid!");
	                				}
	                			}

	                		}
	                		
	                	} //user quited

	                    
	                }catch(Exception ex){//which means no connection from the serverSocket?
	                	//simply ignoring :)
	                }finally{
	                	socket.close(); //closing the socket at the end, to save resources
	                	//Finishing
	                	
	                	//save logs
	                	//save stock
	                }
            	}catch(Exception ex){
            		System.out.println("Client Handler Error!\n"+ex.getMessage());
            	}
            }   
        });
		t1.start();
	}

	private void init(){
		myframe.setLayout(null); //absolute layout :)
		JPanel panel=new JPanel();
		panel.setLayout(null);
		panel.setSize(520,500);

		int len=displaySymbols.length;
		label=new JLabel[len];
		labelDisplay=new JLabel[len];

		//creating Labels
		for(int index=0;index<len;index++){
			sym=displaySymbols[index];
			//initializing new UI objects
			label[index]=new JLabel();
			labelDisplay[index]=new JLabel();

			//setting properties
			label[index].setText(sym);
				label[index].setLocation(LABEL_PADDING_HOR,LABEL_PADDING_VER*(index+1));
				label[index].setSize(LABEL_WIDTH,LABEL_HEIGHT);
				label[index].setFont(new Font(
						"Tahoma", //font face
						1, //style (plain=0, bold-1)
						20 //font size
					)
				);
				
			labelDisplay[index].setText(
				String.valueOf(this.stock.get(sym).getStockPrice())
			);
				labelDisplay[index].setLocation(LABEL_PADDING_HOR+LABEL_WIDTH+LABEL_GAP,LABEL_PADDING_VER*(index+1));
				labelDisplay[index].setSize(LABEL_WIDTH,LABEL_HEIGHT);
				labelDisplay[index].setFont(new Font(
						"Tahoma", //font face
						1, //style (plain=0, bold-1)
						18 //font size
					)
				);

			//adding elements to the panel	
			panel.add(label[index]);
			panel.add(labelDisplay[index]);
		}

		//creating log elements
		buttonLog=new JButton("View Log");
		buttonLog.setLocation(LABEL_PADDING_HOR,LABEL_PADDING_VER*10);
		buttonLog.setSize(LABEL_WIDTH+75,LABEL_HEIGHT);
		buttonLog.setFont(
			new Font(
						"Tahoma", //font face
						1, //style (plain=0, bold-1)
						18 //font size
			)
		);
		buttonLog.addActionListener(this);
		panel.add(buttonLog);


		//adding panel to the JFrame
		myframe.add(panel);

		//setting finishing JFrame properties
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setVisible(true);
		myframe.pack(); //JFrame pack();
		//System.out.println("GUI OK");
		
		//update JFrame with inital elements
		initUi();
		//invoke the ui updating loop
		updateUi();
	}

	private void initUi(){ //initial ui update
		Thread uiThread=new Thread(new Runnable(){
			@Override
            public void run(){
            		SwingUtilities.invokeLater(new Runnable() {
	                    public void run() {
	                        //initial repaint
	                    	myframe.repaint();
	                    }
	                });
            }
		});
		uiThread.start();
	}

	private void updateUi(){ //repeated ui update
		Thread uiThread=new Thread(new Runnable(){
			@Override
            public void run(){
            	int len=displaySymbols.length;
            	while(true){

            		SwingUtilities.invokeLater(new Runnable() {
	                    public void run() {
	                        //do ui updates here
	                        for(int index=0;index<len;index++){
								sym=displaySymbols[index];
								labelDisplay[index].setText(
									String.valueOf(stock.get(sym).getStockPrice())
								);
							}
							myframe.repaint();
							//System.out.println("ui updated");
	                    }
	                });
            							
	            	try{Thread.sleep(500);}catch(Exception ex){System.out.println("UiThread-delay-error!");}
            	}
            }
		});
		uiThread.start();
	}

	private String printServerWelcome(){
		return "=============================================================================\n"+
"                        Welcome to the Auction Server!\n"+
"                                    *****\n"+
"\n"+
"procedure:\n"+
"	01) Type your name.\n"+
"	02)\n"+
"            * Enter the Symbol of the Stock Item.\n"+
"            * Enter your Bid amount.\n"+
"	03) Above step 02) will repeatedly continue until you type quit command.\n"+
"	04) To quit from the server at any point, type quit command\n"+
"\n"+
"commands:\n"+
"	quit - to quit from the server.\n"+
"\n"+
"                   developed by group 14 (e14049 / e14305)\n"+
"=============================================================================";
	}

	private void atClose() throws IOException{//do cleanup things here, when the application closing
		this.logger.close();//closing logger

	}

	public void actionPerformed(ActionEvent event){
		if(event.getSource()==buttonLog){//log button event
			FrameLog showlog=new FrameLog();
		}
	}

	//main
	public static void main(String[] args) throws IOException{
		Server server=new Server(2000);
	}
}