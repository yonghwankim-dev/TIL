import sys, os
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
import numpy as np
from dataset.mnist import load_mnist

(x_train, t_train), (x_test, t_test) = \
    load_mnist(normalize=True, one_hot_label=True)

print(f"x_train.shape={x_train.shape}") # 28x28 이미지
print(f"t_train.shape={t_train.shape}")

train_size = x_train.shape[0]
batch_size = 10
batch_mask = np.random.choice(train_size, batch_size)
print(f"batch_mask={batch_mask}")

x_batch = x_train[batch_mask]
t_batch = t_train[batch_mask]

# 정답 레이블(t)이 원-핫 인코딩이 아닌 숫자 레이블로 주어지는 경우
def cross_entropy_error(y, t):
    if y.ndim == 1:
        t = t.reshape(1, t.size)
        y = y.reshape(1, y.size)
    batch_size = y.shape[0]
    return -np.sum(np.log(y[np.arrange(batch_size), t] + 1e-7)) / batch_size

