import os

class Config:
    OPENAI_API_KEY = os.getenv('OPENAI_API_KEY')
    FLASK_API_KEY = os.getenv('FLASK_API_KEY')
    MODEL_ENGINE    = os.getenv('OPENAI_MODEL', 'gpt-4')
    RAG_INDEX_PATH  = os.getenv('RAG_INDEX_PATH', 'data/faiss.index')
    RAG_DOCS_PATH   = os.getenv('RAG_DOCS_PATH', 'data/docs.pkl')
    OPENAI_TIMEOUT  = int(os.getenv('OPENAI_TIMEOUT', '30'))
    SSL_CERT        = os.getenv('SSL_CERT', 'cert.pem')
    SSL_KEY         = os.getenv('SSL_KEY', 'key.pem')