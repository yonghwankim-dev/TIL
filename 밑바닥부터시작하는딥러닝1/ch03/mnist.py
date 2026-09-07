import sys, os
sys.path.append(os.path.join(os.path.dirname(__file__), '..')) # 부모 디렉토리 경로 포함
from dataset.mnist import load_mnist

(x_train, t_train), (x_test, t_test) = \
    load_mnist(flatten = True, normalize = False)

# 각 데이터의 형상 출력
print(f"x_train.shape={x_train.shape}")
print(f"t_train.shape={t_train.shape}")
print(f"x_test.shape={x_test.shape}")
print(f"t_test.shape={t_test.shape}")


