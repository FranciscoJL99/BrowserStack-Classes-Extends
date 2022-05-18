import com.browserstack.local.Local;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserStackSerenityTest {
    static Local browserStackLocal;
    private static final Logger LOGGER = Logger.getLogger(BrowserStackSerenityTest.class.getName() );

    @BeforeClass
    public static void setUp() throws Exception {

        EnvironmentVariables variablesDeEntorno = SystemEnvironmentVariables.createEnvironmentVariables();

        String accessKey = System.getenv().get("BROWSERSTACK_ACCESS_KEY");

        if (accessKey == null) {
            accessKey = variablesDeEntorno.getProperty("browserstack.key");
        }

        String environment = System.getProperty("environment");

        String key = "bstack_browserstack.local";

        boolean is_local = variablesDeEntorno.getProperty(key) != null
                && variablesDeEntorno.getProperty(key).equals("true");

        if (environment != null && !is_local) {
            key = "environment." + environment + ".browserstack.local";
            is_local = variablesDeEntorno.getProperty(key) != null
                    && variablesDeEntorno.getProperty(key).equals("true");
        }

        if (is_local) {
            LOGGER.log(Level.WARNING,  "Ejecucion local");
            browserStackLocal = new Local();
            Map<String, String> bsLocalArgs = new HashMap<String, String>();
            bsLocalArgs.put("key", accessKey);
            browserStackLocal.start(bsLocalArgs);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (browserStackLocal != null) {
            browserStackLocal.stop();
            LOGGER.log(Level.WARNING,  "Servidor local apagado");
        }
    }
}
