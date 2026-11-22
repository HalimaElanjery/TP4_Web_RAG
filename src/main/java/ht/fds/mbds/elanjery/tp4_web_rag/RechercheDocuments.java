
package ht.fds.mbds.elanjery.tp4_web_rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class RechercheDocuments implements Serializable {

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    public List<EmbeddingMatch<TextSegment>> rechercherRag(
            Embedding question,
            int nombreResultats) {

        EmbeddingSearchRequest request =
                EmbeddingSearchRequest.builder()
                        .queryEmbedding(question)
                        .maxResults(nombreResultats)
                        .minScore(0.5)
                        .build();

        EmbeddingSearchResult<TextSegment> result =
                magasinEmbeddings.getMagasinRag().search(request);

        return result.matches();
    }

    public List<EmbeddingMatch<TextSegment>> rechercherReseaux(
            Embedding question,
            int nombreResultats) {

        EmbeddingSearchRequest request =
                EmbeddingSearchRequest.builder()
                        .queryEmbedding(question)
                        .maxResults(nombreResultats)
                        .minScore(0.5)
                        .build();

        EmbeddingSearchResult<TextSegment> result =
                magasinEmbeddings.getMagasinReseaux().search(request);

        return result.matches();
    }
}
