package pxf.tlx.basesystem.action;

import lombok.Getter;
import pxf.tlx.basesystem.data.entity.SystemCors;
import pxf.tlx.basesystem.data.mapper.SystemMapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * 系统服务
 *
 * @author potatoxf
 */
@Getter
public final class SystemAction {
    private final SystemMapper systemMapper;

    public SystemAction(@Nonnull SystemMapper systemMapper) {
        this.systemMapper = Objects.requireNonNull(systemMapper);
    }

    /**
     * 获取索引CORS信息
     *
     * @return {@code List<SystemCors>}
     */
    public List<SystemCors> listCors() {
        return systemMapper.getCorsList();
    }
}
