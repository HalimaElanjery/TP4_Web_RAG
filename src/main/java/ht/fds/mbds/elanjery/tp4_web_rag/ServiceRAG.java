
package ht.fds.mbds.elanjery.tp4_web_rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceRAG implements Serializable {

    @Inject
    private ServiceEmbedding serviceEmbedding;

    @Inject
    private RechercheDocuments rechercheDocuments;

    public String rechercherDansRag(String question) {

        var embedding =
                serviceEmbedding.transformerQuestion(question);

        List<EmbeddingMatch<TextSegment>> resultats =
                rechercheDocuments.rechercherRag(embedding, 3);

        return formaterResultats(resultats);
    }

    public String rechercherDansReseaux(String question) {

        var embedding =
                serviceEmbedding.transformerQuestion(question);

        List<EmbeddingMatch<TextSegment>> resultats =
                rechercheDocuments.rechercherReseaux(embedding, 3);

        return formaterResultats(resultats);
    }

    private String formaterResultats(
            List<EmbeddingMatch<TextSegment>> resultats) {

        if (resultats == null || resultats.isEmpty()) {
            return "Aucun passage pertinent trouve dans les documents.";
        }

        return resultats.stream()
                .map(resultat -> resultat.embedded().text())
                .collect(Collectors.joining("\n\n"));
    }
}
