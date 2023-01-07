package pxf.tl.io;


import pxf.tl.lang.ThreadInstanceManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author potatoxf
 */
public final class FileLoaderManager {

    /**
     *
     */
    private static final ThreadInstanceManager<IFileLoader> FILE_LOADER_THREAD_INSTANCE_MANAGER =
            new ThreadInstanceManager<>(new DefaultFileLoader());

    /**
     * @param address
     * @return
     * @throws IOException
     */
    public static byte[] getFile(String address) throws IOException {
        return FILE_LOADER_THREAD_INSTANCE_MANAGER.getInstance().getFile(address);
    }

    /**
     * @param address
     * @return
     * @throws IOException
     */
    public static InputStream getFileInputStream(String address) throws IOException {
        return FILE_LOADER_THREAD_INSTANCE_MANAGER.getInstance().getFileInputStream(address);
    }

    /**
     * @param defaultFileLoader
     */
    public static void setDefaultFileLoader(IFileLoader defaultFileLoader) {
        FILE_LOADER_THREAD_INSTANCE_MANAGER.setDefaultInstanceSupplier(() -> defaultFileLoader);
    }

    /**
     * @param fileLoader
     */
    public static void setFileLoader(IFileLoader fileLoader) {
        FILE_LOADER_THREAD_INSTANCE_MANAGER.setInstance(fileLoader);
    }

    /**
     * @param fileLoader
     */
    public static void setOnceFileLoader(IFileLoader fileLoader) {
        FILE_LOADER_THREAD_INSTANCE_MANAGER.setOnceInstance(fileLoader);
    }
}
