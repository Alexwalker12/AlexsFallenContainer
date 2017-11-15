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

public class ArtifactUploader {

    private String accessToken;
    private String clientIdentifier;
    private String userLocal;

    private static Properties prop = new Properties();
    private static InputStream inputProp = null;

    private String filePath;
    private String dropboxFilePath;
    private String credentialsFilePath;

    private String shareUrl;

    public static void main(String[] args) throws DbxException {
        ArtifactUploader artifact = new ArtifactUploader();
        if (args.length != 3) {
            System.out.println("Missing Arguments: input path of file to upload, and path to key.");
            return;
        }
        artifact.go(args);
    }


    public void go(String[] args) throws DbxException {
        
        filePath = args[0];
        dropboxFilePath = args[1];
        credentialsFilePath = args[2];

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

                            shareUrl = linkMetaData.getUrl();

                            System.out.println(shareUrl);
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

    public void getCredentials(String credentialsFilePath) {
        try {
            inputProp = new FileInputStream(credentialsFilePath);
            prop.load(inputProp);

            accessToken = prop.getProperty("accessToken");
            clientIdentifier = prop.getProperty("clientIdentifier");
            userLocal = prop.getProperty("userLocale");

        } catch (IOException ex) {
            ex.printStackTrace();
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

    public String getAccessToken() {
        return accessToken;
    }
    public String getClientIdentifier() {
        return clientIdentifier;
    }
    public String getUserLocal() {
        return userLocal;
    }
    public String getFilePath() {
        return filePath;
    }
    public String getDropboxFilePath() {
        return dropboxFilePath;
    }
    public String getCredentialsFilePath() {
        return credentialsFilePath;
    }
    public String getShareUrl() {
        return shareUrl;
    }
}
