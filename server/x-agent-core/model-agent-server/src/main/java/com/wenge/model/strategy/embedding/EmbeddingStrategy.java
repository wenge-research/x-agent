package com.wenge.model.strategy.embedding;

import java.util.List;

public interface EmbeddingStrategy {

    List<Double> embedding(String text);
}
