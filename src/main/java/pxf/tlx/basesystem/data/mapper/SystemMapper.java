package pxf.tlx.basesystem.data.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pxf.tlx.basesystem.data.entity.*;

import java.util.List;

/**
 * @author potatoxf
 */
@Mapper
public interface SystemMapper {
    SystemDir getDirById(Integer id);

    int removeDirById(Integer id);

    int addDir(SystemDir record);

    int addDirSelective(SystemDir record);

    int modifyDirById(SystemDir record);

    int modifyDirByIdSelective(SystemDir record);

    SystemFile getFileById(Integer id);

    int removeFileById(Integer id);

    int addFile(SystemFile record);

    int addFileSelective(SystemFile record);

    int modifyFileById(SystemFile record);

    int modifyFileByIdSelective(SystemFile record);

    SystemEnum getEnumById(Integer id);

    int removeEnumById(Integer id);

    int addEnum(SystemEnum record);

    int addEnumSelective(SystemEnum record);

    int modifyEnumById(SystemEnum record);

    int modifyEnumByIdSelective(SystemEnum record);

    String getSettingPropValue(@Param("module") String module, @Param("designation") String designation);

    SystemSetting getSettingById(Integer id);

    int removeSettingById(Integer id);

    int addSetting(SystemSetting record);

    int addSettingSelective(SystemSetting record);

    int modifySettingById(SystemSetting record);

    int modifySettingByIdSelective(SystemSetting record);

    List<SystemCors> getCorsList();

    SystemCors getCorsById(Integer id);

    int removeCorsById(Integer id);

    int addCors(SystemCors record);

    int modifyCorsByIdSelective(SystemCors record);
}