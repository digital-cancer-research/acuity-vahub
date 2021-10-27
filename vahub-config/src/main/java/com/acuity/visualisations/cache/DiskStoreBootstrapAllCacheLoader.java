package com.acuity.visualisations.cache;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.store.DiskStoreBootstrapCacheLoader;

import java.io.IOException;

/**
 * Enables all the caches on disk. This doesnt load the cached data into memory just enables the cache so that all the ehcache remove/clear methods 'know' about
 * the caches that were on the disk.
 *
 * Otherwise only the caches that are used in the app, which wouldnt be all of the disk caches would be cleared when called removeAll
 *
 * @author Glen Drinkwater
 */
@Slf4j
public class DiskStoreBootstrapAllCacheLoader extends DiskStoreBootstrapCacheLoader {
    private static Boolean hasBeenExecuted = false;

    private DiskCacheService diskCacheService;

    /**
     * Constructor for loader
     *
     * @param asynchronous whether load is asynchronous or synchronous
     * @param diskCacheService diskCacheService to
     */
    public DiskStoreBootstrapAllCacheLoader(final boolean asynchronous, DiskCacheService diskCacheService) {
        super(asynchronous);
        this.diskCacheService = diskCacheService;
    }

    public DiskStoreBootstrapAllCacheLoader(DiskCacheService diskCacheService) {
        this(false, diskCacheService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final Ehcache cache) throws CacheException {
        super.load(cache);

        // activate (set alive all disk caches)
        synchronized (DiskStoreBootstrapAllCacheLoader.class) {
            if (!hasBeenExecuted) {
                hasBeenExecuted = true;
            } else {
                return;
            }
        }

        if (isAsynchronous()) {
            AllBootstrapThread thread = new AllBootstrapThread();
            thread.start();
        } else {
            try {
                doAllLoad();
            } catch (IOException e) {
                log.warn("Error asynchronously performing bootstrap. The cause was: " + e.getMessage(), e);
            }
        }
    }

    public void doAllLoad() throws CacheException, IOException {
        diskCacheService.enableDiskCaches(DiskCacheService.CACHETYPE.ALL);
    }

    /**
     * A background daemon thread that asynchronously calls doLoad
     */
    private final class AllBootstrapThread extends Thread {

        AllBootstrapThread() {
            super("Bootstrap Thread for cache ");
            setDaemon(true);
            setPriority(Thread.NORM_PRIORITY);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                doAllLoad();
            } catch (CacheException | IOException e) {
                log.warn("Error asynchronously performing bootstrap. The cause was: " + e.getMessage(), e);
            }
        }
    }
}
