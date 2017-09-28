public class StockItem{
	private String stockSymbol, stockName;
	private double stockPrice;
	public StockItem(String stockSymbol,String stockName,double stockPrice){
		this.stockSymbol=stockSymbol; this.stockName=stockName; this.stockPrice=stockPrice;
	}

	public synchronized void  updateStockPrice(double price){this.stockPrice=price;}
	public synchronized String getStockSymbol(){return this.stockSymbol;}
	public synchronized String getStockName(){return this.stockName;}
	public synchronized double getStockPrice(){return this.stockPrice;}
}