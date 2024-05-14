package com.example;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import static io.micronaut.http.MediaType.IMAGE_PNG;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;

@Controller("/image")
public class ImageController {
    private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

    private  final ImageModel model1,model2;


    ImageController() {


        model1 = OpenAiImageModel.builder()
                .modelName("dall-e-2")
                .withPersisting()
                .size("1024x1024")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        model2 = OpenAiImageModel.builder()
                .modelName("dall-e-2")
                .size("1024x1024")
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
    }




    @Post(uri = "/generate1/{userId}", consumes = MediaType.TEXT_PLAIN,produces = IMAGE_PNG)
    public HttpResponse<?> generate1(String userId, @Body String userPrompt) {
        try {

            Response<Image> resp = model1.generate(userPrompt);
            URI url = resp.content().url();
            LOG.trace("**********> Image Generated :"+url);
            //   UploadRequest uploadRequest = UploadRequest.fromPath( Paths.get(url), userId+".jpg");
            //   UploadResponse<?> uploadResponse = objectStorage.upload(uploadRequest);
            // return HttpResponse.ok().header(ETAG, uploadResponse.getETag());
            byte[] content = Files.readAllBytes(Paths.get(url));
            return HttpResponse.ok().body(content).header(CONTENT_TYPE,IMAGE_PNG);
        }catch (Exception ex){
            ex.printStackTrace();
            return HttpResponse.serverError().body("Server Error "+ ex);
        }

    }




    @Post(uri = "/generate/{userId}", consumes = MediaType.TEXT_PLAIN,produces = IMAGE_PNG)
    public HttpResponse<?> generate(String userId, @Body String userPrompt) {
        try {
            Response<Image> resp = model2.generate(userPrompt);
            URI url = resp.content().url();
            LOG.trace("**********> Image Generated :"+url);
            //   UploadRequest uploadRequest = UploadRequest.fromPath( Paths.get(url), userId+".jpg");
            //   UploadResponse<?> uploadResponse = objectStorage.upload(uploadRequest);
            // return HttpResponse.ok().header(ETAG, uploadResponse.getETag());
            return HttpResponse.status(HttpStatus.FOUND).body(url).header("Location",url.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            return HttpResponse.serverError().body("Server Error "+ ex);
        }

    }


}
