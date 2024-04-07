package com.adm.lucas.posts.infra;

import com.adm.lucas.posts.core.ports.repositories.PostRepositoryPort;
import com.adm.lucas.posts.core.ports.repositories.UserRepositoryPort;
import com.adm.lucas.posts.core.ports.services.PostServicePort;
import com.adm.lucas.posts.core.ports.services.UserServicePort;
import com.adm.lucas.posts.core.usecases.PostServiceImpl;
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

}