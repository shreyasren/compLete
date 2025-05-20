import os
import pickle

try:
    import faiss
    from sentence_transformers import SentenceTransformer
except Exception:  # pragma: no cover - optional deps may be missing
    faiss = None
    SentenceTransformer = None

class RAGEngine:
    """Lightweight retrieval helper. Falls back to empty results when no index is available."""

    def __init__(self, index_path: str | None = None, docs_path: str | None = None):
        self.index_path = index_path or os.getenv("RAG_INDEX_PATH", "data/faiss.index")
        self.docs_path = docs_path or os.getenv("RAG_DOCS_PATH", "data/docs.pkl")

        self.docs = []
        self.model = None
        self.index = None
        if faiss and SentenceTransformer and os.path.exists(self.index_path):
            self.model = SentenceTransformer("all-MiniLM-L6-v2")
            self.index = faiss.read_index(self.index_path)
            if os.path.exists(self.docs_path):
                with open(self.docs_path, "rb") as f:
                    self.docs = pickle.load(f)

    def retrieve(self, query: str, top_k: int = 5) -> list:
        if not self.index or not self.model or not self.docs:
            return []
        q_emb = self.model.encode([query])
        distances, indices = self.index.search(q_emb, top_k)
        return [self.docs[i] for i in indices[0] if i < len(self.docs)]
