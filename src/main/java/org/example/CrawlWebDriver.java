import org.openqa.selenium.WebDriver; // Import WebDriver từ Selenium
import org.openqa.selenium.chrome.ChromeDriver; // Import ChromeDriver từ Selenium
import com.google.api.client.auth.oauth2.Credential; // Import Credential từ Google API
import com.google.api.services.sheets.v4.model.AppendValuesResponse; // Import AppendValuesResponse từ Google API
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp; // Import AuthorizationCodeInstalledApp từ Google API
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver; // Import LocalServerReceiver từ Google API
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow; // Import GoogleAuthorizationCodeFlow từ Google API
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets; // Import GoogleClientSecrets từ Google API
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport; // Import GoogleNetHttpTransport từ Google API
import com.google.api.client.http.javanet.NetHttpTransport; // Import NetHttpTransport từ Google API
import com.google.api.client.json.JsonFactory; // Import JsonFactory từ Google API
import com.google.api.client.json.gson.GsonFactory; // Import GsonFactory từ Google API
import com.google.api.client.util.store.FileDataStoreFactory; // Import FileDataStoreFactory từ Google API
import com.google.api.services.sheets.v4.Sheets; // Import Sheets từ Google API
import com.google.api.services.sheets.v4.SheetsScopes; // Import SheetsScopes từ Google API
import com.google.api.services.sheets.v4.model.ValueRange; // Import ValueRange từ Google API
import java.io.FileNotFoundException; // Import FileNotFoundException
import java.io.IOException; // Import IOException
import java.io.InputStream; // Import InputStream
import java.io.InputStreamReader; // Import InputStreamReader
import java.security.GeneralSecurityException; // Import GeneralSecurityException
import java.util.Arrays; // Import Arrays
import java.util.Collections; // Import Collections
import java.util.List; // Import List

public class CrawlWebDriver {

    public static final String[] URLS = {
            "https://hocmai.vn/",
            "https://hocmai.vn/giao-vien-noi-tieng.html",
            "https://hocmai.vn/gioi-thieu/",
            "https://hocmai.vn/khoa-hoc-truc-tuyen/130/ngu-van-12.html",
            "https://hocmai.vn/khoa-hoc-truc-tuyen/1330/thanh-thao-cac-bien-phap-tu-tu-co-do-thi-thanh-thu.html"
    };

    private static final String APPLICATION_NAME = "ConnectGoogleSheetAPI";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentialOAuth2.json";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = CrawlWebDriver.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        System.setProperty("webdriver.chrome.driver", "[path]/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId = "<GoogleSheets-id>";
        final String range = "Sheet1!A2:B";
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        try {
            for(String url : URLS) {
                driver.get(url);
                String pageContent = driver.getPageSource();
                String result = pageContent.contains("error") ? "failed" : "pass";
                System.out.println("Domain: " + url);
                System.out.println("Result: " + result);

                // Thêm dữ liệu vào Google Sheets trong vòng lặp
                ValueRange appendBody = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(url, result) // Sử dụng url và result ở đây
                        ));
                AppendValuesResponse appendResult = service.spreadsheets().values()
                        .append(spreadsheetId, "Sheet1", appendBody)
                        .setValueInputOption("USER_ENTERED")
                        .setInsertDataOption("INSERT_ROWS")
                        .setIncludeValuesInResponse(true)
                        .execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit(); // Đóng trình duyệt khi kết thúc
        }
    }
}
