package com.mapago.mapper.email;

import com.mapago.model.email.Email;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailMapper {
    void insertEmail(Email email);
}