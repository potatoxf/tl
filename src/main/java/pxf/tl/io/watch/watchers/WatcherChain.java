package pxf.tl.io.watch.watchers;


import pxf.tl.help.New;
import pxf.tl.io.watch.Watcher;
import pxf.tl.lang.Chain;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Iterator;
import java.util.List;

/**
 * 观察者链<br>
 * 用于加入多个观察者
 *
 * @author potatoxf
 */
public class WatcherChain implements Watcher, Chain<Watcher, WatcherChain> {

    /**
     * 观察者列表
     */
    private final List<Watcher> chain;

    /**
     * 构造
     *
     * @param watchers 观察者列表
     */
    public WatcherChain(Watcher... watchers) {
        chain = New.list(false, watchers);
    }

    /**
     * 创建观察者链{@link WatcherChain}
     *
     * @param watchers 观察者列表
     * @return {@link WatcherChain}
     */
    public static WatcherChain create(Watcher... watchers) {
        return new WatcherChain(watchers);
    }

    @Override
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : chain) {
            watcher.onCreate(event, currentPath);
        }
    }

    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : chain) {
            watcher.onModify(event, currentPath);
        }
    }

    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : chain) {
            watcher.onDelete(event, currentPath);
        }
    }

    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
        for (Watcher watcher : chain) {
            watcher.onOverflow(event, currentPath);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Watcher> iterator() {
        return this.chain.iterator();
    }

    @Override
    public WatcherChain addChain(Watcher element) {
        this.chain.add(element);
        return this;
    }
}
