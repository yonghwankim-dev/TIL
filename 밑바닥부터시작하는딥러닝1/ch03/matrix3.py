import numpy as np

A = np.array([[1,2,3], [4,5,6]])
C = np.array([[1,2], [3,4]])

print(A.shape)
print(C.shape)

print(np.dot(A,C))
