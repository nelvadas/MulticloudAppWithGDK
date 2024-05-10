/*
 * Copyright 2024 Oracle and/or its affiliates
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Headers;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.objectstorage.ObjectStorageEntry;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import static io.micronaut.http.HttpHeaders.ETAG;
import static io.micronaut.http.MediaType.*;

@Controller(ProfilePicturesController.PREFIX)
@ExecuteOn(TaskExecutors.IO)
class ProfilePicturesController implements ProfilePicturesApi {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilePicturesController.class);

    static final String PREFIX = "/pictures";

    private final ObjectStorageOperations<?, ?, ?> objectStorage;
    private final HttpHostResolver httpHostResolver;



    ProfilePicturesController(ObjectStorageOperations<?, ?, ?> objectStorage,
            HttpHostResolver httpHostResolver) {
        this.objectStorage = objectStorage;
        this.httpHostResolver = httpHostResolver;
    }




    @Override
    public HttpResponse<?> upload(CompletedFileUpload fileUpload,
                                  String userId,
                                  HttpRequest<?> request) {
        String key = buildKey(userId);
        UploadRequest objectStorageUpload = UploadRequest.fromCompletedFileUpload(fileUpload, key);
        UploadResponse<?> response = objectStorage.upload(objectStorageUpload);

        return HttpResponse
                .created(location(request, userId))
                .header(ETAG, response.getETag());
    }

    private static String buildKey(String userId) {
        return userId + ".jpg";
    }

    private URI location(HttpRequest<?> request, String userId) {
        return UriBuilder.of(httpHostResolver.resolve(request))
                .path(PREFIX)
                .path(userId)
                .build();
    }

    @Override
    public Optional<HttpResponse<StreamedFile>> download(String userId) {
        String key = buildKey(userId);
        return objectStorage.retrieve(key)
                .map(ProfilePicturesController::buildStreamedFile);
    }

    private static HttpResponse<StreamedFile> buildStreamedFile(ObjectStorageEntry<?> entry) {
        StreamedFile file = new StreamedFile(entry.getInputStream(), IMAGE_JPEG_TYPE).attach(entry.getKey());
        MutableHttpResponse<Object> httpResponse = HttpResponse.ok();
        file.process(httpResponse);
        return httpResponse.body(file);
    }

    @Override
    public void delete(String userId) {
        String key = buildKey(userId);
        objectStorage.delete(key);
    }
}
