package com.adm.lucas.posts.adapter.inbound.dtos.out;

import java.util.Optional;

public record UserDetailDTO(String email, String username, Optional<String> photo) {
}
