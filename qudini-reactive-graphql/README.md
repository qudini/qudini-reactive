# qudini-reactive-graphql

GraphQL over WebFlux thanks to [GOM](https://github.com/qudini/gom).

## Installation

```xml
<dependencies>
    <dependency>
        <groupId>com.qudini</groupId>
        <artifactId>qudini-reactive-graphql</artifactId>
    </dependency>
</dependencies>
```

## Configuration

### Schema

Your GraphQL schema is expected to be available in the classpath, in a file named `schema.graphql`. You can overwrite this behaviour by registering the `TypeDefinitionRegistry` bean yourself:

```java
@Bean
public TypeDefinitionRegistry graphqlRegistry(...) {
    return ...;
}
```

### Error

The default `DataFetcherExceptionHandler` will log exceptions with `ERROR`, unless it's a subclass of `org.springframework.web.server.ResponseStatusException` and its status is 4xx, in which case `WARN` will be used.

This can be overwritten if needed:

```java
@Bean
public DataFetcherExceptionHandler graphqlExceptionHandler(...) {
    return ...;
}
```

### Query Depth

Your GraphQL Max Query Depth  is expected to be available in the classpath, in `application.properties`. `qudini-reactive.graphql-max-depth` is the property name.
Exception is thrown if Query Depth is exceeded. If Property itself is not present. It will throw Run time exception.

### Wiring

The `RuntimeWiring` bean can be overwritten if needed (e.g. if further customisation is needed):

```java
@Bean
public RuntimeWiring graphqlWiring(...) {
    return ...;
}
```

## Usage

### Resolvers

Register your [resolvers](https://github.com/qudini/gom#resolvers) as components (`Mono`s and `Flux`es will be already handled via [converters](https://github.com/qudini/gom#convertersmyconvertersinstance)):

```java
@Component
@TypeResolver("Article")
public class ArticleResolver {

    @Autowired
    private BlogService blogService;

    @FieldResolver("blog")
    public Mono<Blog> blog(Article article) {
        return blogService.findBlogById(article.getBlogId());
    }

}
```

You can still register `TypeRuntimeWiring`s as beans, for example to resolve `union`s or `interface`s:

```java
@Configuration
public class MyInterfaceResolver {

    @Bean
    public TypeRuntimeWiring myInterface() {
        return newTypeWiring("MyInterface").typeResolver(this::resolve).build();
    }

    private GraphQLObjectType resolve(TypeResolutionEnvironment env) {
        var instance = env.getObject();
        if (instance instanceof MyImpl1) {
            return env.getSchema().getObjectType("MyImpl1");
        } else if (instance instanceof MyImpl2) {
            return env.getSchema().getObjectType("MyImpl2");
        } else {
            throw new IllegalStateException(...);
        }
    }

}
```

### @Batched

`com.qudini.reactive.graphql.batched.Batching` help to build the map to be returned by [`@Batched` resolvers](https://github.com/qudini/gom#batched).

#### toOne

For example, one article is linked _to one_ blog:

```java
@Component
@TypeResolver("Article")
public class ArticleResolver {

    @Autowired
    private BlogService blogService;

    @FieldResolver("blog")
    @Batched
    public Mono<Map<Article, Blog>> blog(Set<Article> articles) {
        return Batching.toOne(
                // inputs:
                articles,
                // how to extract the blog id from an article:
                article -> article.getBlogId(),
                // how to fetch blogs given their id:
                blogIds -> blogService.findBlogsByIds(blogIds),
                // how to extract the id from a blog:
                blog -> blog.getId()
        );
    }

}
```

#### toOptionalOne

Same as `toOne`, but to be used when the relationship is optional, i.e. if `article.getBlogId()` returns `Optional<K>` instead of `K`. 

#### toMany

For example, one blog is linked _to many_ articles:

```java
@Component
@TypeResolver("Blog")
public class BlogResolver {

    @Autowired
    private ArticleService articleService;

    @FieldResolver("articles")
    @Batched
    public Mono<Map<Blog, List<Article>>> articles(Set<Blog> blogs) {
        return Batching.toMany(
                // inputs:
                blogs,
                // how to extract the id from a blog:
                blog -> blog.getId(),
                // how to fetch articles given their blog id:
                blogIds -> articleService.findArticlesByBlogIds(blogIds),
                // how to extract the blog id from an article:
                article -> article.getBlogId()
        );
    }

}
```

### Context

The reactive context will be available in your resolvers, meaning Spring Security can be used too, for example:

```java
@Component
@TypeResolver("Mutation")
public class ArticleMutationResolver {

    @Autowired
    private BlogService blogService;

    @FieldResolver("createBlog")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Blog> createBlog(Arguments arguments) {
        return blogService.createBlog(arguments);
    }

}
```

### Scalars

Register custom scalars as components, by extending one of the following classes:

- `com.qudini.reactive.graphql.scalar.StringScalar` for string-based custom scalars,
- `com.qudini.reactive.graphql.scalar.IntScalar` for int-based custom scalars,
- `com.qudini.reactive.graphql.scalar.FloatScalar` for float-based custom scalars,
- `com.qudini.reactive.graphql.scalar.BooleanScalar` for boolean-based custom scalars.

For example, if you need a `LocalDateTime` scalar:

```java
@Component
public class LocalDateTimeScalar extends StringScalar<LocalDateTime> {

    public LocalDateTimeScalar() {
        super("LocalDateTime", "Example: 2007-12-03T10:15:30", LocalDateTime.class);
    }

    @Override
    public String serialise(LocalDateTime input) {
        return input.toString();
    }

    @Override
    public LocalDateTime parse(String input) {
        return LocalDateTime.parse(input);
    }

}
```

### HTTP

There a few ways to [serve GraphQL over HTTP](https://graphql.org/learn/serving-over-http/), but only the JSON-based `POST` endpoint will be registered:

```
POST /graphql

Headers:
    Content-Type: application/json

body:
    {
        "query": "...",
        "operationName": "...",
        "variables": { "myVariable": "someValue", ... }
    }
```
