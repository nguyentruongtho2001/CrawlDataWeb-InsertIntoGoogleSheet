# CrawlDataWeb-InsertIntoGoogleSheet
Abstract: This project automates URL validation using Selenium. It checks the page content of each URL; if an error is found, the result is marked as "Failed," otherwise as "Pass." Results are then uploaded to Google Sheets via the Google Sheets API with Google OAuth2 authentication.

Preparation: Download the latest version of ChromeDriver: [ChromeDriver download link](https://googlechromelabs.github.io/chrome-for-testing/)

Step 1: Go to Google Console, create a new Project
- Access the Google Cloud Console.
- Select Create Project to create a new project.
- Name the project and click Create. <br>
Step 2: Enter the newly created project
- Enable APIs & Services.
- Create an OAuth 2.0 Client ID and download the file, which will be in .json format.  <br>
Step 3: Add the OAuth 2.0 Client ID file
- Add the file to the resources folder in your Java project and rename it to credentialOAuth2.  <br>
Step 4: Create a Google Sheet
- Rename the sheet to Sheet1.
- Copy the Sheet-ID and paste it into the code at: final String spreadsheetId = "<GoogleSheets-id>";  <br>
Step 5: Extract the downloaded ChromeDriver file and copy the path into the code: System.setProperty("webdriver.chrome.driver", "[path]/chromedriver.exe");  <br>

Done!
