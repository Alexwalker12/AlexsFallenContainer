import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {

    private static final String ACCESS_TOKEN = "iLoZ4oGXrDAAAAAAAAAAEwZ_iDq2iN-lRV3QNZYjPoMfbhhM3_4mtRq8Vb6gs5O7";

    public static void main(String args[]) throws DbxException {

        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }
            if (!result.getHasMore()) {
                break;
            }
        }

//        try (InputStream in = new FileInputStream("test.txt")) {
//            FileMetadata metadata = client.files().uploadBuilder("/test.txt").uploadAndFinish(in);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }
}
