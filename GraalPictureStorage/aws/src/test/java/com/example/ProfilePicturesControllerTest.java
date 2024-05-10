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

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.objectstorage.ObjectStorageEntry;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.response.UploadResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Optional;

import static io.micronaut.http.HttpHeaders.ETAG;
import static io.micronaut.http.HttpHeaders.LOCATION;
import static io.micronaut.http.HttpStatus.CREATED;
import static io.micronaut.http.HttpStatus.NO_CONTENT;
import static io.micronaut.http.HttpStatus.OK;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MicronautTest
class ProfilePicturesControllerTest {

    @Inject
    @Client(ProfilePicturesController.PREFIX)
    HttpClient client;

    @Inject
    ObjectStorageOperations<?, ?, ?> objectStorageOperations;

    /*
     * Mock ObjectStorageOperations with Mockito (https://site.mockito.org/)
     * The @MockBean annotation indicates the method returns a mock bean of ObjectStorageOperations.
     * The ObjectStorageOperations mock is injected into the test with @Inject above.
     */
    @MockBean(ObjectStorageOperations.class)
    ObjectStorageOperations<?, ?, ?> objectStorageOperations() {
        return mock(ObjectStorageOperations.class);
    }

    @Test
    void upload() {

        when(objectStorageOperations.upload(any()))
                .then(invocation ->
                        when(mock(UploadResponse.class).getETag())
                                .thenReturn("etag")
                                .getMock());

        var body = MultipartBody.builder()
                    .addPart(
                        "fileUpload",
                        "picture.jpg",
                        "picture".getBytes());

        var request = HttpRequest.POST("/avatar", body)
                        .contentType(MULTIPART_FORM_DATA_TYPE);

        var response = client.toBlocking().exchange(request);

        assertEquals(CREATED, response.status());
        assertTrue(response.header(LOCATION).endsWith("/pictures/avatar"));
        assertEquals("etag", response.header(ETAG));
    }

    @Test
    void download() {
        when(objectStorageOperations.retrieve("avatar.jpg"))
                .then(invocation -> Optional.of(mock(ObjectStorageEntry.class)));
        ObjectStorageEntry<?> objectStorageEntry = mock(ObjectStorageEntry.class);
        when(objectStorageEntry.getInputStream()).then(invocation -> InputStream.nullInputStream());
        when(objectStorageOperations.retrieve("avatar.jpg"))
                .then(invocation -> Optional.of(objectStorageEntry));

        var response = client.toBlocking().exchange("/avatar");

        assertEquals(OK, response.status());
        verify(objectStorageOperations).retrieve("avatar.jpg");
    }

    @Test
    void delete() {

        var response = client.toBlocking().exchange(HttpRequest.DELETE("/avatar"));

        assertEquals(NO_CONTENT, response.status());
        verify(objectStorageOperations).delete("avatar.jpg");
    }
}
