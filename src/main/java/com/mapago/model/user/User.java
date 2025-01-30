package com.mapago.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapago.model.Audit;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends Audit {
    private String userId;
    private String userNm;
    private String password;
    private String email;
    private String useYn;
    private LocalDate birthDay;
    private String nickname;
    private String gender;
    private String kakaoId;
    private String appleId;
    private String naverId;
    private String phoneNumber;
    private String profileImage;
    private String roleName;
    private List<String> roles;
}