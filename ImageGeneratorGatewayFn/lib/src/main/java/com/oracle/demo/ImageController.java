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

package com.oracle.demo;

import com.google.gson.JsonObject;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import static io.micronaut.http.MediaType.IMAGE_PNG;


@Controller("/image/")
class ImageController   {
    private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);


    private  final ImageModel model;


    ImageController() {

        model = OpenAiImageModel.builder()
                .modelName("dall-e-2")
                .withPersisting()
                .size("512x512")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
    }



    @Get(uri = "/status")
    public HttpResponse<?> hello() {
            LOG.info("Status Call - ");
            return HttpResponse.ok().body("Welcome to GDK - Image generation \n");
    }


    @Post(uri = "/generate", consumes = MediaType.TEXT_PLAIN,produces = MediaType.IMAGE_PNG)
    public HttpResponse<?> generate(@Body String userPrompt) {
        try {
            LOG.info(String.format("Image Generation call with - userPrompt=%s ",userPrompt));
            Response<Image> resp = model.generate(userPrompt);
            URI url = resp.content().url();
            LOG.trace("**********> Image Generated :"+url);
            byte[] content = Files.readAllBytes(Paths.get(url));
            return HttpResponse.ok().body(content).header(CONTENT_TYPE,IMAGE_PNG);
        }catch (Exception ex){
            ex.printStackTrace();
            System.err.println(" Error "+ ex);
            return HttpResponse.serverError().body("Server Error "+ ex);
        }

    }

}
