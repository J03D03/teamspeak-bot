import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {

    private final File logFile;

    public Logger(String fileName) {
        logFile = new File(fileName);
    }

    public void log(String s) {
        try {
            FileWriter fw = new FileWriter(this.logFile, true);
            String date = new Date().toString();
            fw.write(date + " : " + s + " ");
            fw.write(System.lineSeparator());
            fw.close();
        } catch (IOException ex) {
            System.err.println("Couldn't log: " + s);
        }
    }
}