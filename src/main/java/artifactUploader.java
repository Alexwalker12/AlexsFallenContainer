import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class artifactUploader {

    private String accessToken;
    private String clientIdentifier;
    private String userLocal;

    private static Properties prop = new Properties();
    private static InputStream inputProp = null;


    public void main(String[] args) throws DbxException {

        if (args.length != 3) {
            System.out.println("Missing Arguments: input path of file to upload, and path to key.");
            return;
        }

        String filePath = args[0];
        String dropboxFilePath = args[1];
        String credentialsFilePath = args[2];

        getCredentials(credentialsFilePath);

        accessToken = prop.getProperty("accessToken");
        clientIdentifier = prop.getProperty("clientIdentifier");
        userLocal = prop.getProperty("userLocale");

        if (accessToken == null || clientIdentifier == null || userLocal == null) {
            System.out.println("One of the properties is null.");
            return;
        }

        uploadFile(filePath, dropboxFilePath);
    }

    public void uploadFile(String filePath, String dropboxFilePath) {

        DbxRequestConfig config = new DbxRequestConfig(clientIdentifier, userLocal);
        DbxClientV2 client = new DbxClientV2(config, accessToken);

        try (InputStream in = new FileInputStream(filePath)) {
            FileMetadata metadata = client.files().uploadBuilder(dropboxFilePath).uploadAndFinish(in);
            String metaDataPath = metadata.getPathLower();
            try {
                SharedLinkMetadata linkMetaData = client.sharing().createSharedLinkWithSettings(metaDataPath);
                System.out.println(linkMetaData.getUrl());

            } catch (CreateSharedLinkWithSettingsErrorException ex) {
                if (ex.errorValue.isSharedLinkAlreadyExists()) {
                    try {
                        ListSharedLinksResult result = client.sharing().listSharedLinksBuilder()
                                .withPath(metaDataPath).withDirectOnly(true).start();
                        for (SharedLinkMetadata linkMetaData : result.getLinks()) {

                            System.out.println(linkMetaData.getUrl());
                        }
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println("File not found at : " );
            ex.printStackTrace();
        } catch (IOException | DbxException ex1) {
            ex1.printStackTrace();
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public String getUserLocal() {
        return userLocal;
    }

    public void getCredentials(String credentialsFilePath) {
        try {
            inputProp = new FileInputStream(credentialsFilePath);
            prop.load(inputProp);

            accessToken = prop.getProperty("accessToken");
            clientIdentifier = prop.getProperty("clientIdentifier");
            userLocal = prop.getProperty("userLocale");

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } finally {
            if (inputProp != null) {
                try {
                    inputProp.close();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }
}
