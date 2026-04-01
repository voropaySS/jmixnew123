package com.company.competitions.security;

import com.company.competitions.entity.Department;

import com.company.competitions.entity.DocRole;
import com.company.competitions.entity.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jmix.authserver.service.mapper.DefaultOAuth2TokenUserMixin;

import java.util.List;

public class OAuth2TokenUserMixin extends DefaultOAuth2TokenUserMixin {

    @JsonIgnore
    private Department department;

    @JsonIgnore
    private Position position;

    @JsonIgnore
    private List<DocRole> docRoles;


}