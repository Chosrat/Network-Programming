package Common;

import java.io.Serializable;

/**
 * Created by Chosrat on 2017-11-30.
 */
public class FileCredentials implements Serializable{

    private int ownerId;
    private String fileName;
    private boolean publik;

    public FileCredentials() {
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isPublik() {
        return publik;
    }

    public void setPublik(boolean publik) {
        this.publik = publik;
    }
}
