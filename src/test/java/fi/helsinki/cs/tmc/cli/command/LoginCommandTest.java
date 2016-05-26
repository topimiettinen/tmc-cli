package fi.helsinki.cs.tmc.cli.command;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import fi.helsinki.cs.tmc.cli.Application;
import fi.helsinki.cs.tmc.cli.tmcstuff.SettingsIo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class LoginCommandTest {

    private static String serverAddress;
    private static String username;
    private static String password;

    private Application app;
    private OutputStream os;

    public LoginCommandTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        LoginCommandTest.serverAddress = System.getenv("TMC_SERVER_ADDRESS");
        LoginCommandTest.username = System.getenv("TMC_USERNAME");
        LoginCommandTest.password = System.getenv("TMC_PASSWORD");

        assertNotNull(LoginCommandTest.serverAddress);
        assertNotNull(LoginCommandTest.username);
        assertNotNull(LoginCommandTest.password);
    }

    @Before
    public void setUp() {
        // Unwanted behaviour? Will delete the real settings file atm.
        new SettingsIo().delete();

        this.app = new Application();
        this.os = new ByteArrayOutputStream();

        PrintStream ps = new PrintStream(os);
        System.setOut(ps);
    }

    @After
    public void tearDown() {
        // Unwanted behaviour? Will delete the real settings file atm.
        new SettingsIo().delete();
    }

    @Test
    public void logsInWithCorrectServerUserAndPassword() {
        String[] args = createArgs(
                LoginCommandTest.serverAddress,
                LoginCommandTest.username,
                LoginCommandTest.password);

        // Unwanted behaviour? Will create real settings file atm.
        app.run(args);

        String output = os.toString();
        assertTrue(output.contains("succesful"));
    }

    @Test
    public void catches401IfCorrectServerAndWrongUsername() {
        String[] args = createArgs(
                LoginCommandTest.serverAddress,
                "",
                LoginCommandTest.password);

        app.run(args);
        String output = os.toString();
        assertTrue(output.contains("Incorrect username or password."));
    }

    private String[] createArgs(String server, String username, String pwd) {
        return new String[]{
            "login",
            "-s", server,
            "-u", username,
            "-p", pwd};
    }

    private String[] createArgs(String username, String pwd) {
        return new String[]{
            "login",
            "-s", LoginCommandTest.serverAddress,
            "-u", username,
            "-p", pwd};
    }
}
