
package ht.fds.mbds.elanjery.tp4_web_rag;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;

@Singleton
@Startup
public class InitialiserDocuments {

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    @PostConstruct
    public void chargerDocuments() {

        chargerPDF("rag.pdf", true);

        chargerPDF("reseaux-informatiques.pdf", false);
    }

    private void chargerPDF(String nomFichier, boolean estRag) {

        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream(nomFichier)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Document introuvable : " + nomFichier
                );
            }

            if (estRag) {
                magasinEmbeddings.ajouterRag(inputStream);
            } else {
                magasinEmbeddings.ajouterReseaux(inputStream);
            }

            System.out.println(
                    "Document indexe avec succes : " + nomFichier
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erreur lors du chargement de " + nomFichier, e
            );
        }
    }
}
