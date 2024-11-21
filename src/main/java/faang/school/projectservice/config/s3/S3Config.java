package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class S3Config {
    private final S3ConfigProperties s3ConfigProperties;

    public S3Config(S3ConfigProperties s3ConfigProperties) {
        this.s3ConfigProperties = s3ConfigProperties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                s3ConfigProperties.getAccessKey(),
                s3ConfigProperties.getSecretKey()
        );

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                s3ConfigProperties.getEndpoint(),
                                ""
                        )
                ).withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}