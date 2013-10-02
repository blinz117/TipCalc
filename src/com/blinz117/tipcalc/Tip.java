package com.blinz117.tipcalc;

import java.math.BigDecimal;

public class Tip
{
	BigDecimal mPercent;
	BigDecimal mAmount;
	BigDecimal mTotal;
	
	public Tip(BigDecimal percent, BigDecimal amount, BigDecimal total)
	{
		mPercent = percent;
		mAmount = amount;
		mTotal = total;
	}
	
	public BigDecimal getPercent() {return mPercent;}
	public BigDecimal getAmount() {return mAmount;}
	public BigDecimal getTotal() {return mTotal;}
}
