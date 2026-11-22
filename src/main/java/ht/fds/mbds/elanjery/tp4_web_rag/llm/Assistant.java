
package ht.fds.mbds.elanjery.tp4_web_rag.llm;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    String chat(String message);
}
