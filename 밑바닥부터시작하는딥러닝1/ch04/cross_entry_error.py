import numpy as np

def cross_entropy_error(y, t):
    delta = 1e-7 # 1*10^7
    return -np.sum(t * np.log(y + delta))

t = [0, 0, 1, 0, 0, 0, 0, 0, 0, 0]
y = [0.1, 0.05, 0.6, 0.0, 0.05, 0.1, 0.0, 0.1, 0.0, 0.0]

e1 = cross_entropy_error(np.array(y), np.array(t))
print(e1)

y = [0.1, 0.05, 0.1, 0.0, 0.05, 0.1, 0.0, 0.6, 0.0, 0.0]
e2 = cross_entropy_error(np.array(y), np.array(t))
print(e2)
