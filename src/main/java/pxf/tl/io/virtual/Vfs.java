package pxf.tl.io.virtual;


import pxf.tl.exception.UnsupportedException;
import pxf.tl.help.Whether;
import pxf.tl.util.ToolLog;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * a simple virtual file system bridge
 *
 * <p>use the {@link Vfs#fromURL(URL)} to get a {@link VirtualDirectory}, then use {@link
 * VirtualDirectory#getFiles()} to iterate over the {@link VirtualFile}
 *
 * <p>for example:
 *
 * <pre>
 *      Vfs.Dir dir = Vfs.fromURL(url);
 *      Iterable<Vfs.File> files = dir.getFiles();
 *      for (Vfs.File file : files) {
 *          InputStream is = file.openInputStream();
 *      }
 * </pre>
 *
 * <p>{@link Vfs#fromURL(URL)} uses static {@link DefaultUrlTypes} to resolve URLs. It contains
 * VfsTypes for handling for common resources such as local jar file, local directory, jar url, jar
 * input stream and more.
 *
 * <p>It can be plugged in with other {@link UrlType} using {@link Vfs#addDefaultURLTypes(UrlType)}
 * or {@link Vfs#setDefaultURLTypes(List)}.
 *
 * <p>for example:
 *
 * <pre>
 *      Vfs.addDefaultURLTypes(new Vfs.UrlType() {
 *          public boolean matches(URL url)         {
 *              return url.getProtocol().equals("http");
 *          }
 *          public Vfs.Dir createDir(final URL url) {
 *              return new HttpDir(url); //implement this type... (check out a naive implementation on VfsTest)
 *          }
 *      });
 *
 *      Vfs.Dir dir = Vfs.fromURL(new URL("http://mirrors.ibiblio.org/pub/mirrors/maven2/org/slf4j/slf4j-api/1.5.6/slf4j-api-1.5.6.jar"));
 * </pre>
 *
 * <p>use {@link Vfs#findFiles(Collection, Predicate)} to get an iteration of files matching given
 * name predicate over given list of urls
 */
public abstract class Vfs {
    private static List<UrlType> defaultUrlTypes =
            new ArrayList<>(Arrays.asList(DefaultUrlTypes.values()));

    /**
     * the default url types that will be used when issuing {@link Vfs#fromURL(URL)}
     */
    public static List<UrlType> getDefaultUrlTypes() {
        return defaultUrlTypes;
    }

    /**
     * sets the static default url types. can be used to statically plug in urlTypes
     */
    public static void setDefaultURLTypes(final List<UrlType> urlTypes) {
        defaultUrlTypes = urlTypes;
    }

    /**
     * add a static default url types to the beginning of the default url types list. can be used to
     * statically plug in urlTypes
     */
    public static void addDefaultURLTypes(UrlType urlType) {
        defaultUrlTypes.add(0, urlType);
    }

    /**
     * tries to create a Dir from the given url, using the defaultUrlTypes
     */
    public static VirtualDirectory fromURL(final URL url) {
        return fromURL(url, defaultUrlTypes);
    }

    /**
     * tries to create a Dir from the given url, using the given urlTypes
     */
    public static VirtualDirectory fromURL(final URL url, final List<UrlType> urlTypes) {
        for (UrlType type : urlTypes) {
            try {
                if (type.matches(url)) {
                    VirtualDirectory virtualDirectory = type.createDir(url);
                    if (virtualDirectory != null) return virtualDirectory;
                }
            } catch (Throwable e) {
                ToolLog.warn(e, () ->
                        "could not create Dir using %s from url "
                                + url.toExternalForm() + ". skipping.", type);
            }
        }

        throw new UnsupportedException(
                "could not create Vfs.Dir from url, no matching UrlType was found ["
                        + url.toExternalForm()
                        + "]\n"
                        + "either use fromURL(final URL url, final List<UrlType> urlTypes) or "
                        + "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) "
                        + "with your specialized UrlType.");
    }

    /**
     * tries to create a Dir from the given url, using the given urlTypes
     */
    public static VirtualDirectory fromURL(final URL url, final UrlType... urlTypes) {
        return fromURL(url, Arrays.asList(urlTypes));
    }

    /**
     * return an iterable of all {@link VirtualFile} in given urls, starting with given packagePrefix
     * and matching nameFilter
     */
    public static Iterable<VirtualFile> findFiles(
            final Collection<URL> inUrls,
            final String packagePrefix,
            final Predicate<String> nameFilter) {
        Predicate<VirtualFile> fileNamePredicate =
                file -> {
                    String path = file.getRelativePath();
                    if (path.startsWith(packagePrefix)) {
                        String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
                        return Whether.noEmpty(filename) && nameFilter.test(filename.substring(1));
                    } else {
                        return false;
                    }
                };
        return findFiles(inUrls, fileNamePredicate);
    }

    /**
     * return an iterable of all {@link VirtualFile} in given urls, matching filePredicate
     */
    public static Iterable<VirtualFile> findFiles(
            final Collection<URL> urls, final Predicate<VirtualFile> filePredicate) {
        return () ->
                urls.stream()
                        .flatMap(
                                url -> {
                                    try {
                                        return StreamSupport.stream(fromURL(url).getFiles().spliterator(), false);
                                    } catch (Throwable e) {
                                        ToolLog.warn(e, () -> "could not findFiles for url. continuing. [%s]", url);
                                        return Stream.of();
                                    }
                                })
                        .filter(filePredicate)
                        .iterator();
    }
}
