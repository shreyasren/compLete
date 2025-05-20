import os
import logging
from flask import Flask, request, jsonify
from jsonschema import validate, ValidationError
import openai
from rag_engine import RAGEngine
from utils import build_prompt, parse_suggestions, execute_java_command

# Configure logging
default_level = logging.INFO
logging.basicConfig(level=os.getenv('LOG_LEVEL', default_level))
logger = logging.getLogger("compLete.middleware")

# Load OpenAI API key
openai.api_key = os.getenv('OPENAI_API_KEY')
MODEL_ENGINE = os.getenv('OPENAI_MODEL', 'gpt-4')

# Initialize RAG engine
RAG_INDEX = os.getenv('RAG_INDEX_PATH', 'data/faiss.index')
RAG_DOCS = os.getenv('RAG_DOCS_PATH', 'data/docs.pkl')
rag = RAGEngine(RAG_INDEX, RAG_DOCS)

# JSON schema for validation
command_schema = {
    "type": "object",
    "properties": {
        "model_context": {"type": "string"},
        "request": {"type": "string"}
    },
    "required": ["model_context", "request"]
}

app = Flask(__name__)

@app.route('/complete_model', methods=['POST'])
def complete_model():
    try:
        data = request.get_json(force=True)
        validate(instance=data, schema=command_schema)
    except ValidationError as e:
        logger.error("JSON validation error: %s", e)
        return jsonify({"error": str(e)}), 400

    model_ctx = data['model_context']
    user_req = data['request']

    # Retrieve domain insights
    references = rag.retrieve(user_req + " " + model_ctx, top_k=5)

    # Build prompt for ChatGPT
    messages = build_prompt(model_ctx, user_req, references)
    logger.debug("Prompt messages: %s", messages)

    # Call ChatGPT
    try:
        response = openai.ChatCompletion.create(
            model=MODEL_ENGINE,
            messages=messages,
            temperature=0.2,
            max_tokens=1024
        )
    except Exception as e:
        logger.exception("OpenAI API call failed")
        return jsonify({"error": str(e)}), 502

    ai_content = response.choices[0].message.content
    logger.info("AI response: %s", ai_content)

    # Parse suggestions
    suggestions = parse_suggestions(ai_content)
    return jsonify(suggestions)

@app.route('/health', methods=['GET'])
def health():
    return jsonify({"status": "ok"}), 200

if __name__ == '__main__':
    port = int(os.getenv('FLASK_PORT', 5000))
    app.run(host='0.0.0.0', port=port)