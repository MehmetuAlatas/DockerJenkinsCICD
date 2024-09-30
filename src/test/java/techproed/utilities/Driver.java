package techproed.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Reporter;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Driver {
    /*
    Açıklamalar:
    Reporter.getCurrentTestResult(): Bu metod, TestNG'nin testng.xml dosyasından gelen parametreleri
    dinamik olarak almanıza olanak tanır. Böylece browser parametresini test sırasında Driver sınıfında çekebiliriz.

    browser Parametresi: Eğer ConfigReader'da bir browser değeri ayarlanmışsa onu kullanırız, değilse
    testng.xml'den gelen browser parametresini alırız. Bu, paralel testlerin farklı tarayıcılarla çalışmasını sağlar.
     */

    // ThreadLocal ile her bir test için bağımsız WebDriver tanımlanıyor
    private static ThreadLocal<WebDriver> driverPool = new ThreadLocal<>();

    // WebDriver'ı uzaktan başlatmak için gereken URL (Hub URL)
//    private static final String HUB_URL = "http://localhost:4445/wd/hub";

    private static final String HUB_URL = ConfigReader.getProperty("hubURL");// Hub servisini expose ettiğiniz port


    public static WebDriver getDriver() {

        synchronized (Driver.class){
            if (driverPool.get() == null) {
                // configuration propertiesden browser parametresini alıyoruz
                String browser = ConfigReader.getProperty("browser");
                if (browser.equalsIgnoreCase("crossbrowser")) {
                    // configuration propertiesden browser parametresi crossbrowser geldiginde Reporter.getCurrentTestResult() ile
                    // xml dosyasindaki browser parametresini alıyoruz
                    browser = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browser");
                }
                String runMode = ConfigReader.getProperty("runMode"); // runMode: docker veya local
                if ("docker".equalsIgnoreCase(runMode)) {
                    driverPool.set(getRemoteWebDriver(browser)); // Docker'da çalıştırılacaksa uzaktan WebDriver alınıyor
                } else {
                    driverPool.set(getLocalWebDriver(browser));  // Local'de çalıştırılacaksa local WebDriver alınıyor
                }
                driverPool.get().manage().window().maximize();
                driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            }
        }
        return driverPool.get();
    }

    // Docker üzerinde çalıştırmak için Remote WebDriver tanımlayıcı
    private static WebDriver getRemoteWebDriver(String browser) {
        try{
            switch (browser.toLowerCase()) {
                case "chrome":
                    return new RemoteWebDriver(new URL(HUB_URL), getChromeOptions());
                case "firefox":
                    return new RemoteWebDriver(new URL(HUB_URL), getFirefoxOptions());
                case "edge":
                    return new RemoteWebDriver(new URL(HUB_URL), getEdgeOptions());
                default:
                    return new RemoteWebDriver(new URL(HUB_URL), getChromeOptions()); // Varsayılan Chrome
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    // Local makinede çalıştırmak için WebDriver tanımlayıcı
    private static WebDriver getLocalWebDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return new ChromeDriver(getChromeOptions());
            case "firefox":
                return new FirefoxDriver(getFirefoxOptions());
            case "edge":
                return new EdgeDriver(getEdgeOptions());
            case "safari":
                return new SafariDriver(); // Safari için headless modu yok
            default:
                return new ChromeDriver(getChromeOptions()); // Varsayılan Chrome
        }
    }

    // ChromeOptions ile headless desteği
    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.getProperty("browser").contains("headless")) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }
        return options;
    }

    // FirefoxOptions ile headless desteği
    private static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigReader.getProperty("browser").contains("headless")) {
            options.addArguments("--headless");
        }
        return options;
    }

    // EdgeOptions ile headless desteği
    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (ConfigReader.getProperty("browser").contains("headless")) {
            options.addArguments("--headless");
        }
        return options;
    }

    // WebDriver'ı kapatmak için kullanılan metot
    public static void closeDriver() {
        if (driverPool.get() != null) {
            driverPool.get().quit();
            driverPool.remove(); // ThreadLocal'i temizleyin
        }
    }
}
