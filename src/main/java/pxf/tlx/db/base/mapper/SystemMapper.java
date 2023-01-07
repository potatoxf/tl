package pxf.tlx.db.base.mapper;

import org.apache.ibatis.annotations.Mapper;
import pxf.tlx.db.base.entity.SystemDir;
import pxf.tlx.db.base.entity.SystemEnum;
import pxf.tlx.db.base.entity.SystemFile;
import pxf.tlx.db.base.entity.SystemSetting;

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


    SystemSetting getSettingById(Integer id);
    int removeSettingById(Integer id);
    int addSetting(SystemSetting record);
    int addSettingSelective(SystemSetting record);
    int modifySettingById(SystemSetting record);
    int modifySettingByIdSelective(SystemSetting record);
}