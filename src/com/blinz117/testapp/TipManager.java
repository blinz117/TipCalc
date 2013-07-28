package com.blinz117.testapp;

import java.math.BigDecimal;
import java.util.Vector;

public class TipManager {
	
	double mMinPercent;
	double mMaxPercent;
	double mBaseAmount;
	
	Vector<BigDecimal> mTipPercentages;
	Vector<BigDecimal> mTipAmounts;
	Vector<BigDecimal> mTotals;
	
	public TipManager(double minPercent, double maxPercent)
	{
		mMinPercent = minPercent;
		mMaxPercent = maxPercent;
		mBaseAmount = 0.00;
		
		mTipPercentages = new Vector<BigDecimal>();
		mTipAmounts = new Vector<BigDecimal>();
		mTotals = new Vector<BigDecimal>();
		
		CalculateTips();
	}
	
	public void CalculateTips()
	{
		mTipPercentages.clear();
		mTipAmounts.clear();
		mTotals.clear();
		
		mTipPercentages.add(round(mMinPercent, 1));
		mTipPercentages.add(round(mMaxPercent, 1));
		
		for (int ndx = 0; ndx < mTipPercentages.size(); ndx++)
		{
			 double currTip = mTipPercentages.elementAt(ndx).doubleValue()/100.0 * mBaseAmount;
			 mTipAmounts.add(round(currTip, 2));
			 
			 double currTotal = mBaseAmount + currTip;
			 mTotals.add(round(currTotal, 2));
		}
	}
	
	public void UpdateBaseAmount(double newBaseAmount)
	{
		mBaseAmount = newBaseAmount;
		CalculateTips();
	}
	
	public void UpdatePercentMinMax(double newMin, double newMax)
	{
		mMinPercent = newMin;
		mMaxPercent = newMax;
		CalculateTips();
	}
	
	public Vector<BigDecimal> GetPercentages() { return mTipPercentages; }
	
	public Vector<BigDecimal> GetTips() { return mTipAmounts; }
	
	public Vector<BigDecimal> GetTotals() { return mTotals; }
	
    public static BigDecimal round(double value, int places)
    {
        BigDecimal result = new BigDecimal(Double.toString(value));
        result = result.setScale(places, BigDecimal.ROUND_HALF_UP);       
        return result;
    }
	
}
