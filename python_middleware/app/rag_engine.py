import os
import pickle
import faiss
from sentence_transformers import SentenceTransformer

class RAGEngine:
    """
    Simple RAG engine using FAISS and SentenceTransformers embeddings.
    """
    def __init__(self, index_path: str, docs_path: str):
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.index = faiss.read_index(index_path)
        with open(docs_path, 'rb') as f:
            self.docs = pickle.load(f)

    def retrieve(self, query: str, top_k: int = 5) -> list:
        # Encode query
        q_emb = self.model.encode([query])
        # Search index
        distances, indices = self.index.search(q_emb, top_k)
        # Return top documents
        return [self.docs[i] for i in indices[0]]