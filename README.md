# **compLete: AI-Driven SysML Modeling Assistant**

**compLete** is an integrated AI-powered framework that accelerates and automates SysML model development, validation, and completion for automotive systems-engineering projects. It combines:

* **Python middleware** (Flask + RAG + OpenAI) that exposes a secure REST API to interpret partial SysML models and user requests, retrieve domain knowledge, and generate structured model-edit suggestions.
* **Java MagicDraw / Cameo plugin** (via the Model Context Protocol) that invokes the middleware, parses AI-generated suggestions, and applies them directly to your SysML model—blocks, ports, connectors, diagrams—in a single undoable session.

---

## 🚀 Key Features

| Capability                          | Description                                                                                                                                 |
| ----------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| **Model Validation**                | *Planned*: automatically check requirements coverage (e.g., ISO 26262, ASPICE) and flag unsatisfied requirements or missing elements. |
| **Model Completion**                | AI-driven addition of missing Blocks, Ports, Connectors, and Requirement allocations from partial models and textual queries.               |
| **Domain Grounding**                | Retrieval-Augmented Generation (RAG) using FAISS + SentenceTransformer over a curated corpus of standards, past models, and best practices. |
| **Secure & Containerized**          | Token-based authentication, TLS on every API call, Docker/Docker-Compose for reproducible deployment.                                       |
| **Seamless Tool-chain Integration** | From MagicDraw/Cameo: **Tools → AI ▶ Complete Model**, then review & apply suggestions with full undo/redo support.                         |

---

## 📦 Repository Layout

```text
compLete/                     ← Git repo root
├── .gitignore                ← ignores build artifacts, venv, IDE files
├── README.md                 ← this document
│
├── python_middleware/        ← AI API service
│   ├── docker-compose.yml    ← middleware container stack
│   ├── app/
│   │   ├── Dockerfile                ← middleware image
│   │   ├── middleware.py             ← Flask REST API (/complete_model, /health)
│   │   ├── rag_engine.py             ← FAISS + SentenceTransformer retrieval
│   │   ├── utils.py                  ← prompt builder, JSON parser, Java-CLI invoker
│   │   ├── auth.py                   ← token-based HTTPBearer auth
│   │   └── config.py                 ← env-based configuration
│   ├── tests/                        ← pytest units
│   │   ├── test_middleware.py
│   │   └── test_rag.py
│   ├── requirements.txt
│   └── data/                         ← FAISS index & documents
│
└── java_plugin/              ← MagicDraw/Cameo plugin
    ├── build.gradle
    ├── settings.gradle
    ├── gradlew*               ← Gradle wrapper scripts
    ├── gradle/                ← wrapper JAR + properties
    ├── src/
    │   ├── main/java/com/complete/plugin/
    │   │   ├── CompLetePlugin.java        ← plugin entry point
    │   │   ├── AppConfig.java
    │   │   ├── ConfigLoader.java
    │   │   ├── SysMLModelService.java     ← model I/O, HTTP calls
    │   │   ├── HttpClientHelper.java
    │   │   └── ModelDiagramHelper.java
    │   └── resources/application.properties
    └── src/test/java/com/complete/plugin/
        └── ConfigLoaderTest.java
```
Note: the middleware's Dockerfile resides in `python_middleware/app/`.

---

## 🔧 Prerequisites

* **Python 3.10 +**
* **Docker & Docker-Compose** (for containerised middleware)
* **OpenAI API key** (GPT-4 or equivalent access)
* **Java JDK 17 +** (for Gradle wrapper & plugin)
* **MagicDraw/Cameo Systems Modeler** (with MCP / Teamwork Cloud SDK)

---

## ⚙️ Setup & Installation

### 1 · Launch the Python middleware

```bash
cd python_middleware

# Copy your FAISS index & docs into ./data/
# Create cert.pem + key.pem for TLS

cat <<EOF > .env
OPENAI_API_KEY=sk-...
FLASK_API_KEY=<secure_token>
EOF

docker-compose up --build
```

*Service starts at* **`https://localhost:5000`**

```bash
curl -k https://localhost:5000/health
# {"status":"ok"}
```

---

### 2 · Build & install the Java plugin

```bash
cd java_plugin
./gradlew clean build          # downloads Gradle, compiles & tests

# Copy plugin JAR into MagicDraw's plugins folder
cp build/libs/compLete-1.0.0.jar \
   "/Applications/Cameo Systems Modeler/plugins/"
```

Adjust `application.properties` inside the JAR (or re-package) to set the middleware URL and API key.

---

### 3 · Use compLete inside MagicDraw

1. Open your SysML model.
2. Navigate to **Tools → AI ▶ Complete Model**.
3. Review AI suggestions, click **Apply** to insert or **Cancel** to discard.
4. Save the model.

---

## 🧪 Testing

```bash
# Python tests
cd python_middleware
pytest

# Java tests
cd ../java_plugin
./gradlew test
```

---

## 🚢 Deployment

| Environment     | Notes                                                                         |
| --------------- | ----------------------------------------------------------------------------- |
| **On-Premises** | Host the Python service behind corporate TLS termination and firewalls.       |
| **Cloud / K8s** | Deploy the container to Kubernetes or Docker-Compose; secure via ingress TLS. |

Configure the Java plugin’s `application.properties` to point to the correct middleware endpoint.

---

## 🛠️ Configuration

| Variable                    | Purpose                          | Default                |
| --------------------------- | -------------------------------- | ---------------------- |
| `OPENAI_API_KEY`            | OpenAI key for LLM calls         | —                      |
| `FLASK_API_KEY`             | Bearer token for middleware auth | —                      |
| `OPENAI_MODEL`              | Model name (e.g., `gpt-4`)       | `gpt-4`                |
| `RAG_INDEX_PATH`            | FAISS index file                 | `data/faiss.index`     |
| `RAG_DOCS_PATH`             | Pickled docs list                | `data/docs.pkl`        |
| `OPENAI_TIMEOUT`            | LLM request timeout (s)          | `30`                   |
| `SSL_CERT` / `SSL_KEY`      | TLS cert/key paths               | `cert.pem` / `key.pem` |
| `middleware.url`            | Python service URL (plugin)      | —                      |
| `middleware.apiKey`         | Token plugin → middleware        | —                      |
| `middleware.connectTimeout` | HTTP connect timeout (ms)        | `5000`                 |
| `middleware.readTimeout`    | HTTP read timeout (ms)           | `30000`                |

---

## 🤝 Contributing

1. **Fork** the repo & clone.
2. `git checkout -b feat/my-feature`
3. Implement & test.
4. `git commit -m "feat: add …"`
5. `git push` and open a **Pull Request**.

---

## 📜 License

Released under the **MIT License**. See [`LICENSE`](LICENSE) for details.

---

## 📚 References

* ISO 26262 – Functional safety for automotive E/E systems
* SysML v1.x / v2 – OMG systems-modeling languages
* FAISS & SentenceTransformers – semantic retrieval stack
* OpenAI GPT-4 API – LLM engine
* MagicDraw/Cameo OpenAPI & MCP – tool integration layer

---

> **compLete** — Your AI co-engineer for robust, complete, and compliant SysML models.
