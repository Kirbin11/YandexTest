import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Класс главной странички магазина. Используется для ввода искомого товара, поиска, сортировки, получения списка найденных
 * товаров, получения названия, цены товара, открытие странички товара.
 *
 */
public class MainPage {
    /** Драйвер движка */
    private WebDriver driver;
    /** Локатор карточек найденных товаров, из которых создаются обьекты Item */
    private By itemLocator = By.xpath("//article[@data-autotest-id = 'product-snippet']");
    /** Локатор Наименования товара внутри карточки, путь начинается с конца локатора карточек */
    private By itemNameLocator = By.xpath(".//h3//span");
    /** Локатор Цены товара внутри карточки, путь начинается с конца локатора карточек */
    private By priceLocator = By.xpath("//div[@data-zone-name='price']//span/span[1]");

    /** Локатор кнопки поиска товаров */
    private By searchButton = By.xpath("//button[@data-r='search-button']");
    /** Локатор поля для ввода поискового запроса товаров */
    private By searchField = By.xpath("//input[@id='header-search']");

    /** Локатор кнопки сортировки найденных результатов по Цене */
    public By sortButton = By.xpath("//button[@data-autotest-id='dprice']");



    /** Конструктор класса*/
    public MainPage(WebDriver driver) {
        this.driver = driver;
    }
    /** Геттер заголовка страницы, используется, чтобы проверить, что открылась нужная страница */
    public String getTitle(){
        return driver.getTitle();

    }

    /** Геттер поля для запросов поиска
     *
     * @return Возвращает элемент поля или null, если не найдено, используя обработку исключения
     */
    public WebElement getSearchField(){
        try {
            return driver.findElement(searchField);
        }
        catch( NoSuchElementException e ){
            return null;
        }
    }
    /** Геттер кнопки поиска
     *
     * @return Возвращает элемент кнопки или null, если не найдено, используя обработку исключения
     */
    public WebElement getSearchButton(){
        try {
            return driver.findElement(searchButton);
        }
            catch( NoSuchElementException e ){
            return null;
        }
    }

    /** Метод для вписывания запроса в поля и нажатия кнопки поиска
     *
     * @param productName поисковой запрос
     * @return возвращает текущий Page Object
     */
    public MainPage search(String productName){
        getSearchField().sendKeys(productName);
        getSearchButton().click();
        return this;
    }

    /** Метод для поиска на странице списка найденных товаров, находит по локатору все карточки найденных товаров на странице
     * по циклу для каждого получает Наименование, Цену, Короткое название и Ссылку на страницу, создает объект Item
     *
     * @param afterSort Так как после сортировки обновляется список карточек найденных товаров, то необходимо дождаться
     *                  обновления этого списка и лишь затем искать найденные карточки. Иначе получим исключение, что элемента больше нет
     *                  на странице. Передается true перед вызовом была вызвана сортировка.
     * @return Возвращает List из Item'ов
     */
    public List<Item> getProducts(boolean afterSort){

        if(afterSort) {
            try {
                (new WebDriverWait(driver, Duration.ofSeconds(15))).until(ExpectedConditions.stalenessOf(driver.findElement(itemLocator)));
            }
            catch (TimeoutException e){
                return Collections.<Item>emptyList();
            }
        }
        List<WebElement> products = new ArrayList<WebElement>();
        try {
             products = (new WebDriverWait(driver, Duration.ofSeconds(15)))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(itemLocator));
        }
        catch (TimeoutException e){
            return Collections.<Item>emptyList();
        }


        List<Item> items = new ArrayList<Item>();
        for(WebElement element : products){

            String prodName = parseName(element);
            int price = parsePrice(element);
            WebElement openProduct = element.findElement(itemNameLocator);
            String shortName = getShortName(element);
            items.add(new Item(prodName, price, openProduct, shortName));
        }
        return items;
    }

    /** Геттер Короткого имени
     * @see Item#getShortName()  */
    private String getShortName(WebElement element) {
        String name  = element.findElement(By.xpath(".//h3//a[@title]")).getAttribute("title");
        return name;
    }

    /** Метод для нажатия на кнопку сортировки результатов поиска по Цене */
    public MainPage sort(){
        driver.findElement(sortButton).click();
        return this;
    }
    /** Метод для нажатия на название(ссылку) товара для открытия его странички
     * @return Возвращает новый объект ItemPage*/
    public ItemPage openItem(Item item){
        item.getOpenProdPage().click();
        return new ItemPage(driver);
    }

    /** Так как наименование товара в карточке разбито на несколько тегов, метод собирает в список все элементы, содержащие
     * части наименования, и складывает строку
     *
     * @param element одна карточка товара
     * @return возвращает строчку полного наименования
     */
    private String parseName(WebElement element){
        List<WebElement> partsOfName = element.findElements(itemNameLocator);
        String name = "";
        for(WebElement part : partsOfName){
            name += part.getText() + " ";
        }
        String cleanName = name.trim().replaceAll(" +", " ");
        return cleanName;
    }

    /** Цена отображается текстом с разделением тысяч пробелом. Метод получает текст цены, очищает пробелы,
     * парсит в int
     * @param element одна карточка товара
     * @return возвращает int цены
     */
    private int parsePrice(WebElement element){
        WebElement priceElement = element.findElement(priceLocator);
        String priceText = priceElement.getText();
        priceText = priceText.replaceAll("\\s","");
        int price = Integer.parseInt(priceText);
        return price;
    }

}
