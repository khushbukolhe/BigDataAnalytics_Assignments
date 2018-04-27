import tensorflow as tf
from os import environ
from flask import Flask, jsonify, render_template, request
from flask_cors import CORS, cross_origin
import base64

environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

# webapp
app = Flask(__name__)


# image_path = sys.argv[1]


@app.route('/api/predict', methods=['POST'])
@cross_origin()
def predict():
    print("Connection created: ", request.environ['REMOTE_ADDR'])
    #data = request.get_json()['imageBase64']
    data = request.values['imageBase64']
    # Since python-3 has unicode string, the bytes datatype was introduced. You have to convert your string to a bytearray,
    # e.g. by using b = bytes(mystring, 'utf-8'), and then using b for the encoding: EncodedString = base64.b64encode(b),
    # which will return a bytearray
    b = bytes(data, 'utf-8')
    with open("imageToPredict.jpeg", "wb") as fh:
        fh.write(base64.decodebytes(b))
    # image_data = re.sub('^data:image/.+;base64,', '', image_b64).decode('base64')

    image_path = 'imageToPredict.jpeg'
    # Read in the image_data
    image_data = tf.gfile.FastGFile(image_path, 'rb').read()

    # Loads label file, strips off carriage return
    label_lines = [line.rstrip() for line
                   in tf.gfile.GFile("data/output_labels.txt")]

    # Unpersists graph from file
    with tf.gfile.FastGFile("data/output_graph.pb", 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')

    with tf.Session() as sess:
        # Feed the image_data as input to the graph and get first prediction
        softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')

        predictions = sess.run(softmax_tensor, \
                               {'DecodeJpeg/contents:0': image_data})

        # Sort to show labels of first prediction in order of confidence
        top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]
        output_string = []
        output_score = []
        for node_id in top_k:
            human_string = label_lines[node_id]
            score = predictions[0][node_id]
            output_string.append(human_string)
            output_score.append(score)
            print('%s (score = %.5f)' % (human_string, score))
    return jsonify(output_string[0])
    # return jsonify(results=[output_string])


@app.route('/')
def main():
    return render_template('index.html')


# set FLASK_APP=label_image.py
# python -m flask run --host=0.0.0.0
# python -m flask run
#  * Running on your ip address

# or
if __name__ == '__main__':
    app.debug = True
    #app.run(host='0.0.0.0')
    app.run()

# and use
# python label_image.py
