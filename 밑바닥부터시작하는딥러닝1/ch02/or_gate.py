import numpy as np
def OR(x1, x2):
    x = np.array([x1, x2])
    w = np.array([0.5, 0.5])
    b = -0.2
    tmp = np.sum(w*x) + b
    if tmp <= 0:
        return 0
    else:
        return 1

print(f"x1=0, x2=0, y={OR(0,0)}")
print(f"x1=1, x2=0, y={OR(1,0)}")
print(f"x1=0, x2=1, y={OR(0,1)}")
print(f"x1=1, x2=1, y={OR(1,1)}")
