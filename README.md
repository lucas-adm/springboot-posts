<h3 align="center">Posts - <a href="https://github.com/lucas-adm/springboot-posts/releases/tag/1.0.0"><i>alpha</i></a></h3>

<br>

<div align="center">

<img width="75px" height="75px" src="https://github.com/lucas-adm/one-challenge-springboot-forum/assets/118030896/963da4e2-d3ff-4de2-902c-14308618c84e">
<img width="75px" height="75px" src="https://github.com/lucas-adm/one-challenge-springboot-forum/assets/118030896/92fc976d-58fa-40b1-bf2e-6769f63831ad">
<img width="75px" height="75px" src="https://github.com/lucas-adm/one-challenge-springboot-hotel/assets/118030896/ba256dce-7970-404f-8ebc-93e482480e77">
<img width="75px" height="75px" src="https://github.com/lucas-adm/one-challenge-springboot-forum/assets/118030896/314fdf40-8e8d-466c-ad78-2dfd9820e08d">
<img width="70px" height="70px" src="https://www.svgrepo.com/show/303576/rabbitmq-logo.svg" />
<img width="75px" height="75px" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/openapi/openapi-original.svg" />
<img width="75px" height="75px" src="https://github.com/lucas-adm/one-challenge-springboot-hotel/assets/118030896/498fd551-bb05-4d22-8560-a14b3f1d076c">

###### *PostgreSQL, Java, Spring Boot, JWT, RabbitMQ, OpenAPI, Docker*

#

</div>

### Como realizar as requisições ⁉

#### ▶ Faça suas requisições *<a href="https://api-srs-posts.onrender.com">aqui</a>*

Para fazer as requisições é necessário estar conectado à uma conta cadastrada, caso não queria se cadastrar, há um conta de demonstração já criada para este propósito, acesse a parte ***users/login*** para mais informações.

<details open>

<summary>/users/register</summary>

<br>

> Por gentileza use um de seus emails, uma mensagem para ativação da conta será enviada

```java
"email": "email válido",
"username": "4 a 33 caracteres",
"password": "4 a 33 caracteres e ao menos 1 letra minúscula, 1 máiuscula e 1 um número.",
"birthDate": "2001-01-01, deve possuir 12+ anos"
```

</details>

<details>
<summary>/users/login</summary>

<br>

> O usuário ***demo*** está permitido à apenas realizar requisições do tipo POST, é recomendado se cadastrar e então se conectar, lembre-se de ativar a conta pelo email.

```java
"username":"Demo",
"senha":"3Tres"
```

<div align="center">

![efetuarLogin](https://i.imgur.com/2QYVSvD.png)

</div>

> Copie o valor do Token JWT retornado

- Agora, na parte de cima da página, acesse o botão ***Authorize*** e cole o valor copiado para liberar todas as requisições!

<br>

<div align="center">

![](https://i.imgur.com/oFJOwA2.png)

</div>

> Este token será utilizado para validar o usuário logado, bloqueando ações não autorizadas

</details>



## Diagrama de Classe do domínio da aplicação

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

## Arquitetura

<details>

![](https://i.imgur.com/A07W2TP.png)

</details>

###### Banco de Dados e Server Deploy por <a href="https://render.com">*Render*</a>

<div align="center">

![swagger](https://i.imgur.com/4Eeni2t.png)

</div>
