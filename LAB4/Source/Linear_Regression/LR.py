import tensorflow as tf
import matplotlib.pyplot as plt
from tensorflow.examples.tutorials.mnist import input_data

VQA = input_data.read_data_sets("MNIST_data/", one_hot=True, validation_size=0)

# Define hyper-parameters
learning_rate = 0.001
batch_size = 30
n_epochs = 50

# Create placeholders
X = tf.placeholder(tf.float32, [batch_size, 784], name="image")
Y = tf.placeholder(tf.float32, [batch_size, 10], name="label")

# Create weights and bias
w = tf.Variable(tf.random_normal(shape=[784, 10], stddev=0.01), name="weights")
b = tf.Variable(tf.zeros([1, 10]), name='bias')

# calculate scores
y_pred = tf.matmul(X, w) + b

# cost function and loss
entropy = tf.nn.softmax_cross_entropy_with_logits(logits=y_pred, labels=Y)
loss = tf.reduce_mean(entropy)

# Define optimizer
optimizer = tf.train.GradientDescentOptimizer(learning_rate=learning_rate).minimize(loss)

# Run optimization and test
loss_history = []
acc_history = []
init = tf.global_variables_initializer()
with tf.Session() as sess:
    sess.run(init)
    n_batches = int(VQA.train.num_examples / batch_size)
    for i in range(n_epochs):
        for _ in range(n_batches):
            X_batch, Y_batch = VQA.train.next_batch(batch_size)
            _, loss_value = sess.run([optimizer, loss], feed_dict={X: X_batch, Y: Y_batch})
        loss_history.append(loss_value)

    # Test the model
    n_batches = int(VQA.test.num_examples / batch_size)
    total_correct_preds = 0
    correct_preds_array = []

    for k in range(n_batches):
        X_batch, Y_batch = VQA.test.next_batch(batch_size)
        test_batch = sess.run(y_pred, feed_dict={X: X_batch, Y: Y_batch})
        preds = tf.nn.softmax(test_batch)
        correct_preds = tf.equal(tf.argmax(preds, 1), tf.argmax(Y_batch, 1))
        correct_preds_array.append(sess.run(correct_preds))
        accuracy = tf.reduce_sum(tf.cast(correct_preds, tf.float32))
        total_correct_preds += sess.run(accuracy)

# Print training cost
print("\n\n*** Linear Regression on VQA Project Dataset ***\n\n")

for i in range(n_epochs):
    print("Epoch = {0}, Training cost = {1:.2f}".format(i+1, loss_history[i]))

plt.plot(loss_history, '-o', label='Cost value')
plt.title('Training Cost')
plt.xlabel('Epoch')
plt.ylabel('Loss Value')
plt.legend(ncol=2)
plt.show()
