import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import persistence.HibernateDao;
import ui.UserInterface;

import java.io.*;

public class UserInterfaceTest {

    @Mock
    private HibernateDao<User> userDao;

    private MockitoSession mockitoSession;

    private PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private InputStream standartIn = System.in;

    @BeforeEach
    void setUp(){
        mockitoSession = Mockito.mockitoSession()
                        .initMocks(this)
                        .startMocking();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown(){
        mockitoSession.finishMocking();
        System.setOut(standardOut);
        System.setIn(standartIn);
    }
}
