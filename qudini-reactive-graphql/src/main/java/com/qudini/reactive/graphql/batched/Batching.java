package com.qudini.reactive.graphql.batched;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
public final class Batching {

    /**
     * <p>Builds the map to be returned by <a href="https://github.com/qudini/gom#batched"><code>@Batched</code> resolvers</a> in a <i>to-one</i> relationship.</p>
     * <p>For example, one article is linked to one blog:</p>
     * <pre><code>
     * &#64;Component
     * &#64;TypeResolver("Article")
     * public class ArticleResolver {
     *
     *     &#64;Autowired
     *     private BlogService blogService;
     *
     *     &#64;FieldResolver("blog")
     *     &#64;Batched
     *     public Mono&lt;Map&lt;Article, Blog&gt;&gt; blog(Set&lt;Article&gt; articles) {
     *         return Batching.toOne(
     *                 // inputs:
     *                 articles,
     *                 // how to extract the blog id from an article:
     *                 article -&gt; article.getBlogId(),
     *                 // how to fetch blogs given their id:
     *                 blogIds -&gt; blogService.findBlogsByIds(blogIds),
     *                 // how to extract the id from a blog:
     *                 blog -&gt; blog.getId()
     *         );
     *     }
     *
     * }
     * </code></pre>
     */
    public static <K, I, O> Mono<Map<I, O>> toOne(
            Set<I> inputs,
            Function<I, K> fkGetter,
            Function<Set<K>, Flux<O>> outputsGetter,
            Function<O, K> pkGetter
    ) {
        return toOptionalOne(
                inputs,
                input -> Optional.ofNullable(fkGetter.apply(input)),
                outputsGetter,
                pkGetter
        );
    }

    /**
     * <p>Builds the map to be returned by <a href="https://github.com/qudini/gom#batched"><code>@Batched</code> resolvers</a> in an optional <i>to-one</i> relationship.</p>
     * <p>Same as {@link #toOne(Set, Function, Function, Function)},
     * but to be used when the relationship is optional,
     * i.e. if <code>article.getBlogId()</code> returns <code>Optional&lt;K&gt;</code> instead of <code>K</code>.</p>
     */
    public static <K, I, O> Mono<Map<I, O>> toOptionalOne(
            Set<I> inputs,
            Function<I, Optional<K>> fkGetter,
            Function<Set<K>, Flux<O>> outputsGetter,
            Function<O, K> pkGetter
    ) {
        var fksByInput = inputs
                .stream()
                .map(input -> fkGetter.apply(input).map(fk -> Tuples.of(input, fk)))
                .flatMap(Optional::stream)
                .collect(toUnmodifiableMap(Tuple2::getT1, Tuple2::getT2));
        return outputsGetter
                .apply(Set.copyOf(fksByInput.values()))
                .collect(toUnmodifiableMap(pkGetter, identity()))
                .map(outputsByPk -> toOne(fksByInput, outputsByPk));
    }

    private static <K, I, O> Map<I, O> toOne(Map<I, K> fksByInput, Map<K, O> outputsByPk) {
        return fksByInput
                .entrySet()
                .stream()
                .map(entry -> Optional.ofNullable(outputsByPk.get(entry.getValue())).map(output -> Tuples.of(entry.getKey(), output)))
                .flatMap(Optional::stream)
                .collect(toUnmodifiableMap(Tuple2::getT1, Tuple2::getT2));
    }

    /**
     * <p>Builds the map to be returned by <a href="https://github.com/qudini/gom#batched"><code>@Batched</code> resolvers</a> in a <i>to-many</i> relationship.</p>
     * <p>For example, one blog is linked to many articles:</p>
     * <pre><code>
     * &#64;Component
     * &#64;TypeResolver("Blog")
     * public class BlogResolver {
     *
     *     &#64;Autowired
     *     private ArticleService articleService;
     *
     *     &#64;FieldResolver("articles")
     *     &#64;Batched
     *     public Mono&lt;Map&lt;Blog, List&lt;Article&gt;&gt;&gt; articles(Set&lt;Blog&gt; blogs) {
     *         return Batching.toMany(
     *                 // inputs:
     *                 blogs,
     *                 // how to extract the id from a blog:
     *                 blog -&gt; blog.getId(),
     *                 // how to fetch articles given their blog id:
     *                 blogIds -&gt; articleService.findArticlesByBlogIds(blogIds),
     *                 // how to extract the blog id from an article:
     *                 article -&gt; article.getBlogId()
     *         );
     *     }
     *
     * }
     * </code></pre>
     */
    public static <K, I, O> Mono<Map<I, List<O>>> toMany(
            Set<I> inputs,
            Function<I, K> pkGetter,
            Function<Set<K>, Flux<O>> outputsGetter,
            Function<O, K> fkGetter) {
        var inputsByPk = inputs
                .stream()
                .collect(toUnmodifiableMap(pkGetter, identity()));
        return outputsGetter
                .apply(inputsByPk.keySet())
                .collect(groupingBy(fkGetter, toUnmodifiableList()))
                .map(outputsByFk -> toMany(inputsByPk, outputsByFk));
    }

    private static <K, I, O> Map<I, List<O>> toMany(Map<K, I> inputsByPk, Map<K, List<O>> outputsByFk) {
        return inputsByPk
                .entrySet()
                .stream()
                .collect(toUnmodifiableMap(Map.Entry::getValue, entry -> outputsByFk.getOrDefault(entry.getKey(), List.of())));
    }

}
