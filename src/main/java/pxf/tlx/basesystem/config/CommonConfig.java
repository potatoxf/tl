package pxf.tlx.basesystem.config;

import lombok.Getter;
import lombok.Setter;
import pxf.tl.lang.MappingFolder;
import pxf.tl.lang.MappingFolderCached;

/**
 * 通用功能配置
 *
 * @author potatoxf
 */
@Getter
@Setter
public abstract class CommonConfig {
    /**
     * 同步内部路径
     */
    private String syncCacheBuiltPath = "/tl/";
    /**
     * 同步的外部路径
     */
    private String syncCacheOuterPath = "./tl/";

    /**
     * 缓存映射目录
     *
     * @return {@code MappingFolderCached}
     */
    public MappingFolderCached mappingFolderCached() {
        return new MappingFolderCached(syncCacheBuiltPath, syncCacheOuterPath);
    }

    /**
     * 映射目录
     *
     * @return {@code MappingFolder}
     */
    public MappingFolder mappingFolder() {
        return new MappingFolder(syncCacheBuiltPath, syncCacheOuterPath);
    }

}
