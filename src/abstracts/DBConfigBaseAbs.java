package abstracts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public abstract class DBConfigBaseAbs {
    protected String url;
    protected String username;
    protected String password;

    protected void loadConfig(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            url = reader.readLine();
            username = reader.readLine();
            password = reader.readLine();
        }
    }
}
