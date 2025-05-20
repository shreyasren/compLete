
import pytest
from app.rag_engine import RAGEngine

@pytest.fixture
def rag():
    return RAGEngine()

def test_retrieve(rag):
    docs = rag.retrieve("test query", top_k=2)
    assert isinstance(docs, list)
    assert len(docs) <= 2
