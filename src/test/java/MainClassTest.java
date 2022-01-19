import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;


import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * Основной класс тестов. Всего проходит 6 тестов. Открывается страница магазина, ищется товар, сортируются результаты
 * открывается страница первого товара, выводится в консоль магазин и цена.
 */
public class MainClassTest {
    /** Объект, содержащий входные данные */
    TestData data = new TestData("G:/WORK/autotest/src/test/resources/data.xml");
    /** Драйвер движка */
    WebDriver driver;
    /** Объект главной страницы */
    MainPage mainPage;
    /** Объект странички товара */
    ItemPage itemPage;
    /** Адрес магазина*/
    String url = data.getAddress();
    /** Поисковой запрос */
    String productName = data.getQuery();
    /** Заголовок страницы магазина, используется для проверки того, что открылась корректная страница */
    String marketTitle = data.getMarketTitle();

    /** Установка движка драйвера, установка неявных ожиданий */
    @BeforeSuite
    public void setUp() {
        System.setProperty(data.getDriverName(), data.getDriverLocation());
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        System.out.println("PRIVET");
    }
    /** Разворачивание окна на весь экран */
    @BeforeTest
    public void profileSetup() {
        driver.manage().window().maximize();
    }
    /** Открытие главной страницы магазина */
    @BeforeClass
    public void appSetup() {
        driver.get(url);
        mainPage = new MainPage(driver);
    }
    /** Первый тест. Проверяется что открылась корректная страница по заголовку. Проверяется наличие поля и кнопки поиска товара */
    @Test
    public void TestMainPageOpened(){
        String title = mainPage.getTitle();
        Assert.assertEquals(title, marketTitle);

        WebElement searchField = mainPage.getSearchField();
        WebElement searchButton = mainPage.getSearchButton();
        Assert.assertNotNull(searchField);
        Assert.assertNotNull(searchButton);
        try {
            TestData.takeSnapShot(driver, "/screenshots/MainPageOpened.png");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /** Второй тест. Зависит от выполнения первого. Выполняет поиск товаров, проверяет что найден хотя бы один товар */
    @Test(dependsOnMethods = "TestMainPageOpened")
    public void TestSearchCompleted()  {

        mainPage.search(productName);

        List<Item> results = mainPage.getProducts(false);

        Assert.assertTrue(results.size() > 0);
        try {
            TestData.takeSnapShot(driver, "/screenshots/SearchCompleted.png");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /** Проверяется что среди результатов есть товар, в названии которого присутствует искомый тектс */
    @Test(dependsOnMethods = "TestSearchCompleted")
    public void TestResultsHaveProduct()  {

        List<Item> results = mainPage.getProducts(false);
        boolean haveProduct = false;
        for(Item item : results){
            if(item.getProductName().toLowerCase(Locale.ROOT).contains(productName.toLowerCase(Locale.ROOT))){
                haveProduct = true;
            }
        }
        Assert.assertTrue(haveProduct);

    }
    /** Сортируются результаты поиска по цене. Проверяется что сортировка осуществилась - что цены товаров идут по возрастанию */
    @Test(dependsOnMethods = "TestResultsHaveProduct")
    public void TestSortingCompleted()  {
        mainPage.sort();
        MainPage mainPageAfterSort = new MainPage(driver);
        List<Item> results = mainPageAfterSort.getProducts(true);
        boolean sorted = true;
        for(int i=0; i < results.size() - 1; i++){
            if(results.get(i).getPrice() > results.get(i+1).getPrice()){
                sorted = false;
            }
        }

        Assert.assertTrue(sorted);
        try {
            TestData.takeSnapShot(driver, "/screenshots/SortingCompleted.png");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /** Открывает страничку товара, находящегося первым в списке отсортированных. Проверяет что открылась страница этого товара
     * сравнивая заголовок страницы и короткое название в карточке*/
    @Test(dependsOnMethods = "TestSortingCompleted")
    public void TestItemPageOpened()  {
        MainPage mainPageAfterSort = new MainPage(driver);
        List<Item> resultsAfterSort = mainPageAfterSort.getProducts(false);
        ItemPage itemPage = mainPageAfterSort.openItem(resultsAfterSort.get(0));
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        String itemTitle = resultsAfterSort.get(0).getShortName();
        String title = itemPage.getTitle();
        Assert.assertTrue(title.toLowerCase(Locale.ROOT).contains(itemTitle.toLowerCase(Locale.ROOT)));
        try {
            TestData.takeSnapShot(driver, "/screenshots/ItemPageOpened.png");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /** Получает название магазина и цену товара на открытой страничке. Проверяет что они существуют. Пишет в консоли
     */
    @Test(dependsOnMethods = "TestItemPageOpened")
    public void TestItemHaveData(){
        itemPage = new ItemPage(driver);
        String title = itemPage.getTitle();
        WebElement shopName = itemPage.getShopNameElement();
        WebElement price = itemPage.getPriceElement();

        Assert.assertNotNull(shopName);
        Assert.assertNotNull(price);

        System.out.println("КУПИТЕ В МАГАЗИНЕ " + itemPage.getShopName() + " ПО СМЕШНЫМ " + itemPage.getPrice() + " Рублей");
    }
    /** Закрывает активную вкладку */
    @AfterClass
    public void closeUp() {
        driver.close();
        System.out.println("The close_up process is completed");
    }
    /** Закрывает браузер */
    @AfterTest
    public void exit(){
        driver.quit();
    }

}
