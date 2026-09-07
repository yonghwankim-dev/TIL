import numpy as np
def relu(x):
    return np.maximum(0, x)

print(f"x=-1, relu(x)={relu(-1)}")
print(f"x=0, relu(x)={relu(0)}")
print(f"x=1, relu(x)={relu(1)}")
print(f"x=2, relu(x)={relu(2)}")



