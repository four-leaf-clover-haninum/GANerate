package com.example.GANerate.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

import static org.springframework.restdocs.snippet.Attributes.Attribute;

@TestConfiguration
public class RestDocsConfig {

    @Bean
    public RestDocumentationResultHandler write(){
        return MockMvcRestDocumentation.document(
                //이 부분을 통해 생성되는 조각의 디렉토리 명을 클래스명/메서드명 으로 지정
                "{class-name}/{method-name}",
                // JSON을 이쁘게 출력(prettyPrint)
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        );
    }

    //제약조건 같이 커스텀으로 작성하는 내용을 추가하기 위해 생성
    public static final Attribute field(
            final String key,
            final String value){
        return new Attribute(key,value);
    }
}
