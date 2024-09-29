package techproed.tests.smoke;

import org.testng.Assert;
import org.testng.annotations.Test;
import techproed.pages.DataProviderPage;
import techproed.utilities.DataProviderUtils;
import techproed.utilities.Driver;

public class DataProviderTest {

    @Test(dataProvider = "positiveTestData", dataProviderClass = DataProviderUtils.class)
    public void testName(String age) {

        Driver.getDriver().get("https://dataprovider.netlify.app/");
        DataProviderPage dataProviderPage = new DataProviderPage();
        dataProviderPage.searchBox.sendKeys(age);
        dataProviderPage.button.click();
        Assert.assertTrue(dataProviderPage.positiveVerifyMessage.isDisplayed());
        Driver.closeDriver();

    }

    @Test(dataProvider = "negativeTestData", dataProviderClass = DataProviderUtils.class)
    public void negativeTest(String age) {
        Driver.getDriver().get("https://dataprovider.netlify.app/");
        DataProviderPage dataProviderPage = new DataProviderPage();
        dataProviderPage.searchBox.sendKeys(age);
        dataProviderPage.button.click();
        Assert.assertTrue(dataProviderPage.negativeVerifyMessage.isDisplayed());
        Driver.closeDriver();

    }


}
