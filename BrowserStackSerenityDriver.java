import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserStackSerenityDriver implements DriverSource {
    private static final Logger LOGGER = Logger.getLogger(BrowserStackSerenityDriver.class.getName() );

    public WebDriver newDriver() {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();

        String username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = environmentVariables.getProperty("browserstack.user");
        }

        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = environmentVariables.getProperty("browserstack.key");
        }
        String build = System.getenv("BROWSERSTACK_BUILD");
        if (build==null){
            build= environmentVariables.getProperty("browserstack.build");
        }
        String environment = System.getProperty("environment");
        DesiredCapabilities capabilities = new DesiredCapabilities();

        if(!environment.contains("_") && !environment.equals("single")){
            Integer maxForks = Integer.valueOf(System.getProperty("serenity.fork.count"));
            Integer worker = Integer.valueOf(System.getProperty("org.gradle.test.worker"));
            int index = (worker % maxForks) + 1;
            environment = environment + "_"+index;
        }
        Iterator<String> it = environmentVariables.getKeys().iterator();
        while (it.hasNext()) {
            String key = it.next();

            if (key.equals("browserstack.user") || key.equals("browserstack.key")
                    || key.equals("browserstack.server")) {
                LOGGER.log(Level.INFO,  "Capabilitie omitida");
                continue;
            } else if (key.startsWith("bstack_")) {
                capabilities.setCapability(key.replace("bstack_", ""), environmentVariables.getProperty(key));
                if (key.equals("bstack_browserstack.local")
                        && environmentVariables.getProperty(key).equalsIgnoreCase("true")) {
                    System.setProperty("browserstack.local", "true");
                }
            } else if (key.startsWith("environment." + environment)) {

                if(!key.startsWith("environment." + environment)){
                    LOGGER.log(Level.INFO,  "Capabilitie omitida");
                    continue;
                }
                capabilities.setCapability(key.replace("environment." + environment +".", ""), environmentVariables.getProperty(key));

                if (key.equals("environment." + environment + ".browserstack.local")
                        && environmentVariables.getProperty(key).equalsIgnoreCase("true")) {
                    System.setProperty("browserstack.local", "true");
                }
            }
            capabilities.setCapability("build", build);
        }

        try {
            return new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + "@"
                    + environmentVariables.getProperty("browserstack.server") + "/wd/hub"), capabilities);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING,  e.getMessage(), e);
            return null;
        }
    }

    public boolean takesScreenshots() {
        return true;
    }
}
