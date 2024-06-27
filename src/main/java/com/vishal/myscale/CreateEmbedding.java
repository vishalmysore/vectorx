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
    public static Float[] embedAsObject(String str) {
        float[] embQuery = embed(str);
        Float[] embQueryObj = new Float[embQuery.length];
        for (int i = 0; i < embQuery.length; i++) {
            embQueryObj[i] = embQuery[i];
        }
        return embQueryObj;
    }

}
