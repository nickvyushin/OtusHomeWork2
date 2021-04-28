import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class configBrowserTests {

    private Logger logger = LogManager.getLogger(configBrowserTests.class);
    protected static WebDriver driver;
    ServerConfig cfg = ConfigFactory.create(ServerConfig.class);
    
    @Before
    public void startTest() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        logger.info("Browser opened");
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
    }
    
    @After
    public void endTest() {
        if (driver != null)
            driver.quit();
        logger.info("Browser closed");
    }

    @Test
    public void testCheckAddress() {
        //Открываем сайт https://otus.ru;
        openPage(cfg.otus());
        //Переходим на вкладку "Контакты";
        driver.findElement(By.xpath("//*[@class='header2_subheader-container__right']//*[@title='Контакты']")).click();
        logger.info("Section 'Contacts' opened");
        //Проверяем адрес: 125167, г. Москва, Нарышкинская аллея., д. 5, стр. 2, тел. +7 499 938-92-02;
        var address = driver.findElement(By.xpath("//*[@id='__next']//*[text()='Адрес']/following-sibling::*[1]")).getText();
        logger.info("Address: {" + address + "}");
        Assert.assertEquals("125167, г. Москва, Нарышкинская аллея., д. 5, стр. 2, тел. +7 499 938-92-02", address);
        //Разворачиваем окно браузера на полный экран(не киоск);
        driver.manage().window().maximize();
        logger.info("Window size maximized: " + driver.manage().window().getSize());
        //Проверяем title страницы.
        var title = driver.getTitle();
        logger.info("Title: {" + title + "}");
        Assert.assertEquals("Контакты | OTUS", title);
    }

    @Test
    public void numberSearchTest() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        //Переходим на сайт теле2 страница https://msk.tele2.ru/shop/number;
        openPage(cfg.tele2());
        //Ввести в поле "поиск номера" 97 и начать поиск;
        var fieldXp = "//input[@id='searchNumber']";
        driver.findElement(By.xpath(fieldXp)).sendKeys("97");
        logger.info("Search started");
        //Дождаться появления номеров.
        var numXp = "//*[@class='number-box']";
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(numXp))).isDisplayed());
        logger.info("Element is visible");
    }

    @Test
    public void checkCourseInfoTest() {
        //Переходим на сайт https://otus.ru;
        openPage(cfg.otus());
        //Переходим на F.A.Q;
        var faqXp = "//*[@href='/journal/']/preceding::*[@href='/faq/']";
        driver.findElement(By.xpath(faqXp)).click();
        logger.info("Section 'FAQ' opened");
        //Наажимаем на вопрос: "Где посмотреть программу интересующего курса?";
        var questionXp = "//*[text()='Где посмотреть программу интересующего курса?']";
        driver.findElement(By.xpath(questionXp)).click();
        logger.info("Question opened");
        //Проверяем, что текст соответствует следующему:
        // "Программу курса в сжатом виде можно увидеть на странице курса после блока с преподавателями.
        // Подробную программу курса можно скачать кликнув на “Скачать подробную программу курса”.
        var answerXp = questionXp + "/following-sibling::*";
        Assert
                .assertEquals("Программу курса в сжатом виде можно увидеть на странице курса после блока с преподавателями. " +
                                "Подробную программу курса можно скачать кликнув на “Скачать подробную программу курса”",
                        driver.findElement(By.xpath(answerXp)).getText());
    }

    @Test
    public void checkSubscriptionTest() {
        //Переходим на сайт https://otus.ru;
        openPage(cfg.otus());
        //Заполняем тестовый почтовый ящик в поле "Подпишитесь на наши новости";
        var emailFieldXp = "//footer//form//input[@name='email']";
        driver.findElement(By.xpath(emailFieldXp)).sendKeys(cfg.email());
        logger.info("The field 'email' is filled");
        //Нажимаем кнопку "Подписаться";
        var submitButtonXp = "//footer//button";
        driver.findElement(By.xpath(submitButtonXp)).click();
        logger.info("Field submitted");
        //Проверяем, что появилось сообщение: "Вы успешно подписались".
        var successXp = "//footer//*[text()='Вы успешно подписались']";
        Assert.assertTrue(driver.findElement(By.xpath(successXp)).isDisplayed());
    }

    private void openPage(String address) {
        driver.get(address);
        logger.info("Page " + address + " opened");
    }
}
