package com.mapago.mapper.file;

import com.mapago.model.file.File;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

    List<File> selectFilesByFileIds(String fileIdList);

    int saveFile(File file);

    Integer deleteFile(File file);

}