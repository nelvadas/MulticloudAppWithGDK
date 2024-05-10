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

import java.util.Optional;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import static io.micronaut.http.HttpStatus.NO_CONTENT;
import io.micronaut.http.MediaType;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;

public interface ProfilePicturesApi {


    @Post(uri = "/{userId}", consumes = MULTIPART_FORM_DATA)
    HttpResponse<?> upload(CompletedFileUpload fileUpload, String userId, HttpRequest<?> request);

    @Get("/{userId}")
    Optional<HttpResponse<StreamedFile>> download(String userId);

    @Status(NO_CONTENT)
    @Delete("/{userId}")
    void delete(String userId);
}
