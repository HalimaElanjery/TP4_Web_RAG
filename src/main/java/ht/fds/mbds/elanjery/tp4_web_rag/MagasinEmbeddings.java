
package ht.fds.mbds.elanjery.tp4_web_rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;

@ApplicationScoped
public class MagasinEmbeddings {

    private final EmbeddingStore<TextSegment> magasinRag;
    private final EmbeddingStore<TextSegment> magasinReseaux;

    private final EmbeddingModel modeleEmbedding;
    private final ApacheTikaDocumentParser parser;

    public MagasinEmbeddings() {

        this.magasinRag = new InMemoryEmbeddingStore<>();

        this.magasinReseaux = new InMemoryEmbeddingStore<>();

        this.modeleEmbedding = new AllMiniLmL6V2EmbeddingModel();

        this.parser = new ApacheTikaDocumentParser();
    }

    public void ajouterRag(InputStream inputStream) {

        Document document = parser.parse(inputStream);

        indexerDocument(document, magasinRag);
    }

    public void ajouterReseaux(InputStream inputStream) {

        Document document = parser.parse(inputStream);

        indexerDocument(document, magasinReseaux);
    }

    private void indexerDocument(
            Document document,
            EmbeddingStore<TextSegment> magasin
    ) {

        EmbeddingStoreIngestor ingestor =
                EmbeddingStoreIngestor.builder()
                        .documentSplitter(DocumentSplitters.recursive(300, 30))
                        .embeddingModel(modeleEmbedding)
                        .embeddingStore(magasin)
                        .build();

        ingestor.ingest(document);
    }

    public EmbeddingStore<TextSegment> getMagasinRag() {
        return magasinRag;
    }

    public EmbeddingStore<TextSegment> getMagasinReseaux() {
        return magasinReseaux;
    }

    public EmbeddingModel getModeleEmbedding() {
        return modeleEmbedding;
    }
}
