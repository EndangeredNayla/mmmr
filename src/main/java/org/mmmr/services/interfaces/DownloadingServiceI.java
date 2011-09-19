package org.mmmr.services.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Jurgen
 */
public interface DownloadingServiceI {
    public abstract void downloadURL(URL url, File target) throws IOException;
}