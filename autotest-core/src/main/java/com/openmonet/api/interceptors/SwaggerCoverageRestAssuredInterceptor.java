package com.openmonet.api.interceptors;

import com.github.viclovsky.swagger.coverage.CoverageOutputWriter;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.*;

import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

public class SwaggerCoverageRestAssuredInterceptor implements OrderedFilter {

    private CoverageOutputWriter writer;

    public SwaggerCoverageRestAssuredInterceptor(String path) {
        this.writer = new FileSystemOutputWriter(Paths.get(path));
    }

    public int getOrder() {
        return 2147483647;
    }

    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Operation operation = new Operation();
        requestSpec.getPathParams().forEach((n, v) -> operation.addParameter(((new PathParameter()).name(n)).example(v)));

        try {
            requestSpec.getQueryParams().forEach((n, v) -> operation.addParameter(((new QueryParameter()).name(n)).example(v)));
        } catch (ClassCastException var8) {
            requestSpec.getQueryParams().keySet().forEach((n) -> operation.addParameter((new QueryParameter()).name(n)));
        }

        try {
            requestSpec.getFormParams().forEach((n, v) -> operation.addParameter(((new FormParameter()).name(n)).example(v)));
        } catch (ClassCastException var7) {
            requestSpec.getFormParams().keySet().forEach((n) -> operation.addParameter((new FormParameter()).name(n)));
        }

        requestSpec.getHeaders().forEach((header) -> operation.addParameter(((new HeaderParameter()).name(header.getName())).example(header.getValue())));
        requestSpec.getMultiPartParams().forEach((multiPartSpecification) -> operation.addParameter((new FormParameter()).name(multiPartSpecification.getControlName())));

        if (Objects.nonNull(requestSpec.getBody())) {
            operation.addParameter((new BodyParameter()).name("body"));
        }

        Response response = ctx.next(requestSpec, responseSpec);
        operation.addResponse(String.valueOf(response.statusCode()), new io.swagger.models.Response());

        URI uri = URI.create(requestSpec.getURI());
        String path = uri.getPath();
        if (requestSpec.getUserDefinedPath() == null || requestSpec.getUserDefinedPath().isEmpty()) {
            path = uri.getPath();
        }
        Swagger swagger = (new Swagger())
                .scheme(Scheme.forValue(uri.getScheme()))
                .host(uri.getHost())
                .consumes(requestSpec.getContentType())
                .produces(response.getContentType())
                .path(path, (new Path()).set(requestSpec.getMethod().toLowerCase(), operation));
        this.writer.write(swagger);
        return response;
    }

    public static void generateSwaggerFiles(RequestSpecBuilder requestSpecBuilder, String path) {
        if (Boolean.parseBoolean(System.getProperty("SWAGGER", "false"))) {
            requestSpecBuilder.addFilter(new SwaggerCoverageRestAssuredInterceptor(path));
        }
    }
}
