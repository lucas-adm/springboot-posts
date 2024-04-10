package com.adm.lucas.posts.infra;

import com.adm.lucas.posts.core.ports.repositories.CommentRepositoryPort;
import com.adm.lucas.posts.core.ports.repositories.PostRepositoryPort;
import com.adm.lucas.posts.core.ports.repositories.UpvoteRepositoryPort;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.core.ports.services.CommentServicePort;
import com.adm.lucas.posts.core.ports.services.PostServicePort;
import com.adm.lucas.posts.core.ports.services.UpvoteServicePort;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import com.adm.lucas.posts.core.usecases.CommentServiceImpl;
import com.adm.lucas.posts.core.usecases.PostServiceImpl;
import com.adm.lucas.posts.core.usecases.UpvoteServiceImpl;
import com.adm.lucas.posts.core.usecases.UserServiceImpl;
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

}