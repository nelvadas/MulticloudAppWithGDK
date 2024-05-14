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

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.micronaut.http.HttpHeaders.ETAG;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import static io.micronaut.http.MediaType.IMAGE_JPEG_TYPE;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
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

@Controller(ProfilePicturesController.PREFIX)
@ExecuteOn(TaskExecutors.IO)
class ProfilePicturesController implements ProfilePicturesApi {
    private static final Logger LOG = LoggerFactory.getLogger(ProfilePicturesController.class);

    static final String PREFIX = "/pictures";

    private final ObjectStorageOperations<?, ?, ?> objectStorage;
    private final HttpHostResolver httpHostResolver;

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

   


    ProfilePicturesController(ObjectStorageOperations<?, ?, ?> objectStorage,
            HttpHostResolver httpHostResolver) {
        this.objectStorage = objectStorage;
        this.httpHostResolver = httpHostResolver;
    }


    private URI location(HttpRequest<?> request, String userId) {
        return UriBuilder.of(httpHostResolver.resolve(request))
                .path(PREFIX)
                .path(userId)
                .build();
    }



    private static String buildKey(String userId) {
        return userId + ".jpg";
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
