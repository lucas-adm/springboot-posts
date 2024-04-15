## Diagrama de Classe do Domain

```mermaid
classDiagram
    class UserEntity {
        UUID id
        String email
        String username
        String password
        String photo
        LocalDate birthDate
        Role role
    }
    class PostEntity {
        UUID id
        UserEntity user
        String username
        LocalDateTime datePost
        Status status
        String text
        Set<UpvoteEntity> upvotes
        List<CommentEntity> comments
    }
    class CommentEntity {
        UUID id
        UserEntity user
        PostEntity post
        String text
        LocalDateTime dateComment
        List<AnswerEntity> answers
    }
    class AnswerEntity {
        UUID id
        UserEntity user
        CommentEntity comment
        LocalDateTime dateAnswer
        String text
    }
    class UpvoteEntity {
        UUID id
        UserEntity user
        PostEntity post
    }
    UserEntity "1" -- "0..*" PostEntity : has
    UserEntity "1" -- "0..*" CommentEntity : has
    UserEntity "1" -- "0..*" AnswerEntity : has
    UserEntity "1" -- "0..*" UpvoteEntity : has
    PostEntity "1" -- "0..*" CommentEntity : has
    PostEntity "1" -- "0..*" UpvoteEntity : has
    CommentEntity "1" -- "0..*" AnswerEntity : has

```
