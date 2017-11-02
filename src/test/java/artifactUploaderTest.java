import com.sun.javafx.runtime.SystemProperties;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import javafx.beans.property.ReadOnlyProperty;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class artifactUploaderTest {
    @Test
    void main() {
        System.out.println("main Method being Tested.");
    }

    @Test
    void uploadFile() {
        System.out.println("uploadFile being Tested.");
    }

    @Test
    void getCredentials() {
        System.out.println("getCredentials being Tested.");
        artifactUploader testObject = new artifactUploader();
        testObject.getCredentials("/Users/alexwalker/DropboxApp/accessToken");
        assertEquals("iLoZ4oGXrDAAAAAAAAAAEwZ_iDq2iN-lRV3QNZYjPoMfbhhM3_4mtRq8Vb6gs5O7", testObject.getAccessToken());
        assertEquals("dropbox-core-sdk:3.0.5", testObject.getClientIdentifier());
        assertEquals("EN_US", testObject.getUserLocal());
    }

}