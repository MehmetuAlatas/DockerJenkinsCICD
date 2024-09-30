package techproed.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.Reporter;

import java.time.Duration;

public class Driver {

    // ThreadLocal ile her bir test için bağımsız WebDriver tanımlanıyor
    private static ThreadLocal<WebDriver> driverPool = new ThreadLocal<>();

    public static WebDriver getDriver() {
        synchronized (Driver.class) {
            if (driverPool.get() == null) {
                // ConfigReader'dan browser parametresini alıyoruz
                String browser = ConfigReader.getProperty("browser");
                if (browser.equalsIgnoreCase("crossbrowser")) {
                    // XML'deki browser parametresini alıyoruz
                    browser = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browser");
                }
                // Yerel WebDriver ayarlarını yapılandırıyoruz
                driverPool.set(getLocalWebDriver(browser));

                // Tarayıcı ayarları
                driverPool.get().manage().window().maximize();
                driverPool.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            }
        }
        return driverPool.get();
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
            options.addArguments("--headless");
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
