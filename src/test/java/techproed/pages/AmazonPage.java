package techproed.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import techproed.utilities.Driver;
import techproed.utilities.ReusableMethods;

public class AmazonPage {

    public AmazonPage(){
        PageFactory.initElements(Driver.getDriver(),this);
    }

    @FindBy(id = "twotabsearchtextbox")
    public WebElement searchBox;


        @FindBy(xpath = "//*[contains( text() ,'Try different image')]")
        public WebElement captchaHandling;

         public void handleCaptcha(){
             try {
                 ReusableMethods.click(captchaHandling);
             }catch (Exception e){
     //captcha sayfada cikmamasi durumunda nosuchelement exception i boylece ignore ettik
             }

            }





}
