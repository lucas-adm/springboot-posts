package com.adm.lucas.posts.infra.beans;

import com.adm.lucas.posts.core.ports.repositories.*;
import com.adm.lucas.posts.core.ports.services.*;
import com.adm.lucas.posts.core.usecases.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfigurations {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public UserServicePort userServicePort(UserRepositoryPort userRepositoryPort) {
        return new UserServiceImpl(userRepositoryPort);
    }

    @Bean
    public PostServicePort postServicePort(PostRepositoryPort postRepositoryPort) {
        return new PostServiceImpl(postRepositoryPort);
    }

    @Bean
    public UpvoteServicePort upvoteServicePort(UpvoteRepositoryPort upvoteRepositoryPort) {
        return new UpvoteServiceImpl(upvoteRepositoryPort);
    }

    @Bean
    public CommentServicePort commentServicePort(CommentRepositoryPort commentRepositoryPort) {
        return new CommentServiceImpl(commentRepositoryPort);
    }

    @Bean
    public AnswerServicePort answerServicePort(AnswerRepositoryPort answerRepositoryPort) {
        return new AnswerServiceImpl(answerRepositoryPort);
    }

}