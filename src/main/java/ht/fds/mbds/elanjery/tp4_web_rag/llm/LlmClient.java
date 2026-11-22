
package ht.fds.mbds.elanjery.tp4_web_rag.llm;

import ht.fds.mbds.elanjery.tp4_web_rag.MagasinEmbeddings;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.service.AiServices;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.io.Serializable;
import java.util.Map;

@Dependent
public class LlmClient implements Serializable {

    private Assistant assistant;
    private ChatMemory chatMemory;

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    @PostConstruct
    public void initialiser() {

        // 1. Initialiser le modèle Gemini
        String cle = System.getenv("GEMINI_KEY");

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName("gemini-3.5-flash")
                .build();

        // 2. Créer le retriever du premier PDF
        ContentRetriever retrieverRag =
                EmbeddingStoreContentRetriever.builder()
                        .displayName("rag.pdf")
                        .embeddingStore(magasinEmbeddings.getMagasinRag())
                        .embeddingModel(magasinEmbeddings.getModeleEmbedding())
                        .maxResults(3)
                        .minScore(0.5)
                        .build();

        // 3. Créer le retriever du deuxième PDF
        ContentRetriever retrieverReseaux =
                EmbeddingStoreContentRetriever.builder()
                        .displayName("reseaux-informatiques.pdf")
                        .embeddingStore(magasinEmbeddings.getMagasinReseaux())
                        .embeddingModel(magasinEmbeddings.getModeleEmbedding())
                        .maxResults(3)
                        .minScore(0.5)
                        .build();

        // 4. Décrire les documents pour le routeur
        Map<ContentRetriever, String> descriptions = Map.of(

                retrieverRag,
                "Document rag.pdf : traite du RAG, des embeddings, "
                        + "de la recherche vectorielle, des chunks, "
                        + "de Tika et de LangChain4j.",

                retrieverReseaux,
                "Document reseaux-informatiques.pdf : traite des réseaux "
                        + "informatiques, du modèle OSI, du protocole TCP/IP, "
                        + "des couches réseau et des protocoles."
        );

        // 5. Initialiser la mémoire conversationnelle
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        // 6. Transformer les questions selon le contexte
        CompressingQueryTransformer queryTransformer =
                CompressingQueryTransformer.builder()
                        .chatModel(model)
                        .build();

        // 7. Créer le routeur intelligent
        LanguageModelQueryRouter queryRouter =
                new LanguageModelQueryRouter(model, descriptions);

        // 8. Construire le pipeline RAG
        DefaultRetrievalAugmentor retrievalAugmentor =
                DefaultRetrievalAugmentor.builder()
                        .queryTransformer(queryTransformer)
                        .queryRouter(queryRouter)
                        .build();

        // 9. Construire l'assistant
        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .retrievalAugmentor(retrievalAugmentor)
                .build();

        System.out.println(
                "[RAG] Assistant initialise avec LanguageModelQueryRouter."
        );

        System.out.println(
                "[RAG] CompressingQueryTransformer initialise."
        );
    }

    public void setSystemRole(String role) {
        this.chatMemory.clear();
        this.chatMemory.add(
                dev.langchain4j.data.message.SystemMessage.from(role)
        );
    }

    public String envoyerRequete(String prompt) {
        System.out.println("[QUESTION] " + prompt);
        return this.assistant.chat(prompt);
    }
}
