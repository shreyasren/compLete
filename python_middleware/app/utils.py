import os
import json
import subprocess

JAVA_JAR_PATH = os.getenv('JAVA_JAR_PATH', 'java_plugin/SysMLModelBuilder.jar')
JAVA_CMD = os.getenv('JAVA_CMD', 'java')


def build_prompt(model_context: str, user_request: str, references: list) -> list:
    """
    Build a ChatGPT prompt with system/user messages, model context, user request, and references.
    """
    messages = [
        {"role": "system", "content": (
            "You are an expert systems engineer and SysML modeling assistant. "
            "Use the provided model context and references to suggest model changes. "
            "Output in JSON format with keys: new_blocks, new_connections."
        )},
        {"role": "user", "content": f"Model Context:\n{model_context}"},
        {"role": "user", "content": f"Request: {user_request}"}
    ]
    for idx, ref in enumerate(references, start=1):
        messages.append({"role": "user", "content": f"Reference {idx}: {ref}"})
    messages.append({"role": "user", "content": (
        "Format your answer strictly as JSON: {\"new_blocks\": [...], \"new_connections\": [...]}" )})
    return messages


def parse_suggestions(text: str) -> dict:
    """
    Parse AI output into a suggestions dict. Fallback to raw text if JSON fails.
    """
    try:
        return json.loads(text)
    except json.JSONDecodeError:
        return {"raw_suggestions": text}


def execute_java_command(args: list) -> str:
    """Invoke the Java backend via subprocess."""
    cmd = [JAVA_CMD, '-jar', JAVA_JAR_PATH] + args
    proc = subprocess.run(cmd, capture_output=True, text=True)
    if proc.returncode != 0:
        raise RuntimeError(f"Java command failed: {proc.stderr}")
    return proc.stdout