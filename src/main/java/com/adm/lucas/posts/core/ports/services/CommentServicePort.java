package com.adm.lucas.posts.core.ports.services;

import java.util.UUID;

public interface CommentServicePort {

    void comment(String username, UUID uuid, String text);

    void edit(String username, UUID uuid, String text);

    void remove(String username, UUID uuid);

}