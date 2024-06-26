package com.vishal.myscale;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

public class CreateEmbedding {
    public static float[] embed(String str) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        TextSegment segment1 = TextSegment.from(str);
        Embedding embedding1 = embeddingModel.embed(segment1).content();
        return embedding1.vector();
    }
}
