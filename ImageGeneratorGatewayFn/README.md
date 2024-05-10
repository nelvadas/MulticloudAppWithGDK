## Micronaut 4.3.7 Documentation

- [User Guide](https://docs.micronaut.io/4.3.7/guide/)
- [API Reference](https://docs.micronaut.io/4.3.7/api/)
- [Configuration Reference](https://docs.micronaut.io/4.3.7/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/)
---
## Deployment with GraalVM

If you want to deploy to AWS Lambda as a GraalVM native image, run:

```bash
./mvnw package -Dpackaging=docker-native -Dmicronaut.runtime=lambda -Pgraalvm
```

This will build the GraalVM native image inside a docker container and generate the `function.zip` ready for the deployment.


## Handler

Handler: io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction

[AWS Lambda Handler](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)

- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)
## Feature amazon-api-gateway documentation

- [Micronaut Amazon API Gateway REST API documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#amazonApiGateway)

- [https://docs.aws.amazon.com/apigateway/](https://docs.aws.amazon.com/apigateway/)


## Feature aws-lambda documentation

- [Micronaut AWS Lambda Function documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#lambda)


## Feature aws-lambda-custom-runtime documentation

- [Micronaut Custom AWS Lambda runtime documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/index.html#lambdaCustomRuntimes)

- [https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html)


## Feature aws-lambda-events-serde documentation

- [Micronaut AWS Lambda Events Serde documentation](https://micronaut-projects.github.io/micronaut-aws/snapshot/guide/#eventsLambdaSerde)

- [https://github.com/aws/aws-lambda-java-libs/tree/main/aws-lambda-java-events](https://github.com/aws/aws-lambda-java-libs/tree/main/aws-lambda-java-events)


## Feature http-client-jdk documentation

- [Micronaut HTTP Client Jdk documentation](https://docs.micronaut.io/latest/guide/index.html#jdkHttpClient)

- [https://openjdk.org/groups/net/httpclient/intro.html](https://openjdk.org/groups/net/httpclient/intro.html)


## Feature maven-enforcer-plugin documentation

- [https://maven.apache.org/enforcer/maven-enforcer-plugin/](https://maven.apache.org/enforcer/maven-enforcer-plugin/)


## Feature oracle-function documentation

- [Micronaut Oracle Function documentation](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#functions)

- [https://docs.cloud.oracle.com/iaas/Content/Functions/Concepts/functionsoverview.htm](https://docs.cloud.oracle.com/iaas/Content/Functions/Concepts/functionsoverview.htm)


## Feature oracle-function-http documentation

- [Micronaut Oracle Function documentation](https://micronaut-projects.github.io/micronaut-oracle-cloud/latest/guide/#httpFunctions)


## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


