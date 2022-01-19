import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

/**
 * Класс странички продукта. Используется для получения Названия магазина и Цены товара.
 */

public class ItemPage {
    /** Драйвер селениума*/
    private WebDriver driver;

    /** Локатор Названия магазина */
    private By shopNameLocator = By.xpath("//div[@data-autotest-shopid]//a//span");
    /** Локатор Цены товара */
    private By priceLocator = By.xpath("//div[@data-auto='main']//span[@data-autotest-value]/span");

    /** Конструктор страницы.
     *  Так как страница всегда открывается в новой вкладке, используется "driver.getWindowHandles();" чтобы перевести
     *  фокус драйвера на новую вкладку.
     * @param driver
     */
    public ItemPage(WebDriver driver) {
        this.driver = driver;

        Set<String> handles = driver.getWindowHandles();
        for (String tab : handles) {
            String currentTab = driver.getWindowHandle();
            if (!tab.equals(currentTab)) {
                driver.switchTo().window(tab);
            }
        }
    }
    /** Функция, ищущая и возвращающая элемент Названия магазина
     * Сделана отдельно вне геттера, т.к. сам элемент также используется в тесте */
    public WebElement getShopNameElement(){
        try { return driver.findElement(shopNameLocator);  }
        catch(NoSuchElementException e ){
            return null;
        }
    }
    /** Функция, ищущая и возвращающая элемент Цены товара
     * Сделана отдельно вне геттера, т.к. сам элемент также используется в тесте */
    public WebElement getPriceElement(){
        try {return driver.findElement(priceLocator); }
        catch(NoSuchElementException e ){
            return null;
        }
    }

    /** Геттер Названия магазина в строчном типе */
    public String getShopName(){
        String name = getShopNameElement().getText();
        return name;

    }

    /** Геттер заголовка страницы, используется для проверки того, что открылась страница именно этого продукта */
    public String getTitle(){
        return driver.getTitle();
    }

    /** Геттер Цены товара численного типа. Внутри происходит получение элемента, извлечение текста Цены, конвертация в тип
     * int
     * @return возвращает Цену типа int
     */
    public int getPrice() {
        WebElement priceElement = getPriceElement();
        String priceText = priceElement.getText();
        priceText = priceText.replaceAll("\\s","");
        int price = Integer.parseInt(priceText);
        return price;
    }
}
