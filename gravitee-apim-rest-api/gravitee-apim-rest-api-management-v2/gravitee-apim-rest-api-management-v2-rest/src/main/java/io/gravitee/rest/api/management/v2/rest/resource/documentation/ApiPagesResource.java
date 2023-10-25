/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.v2.rest.resource.documentation;

import io.gravitee.apim.core.audit.model.AuditActor;
import io.gravitee.apim.core.audit.model.AuditInfo;
import io.gravitee.apim.core.documentation.model.Page;
import io.gravitee.apim.core.documentation.usecase.ApiCreateDocumentationPageUsecase;
import io.gravitee.apim.core.documentation.usecase.ApiGetDocumentationPageUsecase;
import io.gravitee.apim.core.documentation.usecase.ApiGetDocumentationPagesUsecase;
import io.gravitee.apim.core.documentation.usecase.ApiUpdateDocumentationPageUsecase;
import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.v2.rest.mapper.PageMapper;
import io.gravitee.rest.api.management.v2.rest.model.*;
import io.gravitee.rest.api.management.v2.rest.resource.AbstractResource;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.rest.annotation.Permission;
import io.gravitee.rest.api.rest.annotation.Permissions;
import io.gravitee.rest.api.service.common.GraviteeContext;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;

@Path("/environments/{envId}/apis/{apiId}/pages")
public class ApiPagesResource extends AbstractResource {

    @Inject
    private ApiGetDocumentationPagesUsecase apiGetDocumentationPagesUsecase;

    @Inject
    private ApiCreateDocumentationPageUsecase apiCreateDocumentationPageUsecase;

    @Inject
    private ApiGetDocumentationPageUsecase apiGetDocumentationPageUsecase;

    @Inject
    private ApiUpdateDocumentationPageUsecase updateDocumentationPageUsecase;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = { RolePermissionAction.READ }) })
    public Response getApiPages(@PathParam("apiId") String apiId, @QueryParam("parentId") String parentId) {
        final var mapper = Mappers.getMapper(PageMapper.class);
        var result = apiGetDocumentationPagesUsecase.execute(new ApiGetDocumentationPagesUsecase.Input(apiId, parentId));
        var response = ApiDocumentationPagesResponse.builder().pages(mapper.mapPageList(result.pages()));
        if (!StringUtils.isEmpty(parentId)) {
            response.breadcrumb(mapper.map(result.breadcrumbList()));
        }
        return Response.ok(response.build()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = { RolePermissionAction.CREATE }) })
    public Response createDocumentationPage(@PathParam("apiId") String apiId, @Valid @NotNull CreateDocumentation createDocumentation) {
        Page pageToCreate = createDocumentation.getActualInstance() instanceof CreateDocumentationMarkdown
            ? Mappers.getMapper(PageMapper.class).map(createDocumentation.getCreateDocumentationMarkdown())
            : Mappers.getMapper(PageMapper.class).map(createDocumentation.getCreateDocumentationFolder());

        pageToCreate.setReferenceId(apiId);
        pageToCreate.setReferenceType(Page.ReferenceType.API);

        var createdPage = apiCreateDocumentationPageUsecase
            .execute(ApiCreateDocumentationPageUsecase.Input.builder().page(pageToCreate).auditInfo(getAuditInfo()).build())
            .createdPage();

        return Response.ok(Mappers.getMapper(PageMapper.class).mapPage(createdPage)).build();
    }

    @GET
    @Path("{pageId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = { RolePermissionAction.READ }) })
    public Response getApiPage(@PathParam("apiId") String apiId, @PathParam("pageId") String pageId) {
        var page = apiGetDocumentationPageUsecase.execute(new ApiGetDocumentationPageUsecase.Input(apiId, pageId)).page();
        return Response.ok(Mappers.getMapper(PageMapper.class).mapPage(page)).build();
    }

    @PUT
    @Path("{pageId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = { RolePermissionAction.UPDATE }) })
    public Response updateApiPage(
        @PathParam("apiId") String apiId,
        @PathParam("pageId") String pageId,
        @Valid @NotNull UpdateDocumentation updateDocumentation
    ) {
        var mapper = Mappers.getMapper(PageMapper.class);
        var auditInfo = getAuditInfo();
        var input = updateDocumentation.getActualInstance() instanceof UpdateDocumentationMarkdown
            ? mapper.map(updateDocumentation.getUpdateDocumentationMarkdown(), apiId, pageId, auditInfo)
            : mapper.map(updateDocumentation.getUpdateDocumentationFolder(), apiId, pageId, auditInfo);

        var page = updateDocumentationPageUsecase.execute(input).page();
        return Response.ok(mapper.mapPage(page)).build();
    }

    private AuditInfo getAuditInfo() {
        var executionContext = GraviteeContext.getExecutionContext();
        var user = getAuthenticatedUserDetails();
        return AuditInfo
            .builder()
            .organizationId(executionContext.getOrganizationId())
            .environmentId(executionContext.getEnvironmentId())
            .actor(AuditActor.builder().userId(user.getUsername()).userSource(user.getSource()).userSourceId(user.getSourceId()).build())
            .build();
    }
}
