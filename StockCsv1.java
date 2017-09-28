import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.util.*; //hashmap
import java.util.concurrent.ConcurrentHashMap;//concurrent accessible hashmap
public class StockCsv{
	private final String
		STR_SYM="Symbol",
		STR_NAME="Security Name",
		STR_PRICE="Price";
	public String[] header,arr_currentRecord;
	private String currentRecord;
	public int[] header_index;
	private HashMap<String,StockItem> data=null;

	public StockCsv(String filename){
		filename=StockCsv.class.getProtectionDomain().
        	getCodeSource().getLocation().getPath().replace("%20"," ")+"/"+filename;
        BufferedReader br=null;
        FileReader fr=null;

        data=new HashMap<String,StockItem>();
        header_index=new int[3];

        try{
            fr=new FileReader(filename);
            br=new BufferedReader(fr);

            String currentRecord;
            
            if((currentRecord=br.readLine())!=null){
            	header=currentRecord.trim().split(",");
            }

            for(int index=0;index<header.length;index++){
            	if(header[index].equals(STR_SYM)){ header_index[0]=index;}
            	else if(header[index].equals(STR_NAME)){ header_index[1]=index;}
            	else if(header[index].equals(STR_PRICE)){ header_index[2]=index;}	
            }

            while((currentRecord=br.readLine())!=null){
            	arr_currentRecord=currentRecord.trim().split(",");
            	data.put(
            		arr_currentRecord[header_index[0]], //key
            		new StockItem( //value
            			arr_currentRecord[header_index[0]],
            			arr_currentRecord[header_index[1]],
            			Double.valueOf(arr_currentRecord[header_index[2]])	
            		)
            	);
            }

        }catch (Exception ex){
            System.out.println("CSV FileRead Error\n"+ex.getMessage());
        }
	}

	public HashMap<String,StockItem> getData(){
		return this.data;
	}

	public synchronized StockItem getItem(String stockSymbol){
		return this.data.get(stockSymbol);
	}
}