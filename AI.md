# Utilisation de l'IA – TP4 Application Web

## 1. Objectif de l'utilisation de l'IA

ChatGPT a été utilisé comme assistant pédagogique et technique pendant la réalisation de l'application Web du TP4.

L'objectif était de comprendre comment reprendre l'application Web du TP2 et y intégrer un système RAG avec LangChain4j, tout en ajoutant deux fonctionnalités étudiées dans les tests précédents :

- Test 2 : `CompressingQueryTransformer`.
- Test 3 : `LanguageModelQueryRouter`.

L'utilisation de l'IA m'a permis de comprendre les différentes étapes d'intégration, de résoudre les erreurs rencontrées et de vérifier progressivement le fonctionnement de l'application.

---

## 2. Reprise et configuration de l'application Web

### Problème rencontré

Il fallait créer une nouvelle application Jakarta EE Web à partir de l'application du TP2, tout en conservant son fonctionnement initial et en adaptant les dépendances et les packages au nouveau projet.

### Raison de l'utilisation de l'IA

J'ai utilisé ChatGPT pour m'accompagner dans la configuration du projet, l'organisation des packages et l'intégration des dépendances LangChain4j dans le fichier `pom.xml`.

### Prompts utilisés

- Comment reprendre le code de mon application Web du TP2 dans un nouveau projet Jakarta EE 11 avec Payara 7 ?
- Quelles dépendances LangChain4j dois-je ajouter dans mon fichier pom.xml ?
- Comment vérifier que mon application fonctionne correctement après la migration ?

### Réponse de l'IA – Extrait pertinent

ChatGPT m'a guidée dans les étapes suivantes :

1. Création du nouveau projet Jakarta EE Web Application.
2. Copie des classes Java, des pages JSF, des ressources et du fichier `web.xml`.
3. Adaptation des packages.
4. Ajout des dépendances nécessaires dans `pom.xml`.
5. Compilation avec Maven et déploiement sur Payara 7.

### Ce que j'ai appris

J'ai compris comment reprendre une application existante dans un nouveau projet Jakarta EE, comment organiser les packages et comment gérer les dépendances Maven.

---

## 3. Intégration des deux documents PDF

### Problème rencontré

L'application devait utiliser au minimum deux fichiers PDF pour effectuer la recherche documentaire.

Il fallait charger les documents, extraire leur contenu, calculer leurs embeddings et stocker les représentations vectorielles afin de permettre la recherche sémantique.

### Raison de l'utilisation de l'IA

J'ai utilisé ChatGPT pour comprendre comment réutiliser la classe `MagasinEmbeddings` développée dans les tests précédents et comment adapter son fonctionnement à l'application Web.

### Prompts utilisés

- Comment intégrer deux fichiers PDF dans mon application Web RAG ?
- Comment utiliser deux `InMemoryEmbeddingStore` distincts ?
- Comment initialiser automatiquement les documents au démarrage de l'application avec Jakarta EE ?

### Réponse de l'IA – Extrait pertinent

La solution proposée repose sur :

- La classe `MagasinEmbeddings`, qui contient deux magasins d'embeddings distincts :
    - `getMagasinRag()` pour le document `rag.pdf`.
    - `getMagasinReseaux()` pour le document `reseaux-informatiques.pdf`.
- Le modèle `AllMiniLmL6V2EmbeddingModel` pour transformer les segments de texte en vecteurs.
- La classe `InitialiserDocuments`, qui charge et indexe les deux documents au démarrage de l'application.

L'indexation des documents a été confirmée dans les logs de Payara.

### Ce que j'ai appris

J'ai compris l'intérêt d'utiliser plusieurs magasins d'embeddings pour séparer les connaissances provenant de différentes sources documentaires.

J'ai également appris à initialiser automatiquement les ressources nécessaires au RAG au démarrage d'une application Jakarta EE.

---

## 4. Intégration du Test 2 – CompressingQueryTransformer

### Problème rencontré

Dans une conversation, une question complémentaire peut dépendre d'une question précédente.

Par exemple, après avoir demandé une explication sur le modèle OSI, l'utilisateur peut simplement demander : « Quel est son rôle ? ».

Cette deuxième question manque de contexte lorsqu'elle est utilisée seule pour effectuer une recherche documentaire.

### Raison de l'utilisation de l'IA

J'ai utilisé ChatGPT pour comprendre comment intégrer `CompressingQueryTransformer` dans le système RAG de l'application Web, sans modifier le fonctionnement de l'interface JSF.

### Prompts utilisés

- Comment intégrer `CompressingQueryTransformer` dans mon `LlmClient` ?
- Comment l'utiliser avec `DefaultRetrievalAugmentor` ?
- Comment conserver l'historique de conversation avec `MessageWindowChatMemory` ?

### Réponse de l'IA – Extrait pertinent

Le composant `CompressingQueryTransformer` permet de transformer une question en tenant compte du contexte conversationnel afin de produire une requête plus adaptée à la recherche documentaire.

Son intégration se fait dans le `DefaultRetrievalAugmentor` :

```java
CompressingQueryTransformer queryTransformer =
        CompressingQueryTransformer.builder()
                .chatModel(model)
                .build();

DefaultRetrievalAugmentor retrievalAugmentor =
        DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .queryRouter(queryRouter)
                .build();
```

Le composant `MessageWindowChatMemory` est également utilisé pour conserver les derniers messages de la conversation.

### Ce que j'ai appris

J'ai compris la différence entre la mémoire conversationnelle et la transformation de requêtes.

La mémoire conserve les échanges précédents, tandis que le `CompressingQueryTransformer` cherche à reformuler la question pour améliorer la recherche documentaire.

---

## 5. Intégration du Test 3 – LanguageModelQueryRouter

### Problème rencontré

L'application utilise deux documents PDF contenant des sujets différents.

Il fallait éviter de rechercher systématiquement dans les deux documents et permettre au système de sélectionner automatiquement la source documentaire adaptée à la question.

### Raison de l'utilisation de l'IA

J'ai utilisé ChatGPT pour comprendre comment intégrer `LanguageModelQueryRouter` avec plusieurs `ContentRetriever` dans le système RAG.

### Prompts utilisés

- Comment intégrer `LanguageModelQueryRouter` dans mon application Web avec deux documents PDF ?
- Comment associer chaque `ContentRetriever` à une description ?
- Comment configurer `DefaultRetrievalAugmentor` pour utiliser le routeur ?

### Réponse de l'IA – Extrait pertinent

La solution consiste à créer deux `EmbeddingStoreContentRetriever`, chacun associé à son propre magasin d'embeddings.

Une description est ensuite associée à chaque retriever :

```java
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

LanguageModelQueryRouter queryRouter =
        new LanguageModelQueryRouter(model, descriptions);
```

Le routeur utilise le modèle de langage pour choisir le retriever correspondant à la question.

### Ce que j'ai appris

J'ai compris comment utiliser un routeur intelligent pour sélectionner automatiquement une source documentaire en fonction du sujet de la question.

J'ai également compris l'intérêt de fournir des descriptions précises des différents retrievers.

---

## 6. Intégration du RAG dans l'application Web

### Problème rencontré

Il fallait connecter les composants RAG à l'interface Web afin que l'utilisateur puisse poser des questions et recevoir des réponses générées à partir des documents PDF.

### Raison de l'utilisation de l'IA

J'ai utilisé ChatGPT pour comprendre comment relier le service LangChain4j à l'interface JSF tout en conservant la mémoire conversationnelle et le rôle système.

### Prompts utilisés

- Comment intégrer le système RAG dans ma classe `LlmClient` ?
- Comment connecter `Assistant` à `AiServices` ?
- Comment utiliser `MessageWindowChatMemory` avec mon application JSF ?
- Comment transmettre les questions de `Bb.java` au service LLM ?

### Réponse de l'IA – Extrait pertinent

L'interface `Assistant` définit la méthode de conversation :

```java
public interface Assistant {
    String chat(String message);
}
```

La classe `LlmClient` configure le modèle Gemini, la mémoire conversationnelle et le `DefaultRetrievalAugmentor`.

L'assistant est construit avec `AiServices` :

```java
this.assistant = AiServices.builder(Assistant.class)
        .chatModel(model)
        .chatMemory(chatMemory)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
```

La classe `Bb.java` permet ensuite de transmettre les questions de l'utilisateur à `LlmClient`.

### Ce que j'ai appris

J'ai compris le rôle de chaque composant dans l'architecture de l'application :

- JSF : interface utilisateur.
- `Bb.java` : gestion des interactions.
- `LlmClient.java` : configuration et communication avec le modèle.
- `Assistant.java` : définition de l'interface de conversation.
- `MagasinEmbeddings.java` : gestion des magasins d'embeddings.
- `InitialiserDocuments.java` : indexation des documents au démarrage.

---

## 7. Tests et vérifications

ChatGPT m'a également accompagnée dans l'analyse des erreurs Maven, des problèmes de déploiement Payara et des erreurs retournées par l'API Gemini.

Les vérifications réalisées ont permis de constater :

- La compilation Maven avec succès.
- Le démarrage de l'application sur Payara 7.
- L'indexation réussie des deux documents PDF.
- L'initialisation de `CompressingQueryTransformer`.
- L'initialisation de `LanguageModelQueryRouter`.
- Une réponse pertinente à une question sur le RAG.
- Une réponse à une question sur TCP/IP provenant du document consacré aux réseaux.

Certains tests complémentaires n'ont pas pu être terminés en raison de la limite de quota gratuit de l'API Gemini (erreur 429 – `RESOURCE_EXHAUSTED`). Leur validation reste à effectuer après le renouvellement du quota.

---

## 8. Bilan personnel

Grâce à ce travail, j'ai approfondi ma compréhension de l'intégration du RAG dans une application Web Jakarta EE.

J'ai appris à :

- Réutiliser une application Web existante dans un nouveau projet.
- Charger et indexer plusieurs documents PDF.
- Utiliser des magasins d'embeddings distincts.
- Intégrer un `CompressingQueryTransformer`.
- Utiliser un `LanguageModelQueryRouter`.
- Configurer un `DefaultRetrievalAugmentor`.
- Relier LangChain4j, Gemini et JSF.
- Analyser les erreurs de compilation, de déploiement et d'API.

ChatGPT a été utilisé comme outil d'accompagnement pour comprendre les concepts, proposer des solutions et résoudre les difficultés rencontrées. Les modifications et les tests ont été réalisés progressivement dans mon propre projet.

**Dépôt GitHub :**  
https://github.com/HalimaElanjery/TP4_Web_RAG

**Outil utilisé :** ChatGPT