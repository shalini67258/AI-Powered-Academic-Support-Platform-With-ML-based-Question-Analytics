from flask import Flask, request, jsonify
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

model = SentenceTransformer('all-MiniLM-L6-v2')

SIMILARITY_THRESHOLD = 0.85

@app.route("/check", methods=["POST"])
def check_similarity():

    data = request.json

    question1 = data.get("question1")
    question2 = data.get("question2")

    if not question1 or not question2:
        return jsonify({"error": "Two questions required"}), 400

    embeddings = model.encode([question1, question2])

    similarity_score = cosine_similarity(
        [embeddings[0]],
        [embeddings[1]]
    )[0][0]

    print("Similarity Score:", similarity_score)

    if similarity_score >= SIMILARITY_THRESHOLD:
        return jsonify({"similar": True})
    else:
        return jsonify({"similar": False})


if __name__ == "__main__":
    app.run(port=5000, debug=True)