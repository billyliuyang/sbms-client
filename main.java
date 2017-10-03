package uk.ac.sheffield;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Test {

	public static void main(String[] args) throws InterruptedException, AWTException, TesseractException {
		final Rectangle screenRect = new Rectangle(156,160,100,100);
		final double average = 70;
		double[] arr = new double[60];
		StandardDeviation sd = new StandardDeviation();
		Mean m = new Mean();
		ITesseract instance = new Tesseract();
		instance.setTessVariable("tessedit_char_whitelist", "0123456789");
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);                 
		caps.setCapability(
		                        PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
		                        "E:\\programming workspace\\HeartRateReader\\phantomjs.exe"
		                    );

		WebDriver driver = new PhantomJSDriver(caps);
		driver.get("https://smartbackgroundmusic.herokuapp.com/login");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.findElement(By.name("name")).sendKeys("admin");
		driver.findElement(By.name("password")).sendKeys("admin");
		driver.findElement(By.name("commit")).click();
		System.out.println("login successfully.");
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.get("https://smartbackgroundmusic.herokuapp.com/collection");

		while(true){
			Thread.sleep(2000);
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			String result = instance.doOCR(capture);
			result = result.split("\n")[0];
			if (result.startsWith("1")){
				Rectangle overScreenRect = new Rectangle(168,160,129,100);
				BufferedImage overCapture = new Robot().createScreenCapture(overScreenRect);
				result = instance.doOCR(overCapture);
				result = result.split("\n")[0];
			}
			arr = push(arr, Double.parseDouble(result));
			if (noZero(arr)){
				driver.findElement(By.name("emotionv")).sendKeys(emotionValence(sd.evaluate(arr)));
				driver.findElement(By.name("emotiona")).sendKeys(emotionArousal(average, m.evaluate(arr)));
				System.out.println("Emotion Valence:"+emotionValence(sd.evaluate(arr)));
				System.out.println("Emotion Arousal:"+emotionArousal(average, m.evaluate(arr)));
			}
			driver.findElement(By.name("currenthr")).sendKeys(result);
	    	driver.findElement(By.name("commit")).click();
		}
		
	}
	
	private static double[] push(double[] array, double push) {

	    double[] update = new double[array.length];

	    for (int i = 0; i < array.length-1; i++){
	    	update[i] = array[i+1];
	    }
	    update[array.length-1] = push;
	    return update;
	}


	private static boolean noZero(double[] array){

		boolean b = true;

		for (double d : array){

			if (d == 0.0){

				b = false;

				break;
			}
		}
		return b;
	}
	
	private static String emotionArousal(double average, double mean){
		String s = null;
		if (mean<(average-15)){
			s = "-5";
		}else if(mean >= (average-15) && mean < (average-12)){
			s = "-4";
		}else if(mean >= (average-12) && mean < (average-9)){
			s= "-3";
		}else if(mean >= (average-9) && mean < (average-5)){
			s= "-2";
		}else if(mean >= (average-5) && mean < average){
			s= "-1";
		}else if(mean > average && mean <= (average+5)){
			s= "1";
		}else if(mean > (average+5) && mean <= (average+10)){
			s= "2";
		}else if(mean > (average+10) && mean <= (average+16)){
			s= "3";
		}else if(mean > (average+16) && mean <= (average+22)){
			s= "4";
		}else {
			s= "5";
		}
		return s;
	}
	
	private static String emotionValence(double sd){
		String s = null;
		if (sd < 1){
			s = "-5";
		}else if (sd >= 1 && sd < 1.5){
			s = "-4";
		}else if (sd >= 1.5 && sd < 2){
			s = "-3";
		}else if (sd >= 2 && sd < 2.5){
			s = "-2";
		}else if (sd >= 2.5 && sd < 3){
			s = "-1";
		}else if (sd >= 3 && sd < 3.5){
			s = "1";
		}else if (sd >= 3.5 && sd < 4){
			s = "2";
		}else if (sd >= 4 && sd < 4.5){
			s = "3";
		}else if (sd >= 4.5 && sd < 5){
			s = "4";
		}else {
			s = "5";
		}
		return s;
	}
}
