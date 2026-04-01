package com.company.competitions.security;

import com.company.competitions.entity.Department;
import com.company.competitions.entity.Document;
import com.company.competitions.entity.DocumentVersion;
import com.company.competitions.entity.User;
import io.jmix.rest.security.role.RestMinimalRole;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.UiFilterRole;

@ResourceRole(name = "User management", code = UserManagementRole.CODE, scope = "API")
public interface UserManagementRole extends RestMinimalRole, UiFilterRole {

    String CODE = "user-management";

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = User.class, actions = EntityPolicyAction.ALL)
    void user();


    @EntityAttributePolicy(entityClass = Document.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Document.class, actions = EntityPolicyAction.ALL)
    void document();

    @SpecificPolicy(resources = "rest.fileUpload.enabled.")
    void fileUpload();

    @EntityAttributePolicy(entityClass = DocumentVersion.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = DocumentVersion.class, actions = EntityPolicyAction.ALL)
    void documentVersion();

    @EntityAttributePolicy(entityClass = Department.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Department.class, actions = EntityPolicyAction.ALL)
    void department();
}