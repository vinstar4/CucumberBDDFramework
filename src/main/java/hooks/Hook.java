package hooks;

import org.openqa.selenium.WebDriver;
import dataprovider.ConfigReader;
import factory.BrowserFactory;
import helper.Utility;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hook {
	
	public WebDriver driver;
	
	@Before
	public void startSession()
	{
		System.out.println("LOG:INFO - Running Before Hook- Creating Browser Session");
		
		driver=BrowserFactory.startBrowser(ConfigReader.getProperty("browser"), ConfigReader.getProperty("url"));
	}
	


	@After
	public void closeSession()
	{
		System.out.println("LOG:INFO - Running After Hook- Deleting Browser Session");
		driver.quit();
	}
	
	@AfterStep
	public void tearDown(Scenario scenario)
	{
		System.out.println("LOG:INFO - Running AfterStep Hook- Captuing State of Scenario");
		
		if(scenario.isFailed())
		{
			byte []arr=Utility.captureScreenshot(driver);
			
			scenario.attach(arr, "image/png", scenario.getName());
		}
		
	}
	
}
