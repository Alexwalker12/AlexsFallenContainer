import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Example cammond - line app that uses the web-based 0Auth flow
 *(using {link DbxWebAuth}).
 */

public class CommandLineMain {

    public static void main(String[] args) throws IOException {
        //Display Important Log Messages
        Logger.getLogger("").setLevel(Level.WARNING);

        if (args.length != 2) {
            System.out.println("Usage: COMMAND <app-info-file> auth-file-output");
            System.out.println("");
            System.out.println("<app-info-file>: a JSON file with information about your API app.  Example:");
            System.out.println("");
            System.out.println("  {");
            System.out.println("    \"key\": \"Your Dropbox API app key...\",");
            System.out.println("    \"secret\": \"Your Dropbox API app secret...\"");
            System.out.println("  }");
            System.out.println("");
            System.out.println("  Get an API app key by registering with Dropbox:");
            System.out.println("    https://dropbox.com/developers/apps");
            System.out.println("");
            System.out.println("<auth-file-output>: If authorization is successful, the resulting API");
            System.out.println("  access token will be saved to this file, which can then be used with");
            System.out.println("  other example programs, such as the one in \"examples/account-info\".");
            System.out.println("");
            System.exit(1);
            return;
        }

        String argAppInforFile = args[0];
        String argAuthFileOutput = args[1];

        //Read app info file (contains app key and app secret)
        DbxAppInfo appInfo;
        try {
            appInfo = DbxAppInfo.Reader.readFromFile(argAppInforFile);
        } catch (JsonReader.FileLoadException ex) {
            System.err.println("Error reading <app-info-file>: " + ex.getMessage());
            System.exit(1); return;
        }

        //run through Dropbox API authorization process
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);
        DbxWebAuth.Request webAuthRequest = DbxWebAuth.newRequestBuilder()
                .withNoRedirect()
                .build();
        String authorizeUrl = webAuth.authorize(webAuthRequest);
        System.out.println("1. Go to " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first).");
        System.out.println("3. Copy the authorization code");
        System.out.println("Enter the authorization code here: ");

        String code = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if(code == null) {
            System.exit(1); return;
        }
        code = code.trim();

        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1); return;
        }

        System.out.println("Authorization Complete.");
        System.out.println("-User ID: " + authFinish.getUserId());
        System.out.println("- Acces Token: " + authFinish.getAccessToken());

        //Save Auth Information to output file.
        DbxAuthInfo authInfo = new DbxAuthInfo(authFinish.getAccessToken(), appInfo.getHost());
        File output = new File(argAuthFileOutput);
        try {
            DbxAuthInfo.Writer.writeToFile(authInfo, output);
            System.out.println("Saved Authorization information to \"" + output.getCanonicalPath() + "\".");
        } catch (IOException ex) {
            System.err.println("Error saving to <auth-file-out>: " + ex.getMessage());
            System.err.println("Dumping to stderr instead:");
            DbxAuthInfo.Writer.writeToStream(authInfo, System.err);
            System.exit(1); return;
        }
    }

}
