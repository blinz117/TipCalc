package com.blinz117.tipcalc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;

public class TipManager {
	
	double mMinPercent;
	double mMaxPercent;
	double mBaseAmount;
	
	boolean mRoundTips;
	
	ArrayList<BigDecimal> mTipPercentages;
	ArrayList<BigDecimal> mTipAmounts;
	ArrayList<BigDecimal> mTotals;
	
	List<Tip> mTips;
	
	TipCalculationListener mTipListener;
	
	CalculateTipsTask calcThread;
	
	public interface TipCalculationListener{
		
		public void onTipCalculation();
		public void preTipCalculation();
	}
	
	private class CalculateTipsTask extends AsyncTask<Double, Void, Void>{
		protected Void doInBackground(Double...values)
		{
			mTips.clear();
			
			double baseAmount = values[0];
			double min = values[1];
			double max = values[2];
			BigDecimal percent, amount, total;
			BigDecimal minTotal, maxTotal;
			double currTip, currTotal;

			
			boolean bMinMaxEqual = (max == min);
			
			// add minimum
			percent = round(min, 1);
			currTip = percent.doubleValue()/100.0 * baseAmount;
			amount = round(currTip, 2);
			 
			currTotal = baseAmount + currTip;
			total = round(currTotal, 2);
			
			mTips.add(new Tip(percent, amount, total));
			
			minTotal = total;
			
			if (!bMinMaxEqual)
			{
				percent = round(max, 1);
				currTip = percent.doubleValue()/100.0 * baseAmount;
				amount = round(currTip, 2);
				 
				currTotal = baseAmount + currTip;
				total = round(currTotal, 2);
				
				mTips.add(new Tip(percent, amount, total));		
			}
			
			maxTotal = total;
			
			if (!bMinMaxEqual && mRoundTips)
			{
				BigDecimal nextDollar = minTotal.setScale(0, BigDecimal.ROUND_DOWN).add(BigDecimal.ONE);
				
				while (maxTotal.compareTo(nextDollar) > 0)
				{
					BigDecimal roundAmount = nextDollar.setScale(2);
					// calculate tip and percentage
					BigDecimal newTipAmount = roundAmount.subtract(round(baseAmount,2));
					double dPercent = newTipAmount.doubleValue() * 100.0 / baseAmount;
					BigDecimal newPercent = round(dPercent, 1);
					
					mTips.add(new Tip(newPercent, newTipAmount, roundAmount));
					
					nextDollar = roundAmount.add(BigDecimal.ONE);
				}
			}
			return null;
		}
		
		protected void onPreExecute()
		{
			mTipListener.preTipCalculation();
		}
		
		protected void onPostExecute(Void result)
		{
			mTipListener.onTipCalculation();
		}
	}
	
	public TipManager(Activity tipListener, double minPercent, double maxPercent)
	{
		
        try {
            mTipListener = (TipCalculationListener) tipListener;
        } catch (ClassCastException e) {
            throw new ClassCastException(tipListener.toString() + " must implement NumPadListener");
        }
        
        calcThread = null;
		
		mMinPercent = minPercent;
		mMaxPercent = maxPercent;
		mBaseAmount = 0.00;
		
		mRoundTips = false;
		
		mTipPercentages = new ArrayList<BigDecimal>();
		mTipAmounts = new ArrayList<BigDecimal>();
		mTotals = new ArrayList<BigDecimal>();
		
		mTips = new ArrayList<Tip>();
		
		CalculateTips();
	}
	
	public void CalculateTips()
	{
		if (calcThread != null)
			stopCalculating();
		
		calcThread = new CalculateTipsTask();
		calcThread.execute(mBaseAmount, mMinPercent, mMaxPercent);
	}
	
	public void stopCalculating()
	{
		if (calcThread != null)
		{
			calcThread.cancel(true);
			calcThread = null;
		}
	}
	
	public void UpdateBaseAmount(double newBaseAmount)
	{
		mBaseAmount = newBaseAmount;
	}
	
	public void UpdatePercentMinMax(double newMin, double newMax)
	{
		// TODO: Add validation to min and max values
		// Set min and max based on which is actually bigger, since there is no
		// validation on the values right now
		mMinPercent = Math.min(newMin, newMax);
		mMaxPercent = Math.max(newMin, newMax);
	}
	
	public void UpdateRoundTips(boolean bUseRoundTips)
	{
		mRoundTips = bUseRoundTips;
	}
	
	public ArrayList<BigDecimal> GetPercentages() { return mTipPercentages; }
	
	public ArrayList<BigDecimal> GetTips() { return mTipAmounts; }
	
	public ArrayList<BigDecimal> GetTotals() { return mTotals; }
	
    public static BigDecimal round(double value, int places)
    {
        BigDecimal result = new BigDecimal(Double.toString(value));
        result = result.setScale(places, BigDecimal.ROUND_HALF_UP);       
        return result;
    }
	
}
