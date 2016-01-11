package nl.xillio.xill.plugins.web.services.web;

import com.google.inject.Singleton;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The implementation of the {@link WebService} interface.
 */
@Singleton
public class WebServiceImpl implements WebService {

    @Override
    public void click(final WebVariable node) throws StaleElementReferenceException {
        WebElement webElement = node.getElement();
        webElement.click();
    }

    @Override
    public void moveToElement(final WebVariable var) {
        WebDriver page = var.getDriver();
        WebElement element = var.getElement();
        new Actions(page).moveToElement(element).perform();
    }

    @Override
    public String getTagName(final WebVariable var) {
        WebElement element = var.getElement();
        return element.getTagName();
    }

    @Override
    public String getAttribute(final WebVariable var, final String name) {
        WebElement element = var.getElement();
        return element.getAttribute(name);
    }

    @Override
    public String getText(final WebVariable var) {
        String text;
        if (var instanceof PageVariable) {
            try {
                WebElement element = var.getDriver().findElement(By.xpath("//body"));
                text = element.getText();
            } catch (NoSuchElementException e) {
                throw new RobotRuntimeException("Cannot find <body> tag!");
            }
        } else {
            text = var.getElement().getText();
        }
        return text;
    }

    @Override
    public String getSource(final PageVariable page) {
        return page.getDriver().getPageSource();
    }

    @Override
    public List<WebVariable> findElementsWithCssPath(final WebVariable var, final String cssPath) throws InvalidSelectorException {
        SearchContext node;
        if (var instanceof PageVariable) {
            node = var.getDriver();
        } else {
            node = var.getElement();
        }
        List<WebElement> searchResults = node.findElements(By.cssSelector(cssPath));
        return searchResults.stream()
                .map(element -> new NodeVariable(null, element))
                .collect(Collectors.toList());
    }

    @Override
    public List<WebVariable> findElementsWithXpath(final WebVariable var, final String xpath) throws InvalidSelectorException {
        SearchContext node;
        if (var instanceof PageVariable) {
            node = var.getDriver();
        } else {
            node = var.getElement();
        }
        List<WebElement> searchResults = node.findElements(By.xpath(xpath));
        return searchResults.stream()
                .map(element -> new NodeVariable(null, element))
                .collect(Collectors.toList());
    }

    @Override
    public String getCurrentUrl(final WebVariable var) {
        WebDriver driver = var.getDriver();
        return driver.getCurrentUrl();
    }

    @Override
    public void clear(final WebVariable var) {
        WebElement element = var.getElement();
        element.clear();
    }

    @Override
    public void sendKeys(final WebVariable var, final String key) throws Exception {
        WebElement element = var.getElement();
        element.sendKeys(key);
    }

    @Override
    public String getTitle(final WebVariable var) {
        WebDriver driver = var.getDriver();
        return driver.getTitle();
    }

    @Override
    public Set<Cookie> getCookies(final WebVariable var) {
        WebDriver driver = var.getDriver();
        return driver.manage().getCookies();
    }

    @Override
    public String getName(final Cookie cookie) {
        return cookie.getName();
    }

    @Override
    public String getDomain(final Cookie cookie) {
        return cookie.getDomain();
    }

    @Override
    public String getPath(final Cookie cookie) {
        return cookie.getPath();
    }

    @Override
    public String getValue(final Cookie cookie) {
        return cookie.getValue();
    }

    @Override
    public void deleteCookieNamed(final WebVariable var, final String name) {
        WebDriver driver = var.getDriver();
        driver.manage().deleteCookieNamed(name);
    }

    @Override
    public void deleteCookies(final WebVariable var) throws Exception {
        WebDriver driver = var.getDriver();
        driver.manage().deleteAllCookies();
    }

    @Override
    public File getScreenshotAsFile(final WebVariable var) {
        WebDriver driver = var.getDriver();
        PhantomJSDriver castedDriver = getJSDriver(driver);
        return castedDriver.getScreenshotAs(OutputType.FILE);
    }

    PhantomJSDriver getJSDriver(WebDriver driver) {
        return (PhantomJSDriver) driver;
    }

    @Override
    public boolean isSelected(final WebVariable var) {
        WebElement element = var.getElement();
        return element.isSelected();
    }

    @Override
    public void switchToFrame(final WebVariable page, final WebVariable elem) {
        WebElement element = elem.getElement();
        WebDriver driver = page.getDriver();
        driver.switchTo().frame(element);
    }

    @Override
    public void switchToFrame(final WebVariable var, final String element) {
        WebDriver driver = var.getDriver();
        driver.switchTo().frame(element);
    }

    @Override
    public void switchToFrame(final WebVariable var, final int element) {
        WebDriver driver = var.getDriver();
        driver.switchTo().frame(element);
    }

    @Override
    public void addCookie(final WebVariable var, final CookieVariable cookieVar) {
        WebDriver driver = var.getDriver();
        Cookie cookie = new Cookie(cookieVar.getName(), cookieVar.getValue(), cookieVar.getDomain(), cookieVar.getPath(), cookieVar.getExpireDate(), false);
        driver.manage().addCookie(cookie);
    }

    @Override
    public NodeVariable createNodeVariable(final WebVariable page, final WebVariable element) {
        WebDriver newDriver = page.getDriver();
        WebElement newElement = element.getElement();
        return new NodeVariable(newDriver, newElement);
    }

    @Override
    public void httpGet(final WebVariable var, final String url) throws ClassCastException, MalformedURLException {
        PhantomJSDriver driver = getJSDriver(var.getDriver());
        if (getRef(url) != null) {
            driver.get("about:blank");
        }
        driver.get(url);
    }

    /**
     * Creates an URL and gets the anchor (also known as the "reference") of this URL.
     *
     * @param url The url.
     * @return the reference of this url.
     * @throws MalformedURLException
     */
    String getRef(final String url) throws MalformedURLException {
        URL newURL = new URL(url);
        if (!checkSupportedURL(newURL)) {
            throw new MalformedURLException();
        }
        return newURL.getRef();
    }

    boolean checkSupportedURL(URL url) {
        return "http".equalsIgnoreCase(url.getProtocol())
                || "https".equalsIgnoreCase(url.getProtocol())
                || "file".equalsIgnoreCase(url.getProtocol());
    }

    @Override
    public PageVariable createPage(final Options options) {
        if (options == null) {
            throw new NullPointerException("Options cannot be null.");
        }
        WebDriver driver = options.createDriver();
        return new PageVariable(driver, null);
    }

    @Override
    public void setDriverOptions(final WebVariable var, final int timeOut) {
        WebDriver driver = var.getDriver();
        // setting up bigger size of viewport (default is 400x300)
        driver.manage().window().setSize(new Dimension(1920, 1080));

        // page load timeout
        if (timeOut != 0) {
            driver.manage().timeouts().pageLoadTimeout(timeOut, TimeUnit.MILLISECONDS);
        } else {
            // set infinite timeout
            driver.manage().timeouts().pageLoadTimeout(-1, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void quit(final WebVariable var) {
        WebDriver driver = var.getDriver();
        driver.quit();
    }

    @Override
    public WebVariable getPageFromPool(final PhantomJSPool pool, final Options options) {
        return pool.get(pool.createIdentifier(options), this).getPage();
    }

    CookieStore createCookieStore(Set<Cookie> seleniumCookieSet) {
        CookieStore cookieStore = new BasicCookieStore();

        for (Cookie seleniumCookie : seleniumCookieSet) {
            BasicClientCookie cookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
            cookie.setDomain(seleniumCookie.getDomain());
            cookie.setPath(seleniumCookie.getPath());
            cookie.setExpiryDate(seleniumCookie.getExpiry());
            cookie.setSecure(seleniumCookie.isSecure());
            cookieStore.addCookie(cookie);
        }
        return cookieStore;
    }

    void copyInputStreamToFile(final InputStream stream, final File targetFile) throws IOException {
        FileUtils.copyInputStreamToFile(stream, targetFile);
    }

    @Override
    public void download(final String url, final File targetFile, final WebVariable webContext, int timeout) throws IOException {

        // Check URL
        URL newURL = new URL(url);
        if (!checkSupportedURL(newURL)) {
            throw new MalformedURLException();
        }

        // Set timeout
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();

        HttpClientBuilder builder;

        if (webContext != null) {
            // Takeover cookies from web context
            CookieStore cookieStore = createCookieStore(webContext.getDriver().manage().getCookies());
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);

            // Create httpclient
            builder = HttpClients.custom().setDefaultRequestConfig(requestConfig).setDefaultCookieStore(cookieStore).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        } else {
            // Create httpclient
            builder = HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
        }

        HttpGet httpget = new HttpGet(url);

        try (CloseableHttpResponse response = builder.build().execute(httpget)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                try (InputStream stream = entity.getContent()) {
                    copyInputStreamToFile(stream, targetFile);
                }
            } else {
                throw new RobotRuntimeException("Cannot do a http request!");
            }
        }
    }
}
