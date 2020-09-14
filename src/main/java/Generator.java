import okhttp3.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;


public class Generator {
    private List<String> nicks;
    private List<String> cpfs;
    private Integer QUANTITY;

    public Generator() {
        nicks = new ArrayList<>();
        cpfs = new ArrayList<>();
        QUANTITY = 50;
    }

    public void getNicks(String URL) {
        System.setProperty("webdriver.chrome.driver", "chromedriver_linux64/chromedriver");
/*      Runs browser window silently in background
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options); */
        WebDriver driver = new ChromeDriver();
        driver.get(URL);

        WebElement comboboxElement = driver.findElement(By.id("method"));
        driver.findElement(By.xpath("//*[@id='method']")).click();
        Select combobox = new Select(comboboxElement);
        combobox.selectByValue("random");

        WebElement quantidade = driver.findElement(By.id("quantity"));
        quantidade.clear();
        quantidade.sendKeys(QUANTITY.toString());

        WebElement limitElement = driver.findElement(By.id("limit"));
        driver.findElement(By.xpath("//*[@id='limit']")).click();
        Select limit = new Select(limitElement);
        limit.selectByValue("8");

        driver.findElement(new By.ById("bt_gerar_nick")).click();

        driver.findElements(new By.ByClassName("generated-nick"))
                .forEach((nick) -> {
                    System.out.println(nick.getText());
                    nicks.add(nick.getText());
                });
        driver.close();
    }

    public void getCPFs() {
        nicks.forEach(nick -> {

            String cpf = requestWs();
            System.out.println(cpf);
            cpfs.add(cpf);

        });
    }

    public String requestWs() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "acao=gerar_cpf&pontuacao=S");
        Request request = new Request.Builder()
                .url("https://www.4devs.com.br/ferramentas_online.php")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeToFile(String filename) {
        FileWriter writer;
        try {
            writer = new FileWriter(filename);
            writer.write("NICK; CPF" + "\n");
            for (int i = 0; i < QUANTITY; i++) {
                try {
                    String line = nicks.get(i) + "; " + cpfs.get(i) + "\n";
                    writer.write(line);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            ;
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        generator.getNicks("https://www.4devs.com.br/gerador_de_nicks");
        generator.getCPFs();
        generator.writeToFile("arquivo nicks");
        System.exit(0);
    }

}







