import java.util.*;

public class TestStockCsv{
	public static void main(String[] args){
		StockCsv stock=new StockCsv("stocks.csv");
		for(Map.Entry<String,StockItem> s:stock.getData().entrySet()){
			System.out.println(s.getValue().getStockSymbol()+"\t"+s.getValue().getStockName()+"\t"+s.getValue().getStockPrice());
		}
	}
}