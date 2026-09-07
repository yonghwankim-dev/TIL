import sys, os
sys.path.append(os.path.join(os.path.dirname(__file__), '..')) # 부모 디렉토리 경로 포함
import numpy as np
from dataset.mnist import load_mnist
from PIL import Image

def img_show(img):
    pil_img = Image.fromarray(np.uint8(img))
    pil_img.show()

(x_train, t_train), (x_test, t_test) = \
    load_mnist(flatten = True, normalize = False)

img = x_train[0]
label = t_train[0]
print(f"label={label}")

print(f"img.shape={img.shape}")
img = img.reshape(28,28) # 원래 이미지의 모양으로 변형
print(f"img.shape={img.shape}")

img_show(img)

