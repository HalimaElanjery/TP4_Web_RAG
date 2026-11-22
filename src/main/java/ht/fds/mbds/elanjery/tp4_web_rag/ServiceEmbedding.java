
package ht.fds.mbds.elanjery.tp4_web_rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.Serializable;

@ApplicationScoped
public class ServiceEmbedding implements Serializable {

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    public Embedding transformerQuestion(String question) {

        EmbeddingModel modele =
                magasinEmbeddings.getModeleEmbedding();

        return modele.embed(question).content();
    }
}
