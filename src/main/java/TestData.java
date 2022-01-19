import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Класс, описывающий обьект, содержащий входные данные. Используются данные: название, расположение драйвера, адрес страницы
 * магазина, поисковой запрос, заголовок страницы.
 * Метод обрабатывает передаваемый xml-файл, в котором занесены входные данные, и заполняет поля объекта.
 */
public class TestData {
    private String dataFile;
    private String address;
    private String query;
    private String marketTitle;
    private String driverName;
    private String driverLocation;

    public TestData(String dataFile) {
        this.dataFile = dataFile;
        readData(dataFile);
    }

    public String getAddress() {
        return address;
    }

    public String getQuery() {
        return query;
    }

    public String getMarketTitle() {
        return marketTitle;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverLocation() {
        return driverLocation;
    }

    private void readData(String fileName){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(fileName));

            driverName = doc.getElementsByTagName("driver-name").item(0).getTextContent();
            driverLocation = doc.getElementsByTagName("driver-location").item(0).getTextContent();
            address = doc.getElementsByTagName("address").item(0).getTextContent();
            query = doc.getElementsByTagName("query").item(0).getTextContent();
            marketTitle = doc.getElementsByTagName("market-title").item(0).getTextContent();


        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }


    public static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception{

        //Convert web driver object to TakeScreenshot

        TakesScreenshot scrShot =((TakesScreenshot)webdriver);

        //Call getScreenshotAs method to create image file

        File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination

        File DestFile=new File(fileWithPath);

        //Copy file at destination

        FileUtils.copyFile(SrcFile, DestFile);

    }
}
