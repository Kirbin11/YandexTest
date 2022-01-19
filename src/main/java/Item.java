import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;

/**
 * Класс Item для работы с результатами поиска товаров на странице.
 * Используются свойства Наименование, Цена, Короткое название, Ссылка на страницу товара
 */
public class Item {
    /** Переменная для наименования товара */
    private String productName;
    /** Переменная для цены товара */
    private int price;
    /** Заголовок продукта, который является ссылкой на его страничку */
    private WebElement openProdPage;
    /** Используется для проверки того, что открылась страница именно этого продукта */
    private String shortName;

    /** Конструктор
     *
     * @param productName
     * @param price
     * @param openProdPage
     * @param shortName
     */
    public Item(String productName, int price, WebElement openProdPage, String shortName) {
        this.productName = productName;
        this.price = price;
        this.openProdPage = openProdPage;
        this.shortName = shortName;
    }

    /** Геттер Наименования */
    public String getProductName() {
        return productName;
    }

    /** Геттер Цены */
    public int getPrice() {
        return price;
    }
    /** Геттер Короткого имени
     * @see Item#shortName*/
    public String getShortName(){
        return shortName;
    }
    /** Геттер кнопки открытия странички товара */
    public WebElement getOpenProdPage() {
        return openProdPage;
    }
}
