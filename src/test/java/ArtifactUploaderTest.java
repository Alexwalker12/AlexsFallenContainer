import com.dropbox.core.DbxException;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ArtifactUploaderTest {
    @Test
    void go() throws DbxException {
        System.out.println("main Method being Tested.");
        String[] args = {"/Users/alexwalker/DropboxApp/SmartDriveArtifactUploader/secondTest.txt", "/secondTest.txt", "/Users/alexwalker/DropboxApp/accessToken"};
        ArtifactUploader testObject = new ArtifactUploader();
        testObject.go(args);
        assertEquals("/Users/alexwalker/DropboxApp/SmartDriveArtifactUploader/secondTest.txt", testObject.getFilePath());
        assertEquals("/secondTest.txt", testObject.getDropboxFilePath());
        assertEquals("/Users/alexwalker/DropboxApp/accessToken", testObject.getCredentialsFilePath());

    }

    @Test
    void uploadFile() {
        System.out.println("uploadFile being Tested.");
        ArtifactUploader testObject = new ArtifactUploader();
        testObject.getCredentials("/Users/alexwalker/DropboxApp/accessToken");
        testObject.uploadFile("/Users/alexwalker/DropboxApp/SmartDriveArtifactUploader/secondTest.txt", "/secondTest.txt");
        assertEquals("https://www.dropbox.com/s/fk1c1p1gmkeiovc/secondTest.txt?dl=0", testObject.getShareUrl());

    }

    @Test
    void getCredentials() {
        System.out.println("getCredentials being Tested.");
        ArtifactUploader testObject = new ArtifactUploader();
        testObject.getCredentials("/Users/alexwalker/DropboxApp/accessToken");
        assertEquals("iLoZ4oGXrDAAAAAAAAAAEwZ_iDq2iN-lRV3QNZYjPoMfbhhM3_4mtRq8Vb6gs5O7", testObject.getAccessToken());
        assertEquals("dropbox-core-sdk:3.0.5", testObject.getClientIdentifier());
        assertEquals("EN_US", testObject.getUserLocal());
    }

}