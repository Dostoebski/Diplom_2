import io.qameta.allure.Step;
import util.StellarBurgersClient;
import org.junit.BeforeClass;

public class BaseTest {

    static StellarBurgersClient burgersClient;

    @BeforeClass
    public static void init() {
        burgersClient = new StellarBurgersClient();
    }

    @Step("{0}")
    public void step(String message){
    }
}
